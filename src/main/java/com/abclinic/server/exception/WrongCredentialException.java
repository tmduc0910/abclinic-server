package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;

public class WrongCredentialException extends BaseRuntimeException {
    public WrongCredentialException() {
        super("Wrong credential");
    }
}
