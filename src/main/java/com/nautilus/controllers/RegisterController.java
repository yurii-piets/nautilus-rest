package com.nautilus.controllers;

import com.nautilus.constants.RegisterStatus;
import com.nautilus.dto.register.RegisterDTO;
import com.nautilus.dto.register.RegisterStatusDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/register")
public class RegisterController {

    @RequestMapping(method = RequestMethod.POST)
    public RegisterStatusDTO register(@RequestBody RegisterDTO registerDTO){
        return new RegisterStatusDTO(RegisterStatus.Accepted);
    }
}
