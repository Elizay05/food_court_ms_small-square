package com.example.food_court_ms_small_square.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String idCliente;
    private String nitRestaurante;
    private LocalDateTime fecha;
    private String estado;
    private String idChef;
    private List<OrderDishResponseDto> platos;
}
