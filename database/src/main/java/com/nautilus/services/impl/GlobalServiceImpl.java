package com.nautilus.services.impl;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.domain.UserConfig;
import com.nautilus.exceptions.WrongCarBeaconIdException;
import com.nautilus.repository.CarLocationRepository;
import com.nautilus.repository.CarRepository;
import com.nautilus.repository.CarStatusSnapshotRepository;
import com.nautilus.repository.UserRepository;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service
@Transactional
public class GlobalServiceImpl implements GlobalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarStatusSnapshotRepository statusSnapshotRepository;

    @Autowired
    private CarLocationRepository carLocationRepository;

    @Override
    public void save(UserConfig user) {
        userRepository.save(user);
    }

    @Override
    public boolean checkEmailIsFree(String email) {
        return userRepository.findUserConfigByEmail(email) == null;
    }

    @Override
    public boolean checkPhoneNumberIsFree(String phoneNumber) {
        return userRepository.findUserConfigByPhoneNumber(phoneNumber) == null;
    }

    @Override
    public UserConfig findUserConfigByPhoneNumber(String phoneNumber) {
        return userRepository.findUserConfigByPhoneNumber(phoneNumber);
    }

    @Override
    public UserConfig findUserConfigByEmail(String email) {
        return userRepository.findUserConfigByEmail(email);
    }

    @Override
    public CarStatus getCarStatusByCarBeaconId(String beaconId) throws WrongCarBeaconIdException {
        Car car = carRepository.findCarByBeaconId(beaconId);

        if(car == null){
            throw new WrongCarBeaconIdException("No such car with beacon id: " + beaconId);
        }

        return car.getStatus();
    }

    @Override
    public void save(Car car) {
        carRepository.save(car);
    }

    @Override
    public void saveCarLastLocation(String carBeaconId, CarLocation carLocation) {
        Car car = carRepository.findCarByBeaconId(carBeaconId);
        CarStatusSnapshot statusSnapshot = new CarStatusSnapshot();
        statusSnapshot.setCar(car);
        statusSnapshot.setCarLocation(carLocation);
        statusSnapshot.setTimestamp(new Timestamp(new Date().getTime()));

        statusSnapshotRepository.save(statusSnapshot);
    }

    @Override
    public Car findCarByBeaconId(String beaconId) {
        return carRepository.findCarByBeaconId(beaconId);
    }

    @Override
    public void save(CarLocation carLocation) {
        carLocationRepository.save(carLocation);
    }

    //    public void save(Car car) {
//        carRepository.save(car);

//    @Override
//    public void updateCar(Car car) {
//        carRepository.update(car);
//    }
//
//    @Override
//    public CarStatus checkCarStatus(String beaconId) {
//        return carRepository.findCarStatusByBeaconId(beaconId);
//    }
//
//    }

//    @Override

//    @Override
//    public boolean checkUser(UserConfig user) {
//        String password = userRepository.findPasswordByEmail(user.getEmail());
//        return password != null && password.equals(user.getPassword());
//    }
}
