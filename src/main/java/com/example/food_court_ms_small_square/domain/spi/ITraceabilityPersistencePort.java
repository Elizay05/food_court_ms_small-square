package com.example.food_court_ms_small_square.domain.spi;

public interface ITraceabilityPersistencePort {
    void saveTraceability(Long idPedido, String idCliente, String nitRestaurante, String idChef, String estado);
    void deleteTraceability(Long idPedido);
}
