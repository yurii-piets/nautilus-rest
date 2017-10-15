package com.nautilus.rest.controllers.user;

import com.nautilus.constants.Authorities;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "${user.register}")
public class RegisterController {

    @Autowired
    private GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public Set<RegisterStatus> register(@RequestBody @Valid RegisterUserDTO registerDTO) {
        Set<RegisterStatus> statuses = new HashSet<>();

        if (!service.checkEmailIsFree(registerDTO.getEmail())) {
            statuses.add(RegisterStatus.EMAIL_NOT_FREE);
        }

        if (!service.checkPhoneNumberIsFree(registerDTO.getPhoneNumber())) {
            statuses.add(RegisterStatus.PHONE_NUMBER_NOT_FREE);
        }

        if (statuses.isEmpty()) {
            UserConfig user = new UserConfig(registerDTO);
            user.setAuthorities(Authorities.USER);
            service.save(user);
            statuses.add(RegisterStatus.REGISTERED);
        }

        return statuses;
    }
}
