package com.nautilus.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class RegisterUserDto {

    private static final String EMAIL_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private static final String PHONE_NUMBER_PATTERN = "\\+?[0-9]{9,12}";
    private static final String USERNAME_PATTERN = "[a-zA-Z]+";
    private static final String USER_SURNAME_PATTERN = "[a-zA-Z]+";
    private static final int PASSWORD_LENGTH = 1;

    @Pattern(regexp = EMAIL_PATTERN)
    @NotNull
    private String email;

    @Pattern(regexp = PHONE_NUMBER_PATTERN)
    @NotNull
    private String phoneNumber;

    @Pattern(regexp = USERNAME_PATTERN)
    @NotNull
    private String name;

    @Pattern(regexp = USER_SURNAME_PATTERN)
    @NotNull
    private String surname;

    @Size(min = PASSWORD_LENGTH)
    @NotNull
    private String password;
}
