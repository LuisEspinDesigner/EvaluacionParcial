package com.example.evaluacionparcial;

import java.io.Serializable;

public class Pais implements Serializable {
    String nombre;
    String capital;
    String Telf;
    String center;
    String url;
    String coordenadas;

    public String getTelf() {
        return Telf;
    }

    public void setTelf(String telf) {
        Telf = telf;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getCapital() {
        return capital;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
