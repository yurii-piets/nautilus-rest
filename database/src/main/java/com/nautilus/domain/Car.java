package com.nautilus.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nautilus.constants.CarStatus;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.dto.car.CarUpdateDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(exclude="statusSnapshots")
@ToString(exclude="statusSnapshots")
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
    @JsonIgnore
    private UserConfig owner;

    @OneToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CarStatusSnapshot> statusSnapshots;

    private CarStatus status;

    public Car(CarRegisterDTO carRegisterDTO) {
        this.beaconId = carRegisterDTO.getBeaconId();
        this.registerNumber = carRegisterDTO.getRegisterNumber();
        this.mark = carRegisterDTO.getMark();
        this.model = carRegisterDTO.getModel();
        this.color = carRegisterDTO.getColor();
        this.yearOfProduction = carRegisterDTO.getYearOfProduction();
        this.description = carRegisterDTO.getDescription();
    }

    public static Car mergeWithUpdateDto(Car car, CarUpdateDTO carUpdateDTO){
        Car updatedCar = new Car();

        updatedCar.setBeaconId(car.getBeaconId());

        if(carUpdateDTO.getRegisterNumber() != null){
            updatedCar.setRegisterNumber(carUpdateDTO.getRegisterNumber());
        } else {
            updatedCar.setRegisterNumber(car.getRegisterNumber());
        }

        if(carUpdateDTO.getMark() != null){
            updatedCar.setMark(carUpdateDTO.getMark());
        } else {
            updatedCar.setMark(car.getMark());
        }

        if(carUpdateDTO.getModel() != null){
            updatedCar.setModel(carUpdateDTO.getModel());
        } else {
            updatedCar.setModel(car.getModel());
        }

        if(carUpdateDTO.getColor() != null){
            updatedCar.setColor(carUpdateDTO.getColor());
        } else {
            updatedCar.setColor(car.getColor());
        }

        if(carUpdateDTO.getYearOfProduction() != null){
            updatedCar.setYearOfProduction(carUpdateDTO.getYearOfProduction());
        } else {
            updatedCar.setYearOfProduction(car.getYearOfProduction());
        }

        if(carUpdateDTO.getDescription() != null){
            updatedCar.setDescription(carUpdateDTO.getDescription());
        } else {
            updatedCar.setDescription(car.getDescription());
        }

        if(carUpdateDTO.getStatus() != null){
            updatedCar.setStatus(carUpdateDTO.getStatus());
        } else {
            updatedCar.setStatus(car.getStatus());
        }

        updatedCar.setOwner(car.getOwner());
        updatedCar.setStatusSnapshots(car.getStatusSnapshots());

        return updatedCar;
    }
}
