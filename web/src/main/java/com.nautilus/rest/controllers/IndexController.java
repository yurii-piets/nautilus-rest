package com.nautilus.rest.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.nautilus.rest.controllers.IndexController.INDEX_MAPPING;

@RestController
@RequestMapping(value = INDEX_MAPPING)
public class IndexController {

    public final static String INDEX_MAPPING = "/index";

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> index(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
