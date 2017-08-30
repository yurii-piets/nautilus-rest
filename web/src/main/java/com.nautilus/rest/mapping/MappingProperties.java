package com.nautilus.rest.mapping;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
//@ConfigurationProperties(prefix = "user")
@PropertySource("classpath:mapping.properties")
public class MappingProperties {

    @Value("${user.login}")
    private String login;

    @Value("${test.index")
    private String index;
}

