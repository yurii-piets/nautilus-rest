package com.nautilus.domain;

import com.nautilus.domain.Car;
import com.nautilus.dto.user.RegisterUserDTO;
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

    public UserConfig(RegisterUserDTO registerDTO) {
        this.name = registerDTO.getUserName();
        this.surname = registerDTO.getUserSurname();
        this.phoneNumber = registerDTO.getPhoneNumber();
        this.email = registerDTO.getEmail();
        this.password = registerDTO.getPassword();
    }
}


