package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/remind")
public class RemindController {

    @RequestMapping(method = RequestMethod.POST)
    public Status remind(@RequestBody @Valid String phoneOrMail) {

        return Status.ACCEPTED;
    }
}
