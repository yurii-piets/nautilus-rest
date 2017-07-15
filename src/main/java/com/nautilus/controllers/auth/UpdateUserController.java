package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.dto.user.register.RegisterUserDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/updateUser")
public class UpdateUserController {

    @RequestMapping(method = RequestMethod.POST)
    public Status update(@RequestBody RegisterUserDTO updateDto) {

        return Status.ACCEPTED;
    }
}
