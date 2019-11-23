package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseRuntimeException {
    public NotFoundException() {
        super("Resource not found", HttpStatus.NOT_FOUND);
    }

    public NotFoundException(int userId) {
        super("Resource not found", userId, HttpStatus.NOT_FOUND);
    }
}