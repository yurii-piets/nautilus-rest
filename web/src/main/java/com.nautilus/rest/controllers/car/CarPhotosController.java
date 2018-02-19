package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.service.AuthorizationService;
import com.nautilus.service.FileAccessService;
import com.nautilus.services.GlobalService;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nautilus.rest.controllers.car.CarPhotosController.CAR_PHOTOS_MAPPING;

@RestController
@RequestMapping(path = CAR_PHOTOS_MAPPING)
@RequiredArgsConstructor
public class CarPhotosController {

    public final static String CAR_PHOTOS_MAPPING = "/car/photos";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final GlobalService service;

    private final FileAccessService fileAccessService;

    private final AuthorizationService authorizationService;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private Integer port;

    @Value("${server.contextPath}")
    private String contextPath;

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> photos(@PathVariable String beaconId) {
        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserConfig owner = car.getOwner();
        if (owner == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Long userId = owner.getUserId();
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Set<URL> urls = new HashSet<>();
        List<Integer> indices = fileAccessService.getListOfIndices(userId, beaconId);

        for (Integer index : indices) {
            urls.add(buildUrl(beaconId, index));
        }

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = "/{beaconId}/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> photo(@PathVariable String beaconId,
                                   @PathVariable String index
    ) {
        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileAccessService.getCarPhotos(beaconId, index);

        try {
            byte[] b = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            return new ResponseEntity<>(b, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.POST)
    public ResponseEntity<?> register(@PathVariable String beaconId,
                                      @RequestParam("file") List<MultipartFile> files) {

        if (!authorizationService.hasAccessByBeaconId(beaconId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Car car = service.getCarById(beaconId);
        if (car == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        fileAccessService.saveCarPhotos(beaconId, files);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{beaconId}/{index}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String beaconId,
                                    @PathVariable String index
    ) {
        if (!authorizationService.hasAccessByBeaconId(beaconId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserConfig owner = car.getOwner();
        if (owner == null || owner.getUserId() == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        boolean deleted = fileAccessService.deleteCarPhoto(owner.getUserId(), beaconId, index);
        if (!deleted) {
            return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private URL buildUrl(String beaconId, int index) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder
                .append(contextPath)
                .append(CAR_PHOTOS_MAPPING)
                .append("/").append(beaconId)
                .append("/").append(index);
        URL url = null;
        try {
            url = new URL(protocol, host, port, pathBuilder.toString());
        } catch (MalformedURLException e) {
            logger.error("Unexpected: ", e);
        }
        return url;
    }
}
