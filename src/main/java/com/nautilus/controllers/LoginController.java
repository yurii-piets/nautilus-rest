package com.nautilus.controllers;

import com.nautilus.constants.LoginStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nautilus.dto.login.LoginStatusDTO;
import com.nautilus.dto.login.LoginDTO;

@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @RequestMapping(method = RequestMethod.POST)
    public LoginStatusDTO login(@RequestBody LoginDTO loginDTO){
        System.out.println(loginDTO.getUserName() + ": " + loginDTO.getPassword());
        return new LoginStatusDTO(LoginStatus.Accepted.toString());
    }
}
