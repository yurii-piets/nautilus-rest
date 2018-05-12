package com.nautilus.controller.exception;

import com.nautilus.exception.FileNotDeletedException;
import com.nautilus.exception.IllegalAccessException;
import com.nautilus.exception.OverLimitNumberOfFilesException;
import com.nautilus.exception.WrongBeaconIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;

@RestControllerAdvice
public class ExceptionHandlingController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalAccessException.class})
    public ResponseEntity<?> handle(IllegalAccessException e) {
        logger.error("Unexpected: ", e);
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = WrongBeaconIdException.class)
    public ResponseEntity<?> handle(WrongBeaconIdException e) {
        logger.error("Unexpected: ", e);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<?> handle(FileNotFoundException e) {
        logger.error("Unexpected: ", e);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = OverLimitNumberOfFilesException.class)
    public ResponseEntity<?> handle(OverLimitNumberOfFilesException e) {
        logger.error("Unexpected: ", e);
        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = {FileNotDeletedException.class})
    public ResponseEntity<?> handle(FileNotDeletedException e) {
        logger.error("Unexpected: ", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handle(Exception e) {
        logger.error("Unexpected: ", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
