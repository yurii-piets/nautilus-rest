package com.nautilus.dto.car;

import com.nautilus.dto.constants.CarStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CarDto {

    private String beaconId;

    private String registerNumber;

    private String mark;

    private String model;

    private String color;

    private String yearOfProduction;

    private String description;

    private CarStatus status;

    @Builder
    public CarDto(String beaconId, String registerNumber, String mark, String model, String color, String yearOfProduction, String description, CarStatus status) {
        this.beaconId = beaconId;
        this.registerNumber = registerNumber;
        this.mark = mark;
        this.model = model;
        this.color = color;
        this.yearOfProduction = yearOfProduction;
        this.description = description;
        this.status = status;
    }
}
