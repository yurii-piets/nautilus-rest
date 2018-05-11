package com.nautilus.exception;

public class FileNotDeletedException extends RuntimeException {
    public FileNotDeletedException(String message) {
        super(message);
    }
}
