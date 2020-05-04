package com.abclinic.server.common.base;

import com.abclinic.server.model.dto.ErrorDto;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public abstract class CustomController {

    protected Logger logger;

    @PostConstruct
    public abstract void init();

    @ExceptionHandler(value = CustomRuntimeException.class)
    public ResponseEntity<ErrorDto> handleException(CustomRuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), e.getStatus());
    }
}
