package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseRuntimeException {
    public ForbiddenException(long userId, String message) {
        super("Forbidden action: " + message, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(long userId) {
        super("Forbidden action", userId, HttpStatus.FORBIDDEN);
    }
}
