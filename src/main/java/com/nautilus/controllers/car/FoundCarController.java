package com.nautilus.controllers.car;

import com.nautilus.constants.CarStatus;
import com.nautilus.dto.car.CarDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/found")
public class FoundCarController {

    @RequestMapping(method = RequestMethod.POST)
    public CarStatus found(@RequestBody CarDTO carDTO) {

        return CarStatus.OK;
    }
}
