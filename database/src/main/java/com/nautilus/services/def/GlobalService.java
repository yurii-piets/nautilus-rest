package com.nautilus.services.def;

import com.nautilus.domain.UserConfig;

public interface GlobalService {
    void save(UserConfig user);
    boolean checkEmailIsFree(String email);
    boolean checkPhoneNumberIsFree(String phoneNumber);

//    UserConfig update(UserConfig user);
//    void save(Car car);
//    void updateCar(Car car);
//    CarStatus checkCarStatus(String beaconId);
//
//    boolean checkUser(UserConfig user);
}
