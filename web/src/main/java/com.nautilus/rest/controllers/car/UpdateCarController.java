package com.nautilus.rest.controllers.car;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.domain.Car;
import com.nautilus.service.AuthorizationService;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.FileAccessUtility;
import com.nautilus.utilities.JsonPatchUtility;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.nautilus.rest.controllers.car.UpdateCarController.CAR_UPDATE_MAPPING;

@RestController
@RequestMapping(path = CAR_UPDATE_MAPPING)
@RequiredArgsConstructor
public class UpdateCarController {

    public final static String CAR_UPDATE_MAPPING = "/car/update";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final GlobalService service;

    private final FileAccessUtility fileSaveUtility;

    private final JsonPatchUtility patchUtility;

    private final AuthorizationService authorizationService;

    /*
    * @param Car beaconId
    * @param update entity, matches pattern : [{"op": "replace", "path": "/registerNumber", "value": "WW121"}]
    * @return ResponseEntity with HttpStatus
    */
    @RequestMapping(value = "/{beaconId}", method = RequestMethod.PATCH)
    public ResponseEntity update(@PathVariable String beaconId,
                                 @RequestBody String updateBody) {

        if (!authorizationService.hasAccessByBeaconId(beaconId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            Car mergedCar = (Car) patchUtility.patch(updateBody, car).get();
            mergedCar.setOwner(car.getOwner());
            mergedCar.setStatusSnapshots(car.getStatusSnapshots());
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

    @RequestMapping(value = "/photos/{beaconId}", method = RequestMethod.PUT)
    public ResponseEntity updatePhotos(@PathVariable String beaconId,
                                       @RequestParam("file") List<MultipartFile> files) {

        if (!authorizationService.hasAccessByBeaconId(beaconId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (files == null || files.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        fileSaveUtility.deleteAndSaveCarPhotos(beaconId, files);

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

}
