package com.nautilus.rest.controllers.auth;

import com.nautilus.dto.user.RegisterUserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${user.update}")
public class UpdateUserController {

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus update(@RequestBody @Valid RegisterUserDTO updateDto) {
        return HttpStatus.OK;
    }
}
