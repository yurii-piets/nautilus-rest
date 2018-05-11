package com.nautilus.dto.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum CarStatus {
    OK("OK"), TESTING("TESTING"), STOLEN("STOLEN");

    private String value;

    @Override
    public String toString(){
        return value;
    }
}
