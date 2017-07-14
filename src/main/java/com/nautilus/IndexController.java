package com.nautilus;

import com.nautilus.dto.MessageDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping(value = "/")
    public MessageDTO index() {
        MessageDTO dto = new MessageDTO();
        dto.setValue("Hello from server");
        return dto;
    }
}
