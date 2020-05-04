package com.abclinic.server.exception;

import com.abclinic.server.common.base.CustomRuntimeException;
import org.springframework.http.HttpStatus;

public class DuplicateValueException extends CustomRuntimeException {
    public DuplicateValueException() {
        super("Conflict: Trùng thông tin đăng ký đã có", HttpStatus.CONFLICT);
    }
}
