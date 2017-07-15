package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.database.def.UserService;
import com.nautilus.dto.user.register.RegisterUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/registerUser")
public class RegisterController {
    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.POST)
    public Status register(@RequestBody @Valid RegisterUserDTO registerDTO) {
        Status status = service.registerUser(registerDTO);
        return status;
    }
}
