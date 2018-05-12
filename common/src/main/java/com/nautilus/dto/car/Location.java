package com.nautilus.dto.car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LONGITUDE = 180;

    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = 90;

    @NotNull
    @Min(value = MIN_LONGITUDE)
    @Max(value = MAX_LONGITUDE)
    private BigDecimal longitude;

    @NotNull
    @Min(value = MIN_LATITUDE)
    @Max(value = MAX_LATITUDE)
    private BigDecimal latitude;
}
