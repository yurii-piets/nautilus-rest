package com.nautilus.rest.controllers.auth;

import com.nautilus.algorithm.MD5;
import com.nautilus.constants.CarStatus;
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

    @RequestMapping(method = RequestMethod.POST)
    public HttpStatus update(@RequestBody @Valid UpdateUserDTO updateDTO) {
        UserConfig user = null;
        if (updateDTO.getPhoneNumber() != null) {
            user = service.findUserConfigByPhoneNumber(updateDTO.getPhoneNumber());
        }

        if (user == null && updateDTO.getEmail() != null) {
            user = service.findUserConfigByEmail(updateDTO.getEmail());
        }

        if (user != null) {
            UserConfig updatedUser = buildUpdatedUser(user, updateDTO);
            service.save(updatedUser);
        }

        return HttpStatus.OK;
    }

    private UserConfig buildUpdatedUser(UserConfig user, UpdateUserDTO updateDTO) {
        UserConfig updateUser = new UserConfig();

        String name = updateDTO.getUserName();
        String surname = updateDTO.getUserSurname();
        String phoneNumber = updateDTO.getPhoneNumber();
        String email = updateDTO.getEmail();
        String password = updateDTO.getPassword();

        if (name != null) {
            updateUser.setName(name);
        } else {
            updateUser.setName(user.getName());
        }

        if(surname != null){
            updateUser.setSurname(surname);
        } else {
            updateUser.setSurname(user.getSurname());
        }

        if(phoneNumber != null){
            updateUser.setPhoneNumber(phoneNumber);
        } else {
            updateUser.setPhoneNumber(user.getPhoneNumber());
        }

        if(email != null){
            updateUser.setEmail(email);
        } else {
            updateUser.setPhoneNumber(user.getPhoneNumber());
        }

        if(password != null){
            updateUser.setPassword(new MD5().encode(password));
        } else {
            updateUser.setPassword(user.getPassword());
        }

        Long id = user.getUserId();
        updateUser.setUserId(id);

        return updateUser;
    }
}
