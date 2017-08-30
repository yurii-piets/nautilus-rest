package com.nautilus.rest.controllers.auth;

import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.services.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${user.register}")
public class RegisterController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus register(@RequestBody @Valid RegisterUserDTO registerDTO) {
        return HttpStatus.ACCEPTED;
    }
}
