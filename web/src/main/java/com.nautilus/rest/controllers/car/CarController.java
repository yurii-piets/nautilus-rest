package com.nautilus.rest.controllers.car;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.service.AuthorizationService;
import com.nautilus.service.FileAccessService;
import com.nautilus.services.GlobalService;
import com.nautilus.utilities.JsonPatchUtility;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashSet;

import static com.nautilus.rest.controllers.car.CarController.CAR_MAPPING;

@RestController
@RequestMapping(path = CAR_MAPPING)
@RequiredArgsConstructor
public class CarController {

    public final static String CAR_MAPPING = "/car";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final GlobalService service;

    private final JsonPatchUtility patchUtility;

    private final AuthorizationService authorizationService;

    private final FileAccessService fileAccessService;

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> info(@PathVariable String beaconId) {
        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(car, HttpStatus.OK);
    }

    @RequestMapping(value = "/status/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> status(@PathVariable String beaconId) {
        Car car = service.findCarByBeaconId(beaconId);

        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(car.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid CarRegisterDTO carRegisterDTO) {
        UserConfig user = service.findUserConfigByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Car duplicatedCar = service.findCarByBeaconIdOrRegisterNumber(carRegisterDTO.getBeaconId(), carRegisterDTO.getRegisterNumber());
        if (duplicatedCar != null) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        Car car = new Car(carRegisterDTO);

        car.setOwner(user);
        car.setStatus(CarStatus.TESTING);

        user.getCars().add(car);

        service.save(car);

        service.save(new HashSet<UserConfig>() {{
            add(user);
        }});

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * @param beaconId
     * @param updateBody, matches pattern : [{"op": "replace", "path": "/registerNumber", "value": "WW121"}]
     * @return ResponseEntity with HttpStatus
     */
    @RequestMapping(value = "/{beaconId}", method = RequestMethod.PATCH)
    public ResponseEntity update(@PathVariable String beaconId,
                                 @RequestBody String updateBody) {

        authorizationService.hasAccessByBeaconId(beaconId);

        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            Car mergedCar = (Car) patchUtility.patch(updateBody, car).get();
            mergedCar.setOwner(car.getOwner());
            mergedCar.setStatusSnapshots(car.getStatusSnapshots());
            mergedCar.setCarId(car.getCarId());
            service.update(mergedCar);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);

        Car car = service.findCarByBeaconId(beaconId);
        if (car == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        service.delete(car);
        fileAccessService.deleteCar(car.getOwner().getUserId(), car.getBeaconId());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
