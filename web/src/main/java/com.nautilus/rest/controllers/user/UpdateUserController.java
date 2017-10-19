package com.nautilus.rest.controllers.user;

import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.UpdateUserDTO;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${user.update}")
public class UpdateUserController {

    @Autowired
    private GlobalService service;

//    @RequestMapping(method = RequestMethod.POST)
//    public HttpStatus update(@RequestBody @Valid UpdateUserDTO updateDTO) {
//        UserConfig user = null;
//        if (updateDTO.getPhoneNumber() != null) {
//            user = service.findUserConfigByPhoneNumber(updateDTO.getPhoneNumber());
//        }
//
//        if (user == null && updateDTO.getEmail() != null) {
//            user = service.findUserConfigByEmail(updateDTO.getEmail());
//        }
//
//        if (user != null) {
//            UserConfig updatedUser = UserConfig.mergeWithUpdateDto(user, updateDTO);
//            service.save(updatedUser);
//        }
//
//        return HttpStatus.OK;
//    }
}
