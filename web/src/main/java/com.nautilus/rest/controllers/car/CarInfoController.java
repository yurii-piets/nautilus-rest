package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.service.AuthorizationService;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static com.nautilus.rest.controllers.car.CarInfoController.CAR_INFO_BASIC_MAPPING;

@RestController
@RequestMapping(path = CAR_INFO_BASIC_MAPPING)
@RequiredArgsConstructor
public class CarInfoController {

    public final static String CAR_INFO_BASIC_MAPPING = "/car/info";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final GlobalService service;

    private final FileAccessUtility fileAccessUtility;

    private final AuthorizationService authorizationService;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private Integer port;

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> car(@PathVariable String beaconId) {
        if(!authorizationService.hasAccessByBeaconId(beaconId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @RequestMapping(value = "/photos/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<Set<URL>> photos(@PathVariable String beaconId) {
        if(!authorizationService.hasAccessByBeaconId(beaconId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

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
        int size = fileAccessUtility.countOfPhotos(userId, beaconId);

        for (int i = 0; i < size; ++i) {
            urls.add(buildUrl(beaconId, userId, i));
        }

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @RequestMapping(value = "photos/{userId}/{beaconId}/{index}", method = RequestMethod.GET)
    public ResponseEntity<?> photo(@PathVariable Long userId,
                                        @PathVariable String beaconId,
                                        @PathVariable String index
    ) {
        if(!authorizationService.hasAccessByBeaconId(beaconId)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileAccessUtility.getCarPhotos(userId, beaconId, index);

        try {
            byte[] b = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            return new ResponseEntity<>(b, headers, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Unexpected", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private URL buildUrl(@RequestParam String beaconId, Long userId, int i) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(CAR_INFO_BASIC_MAPPING + "/photos")
                .append("/").append(userId)
                .append("/").append(beaconId)
                .append("/").append(i);
        URL url = null;
        try {
            url = new URL(protocol, host, port, pathBuilder.toString());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        }
        return url;
    }
}
