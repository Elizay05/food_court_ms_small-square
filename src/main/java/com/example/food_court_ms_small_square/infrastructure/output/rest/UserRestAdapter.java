package com.example.food_court_ms_small_square.infrastructure.output.rest;

import com.example.food_court_ms_small_square.domain.spi.IUserValidationPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class UserRestAdapter implements IUserValidationPersistencePort {

    private final RestTemplate restTemplate;

    @Override
    public boolean isValidOwner(String documentNumber) {
        String url = DomainConstants.URL_VALIDATE_OWNER.replace("{documentNumber}", documentNumber);

        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }


}
