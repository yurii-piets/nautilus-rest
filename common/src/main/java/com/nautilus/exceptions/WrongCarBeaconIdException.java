package com.nautilus.exceptions;

public class WrongCarBeaconIdException extends Exception {
    public WrongCarBeaconIdException(){
        super();
    }

    public WrongCarBeaconIdException(String message){
        super(message);
    }
}
