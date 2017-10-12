package com.nautilus.domain;

import com.nautilus.dto.car.CarStatusDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Data
@NoArgsConstructor
public class CarLocation {
    private static final int MIN_LONGITUDE = -180;
    private static final int MAX_LONGITUDE = 180;

    private static final int MIN_LATITUDE = -90;
    private static final int MAX_LATITUDE = 90;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @NotNull
    @Min(value = MIN_LONGITUDE)
    @Max(value = MAX_LONGITUDE)
    private Double longitude;

    @NotNull
    @Min(value = MIN_LATITUDE)
    @Max(value = MAX_LATITUDE)
    private Double latitude;

    public CarLocation(CarStatusDTO carStatusDTO) {
        this.longitude = carStatusDTO.getLocation().getLongitude();
        this.latitude = carStatusDTO.getLocation().getLatitude();
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + (locationId == null ? 0 : locationId.hashCode());
        hashCode = 31 * hashCode + (longitude == null ? 0 : longitude.hashCode());
        hashCode = 31 * hashCode + (latitude == null ? 0 : latitude.hashCode());
        return hashCode;
    }
}
