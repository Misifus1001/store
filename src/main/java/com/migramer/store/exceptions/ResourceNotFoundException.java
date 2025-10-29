package com.migramer.store.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resource, Integer id) {
        super(String.format("%s con ID %d no encontrado", resource, id), HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resource, String identifier) {
        super(String.format("%s '%s' no encontrado", resource, identifier), HttpStatus.NOT_FOUND);
    }
}