package com.example.food_court_ms_small_square.infrastructure.output.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserRestAdapterTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void valid_token_returns_true_when_owner_confirmed() {
        // Arrange
        String documentNumber = "12345";
        String token = "Bearer xyz123";
        String expectedUrl = "http://localhost:8080/api/v1/users/validate-owner/12345";

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(new ResponseEntity<>(true, HttpStatus.OK));

        // Act
        boolean result = userRestAdapter.isValidOwner(documentNumber);

        // Assert
        assertTrue(result);
    }

    @Test
    public void null_token_throws_runtime_exception() {
        // Arrange
        String documentNumber = "12345";
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        when(request.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userRestAdapter.isValidOwner(documentNumber);
        });

        assertEquals("No se encontró un token de autenticación válido", exception.getMessage());
    }

    @Test
    public void empty_token_throws_runtime_exception() {
        // Arrange
        String documentNumber = "12345";

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        when(request.getHeader("Authorization")).thenReturn("");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userRestAdapter.isValidOwner(documentNumber);
        });
    }

    @Test
    public void generic_exception_returns_false() {
        // Arrange
        String documentNumber = "12345";
        String token = "Bearer xyz123";
        String expectedUrl = "http://localhost:8080/api/v1/users/validate-owner/12345";

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenThrow(new RuntimeException("Generic exception"));

        // Act
        boolean result = userRestAdapter.isValidOwner(documentNumber);

        // Assert
        assertFalse(result);
    }

    @Test
    public void null_response_body_returns_false() {
        // Arrange
        String documentNumber = "12345";
        String token = "Bearer xyz123";
        String expectedUrl = "http://localhost:8080/api/v1/users/validate-owner/12345";

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(restTemplate.exchange(
                eq(expectedUrl),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Boolean.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        // Act
        boolean result = userRestAdapter.isValidOwner(documentNumber);

        // Assert
        assertFalse(result);
    }

    @Test
    public void test_update_nit_success() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        String documentNumber = "123456789";
        String nitRestaurant = "987654321";
        String token = "Bearer token123";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Act & Assert
        assertDoesNotThrow(() -> userRestAdapter.updateNit(documentNumber, nitRestaurant));

        verify(restTemplate).exchange(
                contains(documentNumber),
                eq(HttpMethod.PUT),
                argThat(entity -> entity.getHeaders().getFirst("Authorization").equals(token)),
                eq(Void.class)
        );
    }

    @Test
    public void test_update_nit_no_token() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        String documentNumber = "123456789";
        String nitRestaurant = "987654321";

        when(request.getHeader("Authorization")).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userRestAdapter.updateNit(documentNumber, nitRestaurant));

        assertEquals("No se encontró un token de autenticación válido", exception.getMessage());

        verify(restTemplate, never()).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        );
    }


    @Test
    public void test_update_nit_user_not_found() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        String documentNumber = "123456789";
        String nitRestaurant = "987654321";
        String token = "Bearer token123";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Usuario no encontrado", HttpHeaders.EMPTY, null, null));

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userRestAdapter.updateNit(documentNumber, nitRestaurant));

        assertEquals("No se encontró un usuario con la cédula proporcionada.", exception.getMessage());

        verify(restTemplate).exchange(
                contains(documentNumber),
                eq(HttpMethod.PUT),
                argThat(entity -> entity.getHeaders().getFirst("Authorization").equals(token)),
                eq(Void.class)
        );
    }

    @Test
    public void test_update_nit_handles_generic_rest_client_exception() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate, request);

        String documentNumber = "123456789";
        String nitRestaurant = "987654321";
        String token = "Bearer token123";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Void.class)
        )).thenThrow(new RuntimeException("Generic REST client exception"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userRestAdapter.updateNit(documentNumber, nitRestaurant)
        );

        assertEquals("Error al actualizar el NIT en el microservicio de usuarios: Generic REST client exception", exception.getMessage());
    }
}
