package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;

public class UnauthorizedActionException extends BaseRuntimeException {
    public UnauthorizedActionException(int userId) {
        super("No authority to perform this action", userId);
    }

    public UnauthorizedActionException() {
        super("No authority to perform this action");
    }
}