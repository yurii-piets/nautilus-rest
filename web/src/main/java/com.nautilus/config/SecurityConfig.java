package com.nautilus.config;

import com.nautilus.rest.mapping.MappingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MappingProperties properties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth
            .inMemoryAuthentication()
                    .withUser("admin").password("admin").roles("ADMIN")
                .and()
                    .withUser("temporary").password("temporary").roles("USER");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String indexMapping = properties.getIndex();
        String registerUserMapping = properties.getUserRegister();
        String updateUserMapping = properties.getUserUpdate();
        String carFoundMapping = properties.getCarFound();

        http
                .authorizeRequests()
                .antMatchers(carFoundMapping).authenticated()
                .antMatchers(indexMapping, registerUserMapping, updateUserMapping).permitAll()
                .anyRequest().permitAll()
            .and()
                .httpBasic()
            .and()
                .csrf().disable();
    }
}
