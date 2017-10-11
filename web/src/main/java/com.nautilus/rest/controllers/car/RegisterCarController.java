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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "${car.register}")
public class RegisterCarController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus register(@RequestBody @Valid CarRegisterDTO carRegisterDTO) {
        Car car = new Car(carRegisterDTO);

        UserConfig user = service.findUserConfigByPhoneNumber(carRegisterDTO.getUserPhoneNumber());
        car.setOwner(user);
        car.setStatus(CarStatus.TESTING);
        car.setStatus(CarStatus.OK);

        user.getCars().add(car);

        service.save(car);

        service.save(new HashSet<UserConfig>(){{
            add(user);
        }});
        return HttpStatus.OK;
    }
}
