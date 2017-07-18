package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.dto.user.RegisterUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/registerUser")
public class RegisterController {

    @RequestMapping(method = RequestMethod.POST)
    public Status register(@RequestBody @Valid RegisterUserDTO registerDTO) {
        Status status = Status.ACCEPTED;
        return status;
    }
}