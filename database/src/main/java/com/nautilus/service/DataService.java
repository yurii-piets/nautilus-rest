package com.nautilus.service;

import com.nautilus.dto.constants.CarStatus;
import com.nautilus.exception.WrongBeaconIdException;
import com.nautilus.node.CarNode;
import com.nautilus.node.CarStatusSnapshotNode;
import com.nautilus.node.UserNode;
import com.nautilus.repository.CarRepository;
import com.nautilus.repository.CarStatusSnapshotRepository;
import com.nautilus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DataService {

    private final static Integer DEFAULT_DEPTH = 2;

    private final CarRepository carRepository;

    private final CarStatusSnapshotRepository carStatusSnapshotRepository;

    private final UserRepository userRepository;

    public void save(UserNode user) {
        userRepository.save(user, DEFAULT_DEPTH);
    }

    public void save(CarNode car) {
        carRepository.save(car, DEFAULT_DEPTH);
    }

    public void save(CarStatusSnapshotNode carStatusSnapshot) {
        carStatusSnapshotRepository.save(carStatusSnapshot, DEFAULT_DEPTH);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    public boolean checkEmailIsFree(String email) {
        return userRepository.findUserNodeByEmail(email) == null;
    }

    public boolean checkPhoneNumberIsFree(String phoneNumber) {
        return userRepository.findUserNodeByPhoneNumber(phoneNumber) == null;
    }

    public CarStatus getCarStatusByCarBeaconId(String beaconId) {
        CarNode car = carRepository.findCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }
        return car.getStatus();
    }

    public Long getUserIdByBeaconId(String beaconId) {
        CarNode car = carRepository.findCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }
        UserNode owner = car.getOwner();
        if (owner == null) {
            return null;
        }
        return owner.getId();
    }

    public String getEmailByBeaconId(String beaconId) {
        CarNode car = carRepository.findCarNodeByBeaconId(beaconId);
        if (car == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }
        UserNode owner = car.getOwner();
        if (owner == null) {
            return null;
        }
        return owner.getEmail();
    }

    public UserNode getUserNodeByEmail(String email) {
        return userRepository.findUserNodeByEmail(email);
    }

    public CarNode getCarNodeByBeaconId(String beaconId) {
        return carRepository.findCarNodeByBeaconId(beaconId);
    }

    public Iterable<CarStatusSnapshotNode> getCarStatusSnapshotBeBeaconId(String beaconId) {
        CarNode carNode = carRepository.findCarNodeByBeaconId(beaconId);
        if (carNode == null) {
            throw new WrongBeaconIdException("Car with beacon id [" + beaconId + "] does not exist.");
        }
        if (carNode.getStatusSnapshots() == null) {
            return Collections.emptySet();
        }

        Set<Long> snapshotIds = carNode.getStatusSnapshots()
                .stream()
                .map(CarStatusSnapshotNode::getId)
                .collect(Collectors.toSet());
        return carStatusSnapshotRepository.findAllById(snapshotIds);
    }

    public CarNode getCarNodeByBeaconIdOrRegisterNumber(String beaconId, String registerNumber) {
        return carRepository.findCarNodeByBeaconIdOrRegisterNumber(beaconId, registerNumber);
    }
}
