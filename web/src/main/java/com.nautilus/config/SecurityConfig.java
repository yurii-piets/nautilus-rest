package com.nautilus.config;


import com.nautilus.algorithm.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

import static com.nautilus.rest.controllers.car.CarInfoController.CAR_INFO_BASIC_MAPPING;
import static com.nautilus.rest.controllers.car.FoundCarController.CAR_FOUND_MAPPING;
import static com.nautilus.rest.controllers.car.RegisterCarController.REGISTER_CAR_MAPPING;
import static com.nautilus.rest.controllers.car.UpdateCarController.CAR_UPDATE_MAPPING;
import static com.nautilus.rest.controllers.user.RegisterController.REGISTER_USER_MAPPING;
import static com.nautilus.rest.controllers.user.UpdateUserController.USER_UPDATE_MAPPING;
import static com.nautilus.rest.controllers.user.UserInfoController.USER_CAR_MAPPING;
import static com.nautilus.rest.controllers.user.UserInfoController.USER_INFO_MAPPING;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    private final static String[] AUTHENTICATED_MAPPINGS = new String[]{
            CAR_INFO_BASIC_MAPPING,
            CAR_INFO_BASIC_MAPPING + "/**",
            CAR_FOUND_MAPPING,
            CAR_FOUND_MAPPING + "/**",
            REGISTER_CAR_MAPPING,
            REGISTER_CAR_MAPPING + "/**",
            CAR_UPDATE_MAPPING,
            CAR_UPDATE_MAPPING + "/**",
            USER_UPDATE_MAPPING,
            USER_UPDATE_MAPPING + "/**",
            USER_INFO_MAPPING,
            USER_INFO_MAPPING + "/**",
            USER_CAR_MAPPING,
            USER_CAR_MAPPING + "/**",
    };

    private final static String[] PERMIT_ALL_MAPPINGS = new String[]{
            REGISTER_USER_MAPPING
    };

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
        http
                .authorizeRequests()
                .antMatchers(AUTHENTICATED_MAPPINGS).authenticated()
                .antMatchers(PERMIT_ALL_MAPPINGS).permitAll()
                .anyRequest().permitAll()
            .and()
                .httpBasic()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .csrf().disable();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
