package com.nautilus.rest.controllers;

import com.nautilus.constants.Status;
import com.nautilus.rest.mapping.MappingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${test.index}")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public Status index() {
        return Status.ACCEPTED;
    }
}
