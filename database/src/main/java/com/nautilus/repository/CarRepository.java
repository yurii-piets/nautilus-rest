package com.nautilus.repository;

import com.nautilus.domain.CarNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CarRepository extends Neo4jRepository<CarNode, Long> {

    CarNode findCarNodeByBeaconId(String beaconId);
}
