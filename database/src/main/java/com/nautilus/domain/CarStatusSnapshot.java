package com.nautilus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "carLocation")
@ToString(exclude = "carLocation")
public class CarStatusSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long snapshotId;

    @OneToOne
    private CarLocation carLocation;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Car car;

    private Timestamp captureTime;

    public CarStatusSnapshot(CarLocation carLocation, Timestamp captureTime) {
        this.carLocation = carLocation;
        this.captureTime = captureTime;
    }
}
