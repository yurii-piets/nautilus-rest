package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.dto.register.RegisterDTO;
import com.nautilus.dto.status.StatusDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/registerUser")
public class RegisterController {

    @RequestMapping(method = RequestMethod.POST)
    public Status register(@RequestBody RegisterDTO registerDTO) {

        return Status.ACCEPTED;
    }
}
