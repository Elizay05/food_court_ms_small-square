package com.example.food_court_ms_small_square.infrastructure.output.rest;

import com.example.food_court_ms_small_square.domain.spi.ITraceabilityPersistencePort;
import com.example.food_court_ms_small_square.domain.util.DomainConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class TraceabilityRestAdapter implements ITraceabilityPersistencePort {

    private final RestTemplate restTemplate;
    private final HttpServletRequest request;

    @Override
    public void saveTraceability(Long idPedido, String idCliente, String nitRestaurante, String idChef, String estado) {
        String url = DomainConstants.URL_SAVE_TRACEABILITY;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", request.getHeader("Authorization"));

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pedidoId", idPedido.toString());
        requestBody.put("clienteId", idCliente);
        requestBody.put("restauranteId", nitRestaurante);
        requestBody.put("chefId", idChef);
        requestBody.put("estado", estado);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la trazabilidad: " + e.getMessage());
        }
    }

    @Override
    public void deleteTraceability(Long idPedido) {
        String url = DomainConstants.URL_DELETE_TRACEABILITY_BY_ORDER_ID.replace("{orderId}", idPedido.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la trazabilidad: " + e.getMessage());
        }
    }
}
