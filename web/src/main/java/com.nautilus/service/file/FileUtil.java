package com.nautilus.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

public interface FileUtil {

    File getOriginal(String beaconId, Integer index) throws FileNotFoundException;
    File getMicro(String beaconId, Integer index) throws FileNotFoundException;
    File getCapture(String beaconId, Integer index) throws FileNotFoundException;
    File getCaptureMicro(String beaconId, Integer index) throws FileNotFoundException;

    Collection<Integer> getOriginalIndices(String beaconId);
    Collection<Integer> getCaptureIndices(String beaconId);

    void saveOriginal(String beaconId, Collection<MultipartFile> files) throws IOException;
    void saveCapture(String beaconId, Collection<MultipartFile> files) throws IOException;

    void delete(Long userId);
    void delete(String beaconId);
    void delete(String beaconId, Integer index) throws FileNotFoundException;
}
