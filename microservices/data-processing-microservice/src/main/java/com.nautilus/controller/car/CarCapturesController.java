package com.nautilus.controller.car;

import com.nautilus.dto.constants.CarStatus;
import com.nautilus.dto.car.CarStatusSnapshotDto;
import com.nautilus.node.CarNode;
import com.nautilus.node.CarStatusSnapshotNode;
import com.nautilus.service.AuthorizationService;
import com.nautilus.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.nautilus.controller.car.CarCapturesController.CAR_FOUND_MAPPING;

@RestController
@RequestMapping(value = CAR_FOUND_MAPPING)
@RequiredArgsConstructor
public class CarCapturesController {

    public final static String CAR_FOUND_MAPPING = "/car";

    private final DataService service;

    private final AuthorizationService authorizationService;

    @RequestMapping(value = "/captures/{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> captures(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);
        CarNode car = service.getCarNodeByBeaconId(beaconId);
        return new ResponseEntity<>(car.getStatusSnapshots(), HttpStatus.OK);
    }

    @RequestMapping(value = "/capture/{beaconId}", method = RequestMethod.POST)
    public ResponseEntity<?> capture(
            @PathVariable String beaconId,
            @RequestBody @Valid CarStatusSnapshotDto carStatusSnapshotDto) {

        CarStatus status = service.getCarStatusByCarBeaconId(beaconId);
        if (status == null) {
            status = CarStatus.TESTING;
        }

        if (status.equals(CarStatus.TESTING) || status.equals(CarStatus.STOLEN)) {
            CarNode car = service.getCarNodeByBeaconId(beaconId);
            CarStatusSnapshotNode carStatusSnapshot = CarStatusSnapshotNode.builder()
                    .car(car)
                    .carLocation(carStatusSnapshotDto.getLocation())
                    .time(carStatusSnapshotDto.getCaptureTime())
                    .build();

            service.save(carStatusSnapshot);
        }

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
