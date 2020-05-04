package com.abclinic.server.exception;

import com.abclinic.server.common.base.CustomRuntimeException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomRuntimeException {
    public NotFoundException() {
        super("Tài nguyên không tồn tại", 0, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(long userId, String message) {
        super("Tài nguyên không tồn tại: " + message, userId, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(long userId) {
        super("Tài nguyên không tồn tại", userId, HttpStatus.NOT_FOUND);
    }
}