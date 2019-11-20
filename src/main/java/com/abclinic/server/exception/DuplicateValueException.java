package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;

public class DuplicateValueException extends BaseRuntimeException {
    public DuplicateValueException() {
        super("Duplicate value while sign up");
    }
}
