package com.microservicesbank.cards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s : '%s'", message, fieldName, fieldValue));
    }
}
