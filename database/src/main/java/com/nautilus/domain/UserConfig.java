package com.nautilus.domain;

import com.nautilus.algorithm.MD5;
import com.nautilus.dto.user.RegisterUserDTO;
import com.nautilus.dto.user.UpdateUserDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
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

    public static UserConfig mergeWithUpdateDto(UserConfig user, UpdateUserDTO updateDTO) {
        UserConfig updateUser = new UserConfig();

        String name = updateDTO.getUserName();
        String surname = updateDTO.getUserSurname();
        String phoneNumber = updateDTO.getPhoneNumber();
        String email = updateDTO.getEmail();
        String password = updateDTO.getPassword();
        Boolean enabled = updateDTO.getEnabled();

        if (name != null) {
            updateUser.setName(name);
        } else {
            updateUser.setName(user.getName());
        }

        if(surname != null){
            updateUser.setSurname(surname);
        } else {
            updateUser.setSurname(user.getSurname());
        }

        if(phoneNumber != null){
            updateUser.setPhoneNumber(phoneNumber);
        } else {
            updateUser.setPhoneNumber(user.getPhoneNumber());
        }

        if(email != null){
            updateUser.setEmail(email);
        } else {
            updateUser.setPhoneNumber(user.getPhoneNumber());
        }

        if(password != null){
            updateUser.setPassword(new MD5().encode(password));
        } else {
            updateUser.setPassword(user.getPassword());
        }

        if(enabled != null){
            updateUser.setEnabled(enabled);
        } else {
            updateUser.setEnabled(user.getEnabled());
        }

        Long id = user.getUserId();
        updateUser.setUserId(id);

        return updateUser;
    }

}


