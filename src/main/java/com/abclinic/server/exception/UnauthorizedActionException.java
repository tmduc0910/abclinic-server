package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends BaseRuntimeException {
    public UnauthorizedActionException(long userId) {
        super("No authority to perform this action", userId, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedActionException(long userId, String message) {
        super("No authority to perform this action: " + message, HttpStatus.UNAUTHORIZED);
    }
}