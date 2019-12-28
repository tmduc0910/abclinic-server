package com.abclinic.server.exception;

import com.abclinic.server.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

/**
 * @author tmduc
 * @package com.abclinic.server.exception
 * @created 11/29/2019 9:19 AM
 */
public class BadRequestException extends BaseRuntimeException {
    public BadRequestException(int userId) {
        super("Bad request", userId, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(int userId, String message) {
        super("Bad request: " + message, userId, HttpStatus.BAD_REQUEST);
    }
}
