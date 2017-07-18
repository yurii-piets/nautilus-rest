package com.nautilus.database.services.impl;

import com.nautilus.constants.CarStatus;
import com.nautilus.database.domain.Car;
import com.nautilus.database.domain.UserConfig;
import com.nautilus.database.repository.CarRepository;
import com.nautilus.database.repository.UserRepository;
import com.nautilus.database.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalServiceImpl implements GlobalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Override
    public void saveCar(Car car) {
        carRepository.save(car);
    }

    @Override
    public void updateCar(Car car) {
        carRepository.update(car);
    }

    @Override
    public CarStatus checkCarStatus(String beaconId) {
        return carRepository.findCarStatusByBeaconId(beaconId);
    }

    @Override
    public void saveUser(UserConfig user) {
        userRepository.save(user);
    }

    @Override
    public void updateUser(UserConfig user) {
        userRepository.update(user);
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepository.findUserByEmail(email) == null;
    }

    @Override
    public boolean checkUser(UserConfig user) {
        String password = userRepository.findPasswordByEmail(user.getEmail());
        return password != null && password.equals(user.getPassword());
    }
}
