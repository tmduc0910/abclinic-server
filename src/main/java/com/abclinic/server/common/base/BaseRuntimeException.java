package com.abclinic.server.common.base;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseRuntimeException extends RuntimeException {
    private static final String TEMPLATE = "Invalid attempt: ${error}. " +
            "Caused by: User ${userId}.";
    private final long userId;
    private final HttpStatus status;

    public BaseRuntimeException(String error, HttpStatus status) {
        super(pack(error, -1));
        this.userId = -1;
        this.status = status;
    }

    public BaseRuntimeException(String error, long userId, HttpStatus status) {
        super(pack(error, userId));
        this.userId = userId;
        this.status = status;
    }

    private static String pack(String error, long userId) {
        Map<String, String> values = new HashMap<>();
        values.put("error", error);
        values.put("userId", String.valueOf(userId));
        StringSubstitutor substitutor = new StringSubstitutor(values);
        return substitutor.replace(TEMPLATE);
    }

    public long getUserId() {
        return userId;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
