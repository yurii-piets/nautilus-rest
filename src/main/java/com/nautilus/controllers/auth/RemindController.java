package com.nautilus.controllers.auth;

import com.nautilus.constants.Status;
import com.nautilus.dto.status.StatusDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/remind")
public class RemindController {

    @RequestMapping(method = RequestMethod.POST)
    public Status remind(@RequestBody String phoneOrMail) {

        return Status.ACCEPTED;
    }
}
