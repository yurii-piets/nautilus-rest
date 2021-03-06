package com.nautilus.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nautilus.dto.car.CarDto;
import com.nautilus.dto.car.CarStatusSnapshotDto;
import com.nautilus.dto.constants.CarStatus;
import com.nautilus.dto.car.CarRegisterDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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

    @JsonIgnore
    @Relationship(type = "IS_BELONG")
    private UserNode owner;

    @Relationship(type = "SNAPSHOT")
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

    public CarDto toCarDto() {
        return CarDto.builder()
                .beaconId(beaconId)
                .registerNumber(registerNumber)
                .mark(mark)
                .model(model)
                .color(color)
                .yearOfProduction(yearOfProduction)
                .description(description)
                .status(status)
                .build();
    }

    public Set<CarStatusSnapshotDto> mapCarStatusSnapshotsToDto(){
        if(statusSnapshots == null){
            return Collections.emptySet();
        }
        return statusSnapshots.stream()
                .map(CarStatusSnapshotNode::toCarStatusSnapshotDto)
                .collect(Collectors.toSet());
    }
}
