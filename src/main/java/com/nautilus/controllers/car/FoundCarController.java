package com.nautilus.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.dto.car.CarStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/found")
public class FoundCarController {

    @RequestMapping(method = RequestMethod.POST)
    public CarStatus found(@RequestBody @Valid CarStatusDTO carDTO) {
        CarStatus status = CarStatus.STOLEN;
        return status;
    }
}
