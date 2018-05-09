package com.nautilus.postgres.repository;

import com.nautilus.postgres.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, String> {
    Car findCarByBeaconId(String beaconId);
    Car findCarByBeaconIdOrRegisterNumber(String beaconId, String registerNumber);
}
