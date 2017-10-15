package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ServletContextResource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping(method = RequestMethod.GET)
public class CarInfoController {

    @Autowired
    private GlobalService service;

    @Autowired
    private FileAccessUtility fileAccessUtility;

    @RequestMapping(value = "${car.get.info}")
    public Car car(@RequestParam String carId) {
        Car car = service.findCarByBeaconId(carId);
        return car;
    }

    @RequestMapping(value = "${car.get.photos}")
    @ResponseBody
    public ResponseEntity<List<byte[]>> photos(@RequestParam String carId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        Car car = service.findCarByBeaconId(carId);
        if (car == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.BAD_REQUEST);
        }

        UserConfig owner = car.getOwner();

        if (owner == null) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_ACCEPTABLE);
        }

        List<File> files = fileAccessUtility.getCarPhotos(owner.getUserId(), carId);

        List<byte[]> result = files.stream()
                .map(file -> {
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
                    return b;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
