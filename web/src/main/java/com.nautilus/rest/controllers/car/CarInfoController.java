package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<MultipartFile> photo(){
        return new ResponseEntity<>(HttpStatus.OK);
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
        pathBuilder.append("/"); pathBuilder.append(beaconId);
        pathBuilder.append("/"); pathBuilder.append(userId);
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
