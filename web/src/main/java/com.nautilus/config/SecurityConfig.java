package com.nautilus.config;

import com.nautilus.rest.mapping.MappingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MappingProperties properties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String indexMapping = properties.getIndex();
        String loginMapping = properties.getLogin();

        http
            .authorizeRequests()
                .antMatchers(indexMapping, loginMapping).permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }
}
