package com.example.food_court_ms_small_square.infrastructure.output.rest;



import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageRestAdapterTest {

    @Test
    public void test_send_message_success() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        MessageRestAdapter messageRestAdapter = new MessageRestAdapter(restTemplate, request);

        String phoneNumber = "1234567890";
        String message = "Test message";
        String authHeader = "Bearer token";

        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // Act
        messageRestAdapter.sendOrderReadyMessage(phoneNumber, message);

        // Assert
        verify(restTemplate).postForEntity(
                eq(DomainConstants.URL_SEND_ORDER_READY_MESSAGE),
                argThat(entity -> {
                    HttpEntity<Map<String, String>> httpEntity = (HttpEntity<Map<String, String>>) entity;
                    Map<String, String> body = httpEntity.getBody();
                    return body.get("phoneNumber").equals(phoneNumber) &&
                            body.get("message").equals(message) &&
                            httpEntity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON) &&
                            httpEntity.getHeaders().get("Authorization").contains(authHeader);
                }),
                eq(Void.class)
        );
    }

    @Test
    public void test_send_message_null_phone() {
        // Arrange
        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        MessageRestAdapter messageRestAdapter = new MessageRestAdapter(restTemplate, request);

        String phoneNumber = null;
        String message = "Test message";

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(restTemplate.postForEntity(
                eq(DomainConstants.URL_SEND_ORDER_READY_MESSAGE),
                any(),
                eq(Void.class)
        )).thenThrow(new RuntimeException("Invalid phone number"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            messageRestAdapter.sendOrderReadyMessage(phoneNumber, message);
        });

        verify(restTemplate).postForEntity(
                eq(DomainConstants.URL_SEND_ORDER_READY_MESSAGE),
                any(),
                eq(Void.class)
        );
    }
}
