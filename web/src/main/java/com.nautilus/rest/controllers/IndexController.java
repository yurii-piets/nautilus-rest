package com.nautilus.rest.controllers;

import com.nautilus.constants.Status;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/index")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public Status index() {
//        System.out.println(properties.getIndex());
        return Status.ACCEPTED;
    }
}
