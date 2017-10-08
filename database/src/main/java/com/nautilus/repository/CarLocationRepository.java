package com.nautilus.repository;

import com.nautilus.domain.CarLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarLocationRepository extends JpaRepository<CarLocation, String> {
}
