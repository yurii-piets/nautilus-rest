package com.nautilus.service.file;

import com.nautilus.exception.FileNotDeletedException;
import com.nautilus.exception.OverLimitNumberOfFilesException;
import com.nautilus.service.AsyncPhotoProcessor;
import com.nautilus.services.GlobalService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileUtilImpl implements FileUtil {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final String CAPTURES_FOLDER_NAME = "/captures";

    public static final String MICRO_FOLDER_NAME = "/micro";

    private final Predicate<String> CAR_FILENAME_PREDICATE = s -> s.matches("[0-9]*");

    private final GlobalService service;

    private final AsyncPhotoProcessor photoProcessor;

    @Value("${photos.path}")
    private String mainFolder;

    @Value("${photos.max}")
    private Integer maxPhotos;

    @Override
    public File getOriginal(String beaconId, Integer index) throws FileNotFoundException {
        String path = createPath(beaconId, null);
        return getFile(path, index);
    }

    @Override
    public File getMicro(String beaconId, Integer index) throws FileNotFoundException {
        String path = createPath(beaconId, MICRO_FOLDER_NAME);
        return getFile(path, index);
    }

    @Override
    public File getCapture(String beaconId, Integer index) throws FileNotFoundException {
        String path = createPath(beaconId, CAPTURES_FOLDER_NAME);
        return getFile(path, index);
    }

    @Override
    public File getCaptureMicro(String beaconId, Integer index) throws FileNotFoundException {
        String path = createPath(beaconId, CAPTURES_FOLDER_NAME + MICRO_FOLDER_NAME);
        return getFile(path, index);
    }

    @Override
    public Collection<Integer> getOriginalIndices(String beaconId) {
        String path = createPath(beaconId, null);
        return getIndices(path);
    }

    @Override
    public Collection<Integer> getCaptureIndices(String beaconId) {
        String path = createPath(beaconId, CAPTURES_FOLDER_NAME);
        return getIndices(path);
    }

    @Override
    public void saveOriginal(String beaconId, Collection<MultipartFile> files) throws IOException {
        String path = createPath(beaconId, null);
        Integer photosCount = getIndices(path).size();
        if (files.size() + photosCount > maxPhotos) {
            throw new OverLimitNumberOfFilesException("Excited maximum number of car photos.");
        }

        saveFiles(beaconId, files, null);
    }

    @Override
    public void saveCapture(String beaconId, Collection<MultipartFile> files) throws IOException {
        saveFiles(beaconId, files, CAPTURES_FOLDER_NAME);
    }

    @Override
    public void delete(Long userId) {
        String path = mainFolder + "/" + userId;
        FileSystemUtils.deleteRecursively(new File(path));
    }

    @Override
    public void delete(String beaconId) {
        String path = createPath(beaconId, null);
        FileSystemUtils.deleteRecursively(new File(path));
    }

    @Override
    public void delete(String beaconId, Integer index) throws FileNotFoundException {
        String path = createPath(beaconId, null);
        deleteFile(index, path);

        String microPath = createPath(beaconId, MICRO_FOLDER_NAME);
        deleteFile(index, microPath);
    }

    private File getFile(String path, Integer index) throws FileNotFoundException {
        String wildcardFileName = index + ".**";

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{wildcardFileName});
        scanner.setBasedir(path);
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        if (files == null || files.length != 1) {
            throw new FileNotFoundException("File [" + path + index + "] not found");
        }

        return new File(path + "/" + files[0]);
    }

    private Collection<Integer> getIndices(String path) {
        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptySet();
        }

        boolean oldReadable = dir.canRead();
        if (!dir.canRead()) {
            dir.setReadable(true);
        }

        String[] list = dir.list();
        if (list == null) {
            return Collections.emptySet();
        }

        dir.setReadable(oldReadable);

        return Arrays.stream(list)
                .map(s -> s.split("\\.")[0])
                .filter(CAR_FILENAME_PREDICATE)
                .map(Integer::new)
                .collect(Collectors.toSet());
    }

    private Integer getLastIndex(String beaconId, String postfix) {
        String path = createPath(beaconId, postfix);
        File dir = new File(path);

        String[] list = dir.list();
        if (list == null) {
            return -1;
        }

        return Arrays.stream(list)
                .map(name -> name.split("\\..+")[0])
                .filter(CAR_FILENAME_PREDICATE)
                .map(Integer::new)
                .max(Integer::compare)
                .orElse(0);
    }

    private void saveFiles(String beaconId, Collection<MultipartFile> files, String postfix) throws IOException {
        Integer lastIndex = getLastIndex(beaconId, postfix);
        HashMap<String, byte[]> bytes = new HashMap<>();

        for (MultipartFile file : files) {
            lastIndex = lastIndex + 1;
            if (file == null || file.isEmpty()) {
                logger.warn("File is empty!");
                continue;
            }

            String fileName = lastIndex + "." + getFileType(file);
            bytes.put(fileName, file.getBytes());
        }

        photoProcessor.savePhotos(beaconId, bytes, postfix);
    }

    private String getFileType(MultipartFile file) {
        String[] splited = file.getOriginalFilename().split("\\.");
        return splited.length != 0 ? splited[splited.length - 1] : file.getOriginalFilename();
    }


    private String createPath(String beaconId, String postfix) {
        Long userId = service.getUserIdConfigBeaconId(beaconId);

        StringBuilder builder = new StringBuilder();
        builder.append(mainFolder)
                .append(userId).append("/")
                .append(beaconId);

        if (!(postfix == null || postfix.isEmpty())) {
            builder.append(postfix).append("/");
        } else {
            builder.append("/");
        }

        return builder.toString();
    }

    private void deleteFile(Integer index, String path) throws FileNotFoundException {
        File file = getFile(path, index);

        if (file != null && file.exists()) {
            file.setWritable(true);
            boolean deleted = file.delete();
            if (!deleted) {
                throw new FileNotDeletedException("File [" + path + index + "] was not deleted.");
            }
        } else {
            throw new FileNotFoundException("File [" + path + index + "] not found.");
        }
    }
}
