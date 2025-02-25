package com.example.food_court_ms_small_square.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishResponseDto {
    private String nombre;
    private String descripcion;
    private Float precio;
    private String imagenUrl;
}
