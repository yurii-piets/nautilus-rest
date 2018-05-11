package com.nautilus.dto.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserInfoDto {

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;

    @Builder
    public UserInfoDto(String name, String surname, String email, String phoneNumber) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
