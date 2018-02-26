package com.nautilus.rest.controllers.user;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.constants.Authorities;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.dto.user.UserInfo;
import com.nautilus.service.FileAccessService;
import com.nautilus.services.GlobalService;
import com.nautilus.utilities.JsonPatchUtility;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final JsonPatchUtility patchUtility;

    private final FileAccessService fileAccessService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> info() {
        UserConfig user = service.findUserConfigByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
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

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<?> update(@RequestBody String updateBody) {
        UserConfig user = service.findUserConfigByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            UserConfig mergedUser = (UserConfig) patchUtility.patch(updateBody, user).get();
            service.update(mergedUser);
            return new ResponseEntity(HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error("Unexpected: ", e);
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(){
        UserConfig user = service.findUserConfigByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        service.delete(user);
        fileAccessService.deleteUser(user.getUserId());

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = USER_CAR_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> userCars() {
        UserConfig user = service.findUserConfigByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user.getCars(), HttpStatus.OK);
    }
}
