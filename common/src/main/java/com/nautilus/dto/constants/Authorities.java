package com.nautilus.dto.constants;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public enum Authorities implements GrantedAuthority {
    ADMIN("ADMIN"),
    ACTUATOR("ACTUATOR"),
    USER("USER");

    private String value;

    @Override
    public String toString(){
        return value;
    }

    @Override
    public String getAuthority() {
        return value;
    }
}
