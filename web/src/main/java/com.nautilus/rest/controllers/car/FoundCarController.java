package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.dto.car.CarStatusDTO;
import com.nautilus.exceptions.WrongCarBeaconIdException;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${car.found}")
public class FoundCarController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public CarStatus found(@RequestBody @Valid CarStatusDTO carStatusDTO) {

        CarStatus status;
        try {
            status = service.getCarStatusByCarBeaconId(carStatusDTO.getBeaconId());
        } catch (WrongCarBeaconIdException e) {
            status = CarStatus.TESTING;
            e.printStackTrace();
        }

        if (status.equals(CarStatus.TESTING) || status.equals(CarStatus.STOLEN)) {
            CarLocation carLocation = new CarLocation(carStatusDTO);
            Car car = service.findCarByBeaconId(carStatusDTO.getBeaconId());
            carLocation.setCar(car);
            service.save(carLocation);
            service.saveCarLastLocation(carStatusDTO.getBeaconId(), carLocation);
        }

        return status;
    }
}
