package com.example.food_court_ms_small_square.infrastructure.exceptionhandler;

import com.example.food_court_ms_small_square.domain.exception.*;
import com.example.food_court_ms_small_square.infrastructure.exception.NoSuchElementException;
import com.example.food_court_ms_small_square.infrastructure.exception.UnauthorizedException;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void test_generic_exception_returns_500() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        Exception testException = new RuntimeException("Test error");

        ResponseEntity<ExceptionResponse> response = handler.handleGlobalException(testException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno en el servidor: Test error", response.getBody().getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
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

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("El plato no existe.", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleInvalidArgumentsException() {
        InvalidArgumentsException exception = new InvalidArgumentsException("Debe proporcionar al menos un campo para actualizar.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleInvalidArgumentsException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe proporcionar al menos un campo para actualizar.", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleUnauthorizedException() {
        UnauthorizedException exception = new UnauthorizedException("No tienes permisos para actualizar un plato en este restaurante");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleUnauthorizedException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("No tienes permisos para actualizar un plato en este restaurante", response.getBody().getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleOrderInProgressException() {
        OrderInProgressException exception = new OrderInProgressException("El cliente ya tiene un pedido en proceso.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOrderInProgressException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El cliente ya tiene un pedido en proceso.", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleOrderNotFoundException() {
        OrderNotFoundException exception = new OrderNotFoundException("Orden no encontrada.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOrderNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Orden no encontrada.", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleOrderAssignmentNotAllowedException() {
        OrderAssignmentNotAllowedException exception = new OrderAssignmentNotAllowedException("No tienes permisos para asignar esta orden.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOrderAssignmentNotAllowedException(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permisos para asignar esta orden.", response.getBody().getMessage());
        assertEquals(HttpStatus.FORBIDDEN.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleInvalidOrderStatusException() {
        InvalidOrderStatusException exception = new InvalidOrderStatusException("La orden ya esta lista.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleInvalidOrderStatusException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("La orden ya esta lista.", response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleInvalidPinException() {
        InvalidPinException exception = new InvalidPinException("El pin proporcionado no coincide con la orden.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleInvalidPinException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El pin proporcionado no coincide con la orden.", response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT.toString(), response.getBody().getStatus());
    }

    @Test
    void shouldHandleOrderAlreadyReadyException() {
        OrderAlreadyReadyException exception = new OrderAlreadyReadyException("La orden ya está lista.");

        ResponseEntity<ExceptionResponse> response = globalExceptionHandler.handleOrderAlreadyReadyException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("La orden ya está lista.", response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT.toString(), response.getBody().getStatus());
    }
}
