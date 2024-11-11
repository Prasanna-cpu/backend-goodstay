package com.spring.backend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailResourceNotFoundException extends RuntimeException {
    public EmailResourceNotFoundException(String message) {
        super(message);
    }
}
