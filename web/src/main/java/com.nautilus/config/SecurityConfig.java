package com.nautilus.config;

import com.nautilus.algorithm.MD5;
import com.nautilus.rest.mapping.MappingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.sql.DataSource;

import static org.hibernate.criterion.Restrictions.and;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MappingProperties properties;

    @Autowired
    private DataSource dataSource;

    private String[] authenticatedMappings;

    private String[] permitAllMappings;

    private static final String DEF_USERS_BY_EMAIL_QUERY =
            "select email, password, enabled "
                    + "from userconfig "
                    + "where email = ?";

    private static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
            "select email, authorities "
                    + "from userconfig "
                    + "where email = ?";

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery(DEF_USERS_BY_EMAIL_QUERY)
                .authoritiesByUsernameQuery(DEF_AUTHORITIES_BY_USERNAME_QUERY)
                .passwordEncoder(new MD5())
            .and()
                .inMemoryAuthentication()
                .withUser("actuator").password("actuator").roles("ACTUATOR");
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

    private void initMappings() {
        initAuthenticatedMethods();
        initPermitAllMappings();
    }

    private void initAuthenticatedMethods() {
        authenticatedMappings = new String[]{
                properties.getUserUpdate(),
                properties.getCarFound(),
                properties.getCarRegister(),
                properties.getCarUpdate(),
                properties.getRegisterCarPhotos(),
                properties.getCarUpdatePhotos(),
                properties.getUserInfo(),
                properties.getUserCars(),
                properties.getCarInfo(),
                properties.getCarPhotos()
        };
    }

    private void initPermitAllMappings() {
        permitAllMappings = new String[]{
                properties.getIndex(),
                properties.getUserRegister()
        };
    }
}
