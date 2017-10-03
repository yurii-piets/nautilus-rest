package com.nautilus.rest.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${car.register}")
public class RegisterCarController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus register(@RequestBody @Valid CarRegisterDTO registerDTO) {
        Car car = buildNewCarFromDTO(registerDTO);

        service.save(car);
        return HttpStatus.OK;
    }

    private Car buildNewCarFromDTO(@RequestBody @Valid CarRegisterDTO carRegisterDTO) {
        Car car = new Car(carRegisterDTO);

        UserConfig user = service.findUserByPhoneNumber(carRegisterDTO.getUserPhoneNumber());
        car.setOwner(user);
        car.setStatus(CarStatus.TESTING);

        return car;
    }
}
