package com.nautilus.controllers.car;

import com.nautilus.constants.Status;
import com.nautilus.database.def.CarService;
import com.nautilus.dto.car.CarRegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/registerCar")
public class RegisterCarController {
    @Autowired
    private CarService service;

    @RequestMapping(method = RequestMethod.POST)
    public Status register(@RequestBody CarRegisterDTO registerDTO) {
        Status status = service.addCarForUser(registerDTO);
        return status;
    }
}
