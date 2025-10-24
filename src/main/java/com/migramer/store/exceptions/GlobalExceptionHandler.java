package com.migramer.store.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.migramer.store.models.ResponseGenerico;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGenerico<Object>> handleValidationErrors(MethodArgumentNotValidException exception) {

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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object handleInvalidArgument(Exception exception) {
        return tratarExcepcion(exception);
    }

    public Object tratarExcepcion(Exception exception) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", exception.getMessage());
        logger.error(exception.getMessage(), exception);
        return map;
    }

}
