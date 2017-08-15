package com.nautilus.domain;

import com.nautilus.domain.Car;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String surname;
    private String email;
    private String password;

    @OneToMany
    private Set<Car> cars;
}
