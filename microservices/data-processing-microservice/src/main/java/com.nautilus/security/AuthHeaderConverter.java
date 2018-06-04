package com.nautilus.security;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class AuthHeaderConverter {

    public String convertCredentialsToHeader() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String pass = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();

        String plainCreds = name + ":" + pass;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return "Basic " + new String(base64CredsBytes);
    }
}
