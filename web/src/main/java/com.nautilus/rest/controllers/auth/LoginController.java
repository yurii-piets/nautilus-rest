package com.nautilus.rest.controllers.auth;

import com.nautilus.dto.user.LoginDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${user.login}")
public class LoginController {

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus login(@RequestBody @Valid LoginDTO loginDTO) {
        return HttpStatus.OK;
    }
}
