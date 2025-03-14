package com.example.food_court_ms_small_square.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantRequestDto {

    @NotNull
    @NotBlank
    @Pattern(regexp = "\\d+", message = "El NIT debe contener solo números")
    private String nit;

    @NotNull
    @NotBlank
    private String cedulaPropietario;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?!\\d+$)[\\w\\s]+$", message = "El nombre del restaurante no puede ser solo números")
    private String nombre;

    @NotNull
    @NotBlank
    private String direccion;

    @NotNull
    @NotBlank
    @Size(max = 13)
    @Pattern(regexp = "\\+?\\d+", message = "El celular debe contener un máximo de 13 caracteres y puede contener el símbolo +")
    private String telefono;

    @NotNull
    @NotBlank
    private String urlLogo;
}