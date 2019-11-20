package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class UnauthorizedActionException extends BaseRuntimeException {
    public UnauthorizedActionException(int userId) {
        super("No authority to perform this action", userId, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedActionException() {
        super("No authority to perform this action", HttpStatus.UNAUTHORIZED);
    }
}