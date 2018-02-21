package com.nautilus.service;

import com.nautilus.services.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.nautilus.exception.IllegalAccessException;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final GlobalService globalService;

    public void hasAccessByBeaconId(String beaconId) {
        if(!checkAccessByBeaconId(beaconId)){
            throw new IllegalAccessException("User:" + SecurityContextHolder.getContext().getAuthentication().getName()
                    + " does not have access to car with beacon: " + beaconId);
        }
    }

    private boolean checkAccessByBeaconId(String beaconId) {
        String email = globalService.findEmailByBeaconId(beaconId);
        if (email == null) {
            return false;
        }

        String authenticationEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return email.equals(authenticationEmail);
    }
}
