package com.migramer.store.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessException extends ApplicationException {
    public BusinessException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BusinessException(String message, Object details) {
        super(message, HttpStatus.BAD_REQUEST, details);
    }
}