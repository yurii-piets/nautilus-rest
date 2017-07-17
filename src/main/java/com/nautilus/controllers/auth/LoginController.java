package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.database.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.nautilus.dto.user.LoginDTO;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public Status login(@RequestBody @Valid LoginDTO loginDTO) {
        Status status = Status.ACCEPTED;

        return status;
    }
}
