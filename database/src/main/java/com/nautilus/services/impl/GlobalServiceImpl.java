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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class GlobalServiceImpl implements GlobalService {

    private final UserRepository userRepository;

    private final CarRepository carRepository;

    private final CarStatusSnapshotRepository carStatusSnapshotRepository;

    private final CarLocationRepository carLocationRepository;

    @Override
    public void save(UserConfig user) {
        userRepository.save(user);
    }

    @Override
    public void save(Car car) {
        carRepository.save(car);
    }

    @Override
    public void save(CarLocation carLocation) {
        carLocationRepository.save(carLocation);
    }

    @Override
    public void save(CarStatusSnapshot carStatusSnapshot) {
        carStatusSnapshotRepository.save(carStatusSnapshot);
    }

    @Override
    public void save(Set<UserConfig> userConfigs) {
        userRepository.save(userConfigs);
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
    public Car findCarByBeaconId(String beaconId) {
        return carRepository.findCarByBeaconId(beaconId);
    }

    @Override
    public Car findCarByBeaconIdOrRegisterNumber(String beaconId, String registerNumber) {
        return carRepository.findCarByBeaconIdOrRegisterNumber(beaconId, registerNumber);
    }

    @Override
    public CarStatus getCarStatusByCarBeaconId(String beaconId) throws WrongCarBeaconIdException {
        Car car = carRepository.findCarByBeaconId(beaconId);

        if (car == null) {
            throw new WrongCarBeaconIdException("No such car with beacon id: " + beaconId);
        }

        return car.getStatus();
    }

    @Override
    public Long getUserIdConfigBeaconId(String beaconId) {
        Car car = carRepository.findCarByBeaconId(beaconId);
        UserConfig owner = car.getOwner();
        return owner.getUserId();
    }

    @Override
    public Car getCarById(String beaconId) {
        return carRepository.findCarByBeaconId(beaconId);
    }

    @Override
    public String findEmailByPhoneNumber(String phoneNumber) {
        UserConfig user = userRepository.findUserConfigByPhoneNumber(phoneNumber);
        if (user == null) {
            return null;
        }
        return user.getEmail();
    }

    @Override
    public String findEmailByBeaconId(String beaconId) {
        Car car = carRepository.findCarByBeaconId(beaconId);

        if (car == null){
            return null;
        }

        return car.getOwner().getEmail();
    }
}
