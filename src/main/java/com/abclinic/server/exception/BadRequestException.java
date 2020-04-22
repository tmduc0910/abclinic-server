package com.abclinic.server.exception;

import com.abclinic.server.common.base.BaseRuntimeException;
import org.springframework.http.HttpStatus;

/**
 * @author tmduc
 * @package com.abclinic.server.exception
 * @created 11/29/2019 9:19 AM
 */
public class BadRequestException extends BaseRuntimeException {
    public BadRequestException(long userId) {
        super("Bad request", userId, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(long userId, String message) {
        super("Bad request: " + message, userId, HttpStatus.BAD_REQUEST);
    }
}
