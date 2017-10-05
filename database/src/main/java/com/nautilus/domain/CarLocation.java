package com.nautilus.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class CarLocation {
    private static final int MIN_LONGTITUDE = -180;
    private static final int MAX_LONGTITUDE = 180;

    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String locationId;

    @NotNull
    @Min(value = MIN_LONGTITUDE)
    @Max(value = MAX_LONGTITUDE)
    private Double longitude;

    @NotNull
    @Min(value = MIN_LATITUDE)
    @Max(value = MAX_LATITUDE)
    private Double latitude;

    @OneToOne
    private Car car;
}