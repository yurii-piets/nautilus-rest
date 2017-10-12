package com.nautilus.domain;


import com.nautilus.constants.CarStatus;
import com.nautilus.dto.car.CarRegisterDTO;
import com.nautilus.dto.car.CarUpdateDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Set;

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

    @Override
    public int hashCode(){
        int hashCode = 1;
        hashCode = 31 * hashCode + (beaconId == null ? 0 : beaconId.hashCode());
        hashCode = 31 * hashCode + (registerNumber == null ? 0 : registerNumber.hashCode());
        hashCode = 31 * hashCode + (mark == null ? 0 : mark.hashCode());
        hashCode = 31 * hashCode + (model == null ? 0 : model.hashCode());
        hashCode = 31 * hashCode + (color == null ? 0 : color.hashCode());
        hashCode = 31 * hashCode + (yearOfProduction == null ? 0 : yearOfProduction.hashCode());
        hashCode = 31 * hashCode + (description == null ? 0 : description.hashCode());
        hashCode = 31 * hashCode + (status == null ? 0 : status.hashCode());
        hashCode = 31 * hashCode + (owner == null ? 0 : owner.hashCode());
        return hashCode;
    }
}
