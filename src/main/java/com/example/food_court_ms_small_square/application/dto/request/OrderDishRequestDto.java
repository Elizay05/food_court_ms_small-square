package com.example.food_court_ms_small_square.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishRequestDto {
    private Long idPlato;
    private int cantidad;
}
