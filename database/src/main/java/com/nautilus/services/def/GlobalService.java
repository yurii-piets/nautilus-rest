package com.nautilus.services;

import com.nautilus.domain.UserConfig;

public interface GlobalService {
//    void save(Car car);
//    void updateCar(Car car);
//    CarStatus checkCarStatus(String beaconId);
//
    void save(UserConfig user);
    UserConfig update(UserConfig user);
//    boolean checkEmail(String email);
//    boolean checkUser(UserConfig user);
}
