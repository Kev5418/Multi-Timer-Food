package com.yuquilema.multi_timerfood.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "timers")
public class Timer {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    private String nombreAlimento;
    private int duracionSegundos;
    private String categoria;
    private String estado = "IDLE";

    public Timer() {
    }

    public Timer(int id, String nombreAlimento, int duracionSegundos, String categoria, String estado) {
        this.id = id;
        this.nombreAlimento = nombreAlimento;
        this.duracionSegundos = duracionSegundos;
        this.categoria = categoria;
        this.estado = estado;
    }

    // Constructor for creating new timers (id will be generated)
    public Timer(String nombreAlimento, int duracionSegundos, String categoria) {
        this.nombreAlimento = nombreAlimento;
        this.duracionSegundos = duracionSegundos;
        this.categoria = categoria;
        this.estado = "IDLE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreAlimento() {
        return nombreAlimento;
    }

    public void setNombreAlimento(String nombreAlimento) {
        this.nombreAlimento = nombreAlimento;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(int duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timer timer = (Timer) o;
        return id == timer.id && duracionSegundos == timer.duracionSegundos && Objects.equals(nombreAlimento, timer.nombreAlimento) && Objects.equals(categoria, timer.categoria) && Objects.equals(estado, timer.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreAlimento, duracionSegundos, categoria, estado);
    }
}
