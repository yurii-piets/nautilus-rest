package com.nautilus.exception;

public class WrongBeaconIdException extends RuntimeException {

    public WrongBeaconIdException(String message) {
        super(message);
    }
}
