package com.nautilus.services.def;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.UserConfig;
import com.nautilus.exceptions.WrongCarBeaconIdException;

public interface GlobalService {
    void save(UserConfig user);
    boolean checkEmailIsFree(String email);
    boolean checkPhoneNumberIsFree(String phoneNumber);
    UserConfig findUserByPhoneNumber(String phoneNumber);

    CarStatus getCarStatusByCarBeaconId(String beaconId) throws WrongCarBeaconIdException;

    void saveCarLastLocation(String carBeaconId, CarLocation carLocation);
    void save(Car car);
    Car findCarByBeaconId(String beaconId);

//    UserConfig update(UserConfig user);
//    void updateCar(Car car);
//    CarStatus checkCarStatus(String beaconId);
//
//    boolean checkUser(UserConfig user);
}
