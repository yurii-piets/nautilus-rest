package com.nautilus.service;

import com.nautilus.services.def.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final GlobalService globalService;

    private final static String infoQuery = "select phoneNumber from userconfig where email = ?;";

    public boolean hasAccess(String phoneNumber){
        String email = globalService.findEmailByPhoneNumber(phoneNumber);

        if(email == null){
            return false;
        }

        String authenticationEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return email.equals(authenticationEmail);
    }
}
