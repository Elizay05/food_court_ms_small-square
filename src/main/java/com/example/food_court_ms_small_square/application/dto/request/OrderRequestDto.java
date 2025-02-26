package com.example.food_court_ms_small_square.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private String nitRestaurante;
    private List<OrderDishRequestDto> platos;
}
