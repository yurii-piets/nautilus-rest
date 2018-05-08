package com.nautilus.domain;

import com.nautilus.domain.converter.LocalDateTimeConverter;
import com.nautilus.domain.converter.LocationConverter;
import com.nautilus.dto.car.Location;
import lombok.Data;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDateTime;

@Data
@NodeEntity
public class CarStatusSnapshotNode {

    private Long id;

    @Convert(LocationConverter.class)
    private Location carLocation;

    @Relationship(type = "CAR")
    private CarNode car;

    @Convert(LocalDateTimeConverter.class)
    private LocalDateTime time;
}
