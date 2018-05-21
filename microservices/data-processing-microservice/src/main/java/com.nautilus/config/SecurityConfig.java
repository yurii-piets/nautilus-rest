package com.nautilus.config;

import com.nautilus.algorithm.MD5;
import com.nautilus.dto.constants.Authorities;
import com.nautilus.security.CustomerUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static com.nautilus.controller.ApiSpecificationController.INDEX_MAPPING;
import static com.nautilus.controller.car.CarCapturesController.CAR_FOUND_MAPPING;
import static com.nautilus.controller.car.CarController.CAR_MAPPING;
import static com.nautilus.controller.car.CarPhotosController.CAR_PHOTOS_MAPPING;
import static com.nautilus.controller.user.UserController.USER_CAR_MAPPING;
import static com.nautilus.controller.user.UserController.USER_MAPPING;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
            "/actuator/**",
            "/index/**",
            INDEX_MAPPING
    };

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails actuator = User.builder()
                .username("actuator")
                .password("actuator")
                .roles(Authorities.ACTUATOR.toString())
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("admin")
                .roles(Authorities.ADMIN.toString())
                .build();

        return new InMemoryUserDetailsManager(actuator, admin);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(neo4jUserDetails())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, USER_MAPPING).not().authenticated()
                .antMatchers(AUTHENTICATED_MAPPINGS).authenticated()
                .anyRequest().authenticated()
            .and()
                .httpBasic()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .csrf().disable();
    }

    @Bean
    public UserDetailsService neo4jUserDetails() {
        return new CustomerUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5();
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
