package com.example.food_court_ms_small_square.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDishRequestDto {
    @NotNull(message = "El id del plato no puede ser nulo")
    private Integer id;
    @Positive(message = "El precio del plato debe ser un número entero positivo mayor a 0")
    private Float price;
    private String description;
}
