package com.nautilus.rest.controllers.user;

import com.nautilus.constants.Authorities;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.services.def.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

import static com.nautilus.rest.controllers.user.RegisterController.REGISTER_USER_MAPPING;

@RestController
@RequestMapping(value = REGISTER_USER_MAPPING)
@RequiredArgsConstructor
public class RegisterController {

    public final static String REGISTER_USER_MAPPING = "/user/register";

    private final GlobalService service;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Set<RegisterStatus>> register(@RequestBody @Valid RegisterUserDTO registerDTO) {
        Set<RegisterStatus> statuses = new HashSet<>();

        if (!service.checkEmailIsFree(registerDTO.getEmail())) {
            statuses.add(RegisterStatus.EMAIL_NOT_FREE);
        }

        if (!service.checkPhoneNumberIsFree(registerDTO.getPhoneNumber())) {
            statuses.add(RegisterStatus.PHONE_NUMBER_NOT_FREE);
        }

        if (!statuses.isEmpty()) {
            return new ResponseEntity<>(statuses, HttpStatus.NOT_ACCEPTABLE);
        }

        UserConfig user = new UserConfig(registerDTO);
        user.setAuthorities(Authorities.USER);
        service.save(user);
        statuses.add(RegisterStatus.REGISTERED);

        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }
}
