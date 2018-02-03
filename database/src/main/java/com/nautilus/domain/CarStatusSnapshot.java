package com.nautilus.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.jws.HandlerChain;
import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "carLocation")
@ToString(exclude = "carLocation")
public class CarStatusSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long snapshotId;

    @OneToOne
    private CarLocation carLocation;

    @ManyToOne
    @JsonIgnore
    private Car car;

    private Timestamp captureTime;
}
