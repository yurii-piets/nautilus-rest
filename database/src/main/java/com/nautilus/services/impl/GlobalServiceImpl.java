package com.nautilus.services.impl;

import com.nautilus.domain.UserConfig;
import com.nautilus.repository.CarRepository;
import com.nautilus.repository.UserRepository;
import com.nautilus.services.def.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GlobalServiceImpl implements GlobalService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

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
