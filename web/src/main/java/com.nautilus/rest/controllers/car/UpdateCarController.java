package com.nautilus.rest.controllers.car;

import com.nautilus.domain.Car;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.dto.car.CarUpdateDTO;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${car.update}")
public class UpdateCarController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus update(@RequestBody @Valid CarUpdateDTO updateDto) {

        Car car = service.findCarByBeaconId(updateDto.getBeaconId());

        if(car != null) {
            Car updateCar = Car.mergeWithUpdateDto(car, updateDto);
            service.save(updateCar);
        }

        return HttpStatus.OK;
    }
}
