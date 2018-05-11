package com.nautilus.node;

import com.nautilus.converter.LocationConverter;
import com.nautilus.dto.car.CarStatusSnapshotDto;
import com.nautilus.dto.car.Location;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@NodeEntity
public class CarStatusSnapshotNode {

    @Id
    @GeneratedValue
    private Long id;

    @Convert(LocationConverter.class)
    private Location carLocation;

    @Relationship(type = "CAPTURE")
    private CarNode car;

    private LocalDateTime captureTime;

    @Builder
    public CarStatusSnapshotNode(Location carLocation, CarNode car, LocalDateTime captureTime) {
        this.carLocation = carLocation;
        this.car = car;
        this.captureTime = captureTime;
    }

    public CarStatusSnapshotDto toCarStatusSnapshotDto() {
        return new CarStatusSnapshotDto(carLocation, captureTime);
    }
}
