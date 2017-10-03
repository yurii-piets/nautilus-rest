package com.nautilus.repository;

import com.nautilus.domain.CarStatusSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarStatusSnapshotRepository extends JpaRepository<CarStatusSnapshot, Long> {}
