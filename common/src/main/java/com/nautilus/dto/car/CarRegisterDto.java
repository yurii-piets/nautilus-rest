package com.nautilus.dto.car;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CarRegisterDto {

    @NotNull
    private String beaconId;

    private String registerNumber;

    private String mark;

    private String model;

    private String color;

    private String yearOfProduction;

    private String description;
}
