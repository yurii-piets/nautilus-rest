package com.nautilus.services.def;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.domain.UserConfig;
import com.nautilus.exceptions.WrongCarBeaconIdException;

import java.util.Set;

public interface GlobalService {
    void save(UserConfig user);
    void save(Car car);
    void save(CarLocation carLocation);
    void save(Set<UserConfig> userConfigs);
    void save(CarStatusSnapshot carStatusSnapshot);

    boolean checkEmailIsFree(String email);
    boolean checkPhoneNumberIsFree(String phoneNumber);

    UserConfig findUserConfigByPhoneNumber(String phoneNumber);
    UserConfig findUserConfigByEmail(String email);
    Car findCarByBeaconId(String beaconId);
    CarStatus getCarStatusByCarBeaconId(String beaconId) throws WrongCarBeaconIdException;
}
