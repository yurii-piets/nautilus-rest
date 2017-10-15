package com.nautilus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.jws.HandlerChain;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "carLocation")
@ToString(exclude = "carLocation")
public class CarStatusSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long snapshotId;

    @OneToOne
    @JsonIgnore
    private CarLocation carLocation;

    @ManyToOne
    private Car car;

    private Timestamp timestamp;
}
