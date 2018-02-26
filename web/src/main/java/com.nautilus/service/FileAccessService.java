package com.nautilus.service;

import com.nautilus.services.GlobalService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileAccessService {

    public static final String CAPTURES_FOLDER_NAME = "captures";
    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final String MICRO_FOLDER_NAME = "micro";

    private static final Predicate<String> CAR_FILENAME_PREDICATE = s -> s.matches("[0-9]*");

    private final GlobalService service;

    @Value("${upload.path}")
    private String UPLOAD_FOLDER;

    public void saveCarPhotos(String beaconId, List<MultipartFile> files) {
        Long userID = service.getUserIdConfigBeaconId(beaconId);

        int fileCount = getLastIndex(userID, beaconId, null);
        for (MultipartFile file : files) {
            fileCount = fileCount + 1;
            if (file == null || file.isEmpty()) {
                logger.warn("File is empty!");
            }

            try {
                saveImage(beaconId, userID, fileCount, file);
            } catch (IOException e) {
                logger.error("Unexpected: ", e);
            }
        }
    }

    public void saveCarCapturedPhotos(String beaconId, List<MultipartFile> files) {
        Long userID = service.getUserIdConfigBeaconId(beaconId);

        int fileCount = getLastIndex(userID, beaconId, "captures");
        for (MultipartFile file : files) {
            fileCount = fileCount + 1;
            if (file == null || file.isEmpty()) {
                logger.warn("File is empty!");
            }

            try {
                saveCaptureImage(beaconId, userID, fileCount, file);
            } catch (IOException e) {
                logger.error("Unexpected: ", e);
            }
        }
    }

    private void saveFile(String filePath, MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        Path path = Paths.get(filePath);
        Files.write(path, bytes);
    }

    public File getCarPhotos(String beaconId, String index) {
        Long userId = service.getUserIdConfigBeaconId(beaconId);

        String path = UPLOAD_FOLDER + userId + "/" + beaconId;
        String wildcardFileName = index + ".**";

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{wildcardFileName});
        scanner.setBasedir(path);
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        if (files == null || files.length != 1) {
            return null;
        }

        return new File(path + "/" + files[0]);
    }

    public File getCarMicroPhotos(String beaconId, String index) {
        Long userId = service.getUserIdConfigBeaconId(beaconId);

        String path = UPLOAD_FOLDER + userId + "/" + beaconId + "/" + MICRO_FOLDER_NAME;
        String wildcardFileName = index + ".**";

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{wildcardFileName});
        scanner.setBasedir(path);
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        if (files == null || files.length != 1) {
            return null;
        }

        return new File(path + "/" + files[0]);
    }

    public File getCarCapturesPhotos(String beaconId, String index) {
        Long userId = service.getUserIdConfigBeaconId(beaconId);

        String path = UPLOAD_FOLDER + userId + "/" + beaconId + "/" + CAPTURES_FOLDER_NAME;
        String wildcardFileName = index + ".**";

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{wildcardFileName});
        scanner.setBasedir(path);
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();

        if (files == null || files.length != 1) {
            return null;
        }

        return new File(path + "/" + files[0]);
    }

    public Collection<Integer> getIndices(Long userId, String beaconId, String postfix) {
        String path = UPLOAD_FOLDER + "/" + userId + "/" + beaconId
                + (!(postfix == null || postfix.isEmpty()) ? postfix : "");
        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        if (!dir.canRead()) {
            dir.setReadable(true);
        }

        String[] list = dir.list();
        if (list == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(list)
                .map(s -> s.split("\\.")[0])
                .filter(CAR_FILENAME_PREDICATE)
                .map(Integer::new)
                .collect(Collectors.toSet());
    }

    public void deleteCar(Long userId, String beaconId) {
        String path = UPLOAD_FOLDER + userId + "/" + beaconId;
        FileSystemUtils.deleteRecursively(new File(path));
    }

    public void deleteUser(Long userId) {
        String path = UPLOAD_FOLDER + userId;
        FileSystemUtils.deleteRecursively(new File(path));
    }

    public boolean deleteCarPhoto(Long userId, String beaconId, String index) {
        String path = UPLOAD_FOLDER + userId + "/" + beaconId;
        return deleteFile(index, path)
                && deleteFile(index, path + "/" + MICRO_FOLDER_NAME);
    }

    private void saveImage(String beaconId, Long userID, int fileCount, MultipartFile file) throws IOException {
        String folderPath = UPLOAD_FOLDER + userID + "/" + beaconId + "/";
        createPath(folderPath);
        String[] splited = file.getOriginalFilename().split("\\.");
        String type = splited.length != 0 ? splited[splited.length - 1] : file.getOriginalFilename();
        String originalPath = folderPath + fileCount + "." + type;
        saveFile(originalPath, file);

        createPath(folderPath + MICRO_FOLDER_NAME + "/");
        String microPath = folderPath + MICRO_FOLDER_NAME + "/" + fileCount + "." + type;
        saveFile(microPath, rescalePhoto(file));
    }

    private void saveCaptureImage(String beaconId, Long userID, int fileCount, MultipartFile file) throws IOException {
        String folderPath = UPLOAD_FOLDER + userID + "/" + beaconId + "/" + CAPTURES_FOLDER_NAME + "/";
        createPath(folderPath);
        String[] splited = file.getOriginalFilename().split("\\.");
        String type = splited.length != 0 ? splited[splited.length - 1] : file.getOriginalFilename();
        String originalPath = folderPath + fileCount + "." + type;
        saveFile(originalPath, file);
    }

    private void saveFile(String filePath, BufferedImage bufferedImage) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        ImageIO.write(bufferedImage, "jpg", file);
    }

    private BufferedImage rescalePhoto(MultipartFile file) throws IOException {
        BufferedImage sourceImage = ImageIO.read(file.getInputStream());
        return Scalr.resize(sourceImage, Scalr.Method.ULTRA_QUALITY, 160, 90, Scalr.OP_ANTIALIAS);
    }

    private boolean deleteFile(String index, String path) {
        File dir = new File(path);

        String[] list = dir.list();
        if (list == null) {
            return false;
        }

        File file = Arrays.stream(list)
                .filter(name -> name.matches(index + "\\..+"))
                .findFirst()
                .map(s -> new File(path + "/" + s))
                .orElse(null);

        return file != null
                && file.exists()
                && file.setWritable(true)
                && file.delete();
    }

    private int getLastIndex(Long userId, String beaconId, String postfix) {
        String path = UPLOAD_FOLDER + userId + "/" + beaconId
                + (!(postfix == null || postfix.isEmpty()) ? "/" + postfix : "");
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

    private void createPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        if (file.canWrite()) {
            file.setWritable(true);
        }
    }
}
