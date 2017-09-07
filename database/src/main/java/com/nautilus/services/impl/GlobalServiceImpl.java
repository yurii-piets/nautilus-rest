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


//        carRepository.save(car);
//    public void save(Car car) {
//    @Override

//    }
//
//    }
//        return carRepository.findCarStatusByBeaconId(beaconId);
//    public CarStatus checkCarStatus(String beaconId) {
//    @Override
//
//    }
//        carRepository.update(car);
//    public void updateCar(Car car) {
//    @Override

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
