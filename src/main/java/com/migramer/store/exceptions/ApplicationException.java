package com.migramer.store.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final HttpStatus status;
    private final Object details;

    public ApplicationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.details = null;
    }

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.details = null;
    }

    public ApplicationException(String message, HttpStatus status, Object details) {
        super(message);
        this.status = status;
        this.details = details;
    }
}