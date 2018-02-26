package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.dto.car.CarStatusDTO;
import com.nautilus.service.AuthorizationService;
import com.nautilus.services.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.nautilus.rest.controllers.car.CarCapturesController.CAR_FOUND_MAPPING;

@RestController
@RequestMapping(value = CAR_FOUND_MAPPING)
@RequiredArgsConstructor
public class CarCapturesController {

    public final static String CAR_FOUND_MAPPING = "/car";

    private final GlobalService service;

    private final AuthorizationService authorizationService;

    @RequestMapping(value = "/captures/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> captures(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);

        Car car = service.findCarByBeaconId(beaconId);
        return new ResponseEntity<>(car.getStatusSnapshots(), HttpStatus.OK);
    }

    @RequestMapping(value = "/capture/{beaconId}", method = RequestMethod.POST)
    public ResponseEntity<?> capture(
            @PathVariable String beaconId,
            @RequestBody @Valid CarStatusDTO carStatusDTO) {

        CarStatus status = service.getCarStatusByCarBeaconId(beaconId);
        if (status == null) {
            status = CarStatus.TESTING;
        }

        if (status.equals(CarStatus.TESTING) || status.equals(CarStatus.STOLEN)) {
            CarLocation carLocation = new CarLocation(carStatusDTO);
            service.save(carLocation);

            Car car = service.findCarByBeaconId(beaconId);
            CarStatusSnapshot carStatusSnapshot = CarStatusSnapshot.builder()
                    .carLocation(carLocation)
                    .car(car)
                    .captureTime(carStatusDTO.getCaptureTime())
                    .build();

            service.save(carStatusSnapshot);

            car.getStatusSnapshots().add(carStatusSnapshot);
            service.save(car);
        }

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
