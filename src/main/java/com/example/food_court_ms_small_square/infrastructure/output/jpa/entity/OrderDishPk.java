package com.example.food_court_ms_small_square.infrastructure.output.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishPk implements Serializable {
    private Long idOrden;
    private Long idPlato;
}
