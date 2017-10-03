package com.nautilus.domain;

import com.nautilus.constants.CarStatus;
import com.nautilus.dto.car.CarRegisterDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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
    @Column(columnDefinition = "TEXT", length = 1000)
    private String description;

    @ManyToOne
    private UserConfig owner;

    @OneToMany(fetch = FetchType.LAZY)
    private List<CarStatusSnapshot> statusSnapshots;

    private CarStatus status;

    public Car(CarRegisterDTO carRegisterDTO){
        this.beaconId = carRegisterDTO.getBeaconId();
        this.registerNumber = carRegisterDTO.getRegisterNumber();
        this.mark = carRegisterDTO.getMark();
        this.model = carRegisterDTO.getModel();
        this.color = carRegisterDTO.getColor();
        this.yearOfProduction = carRegisterDTO.getYearOfProduction();
        this.description = carRegisterDTO.getDescription();
    }
}
