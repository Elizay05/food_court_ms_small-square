package com.example.food_court_ms_small_square.infrastructure.output.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Entity
@Data
@Table(name = "restaurant")
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantEntity {
    @Id
    private String nit;
    private String cedulaPropietario;
    private String nombre;
    private String direccion;
    private String telefono;
    private String urlLogo;
}