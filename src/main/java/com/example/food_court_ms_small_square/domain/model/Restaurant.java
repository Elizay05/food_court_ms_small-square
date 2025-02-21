package com.example.food_court_ms_small_square.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Restaurant {
    private String nit;
    private String cedulaPropietario;
    private String nombre;
    private String direccion;
    private String telefono;
    private String urlLogo;
}
