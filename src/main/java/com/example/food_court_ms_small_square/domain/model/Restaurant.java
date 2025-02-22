package com.example.food_court_ms_small_square.domain.model;

public class Restaurant {
    private String nit;
    private String cedulaPropietario;
    private String nombre;
    private String direccion;
    private String telefono;
    private String urlLogo;

    public Restaurant(String nit, String cedulaPropietario, String nombre, String direccion, String telefono, String urlLogo) {
        this.nit = nit;
        this.cedulaPropietario = cedulaPropietario;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.urlLogo = urlLogo;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getCedulaPropietario() {
        return cedulaPropietario;
    }

    public void setCedulaPropietario(String cedulaPropietario) {
        this.cedulaPropietario = cedulaPropietario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }
}
