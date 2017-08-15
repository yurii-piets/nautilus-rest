package com.nautilus.database.repository;

import com.nautilus.constants.CarStatus;
import com.nautilus.database.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

//    @Query
//    void update(Car car);
//
//    @Query
//    CarStatus findCarStatusByBeaconId(String beaconId);
}
