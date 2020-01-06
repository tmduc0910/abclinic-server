package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

public class DuplicateValueException extends BaseRuntimeException {
    public DuplicateValueException() {
        super("Conflict: Trùng thông tin đăng ký đã có", HttpStatus.CONFLICT);
    }
}
