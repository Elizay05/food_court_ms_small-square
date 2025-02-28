package com.example.food_court_ms_small_square.infrastructure.output.rest;

import com.example.food_court_ms_small_square.domain.spi.ITraceabilityPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TraceabilityRestAdapter implements ITraceabilityPersistencePort {

    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Override
    public void saveTraceability(Long idPedido, String idCliente, String estado) {
        String url = DomainConstants.URL_SAVE_TRACEABILITY;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", request.getHeader("Authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pedidoId", idPedido.toString());
        requestBody.put("clienteId", idCliente);
        requestBody.put("estado", estado);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la trazabilidad: " + e.getMessage());
        }
    }
}
