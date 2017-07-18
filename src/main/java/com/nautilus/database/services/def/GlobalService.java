package com.nautilus.database.services.def;

import com.nautilus.constants.CarStatus;
import com.nautilus.database.domain.Car;
import com.nautilus.database.domain.UserConfig;

public interface GlobalService {
    void saveCar(Car car);
    void updateCar(Car car);
    CarStatus checkCarStatus(String beaconId);

    void saveUser(UserConfig user);
    void updateUser(UserConfig user);
    boolean checkEmail(String email);
    boolean checkUser(UserConfig user);
}
