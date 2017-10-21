package com.nautilus.rest.controllers.user;

import com.github.fge.jsonpatch.JsonPatchException;
import com.nautilus.domain.UserConfig;
import com.nautilus.services.def.GlobalService;
import com.nautilus.utilities.JsonPatchUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "${user.update}")
public class UpdateUserController {

    @Autowired
    private GlobalService service;

    @Autowired
    private JsonPatchUtility patchUtility;

    private final Logger logger = LogManager.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity update(@RequestParam String userPhone, @RequestBody String updateBody) {
        UserConfig user = service.findUserConfigByPhoneNumber(userPhone);

        if(user == null){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        try {
            UserConfig mergedUser = (UserConfig) patchUtility.patch(updateBody, user).get();
            service.save(mergedUser);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } catch (JsonPatchException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
