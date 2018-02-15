package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.dto.car.CarStatusDTO;
import com.nautilus.services.def.GlobalService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.Date;

import static com.nautilus.rest.controllers.car.FoundCarController.CAR_FOUND_MAPPING;

@RestController
@RequestMapping(value = CAR_FOUND_MAPPING)
@RequiredArgsConstructor
public class FoundCarController {

    public final static String CAR_FOUND_MAPPING = "/car/found";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final GlobalService service;

    @RequestMapping(value = "{beaconId}", method = RequestMethod.GET)
    public ResponseEntity<?> foundStatus(@PathVariable String beaconId) {
        Car car = service.findCarByBeaconId(beaconId);

        if(car == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(car.getStatus(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> found(@RequestBody @Valid CarStatusDTO carStatusDTO) {

        CarStatus status = service.getCarStatusByCarBeaconId(carStatusDTO.getBeaconId());
        if(status == null) {
            status = CarStatus.TESTING;
        }

        if (status.equals(CarStatus.TESTING) || status.equals(CarStatus.STOLEN)) {
            CarLocation carLocation = new CarLocation(carStatusDTO);
            service.save(carLocation);

            Car car = service.findCarByBeaconId(carStatusDTO.getBeaconId());
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
