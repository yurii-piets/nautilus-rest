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
@RequestMapping(value = "/updateUser")
public class UpdateUserController {

    @RequestMapping(method = RequestMethod.POST)
    public Status update(@RequestBody @Valid RegisterUserDTO updateDto) {
        Status status = Status.ACCEPTED;
        return status;
    }
}
