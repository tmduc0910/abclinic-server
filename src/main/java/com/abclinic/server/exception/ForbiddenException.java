package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseRuntimeException {
    public ForbiddenException() {
        super("Forbidden action", HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(int userId) {
        super("Forbidden action", userId, HttpStatus.FORBIDDEN);
    }
}
