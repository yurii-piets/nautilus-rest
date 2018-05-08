package com.nautilus.domain;

import com.nautilus.constants.CarStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Data
@NodeEntity
@EqualsAndHashCode(exclude = "statusSnapshots")
@ToString(exclude = "statusSnapshots")
public class CarNode {

    private Long id;

    private String beaconId;

    private String registerNumber;

    private String mark;

    private String model;

    private String color;

    private String yearOfProduction;

    private String description;

    private CarStatus status;

    @Relationship(type = "IS_BELONG")
    private UserNode owner;

    @Relationship(type = "SNAPSHOTS")
    private Set<CarStatusSnapshotNode> statusSnapshots;

}
