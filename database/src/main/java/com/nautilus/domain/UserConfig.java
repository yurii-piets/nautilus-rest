package com.nautilus.domain;

import com.nautilus.domain.Car;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

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
    private String phoneNumber;
    private String email;
    private String password;

    @OneToMany
    private Set<Car> cars;

    @Builder
    public UserConfig(
            String name,
            String surname,
            String phoneNumber,
            String email,
            String password) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}


