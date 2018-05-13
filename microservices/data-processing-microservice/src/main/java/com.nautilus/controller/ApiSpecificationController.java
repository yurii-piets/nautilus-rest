package com.nautilus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ApiSpecificationController {

    public final static String INDEX_MAPPING = "/index";

    private final static String SPECIFICATION_VIEW = "api/index.html";

    @RequestMapping(value = INDEX_MAPPING, method = RequestMethod.GET)
    public String index() {
        return SPECIFICATION_VIEW;
    }
}
