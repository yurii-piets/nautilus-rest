package com.nautilus.config;

import com.nautilus.algorithm.MD5;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import static com.nautilus.rest.controllers.IndexController.INDEX_MAPPING;
import static com.nautilus.rest.controllers.car.CarCapturesController.CAR_FOUND_MAPPING;
import static com.nautilus.rest.controllers.car.CarController.CAR_MAPPING;
import static com.nautilus.rest.controllers.car.CarPhotosController.CAR_PHOTOS_MAPPING;
import static com.nautilus.rest.controllers.user.UserController.USER_CAR_MAPPING;
import static com.nautilus.rest.controllers.user.UserController.USER_MAPPING;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    private final static String[] AUTHENTICATED_MAPPINGS = new String[]{
            USER_MAPPING,
            USER_MAPPING + "/**",
            USER_MAPPING + USER_CAR_MAPPING,
            USER_MAPPING + USER_CAR_MAPPING + "/**",
            CAR_MAPPING,
            CAR_MAPPING + "/**",
            CAR_PHOTOS_MAPPING,
            CAR_PHOTOS_MAPPING + "/**",
            CAR_FOUND_MAPPING,
            CAR_FOUND_MAPPING + "/**",
            INDEX_MAPPING
    };

    private final static String[] PERMIT_ALL_MAPPINGS = new String[]{

    };

    private static final String DEF_USERS_BY_EMAIL_QUERY =
            "SELECT email, password, enabled "
                    + "FROM userconfig "
                    + "WHERE email = ?";

    private static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
            "SELECT email, authorities "
                    + "FROM userconfig "
                    + "WHERE email = ?";

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
                .antMatchers(HttpMethod.POST, USER_MAPPING).permitAll()
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
