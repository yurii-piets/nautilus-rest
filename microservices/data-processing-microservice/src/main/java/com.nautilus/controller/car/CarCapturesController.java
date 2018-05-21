package com.nautilus.controller.car;

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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.nautilus.controller.car.CarCapturesController.CAR_CAPTURES_MAPPING;

@RestController
@RequestMapping(value = CAR_CAPTURES_MAPPING)
@RequiredArgsConstructor
public class CarCapturesController {

    public final static String CAR_CAPTURES_MAPPING = "/car/{beaconId}/captures";

    private final DataService service;

    private final AuthorizationService authorizationService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> captures(@PathVariable String beaconId) {
        authorizationService.hasAccessByBeaconId(beaconId);
        Iterable<CarStatusSnapshotNode> carStatusSnapshotBeBeaconId = service.getCarStatusSnapshotBeBeaconId(beaconId);
        Set<CarStatusSnapshotDto> carStatusSnapshotDtos = StreamSupport.stream(carStatusSnapshotBeBeaconId.spliterator(), false)
                .map(CarStatusSnapshotNode::toCarStatusSnapshotDto)
                .collect(Collectors.toSet());
        return new ResponseEntity<>(carStatusSnapshotDtos, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> capture(
            @PathVariable String beaconId,
            @RequestBody @Valid CarStatusSnapshotDto carStatusSnapshotDto) {

        CarNode car = service.getCarNodeByBeaconId(beaconId);

        CarStatusSnapshotNode carStatusSnapshot = CarStatusSnapshotNode.builder()
                .car(car)
                .carLocation(carStatusSnapshotDto.getLocation())
                .captureTime(carStatusSnapshotDto.getCaptureTime())
                .build();

        if (car.getStatusSnapshots() == null) {
            car.setStatusSnapshots(Set.of(carStatusSnapshot));
        } else {
            car.getStatusSnapshots().add(carStatusSnapshot);
        }
        service.save(car);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
