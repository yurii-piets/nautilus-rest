package com.nautilus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartialUpdateBody {

    private String op;

    private String path;

    private Object value;
}
