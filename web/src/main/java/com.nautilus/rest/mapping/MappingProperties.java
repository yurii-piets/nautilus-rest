package com.nautilus.rest.mapping;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:mapping.properties")
public class MappingProperties {

    @Value("${user.register}")
    private String userRegister;

    @Value("${user.update}")
    private String userUpdate;

    @Value("${car.found}")
    private String carFound;

    @Value("${car.register}")
    private String carRegister;

    @Value("${car.register-car-photos}")
    private String registerCarPhotos;

    @Value("${car.update}")
    private String carUpdate;

    @Value("${car.update-photos}")
    private String carUpdatePhotos;

    @Value("${user.get.info}")
    private String userInfo;

    @Value("${user.get.cars}")
    private String userCars;

    @Value("${car.get.info}")
    private String carInfo;

    @Value("${car.get.photos}")
    private String carPhotos;
}

