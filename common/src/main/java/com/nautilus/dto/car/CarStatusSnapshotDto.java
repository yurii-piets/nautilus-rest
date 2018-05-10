package com.nautilus.dto.car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarStatusSnapshotDto {

    @NotNull
    private Location location;

    private LocalDateTime captureTime;
}
