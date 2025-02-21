package com.example.food_court_ms_small_square.infrastructure.output.rest;

import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserRestAdapterTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReturnsTrueWhenResponseBodyIsTrue() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate);
        String documentNumber = "12345";
        String url = DomainConstants.URL_VALIDATE_OWNER.replace("{documentNumber}", documentNumber);
        ResponseEntity<Boolean> response = new ResponseEntity<>(true, HttpStatus.OK);

        when(restTemplate.getForEntity(url, Boolean.class)).thenReturn(response);

        boolean result = userRestAdapter.isValidOwner(documentNumber);

        assertTrue(result);
        verify(restTemplate).getForEntity(url, Boolean.class);
    }

    @Test
    public void testReturnsFalseWhenResponseBodyIsFalse() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate);
        String documentNumber = "12345";
        String url = DomainConstants.URL_VALIDATE_OWNER.replace("{documentNumber}", documentNumber);
        ResponseEntity<Boolean> response = new ResponseEntity<>(false, HttpStatus.OK);

        when(restTemplate.getForEntity(url, Boolean.class)).thenReturn(response);

        boolean result = userRestAdapter.isValidOwner(documentNumber);

        assertFalse(result);
        verify(restTemplate).getForEntity(url, Boolean.class);
    }

    @Test
    public void test_returns_false_when_exception_is_thrown() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        UserRestAdapter userRestAdapter = new UserRestAdapter(restTemplate);
        String documentNumber = "12345";
        String url = DomainConstants.URL_VALIDATE_OWNER.replace("{documentNumber}", documentNumber);

        when(restTemplate.getForEntity(url, Boolean.class)).thenThrow(new RuntimeException("Service error"));

        boolean result = userRestAdapter.isValidOwner(documentNumber);

        assertFalse(result);
        verify(restTemplate).getForEntity(url, Boolean.class);
    }
}
