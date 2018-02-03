package com.nautilus.service;

import com.nautilus.services.def.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final GlobalService globalService;

    private final static String infoQuery = "select phoneNumber from userconfig where email = ?;";

    public boolean hasAccessByPhoneNumber(String phoneNumber) {
        String email = globalService.findEmailByPhoneNumber(phoneNumber);

        if (email == null) {
            return false;
        }

        String authenticationEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if(authenticationEmail == null) {
            return false;
        }

        return email.equals(authenticationEmail);
    }

    public boolean hasAccessByBeaconId(String beaconId) {
        String email = globalService.findEmailByBeaconId(beaconId);
        if(email == null) {
            return false;
        }
        String authenticationEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(authenticationEmail == null){
            return false;
        }

        return email.equals(authenticationEmail);
    }
}
