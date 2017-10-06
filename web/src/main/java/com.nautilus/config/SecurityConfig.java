package com.nautilus.config;

import com.nautilus.rest.mapping.MappingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MappingProperties properties;

    private String[] authenticatedMappings;

    private String[] permitAllMappings;

    private void initMappings() {
        initAuthenticatedMethods();
        initPermitAllMappings();
    }

    private void initAuthenticatedMethods() {
        authenticatedMappings = new String[]{
                properties.getUserUpdate(),
                properties.getCarFound(),
                properties.getCarRegister(),
                properties.getCarUpdate()
        };
    }

    private void initPermitAllMappings() {
        permitAllMappings = new String[]{
                properties.getIndex(),
                properties.getUserRegister()
        };
    }

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
        initMappings();

        http
                .authorizeRequests()
                .antMatchers(authenticatedMappings).authenticated()
                .antMatchers(permitAllMappings).permitAll()
                .anyRequest().permitAll()
            .and()
                .httpBasic()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .csrf().disable();
    }
}
