package com.example.food_court_ms_small_square.infrastructure.output.rest;

import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class UserRestAdapter implements IUserValidationPersistencePort {

    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Override
    public boolean isValidOwner(String documentNumber) {
        String url = DomainConstants.URL_VALIDATE_OWNER.replace("{documentNumber}", documentNumber);

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("No se encontró un token de autenticación válido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.GET, entity, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void updateNit (String documentNumber, String nitRestaurant){
        String url = DomainConstants.URL_UPDATE_NIT
                .replace("{documentNumber}", documentNumber)
                .replace("{nitRestaurant}", nitRestaurant);

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("No se encontró un token de autenticación válido");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.PUT, entity, Void.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("No se encontró un usuario con la cédula proporcionada.");

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el NIT en el microservicio de usuarios: " + e.getMessage());
        }
    }
}
