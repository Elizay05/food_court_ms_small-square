package com.example.food_court_ms_small_square.domain.model;

public class Dish {
    private Long id;
    private String nombre;
    private Category categoria;
    private String descripcion;
    private Float precio;
    private String restauranteNit;
    private String imagenUrl;
    private Boolean activo;

    public Dish(Long id, String nombre, Category categoria, String descripcion, Float precio, String restauranteNit, String imagenUrl, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.restauranteNit = restauranteNit;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Category getCategoria() {
        return categoria;
    }

    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public String getRestauranteNit() {
        return restauranteNit;
    }

    public void setRestauranteNit(String restauranteNit) {
        this.restauranteNit = restauranteNit;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}

