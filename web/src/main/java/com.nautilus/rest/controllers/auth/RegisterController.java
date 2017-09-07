package com.nautilus.rest.controllers.auth;

import com.nautilus.algorithm.MD5;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${user.register}")
public class RegisterController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus register(@RequestBody @Valid RegisterUserDTO registerDTO) {

        UserConfig user = UserConfig.builder()
                .name(registerDTO.getUserName())
                .surname(registerDTO.getUserSurname())
                .phoneNumber(registerDTO.getPhoneNumber())
                .email(registerDTO.getEmail())
                .password(new MD5().encode(registerDTO.getPassword()))
                .build();

        service.save(user);

        return HttpStatus.ACCEPTED;
    }
}
