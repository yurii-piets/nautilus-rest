package com.nautilus.services;

import com.nautilus.constants.CarStatus;
import com.nautilus.domain.Car;
import com.nautilus.domain.CarLocation;
import com.nautilus.domain.CarStatusSnapshot;
import com.nautilus.domain.UserConfig;
import com.nautilus.exception.WrongBeaconIdException;
import com.nautilus.repository.CarLocationRepository;
import com.nautilus.repository.CarRepository;
import com.nautilus.repository.CarStatusSnapshotRepository;
import com.nautilus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class GlobalService {

    private final UserRepository userRepository;

    private final CarRepository carRepository;

    private final CarStatusSnapshotRepository carStatusSnapshotRepository;

    private final CarLocationRepository carLocationRepository;

    public void save(UserConfig user) {
        userRepository.save(user);
    }

    public void save(Car car) {
        carRepository.save(car);
    }

    public void save(CarLocation carLocation) {
        carLocationRepository.save(carLocation);
    }

    public void save(CarStatusSnapshot carStatusSnapshot) {
        carStatusSnapshotRepository.save(carStatusSnapshot);
    }

    public void delete(UserConfig user) {
        userRepository.delete(user);
    }

    public void delete(Car car) {
        carRepository.delete(car);
    }

    public void save(Set<UserConfig> userConfigs) {
        userRepository.saveAll(userConfigs);
    }

    public boolean checkEmailIsFree(String email) {
        return userRepository.findUserConfigByEmail(email) == null;
    }

    public boolean checkPhoneNumberIsFree(String phoneNumber) {
        return userRepository.findUserConfigByPhoneNumber(phoneNumber) == null;
    }

    public Car findCarByBeaconId(String beaconId) {
        Car car = carRepository.findCarByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Requested car with id: [" + beaconId + "] does not exist.");
        }
        return car;
    }

    public Car findCarByBeaconIdOrRegisterNumber(String beaconId, String registerNumber) {
        return carRepository.findCarByBeaconIdOrRegisterNumber(beaconId, registerNumber);
    }

    public CarStatus getCarStatusByCarBeaconId(String beaconId) {
        Car car = carRepository.findCarByBeaconId(beaconId);

        if (car == null) {
            return null;
        }

        return car.getStatus();
    }


    public Long getUserIdConfigBeaconId(String beaconId) {
        Car car = carRepository.findCarByBeaconId(beaconId);
        UserConfig owner = car.getOwner();
        return owner.getUserId();
    }

    public String findEmailByBeaconId(String beaconId) {
        Car car = carRepository.findCarByBeaconId(beaconId);

        if (car == null) {
            return null;
        }

        return car.getOwner().getEmail();
    }

    public UserConfig findUserConfigByEmail(String email) {
        return userRepository.findUserConfigByEmail(email);
    }

    public void update(UserConfig user) {
        userRepository.save(user);
    }

    public void update(Car car) {
        carRepository.save(car);
    }
}
