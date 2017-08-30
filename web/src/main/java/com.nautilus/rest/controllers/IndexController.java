package com.nautilus.rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${test.index}")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public HttpStatus index() {
//        System.out.println(properties.getIndex());
        return HttpStatus.OK;
    }
}
