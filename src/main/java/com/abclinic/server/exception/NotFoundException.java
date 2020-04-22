package com.abclinic.server.exception;

import com.abclinic.server.common.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseRuntimeException {
    public NotFoundException(long userId, String message) {
        super("Resource not found: " + message, userId, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(long userId) {
        super("Resource not found", userId, HttpStatus.NOT_FOUND);
    }
}