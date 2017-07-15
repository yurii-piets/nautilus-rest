package com.nautilus.controllers.car;

import com.nautilus.constants.Status;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.dto.car.CarStatusDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/updateCar")
public class UpdateCarController {

    @RequestMapping(method = RequestMethod.POST)
    public Status update(@RequestBody CarRegisterDTO updateDto) {

        return Status.ACCEPTED;
    }
}
