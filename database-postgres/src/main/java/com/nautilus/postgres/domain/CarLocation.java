package com.nautilus.postgres.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nautilus.dto.car.CarStatusSnapshotDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class CarLocation {
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LONGITUDE = 180;

    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long locationId;

    @NotNull
    @Min(value = MIN_LONGITUDE)
    @Max(value = MAX_LONGITUDE)
    private Double longitude;

    @NotNull
    @Min(value = MIN_LATITUDE)
    @Max(value = MAX_LATITUDE)
    private Double latitude;

    public CarLocation(CarStatusSnapshotDto carStatusSnapshotDto) {
        this.longitude = carStatusSnapshotDto.getLocation().getLongitude();
        this.latitude = carStatusSnapshotDto.getLocation().getLatitude();
    }

}
