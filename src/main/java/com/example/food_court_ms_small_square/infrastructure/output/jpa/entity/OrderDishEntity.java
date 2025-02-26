package com.example.food_court_ms_small_square.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order_dish")
@AllArgsConstructor
@NoArgsConstructor
public class OrderDishEntity {

    @EmbeddedId
    private OrderDishPk id;

    @ManyToOne
    @MapsId("idOrden")
    @JoinColumn(name = "orden_id")
    private OrderEntity orden;

    @ManyToOne
    @MapsId("idPlato")
    @JoinColumn(name = "plato_id")
    private DishEntity plato;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;
}
