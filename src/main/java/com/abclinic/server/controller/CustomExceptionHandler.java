package com.abclinic.server.controller;

import com.abclinic.server.exception.DuplicateValueException;
import com.abclinic.server.exception.WrongCredentialException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handleException(RuntimeException e) {
        logger.error(e.getMessage());
        if (e instanceof DuplicateValueException)
            return new ResponseEntity<>(HttpStatus.IM_USED);
        else if (e instanceof WrongCredentialException)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return null;
    }
}
