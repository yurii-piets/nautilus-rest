package com.nautilus.node;

import com.nautilus.constants.CarStatus;
import com.nautilus.dto.car.CarRegisterDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@Data
@NodeEntity
@NoArgsConstructor
@EqualsAndHashCode(exclude = "statusSnapshots")
@ToString(exclude = "statusSnapshots")
public class CarNode {

    @Id
    @GeneratedValue
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

    public CarNode(CarRegisterDto registerDTO, UserNode owner){
        this.beaconId = registerDTO.getBeaconId();
        this.registerNumber = registerDTO.getRegisterNumber();
        this.mark = registerDTO.getMark();
        this.model = registerDTO.getModel();
        this.color = registerDTO.getColor();
        this.yearOfProduction = registerDTO.getYearOfProduction();
        this.description = registerDTO.getDescription();
        this.status = CarStatus.OK;
        this.owner = owner;
    }
}
