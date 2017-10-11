package com.nautilus.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class CarStatusSnapshot {

    @ManyToOne
    private Car car;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapshotId;

    @OneToOne
    private CarLocation carLocation;

    private Timestamp timestamp;
}
