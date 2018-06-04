package com.nautilus.service;

import com.nautilus.exception.IllegalAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final DataService dataService;

    public void hasAccessByBeaconId(String beaconId) {
        if(!checkAccessByBeaconId(beaconId)){
            throw new IllegalAccessException("User:" + SecurityContextHolder.getContext().getAuthentication().getName()
                    + " does not have access to car with beacon: " + beaconId);
        }
    }

    private boolean checkAccessByBeaconId(String beaconId) {
        String email = dataService.getEmailByBeaconId(beaconId);
        if (email == null) {
            return false;
        }

        String authenticationEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return email.equals(authenticationEmail);
    }
}
