package com.example.food_court_ms_small_square.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "dish")
@AllArgsConstructor
@NoArgsConstructor
public class DishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String descripcion;

    private Float precio;

    private String restauranteNit;

    private String imagenUrl;

    private Boolean activo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoryEntity categoria;
}
