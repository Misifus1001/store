package com.migramer.store.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message, Object details) {
        super(message, HttpStatus.BAD_REQUEST, details);
    }
}