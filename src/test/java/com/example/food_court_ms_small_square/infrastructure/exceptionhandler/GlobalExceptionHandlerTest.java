package com.example.food_court_ms_small_square.infrastructure.exceptionhandler;

import com.example.food_court_ms_small_square.domain.exception.InvalidArgumentsException;
import com.example.food_court_ms_small_square.domain.exception.OwnerInvalidException;
import com.example.food_court_ms_small_square.domain.exception.ElementAlreadyExistsException;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() {
        FieldError fieldError = new FieldError("objectName", "nombre", "The restaurant name cannot be only numbers");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("The restaurant name cannot be only numbers", response.getBody().get("nombre"));
    }

    @Test
    void shouldHandleElementAlreadyExistsException() {
        ElementAlreadyExistsException exception = new ElementAlreadyExistsException("The restaurant already exists");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleRestaurantAlreadyExists(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The restaurant already exists", response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleOwnerInvalidException() {
        OwnerInvalidException exception = new OwnerInvalidException("The owner is not valid.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOwnerInvalid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The owner is not valid.", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleNoSuchElementException() {
        NoSuchElementException exception = new NoSuchElementException("El plato no existe.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleNoSuchElementException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El plato no existe.", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleInvalidArgumentsException() {
        InvalidArgumentsException exception = new InvalidArgumentsException("Debe proporcionar al menos un campo para actualizar.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleInvalidArgumentsException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe proporcionar al menos un campo para actualizar.", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().getStatus());
    }
}
