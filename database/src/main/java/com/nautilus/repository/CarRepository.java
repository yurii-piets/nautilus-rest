package com.nautilus.repository;


import com.nautilus.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

//    @Query
//    void update(Car car);
//
//    @Query
//    CarStatus findCarStatusByBeaconId(String beaconId);
}
