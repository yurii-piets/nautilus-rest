package com.nautilus.rest.controllers.user;

import com.nautilus.domain.Car;
import com.nautilus.domain.UserConfig;
import com.nautilus.dto.user.UserInfo;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(method = RequestMethod.GET)
public class UserInfoController {

    @Autowired
    private GlobalService service;

    @RequestMapping(value="${user.get.info}")
    public UserInfo userInfo(@RequestParam String userPhone){
        UserConfig userConfig = service.findUserConfigByPhoneNumber(userPhone);

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(userConfig.getEmail());
        userInfo.setPhoneNumber(userConfig.getPhoneNumber());
        userInfo.setUserName(userConfig.getName());
        userInfo.setUserSurname(userConfig.getSurname());

        return userInfo;
    }

    @RequestMapping(value="${user.get.cars}")
    public Set<Car> userCars(@RequestParam String userPhone){
        UserConfig config = service.findUserConfigByPhoneNumber(userPhone);
        Set<Car> cars = config.getCars();
        return cars;
    }
}
