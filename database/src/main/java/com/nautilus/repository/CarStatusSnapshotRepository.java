package com.nautilus.repository;

import com.nautilus.node.CarStatusSnapshotNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CarStatusSnapshotRepository extends Neo4jRepository<CarStatusSnapshotNode, Long> {
}
