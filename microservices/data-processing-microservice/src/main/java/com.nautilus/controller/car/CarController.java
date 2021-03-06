package com.nautilus.controller.car;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.dto.PartialUpdateBody;
import com.nautilus.dto.car.CarRegisterDto;
import com.nautilus.feign.CarPhotosClient;
import com.nautilus.node.CarNode;
import com.nautilus.node.UserNode;
import com.nautilus.security.AuthHeaderConverter;
import com.nautilus.service.AuthorizationService;
import com.nautilus.service.DataService;
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
import java.util.Set;

import static com.nautilus.controller.car.CarController.CAR_MAPPING;

@RestController
@RequestMapping(path = CAR_MAPPING)
@RequiredArgsConstructor
public class CarController {

    public final static String CAR_MAPPING = "/car";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final DataService service;

    private final JsonPatchUtility patchUtility;

    private final AuthorizationService authorizationService;

    private final CarPhotosClient carPhotosClient;

    private final AuthHeaderConverter authHeaderConverter;

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> info(@PathVariable String beaconId) {
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        return new ResponseEntity<>(car.toCarDto(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{beaconId}/status", method = RequestMethod.GET)
    public ResponseEntity<?> status(@PathVariable String beaconId) {
        return new ResponseEntity<>(service.getCarStatusByCarBeaconId(beaconId), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid CarRegisterDto carRegisterDto) {
        UserNode user = service.getUserNodeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        CarNode duplicatedCar = service.getCarNodeByBeaconIdOrRegisterNumber(carRegisterDto.getBeaconId(), carRegisterDto.getRegisterNumber());
        if (duplicatedCar != null) {
            return new ResponseEntity(HttpStatus.NOT_ACCEPTABLE);
        }

        CarNode car = new CarNode(carRegisterDto, user);
        if (user.getCars() != null) {
            user.getCars().add(car);
        } else {
            user.setCars(Set.of(car));
        }
        service.save(user);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.PATCH)
    public ResponseEntity update(@PathVariable String beaconId,
                                 @RequestBody Set<PartialUpdateBody> patches) {

        authorizationService.hasAccessByBeaconId(beaconId);

        try {
            CarNode car = service.getCarNodeByBeaconId(beaconId);
            CarNode mergedCar = (CarNode) patchUtility.patch(patches, car).get();
            mergedCar.setOwner(car.getOwner());
            mergedCar.setStatusSnapshots(car.getStatusSnapshots());
            mergedCar.setId(car.getId());
            service.save(mergedCar);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(value = "/{beaconId}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);

        CarNode car = service.getCarNodeByBeaconId(beaconId);
        carPhotosClient.deleteCarPhotos(authHeaderConverter.convertCredentialsToHeader(), beaconId);
        service.deleteCarById(car.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
