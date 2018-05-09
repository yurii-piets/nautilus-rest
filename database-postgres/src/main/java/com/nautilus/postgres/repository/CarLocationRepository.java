package com.nautilus.postgres.repository;

import com.nautilus.postgres.domain.CarLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarLocationRepository extends JpaRepository<CarLocation, Long> {}
