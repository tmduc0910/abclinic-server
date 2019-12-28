package com.abclinic.server.model.dto;

import org.springframework.http.HttpStatus;

/**
 * @author tmduc
 * @package com.abclinic.server.model.dto
 * @created 12/28/2019 3:10 PM
 */
public class ErrorDto {
    private String message;

    public ErrorDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message.substring(message.indexOf("Invalid attempt: ") + "Invalid attempt: ".length(), message.indexOf('.'));
    }

    public String getUserId() {
        return message.substring(message.indexOf("Caused by: User ") + "Caused by: User ".length(), message.lastIndexOf('.'));
    }
}
