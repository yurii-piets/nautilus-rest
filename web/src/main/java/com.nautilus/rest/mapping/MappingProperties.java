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

    @Value("${car.update}")
    private String carUpdate;

    //delete this om production
    @Value("${test.index}")
    private String index;
}

