package com.nautilus.service;

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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileAccessService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Value("${upload.path}")
    private String UPLOAD_FOLDER;

    private final GlobalService service;

    public void saveCarPhotos(String beaconId, List<MultipartFile> files) {
        Long userID = service.getUserIdConfigBeaconId(beaconId);

        int fileCount = getLastIndex(userID, beaconId);
        for (MultipartFile file : files) {
            fileCount = fileCount + 1;
            if (file == null || file.isEmpty()) {
                logger.warn("File is empty!");
            }

            try {
                byte[] bytes = file.getBytes();
                String folderPath = UPLOAD_FOLDER + userID + "/" + beaconId + "/";
                String[] splited = file.getOriginalFilename().split("\\.");
                String type = splited.length != 0 ? splited[splited.length - 1] : file.getOriginalFilename();
                createPath(folderPath);
                Path path = Paths.get(folderPath + fileCount + "." + type);
                Files.write(path, bytes);
                logger.warn("File has been written!");
            } catch (IOException e) {
                logger.error("Unexpected: ", e);
            }
        }
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

        File file = new File(path + "/" + files[0]);

        return file;
    }

    public List<Integer> getListOfIndices(Long userId, String beaconId) {
        String path = UPLOAD_FOLDER + "/" + userId + "/" + beaconId;
        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            return Collections.emptyList();
        }

        if (!dir.canRead()) {
            dir.setReadable(true);
        }

        return Arrays.stream(dir.list())
                .map(s -> s.split("\\.")[0])
                .map(Integer::new)
                .collect(Collectors.toList());
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
        File dir = new File(path);

        File file = Arrays.stream(dir.list())
                .filter(name -> name.matches(index + "\\..+"))
                .findFirst()
                .map(s -> new File(path + "/" + s))
                .orElse(null);

        return file != null
                && file.exists()
                && file.setWritable(true)
                && file.delete();
    }

    private int getLastIndex(Long userId, String beaconId) {
        String path = UPLOAD_FOLDER + userId + "/" + beaconId;
        File dir = new File(path);

        String[] list = dir.list();
        if(list == null){
            return -1;
        }

        return Arrays.stream(list)
                .map(name -> name.split("\\..+")[0])
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
