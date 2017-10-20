package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.rest.mapping.MappingProperties;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(method = RequestMethod.GET)
public class CarInfoController {

    @Autowired
    private GlobalService service;

    @Autowired
    private FileAccessUtility fileAccessUtility;

    @Autowired
    private MappingProperties properties;

    @Value("${server.protocol}")
    private String protocol;

    @Value("${server.host}")
    private String host;

    @Value("${server.port}")
    private int port;

    @RequestMapping(value = "${car.get.info}")
    public ResponseEntity<Car> car(@RequestParam String carId) {
        Car car = service.findCarByBeaconId(carId);

        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @RequestMapping(value = "${car.get.photos}/{userId}/{beaconId}/{index}")
    public ResponseEntity<byte[]> photo(@PathVariable Long userId,
                                               @PathVariable String beaconId,
                                               @PathVariable String index
    ){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        File file = fileAccessUtility.getCarPhotos(userId, beaconId, index);

        byte[] b = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fileInputStream.read(b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    @RequestMapping(value="${car.get.photos}" )
    public ResponseEntity<Set<URL>> photos(@RequestParam String beaconId){

        // TODO: 19/10/2017 create method in service to checj if car with current beaconid exists
        Car car = service.findCarByBeaconId(beaconId);
        if(car == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserConfig owner = car.getOwner();
        if(owner == null){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Long userId = owner.getUserId();
        if(userId == null){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Set<URL> urls = new HashSet<>();
        int size = fileAccessUtility.countOfPhotos(userId, beaconId);

        for(int i = 0; i < size; ++i){
            urls.add(buildUrl(beaconId, userId, i));
        }

        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    private URL buildUrl(@RequestParam String beaconId, Long userId, int i) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(properties.getCarPhotos());
        pathBuilder.append("/"); pathBuilder.append(userId);
        pathBuilder.append("/"); pathBuilder.append(beaconId);
        pathBuilder.append("/"); pathBuilder.append(i);
        URL url = null;
        try {
            url = new URL(protocol, host, port, pathBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}