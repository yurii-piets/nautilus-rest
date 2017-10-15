package com.nautilus.utilities;

import com.nautilus.domain.Car;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class FileAccessUtility {
    @Value("${upload.path}")
    private String UPLOAD_FOLDER;

    @Autowired
    private GlobalService service;

    public void saveCarPhotos(String carId, List<MultipartFile> files) {
        Long userID = service.getUserIdConfigByCarBeaconId(carId);

        int fileCount = 0;
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                System.out.println("File is empty!");
            }

            try {
                byte[] bytes = file.getBytes();
                String folderPath = UPLOAD_FOLDER + userID + "/" + carId + "/";
                String[] splited = file.getOriginalFilename().split("\\.");
                String type = splited.length != 0 ? splited[splited.length - 1] : file.getOriginalFilename();
                createPath(folderPath);
                Path path = Paths.get(folderPath + fileCount + "." + type);
                Files.write(path, bytes);
                System.out.println("File has been written!");
            } catch (IOException e) {
                e.printStackTrace();
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

        if(!dir.exists() || !dir.isDirectory()){
            return;
        }

        if(!dir.canWrite()){
            dir.setWritable(true);
        }

        for(File file : dir.listFiles()){
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

    public List<File> getCarPhotos(Long userId, String carId) {
        String path = UPLOAD_FOLDER + "/" + userId + "/" + carId;
        File dir = new File(path);

        if(!dir.exists() || !dir.isDirectory()){
            return Collections.emptyList();
        }

        if(!dir.canRead()){
            dir.setReadable(true);
        }

        File[] files = dir.listFiles();

        if(files == null){
            return Collections.emptyList();
        }

        return Arrays.asList(files);
    }
}
