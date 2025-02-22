package com.example.food_court_ms_small_square.infrastructure.exceptionhandler;

import com.example.food_court_ms_small_square.domain.exception.OwnerInvalid;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExists;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ElementAlreadyExists.class)
    public ResponseEntity<ExceptionResponse> handleRestaurantAlreadyExists(ElementAlreadyExists ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.CONFLICT.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(OwnerInvalid.class)
    public ResponseEntity<ExceptionResponse> handleOwnerInvalid(OwnerInvalid ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
}