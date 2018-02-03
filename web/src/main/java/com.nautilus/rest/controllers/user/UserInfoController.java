package com.nautilus.rest.controllers.user;

import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.UserInfo;
import com.nautilus.service.AuthorizationService;
import com.nautilus.services.def.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.nautilus.rest.controllers.user.UserInfoController.USER_INFO_MAPPING;

@RestController
@RequestMapping(path = USER_INFO_MAPPING)
@RequiredArgsConstructor
public class UserInfoController {

    public final static String USER_INFO_MAPPING = "/user/info";

    public final static String USER_CAR_MAPPING = "/cars";

    private final GlobalService service;

    private final AuthorizationService authorizationService;

    @RequestMapping(path = "/{userPhone}", method = RequestMethod.GET)
    public ResponseEntity<?> userInfo(@PathVariable String userPhone) {
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
