package com.nautilus.web.rest.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.database.domain.UserConfig;
import com.nautilus.database.services.def.GlobalService;
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

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public Status register(@RequestBody @Valid RegisterUserDTO registerDTO) {
        Status status = Status.ACCEPTED;
        UserConfig user = buildUser(registerDTO);
        service.save(user);
        return status;
    }

    private UserConfig buildUser(RegisterUserDTO dto) {
        UserConfig user = new UserConfig();

        user.setName(dto.getUserName());
        user.setSurname(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        return user;
    }
}
