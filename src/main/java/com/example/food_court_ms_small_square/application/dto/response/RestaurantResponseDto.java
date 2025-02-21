package com.example.food_court_ms_small_square.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantResponseDto {
    private String nit;
    private String nombre;
    private String urlLogo;
}

