package com.nautilus.rest.controllers.user;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.constants.Authorities;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.dto.user.UserInfo;
import com.nautilus.service.AuthorizationService;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.JsonPatchUtility;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.nautilus.rest.controllers.user.UserController.USER_MAPPING;

@RestController
@RequestMapping(path = USER_MAPPING)
@RequiredArgsConstructor
public class UserController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public final static String USER_MAPPING = "/user";

    public final static String USER_CAR_MAPPING = "/cars";

    private final GlobalService service;

    private final AuthorizationService authorizationService;

    private final JsonPatchUtility patchUtility;

    @RequestMapping(path = "/{userPhone}", method = RequestMethod.GET)
    public ResponseEntity<?> info(@PathVariable String userPhone) {
        if (!authorizationService.hasAccessByPhoneNumber(userPhone)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UserConfig user = service.findUserConfigByPhoneNumber(userPhone);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserInfo userInfo = UserInfo.builder()
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userName(user.getName())
                .userSurname(user.getSurname())
                .build();

        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDTO registerDTO) {
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

    @RequestMapping(path = "/{userPhone}", method = RequestMethod.PATCH)
    public ResponseEntity<?> update(@PathVariable String userPhone,
                                    @RequestBody String updateBody) {
        if (!authorizationService.hasAccessByPhoneNumber(userPhone)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UserConfig user = service.findUserConfigByPhoneNumber(userPhone);
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            UserConfig mergedUser = (UserConfig) patchUtility.patch(updateBody, user).get();
            service.save(mergedUser);
            return new ResponseEntity(HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(value = USER_CAR_MAPPING + "/{userPhone}", method = RequestMethod.GET)
    public ResponseEntity<?> userCars(@PathVariable String userPhone) {
        if(!authorizationService.hasAccessByPhoneNumber(userPhone)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        UserConfig user = service.findUserConfigByPhoneNumber(userPhone);
        if (userPhone == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user.getCars(), HttpStatus.OK);
    }
}
