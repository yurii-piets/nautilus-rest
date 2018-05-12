package com.nautilus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class IndexController {

    public final static String INDEX_MAPPING = "/index";

    private final DiscoveryClient discoveryClient;

    @RequestMapping(value = INDEX_MAPPING, method = RequestMethod.GET)
    public ResponseEntity<?> index() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{applicationName}", method = RequestMethod.GET)
    public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
        return discoveryClient.getInstances(applicationName);
    }

}
