package com.nautilus.rest.controllers.car;

import com.nautilus.constants.Status;
import com.nautilus.dto.car.CarRegisterDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/updateCar")
public class UpdateCarController {

    @RequestMapping(method = RequestMethod.POST)
    public Status update(@RequestBody @Valid CarRegisterDTO updateDto) {
        Status status = Status.ACCEPTED;
        return status;
    }
}
