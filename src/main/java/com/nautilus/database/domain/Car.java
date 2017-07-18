package com.nautilus.database.domain;

import com.nautilus.constants.CarStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Car {

    @Id
    private String beaconId;

    private String registerNumber;
    private String mark;
    private String model;
    private String color;
    private String yearOfProduction;
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    private UserConfig owner;

    private CarStatus status;

}
