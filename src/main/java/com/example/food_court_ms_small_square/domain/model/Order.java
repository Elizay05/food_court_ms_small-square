package com.example.food_court_ms_small_square.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private Long id;
    private String idCliente;
    private String nitRestaurante;
    private LocalDateTime fecha;
    private String estado;
    private String idChef;
    private String pin;
    private List<OrderDish> platos;

    public Order(Long id, String idCliente, String nitRestaurante, LocalDateTime fecha, String estado, String idChef, String pin, List<OrderDish> platos) {
        this.id = id;
        this.idCliente = idCliente;
        this.nitRestaurante = nitRestaurante;
        this.fecha = fecha;
        this.estado = estado;
        this.idChef = idChef;
        this.pin = pin;
        this.platos = platos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNitRestaurante() {
        return nitRestaurante;
    }

    public void setNitRestaurante(String nitRestaurante) {
        this.nitRestaurante = nitRestaurante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdChef() {
        return idChef;
    }

    public void setIdChef(String idChef) {
        this.idChef = idChef;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public List<OrderDish> getPlatos() {
        return platos;
    }

    public void setPlatos(List<OrderDish> platos) {
        this.platos = platos;
    }
}
