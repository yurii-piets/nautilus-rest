package com.nautilus.rest.controllers.user;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.constants.RegisterStatus;
import com.nautilus.dto.user.RegisterUserDto;
import com.nautilus.node.UserNode;
import com.nautilus.service.DataService;
import com.nautilus.service.file.FileUtil;
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

    private final DataService service;

    private final JsonPatchUtility patchUtility;

    private final FileUtil fileUtil;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> info() {
        UserNode user = service.getUserNodeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.toUserInfoDto(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto registerDto) {
        Set<RegisterStatus> statuses = new HashSet<>();
        if (!service.checkEmailIsFree(registerDto.getEmail())) {
            statuses.add(RegisterStatus.EMAIL_NOT_FREE);
        }
        if (!service.checkPhoneNumberIsFree(registerDto.getPhoneNumber())) {
            statuses.add(RegisterStatus.PHONE_NUMBER_NOT_FREE);
        }
        if (!statuses.isEmpty()) {
            return new ResponseEntity<>(statuses, HttpStatus.NOT_ACCEPTABLE);
        }
        service.save(new UserNode(registerDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<?> update(@RequestBody String updateBody) {
        UserNode user = service.getUserNodeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            UserNode mergedUser = (UserNode) patchUtility.patch(updateBody, user).get();
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

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> delete() {
        UserNode user = service.getUserNodeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        service.deleteUserById(user.getId());
        fileUtil.delete(user.getId());
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = USER_CAR_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> userCars() {
        UserNode user = service.getUserNodeByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user.getCars(), HttpStatus.OK);
    }
}
