package com.nautilus.services.impl;

import com.nautilus.domain.UserConfig;
import com.nautilus.repository.CarRepository;
import com.nautilus.repository.UserRepository;
import com.nautilus.services.GlobalService;
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

//    @Override
//    public void save(Car car) {
//        carRepository.save(car);
//    }

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
    @Override
    public void save(UserConfig user) {
        userRepository.save(user);
    }

    @Override
    public UserConfig update(UserConfig user) {
        UserConfig userConfig = userRepository.findUserConfigByEmail(user.getEmail());
        user.setUserId(userConfig.getUserId());
        return userRepository.save(user);
    }

//    @Override
//    public boolean checkEmail(String email) {
//        return userRepository.findUserByEmail(email) == null;
//    }

//    @Override
//    public boolean checkUser(UserConfig user) {
//        String password = userRepository.findPasswordByEmail(user.getEmail());
//        return password != null && password.equals(user.getPassword());
//    }
}
