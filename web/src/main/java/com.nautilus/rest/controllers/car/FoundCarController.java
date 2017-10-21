package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.dto.car.CarStatusDTO;
import com.nautilus.exceptions.WrongCarBeaconIdException;
import com.nautilus.services.def.GlobalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequestMapping(value = "${car.found}")
public class FoundCarController {

    @Autowired
    private GlobalService service;

    private final Logger logger = LogManager.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CarStatus> found(@RequestBody @Valid CarStatusDTO carStatusDTO) {

        CarStatus status;
        try {
            status = service.getCarStatusByCarBeaconId(carStatusDTO.getBeaconId());
        } catch (WrongCarBeaconIdException e) {
            status = CarStatus.TESTING;
            logger.error(e.getMessage());
        }

        if (status.equals(CarStatus.TESTING) || status.equals(CarStatus.STOLEN)) {
            CarLocation carLocation = new CarLocation(carStatusDTO);
            service.save(carLocation);

            Car car = service.findCarByBeaconId(carStatusDTO.getBeaconId());
            CarStatusSnapshot carStatusSnapshot = new CarStatusSnapshot();
            carStatusSnapshot.setCarLocation(carLocation);
            carStatusSnapshot.setCar(car);
            carStatusSnapshot.setTimestamp(new Timestamp(new Date().getTime()));
            service.save(carStatusSnapshot);

            car.getStatusSnapshots().add(carStatusSnapshot);
            service.save(car);
        }

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
