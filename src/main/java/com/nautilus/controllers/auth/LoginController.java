package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.database.def.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nautilus.dto.user.login.LoginDTO;

@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.POST)
    public Status login(@RequestBody LoginDTO loginDTO) {
        Status status = service.checkCredential(loginDTO);

        return status;
    }
}
