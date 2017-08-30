package com.nautilus.rest.controllers.car;

import com.nautilus.dto.car.CarRegisterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${car.update}")
public class UpdateCarController {

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus update(@RequestBody @Valid CarRegisterDTO updateDto) {
        return HttpStatus.OK;
    }
}
