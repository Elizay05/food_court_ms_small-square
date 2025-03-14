package com.example.food_court_ms_small_square.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishRequestDto {
    @NotNull(message = "El nombre del plato no puede ser nulo")
    @NotEmpty(message = "El nombre del plato no puede estar vacío")
    private String nombre;

    @NotNull(message = "El precio del plato no puede ser nulo")
    @Positive(message = "El precio del plato debe ser un número entero positivo mayor a 0")
    private Float precio;

    @NotNull(message = "La descripción no puede ser nula")
    @NotEmpty(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "La URL de la imagen no puede ser nula")
    @NotEmpty(message = "La URL de la imagen no puede estar vacía")
    private String imagenUrl;

    @NotNull(message = "La categoría no puede ser nula")
    private Integer categoria;

    @NotNull(message = "El restaurante no puede ser nulo")
    private String restauranteNit;
}