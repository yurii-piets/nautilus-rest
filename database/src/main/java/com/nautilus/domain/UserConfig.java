package com.nautilus.domain;

import com.nautilus.algorithm.MD5;
import com.nautilus.constants.Authorities;
import com.nautilus.dto.user.RegisterUserDTO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "cars")
@ToString(exclude = "cars")
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String password;
    private Boolean enabled;

    private Authorities authorities;

    @OneToMany
    private Set<Car> cars;

    public UserConfig(RegisterUserDTO registerDTO) {
        this.name = registerDTO.getUserName();
        this.surname = registerDTO.getUserSurname();
        this.phoneNumber = registerDTO.getPhoneNumber();
        this.email = registerDTO.getEmail();
        this.password = new MD5().encode(registerDTO.getPassword());
        this.enabled = true;
    }
}


