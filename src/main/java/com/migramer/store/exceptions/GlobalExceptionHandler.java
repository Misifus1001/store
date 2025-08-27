package com.migramer.store.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.migramer.store.models.ResponseGenerico;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGenerico<Object>> handleValidationErrors(MethodArgumentNotValidException exception){

        List<String> errors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        
        ResponseGenerico<Object> responseGenerico = new ResponseGenerico<Object>(
            "Errores de validación", 
            String.valueOf(HttpStatus.BAD_REQUEST.value()),
             errors);

        return ResponseEntity.badRequest().body(responseGenerico); 
    }
    
}
