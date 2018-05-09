package com.nautilus.dto.car;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CarStatusSnapshotDto {

    @NotNull
    private String beaconId;

    @NotNull
    private Location location;

    private LocalDateTime captureTime;
}
