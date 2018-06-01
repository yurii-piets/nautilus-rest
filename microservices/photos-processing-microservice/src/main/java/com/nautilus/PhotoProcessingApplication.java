package com.nautilus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class PhotoProcessingApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoProcessingApplication.class, args);
    }
}
