package com.example.food_court_ms_small_square.infrastructure.output.rest;

import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TraceabilityRestAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TraceabilityRestAdapter adapter;

    @Test
    public void test_save_traceability_success() {
        Long idPedido = 123L;
        String idCliente = "456";
        String nitRestaurante = "1234";
        String idChef = "123";
        String estado = "PENDIENTE";
        String authToken = "Bearer token123";

        when(request.getHeader("Authorization")).thenReturn(authToken);

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        expectedHeaders.set("Authorization", authToken);

        Map<String, String> expectedBody = new HashMap<>();
        expectedBody.put("pedidoId", "123");
        expectedBody.put("clienteId", "456");
        expectedBody.put("restauranteId", "1234");
        expectedBody.put("chefId", "123");
        expectedBody.put("estado", "PENDIENTE");

        HttpEntity<Map<String, String>> expectedEntity = new HttpEntity<>(expectedBody, expectedHeaders);

        // Act
        adapter.saveTraceability(idPedido, idCliente, nitRestaurante, idChef, estado);

        // Assert
        verify(restTemplate).postForEntity(
                DomainConstants.URL_SAVE_TRACEABILITY,
                expectedEntity,
                Void.class
        );
    }

    @Test
    public void test_save_traceability_service_timeout() {
        // Arrange
        Long idPedido = 123L;
        String idCliente = "456";
        String nitRestaurante = "1234";
        String idChef = "123";
        String estado = "PENDIENTE";
        String authToken = "Bearer token123";

        when(request.getHeader("Authorization")).thenReturn(authToken);

        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        expectedHeaders.set("Authorization", authToken);

        Map<String, String> expectedBody = new HashMap<>();
        expectedBody.put("pedidoId", "123");
        expectedBody.put("clienteId", "456");
        expectedBody.put("restauranteId", "1234");
        expectedBody.put("chefId", "123");
        expectedBody.put("estado", "PENDIENTE");

        HttpEntity<Map<String, String>> expectedEntity = new HttpEntity<>(expectedBody, expectedHeaders);

        when(restTemplate.postForEntity(DomainConstants.URL_SAVE_TRACEABILITY, expectedEntity, Void.class))
                .thenThrow(new RuntimeException("Timeout"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adapter.saveTraceability(idPedido, idCliente, nitRestaurante, idChef, estado);
        });
    }
}
