package com.nautilus.controller;

import com.nautilus.dto.constants.CarStatus;
import com.nautilus.exception.IllegalAccessException;
import com.nautilus.service.AuthorizationService;
import com.nautilus.service.DataService;
import com.nautilus.service.file.FileUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nautilus.controller.CarPhotosController.CAR_PHOTOS_MAPPING;

@RestController
@RequestMapping(path = CAR_PHOTOS_MAPPING)
@RequiredArgsConstructor
public class CarPhotosController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public final static String CAR_PHOTOS_MAPPING = "/car/{beaconId}/photos";

    public static final String MICRO_MAPPING = "/micro";

    public static final String CAPTURES_MAPPING = "/captures";

    private final DataService service;

    private final FileUtil fileUtil;

    private final AuthorizationService authorizationService;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.servlet.contextPath}")
    private String contextPath;

    @Value("${photos.max}")
    private Integer maxPhotos;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> photos(@PathVariable String beaconId) {
        service.checkIfCarExistByBeaconId(beaconId);
        try {
            authorizationService.hasAccessByBeaconId(beaconId);
        } catch (IllegalAccessException e) {
            if(CarStatus.STOLEN != service.getCarStatusByCarBeaconId(beaconId)){
                throw e;
            }
        }

        Collection<Integer> indices = fileUtil.getOriginalIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, null))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = MICRO_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> micro(@PathVariable String beaconId) {
        service.checkIfCarExistByBeaconId(beaconId);
        try {
            authorizationService.hasAccessByBeaconId(beaconId);
        } catch (IllegalAccessException e) {
            if(CarStatus.STOLEN != service.getCarStatusByCarBeaconId(beaconId)){
                throw e;
            }
        }

        Collection<Integer> indices = fileUtil.getOriginalIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, MICRO_MAPPING))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> captures(@PathVariable String beaconId) {
        service.checkIfCarExistByBeaconId(beaconId);
        authorizationService.hasAccessByBeaconId(beaconId);

        Collection<Integer> indices = fileUtil.getCaptureIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, CAPTURES_MAPPING))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + MICRO_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> capturesMicro(@PathVariable String beaconId) {
        service.checkIfCarExistByBeaconId(beaconId);
        authorizationService.hasAccessByBeaconId(beaconId);

        Collection<Integer> indices = fileUtil.getCaptureIndices(beaconId);

        Set<URL> urls = indices.stream()
                .map(i -> buildPhotoUrl(beaconId, i, CAPTURES_MAPPING + MICRO_MAPPING))
                .collect(Collectors.toSet());

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> photo(@PathVariable String beaconId,
                                   @PathVariable Integer index
    ) throws IOException {
        service.checkIfCarExistByBeaconId(beaconId);
        try {
            authorizationService.hasAccessByBeaconId(beaconId);
        } catch (IllegalAccessException e) {
            if(CarStatus.STOLEN != service.getCarStatusByCarBeaconId(beaconId)){
                throw e;
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getOriginal(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);

    }

    @RequestMapping(value = MICRO_MAPPING + "/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> microPhoto(@PathVariable String beaconId,
                                        @PathVariable Integer index
    ) throws IOException {
        service.checkIfCarExistByBeaconId(beaconId);
        try {
            authorizationService.hasAccessByBeaconId(beaconId);
        } catch (IllegalAccessException e) {
            if(CarStatus.STOLEN != service.getCarStatusByCarBeaconId(beaconId)){
                throw e;
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getMicro(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + "/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> capturePhoto(@PathVariable String beaconId,
                                          @PathVariable Integer index
    ) throws IOException {
        service.checkIfCarExistByBeaconId(beaconId);
        authorizationService.hasAccessByBeaconId(beaconId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getCapture(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(value = CAPTURES_MAPPING + "/{index}" + MICRO_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> microCapture(@PathVariable String beaconId,
                                          @PathVariable Integer index
    ) throws IOException {
        service.checkIfCarExistByBeaconId(beaconId);
        authorizationService.hasAccessByBeaconId(beaconId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileUtil.getCaptureMicro(beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(b);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@PathVariable String beaconId,
                                      @RequestParam("file") Collection<MultipartFile> files) throws IOException {

        authorizationService.hasAccessByBeaconId(beaconId);
        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        if (files.size() > maxPhotos) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        service.checkIfCarExistByBeaconId(beaconId);
        fileUtil.saveOriginal(beaconId, files);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = CAPTURES_MAPPING, method = RequestMethod.POST)
    public ResponseEntity<?> capture(@PathVariable String beaconId,
                                     @RequestParam("file") List<MultipartFile> files) throws IOException {

        service.checkIfCarExistByBeaconId(beaconId);
        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        if (files.size() > maxPhotos) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }
        fileUtil.saveCapture(beaconId, files);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);
        service.checkIfCarExistByBeaconId(beaconId);
        fileUtil.delete(beaconId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{index}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String beaconId,
                                    @PathVariable Integer index
    ) throws FileNotFoundException {

        authorizationService.hasAccessByBeaconId(beaconId);
        service.checkIfCarExistByBeaconId(beaconId);
        fileUtil.delete(beaconId, index);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private URL buildPhotoUrl(String beaconId, int index, String postfix) {
        StringBuilder pathBuilder = new StringBuilder()
                .append(contextPath.equals("/") ? "" : contextPath)
                .append(CAR_PHOTOS_MAPPING.replace("{beaconId}", beaconId));

        pathBuilder.append("/").append(index);

        if (!(postfix == null || postfix.isEmpty())) {
            pathBuilder.append(postfix);
        }

        URL url = null;
        try {
            url = new URL(protocol, host, port, pathBuilder.toString());
        } catch (MalformedURLException e) {
            logger.error("Unexpected: ", e);
        }
        return url;
    }
}
