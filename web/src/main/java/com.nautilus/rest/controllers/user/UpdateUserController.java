package com.nautilus.rest.controllers.user;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.domain.UserConfig;
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

import java.io.IOException;

import static com.nautilus.rest.controllers.user.UpdateUserController.USER_UPDATE_MAPPING;

@RestController
@RequestMapping(value = USER_UPDATE_MAPPING)
@RequiredArgsConstructor
public class UpdateUserController {

    public final static String USER_UPDATE_MAPPING = "/user/update";

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final GlobalService service;

    private final AuthorizationService authorizationService;

    private final JsonPatchUtility patchUtility;

    @RequestMapping(path = "/{userPhone}", method = RequestMethod.PATCH)
    public ResponseEntity<?> update(@PathVariable String userPhone,
                                    @RequestBody String updateBody) {
        if (!authorizationService.hasAccessByPhoneNumber(userPhone)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
