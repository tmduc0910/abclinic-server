package com.abclinic.server.exception;

import com.abclinic.server.common.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseRuntimeException {
    public ForbiddenException(long userId, String message) {
        super("Forbidden: " + message, userId, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(long userId) {
        super("Forbidden", userId, HttpStatus.FORBIDDEN);
    }
}
