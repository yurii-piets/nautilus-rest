package com.nautilus.rest.controllers.car;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.domain.Car;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import com.nautilus.utilities.JsonPatchUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class UpdateCarController {

    @Autowired
    private GlobalService service;

    @Autowired
    private FileAccessUtility fileSaveUtility;

    @Autowired
    private JsonPatchUtility patchUtility;

    private final Logger logger = LogManager.getLogger(this.getClass());

    /*
    * @param Car beaconId
    * @param update entity, matches pattern : [{"op": "/replace", "path": "/registerNumber", "value": "WW121"}]
    * @return ResponseEntity with HttpStatus
    */
    @RequestMapping(value = "${car.update}", method = RequestMethod.PATCH)
    public ResponseEntity update(@RequestParam String beaconId, @RequestBody String updateBody) {
        Car car = service.findCarByBeaconId(beaconId);

        if (car == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            Car mergedCar = (Car) patchUtility.patch(beaconId, car).get();
            service.save(mergedCar);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "${car.update-photos}", method = RequestMethod.PUT)
    public ResponseEntity updatePhotos(@RequestParam String carId,
                                   @RequestParam("file") List<MultipartFile> files) {
        if (carId == null || carId.isEmpty()) {
            new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if(files == null || files.isEmpty()){
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        fileSaveUtility.deleteAndSaveCarPhotos(carId, files);

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

}
