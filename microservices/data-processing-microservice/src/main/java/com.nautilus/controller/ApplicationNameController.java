package com.nautilus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ApplicationNameController {

    private final DiscoveryClient discoveryClient;

    @RequestMapping(value = "/{applicationName}", method = RequestMethod.GET)
    public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
        return discoveryClient.getInstances(applicationName);
    }

}
