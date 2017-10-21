package com.nautilus.utilities;

import com.nautilus.services.def.GlobalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.DirectoryScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class FileAccessUtility {
    @Value("${upload.path}")
    private String UPLOAD_FOLDER;

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private GlobalService service;

    public void saveCarPhotos(String carId, List<MultipartFile> files) {
        Long userID = service.getUserIdConfigByCarBeaconId(carId);

        int fileCount = 0;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                logger.warn("File is empty!");
            }

            try {
                byte[] bytes = file.getBytes();
                String folderPath = UPLOAD_FOLDER + userID + "/" + carId + "/";
                String[] splited = file.getOriginalFilename().split("\\.");
                String type = splited.length != 0 ? splited[splited.length - 1] : file.getOriginalFilename();
                createPath(folderPath);
                Path path = Paths.get(folderPath + fileCount + "." + type);
                Files.write(path, bytes);
                logger.warn("File has been written!");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            fileCount++;
        }
    }

    public void deleteAndSaveCarPhotos(String carId, List<MultipartFile> files) {
        Long userID = service.getUserIdConfigByCarBeaconId(carId);
        String folderPath = UPLOAD_FOLDER + userID + "/" + carId + "/";
        clearDir(folderPath);

        saveCarPhotos(carId, files);
    }

    private void clearDir(String folderPath) {
        File dir = new File(folderPath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        if (!dir.canWrite()) {
            dir.setWritable(true);
        }

        for (File file : dir.listFiles()) {
            file.delete();
        }
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

    public File getCarPhotos(Long userId, String carId, String index) {
        String path = UPLOAD_FOLDER + userId + "/" + carId;
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

    public int countOfPhotos(Long userId, String carId) {
        String path = UPLOAD_FOLDER + "/" + userId + "/" + carId;
        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory()) {
            return -1;
        }

        if (!dir.canRead()) {
            dir.setReadable(true);
        }

        File[] files = dir.listFiles();

        int size = files != null ? files.length : 0;

        return size;
    }
}
