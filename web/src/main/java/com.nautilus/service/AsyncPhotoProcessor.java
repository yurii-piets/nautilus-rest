package com.nautilus.service;

import com.nautilus.postgres.services.GlobalService;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.nautilus.service.file.FileUtilImpl.MICRO_FOLDER_NAME;

@Async
@Service
@RequiredArgsConstructor
public class AsyncPhotoProcessor {

    private final GlobalService service;

    @Value("${photos.path}")
    private String mainFolder;

    public void savePhotos(String beaconId, Map<String, byte[]> files, String postfix) throws IOException {
        for (Map.Entry<String, byte[]> entry : files.entrySet()) {
            String fileName = entry.getKey();
            byte[] fileBody = entry.getValue();

            String filePath = createPath(beaconId, postfix);
            buildPath(filePath);
            saveFile(filePath + fileName, fileBody);

            filePath = createPath(beaconId, (!(postfix == null || postfix.isEmpty()) ? postfix : "") + MICRO_FOLDER_NAME);
            buildPath(filePath);
            saveFile(filePath + fileName, rescale(fileBody));
        }
    }

    private BufferedImage rescale(byte[] file) throws IOException {
        BufferedImage sourceImage = ImageIO.read(new ByteArrayInputStream(file));
        return Scalr.resize(sourceImage, Scalr.Method.ULTRA_QUALITY, 160, 90, Scalr.OP_ANTIALIAS);
    }

    private void buildPath(String path) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        if (!file.canWrite()) {
            file.setWritable(true);
        }
    }

    private void saveFile(String filePath, byte[] file) throws IOException {
        File f = new File(filePath);
        boolean oldWritable = f.canWrite();
        f.setWritable(true);

        Path path = Paths.get(filePath);
        Files.write(path, file);

        f.setWritable(oldWritable);
    }

    private void saveFile(String filePath, BufferedImage bufferedImage) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        ImageIO.write(bufferedImage, "jpg", file);
    }

    private String createPath(String beaconId, String postfix) {
        Long userId = service.getUserIdConfigBeaconId(beaconId);

        StringBuilder builder = new StringBuilder();
        builder.append(mainFolder)
                .append(userId).append("/")
                .append(beaconId).append("/");

        if (!(postfix == null || postfix.isEmpty())) {
            builder.append(postfix).append("/");
        }

        return builder.toString();
    }
}
