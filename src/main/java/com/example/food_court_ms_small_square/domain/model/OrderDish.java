package com.example.food_court_ms_small_square.domain.model;

public class OrderDish {
    private Long idPlato;
    private int cantidad;

    public OrderDish(Long idPlato, int cantidad) {
        this.idPlato = idPlato;
        this.cantidad = cantidad;
    }

    public Long getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(Long idPlato) {
        this.idPlato = idPlato;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
