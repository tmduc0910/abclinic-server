package com.abclinic.server.exception;

import com.abclinic.server.common.base.CustomRuntimeException;
import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends CustomRuntimeException {
    public UnauthorizedActionException(long userId) {
        super("No authority to perform this action", userId, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedActionException(long userId, String message) {
        super("No authority to perform this action: " + message, userId, HttpStatus.UNAUTHORIZED);
    }
}