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
@RequestMapping(value = "/updateUser")
public class UpdateUserController {
    @Autowired
    private UserService service;

    @RequestMapping(method = RequestMethod.POST)
    public Status update(@RequestBody @Valid RegisterUserDTO updateDto) {
        Status status = service.updateUser(updateDto);
        return status;
    }
}
