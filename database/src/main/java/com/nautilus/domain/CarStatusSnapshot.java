package com.nautilus.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
public class CarStatusSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapshotId;

    @OneToOne
    private CarLocation carLocation;

    @ManyToOne
    private Car car;

    private Timestamp timestamp;

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (snapshotId == null ? 0 : snapshotId.hashCode());
        hashCode = 31 * hashCode + (car == null ? 0 : car.hashCode());
        hashCode = 31 * hashCode + (timestamp == null ? 0 : timestamp.hashCode());
        return hashCode;
    }
}
