package com.yuquilema.multi_timerfood.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
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
    private String notas;
    private String sonido = "Default";
    private String sonidoNotificacion = "default"; // NUEVO
    private long tiempoInicioMillis = 0L;



    public Timer() {
    }

    @Ignore
    public Timer(int id, String nombreAlimento, int duracionSegundos, String categoria, String estado, String notas, String sonido) {
        this.id = id;
        this.nombreAlimento = nombreAlimento;
        this.duracionSegundos = duracionSegundos;
        this.categoria = categoria;
        this.estado = estado;
        this.notas = notas;
        this.sonido = sonido;
    }

    // Constructor for creating new timers (id will be generated)
    @Ignore
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

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getSonido() {
        return sonido;
    }

    public void setSonido(String sonido) {
        this.sonido = sonido;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timer timer = (Timer) o;
        return id == timer.id && duracionSegundos == timer.duracionSegundos && Objects.equals(nombreAlimento, timer.nombreAlimento) && Objects.equals(categoria, timer.categoria) && Objects.equals(estado, timer.estado) && Objects.equals(notas, timer.notas) && Objects.equals(sonido, timer.sonido);
    }

    public String getSonidoNotificacion() { return sonidoNotificacion; }
    public void setSonidoNotificacion(String sonidoNotificacion) { this.sonidoNotificacion = sonidoNotificacion; }

    public long getTiempoInicioMillis() { return tiempoInicioMillis; }
    private int segundosRestantes = 0;
    private boolean sonidoActivado = true;
    private boolean vibracionActivada = true;

    public int getSegundosRestantes() { return segundosRestantes; }
    public void setSegundosRestantes(int segundosRestantes) { this.segundosRestantes = segundosRestantes; }

    public boolean isSonidoActivado() { return sonidoActivado; }
    public void setSonidoActivado(boolean sonidoActivado) { this.sonidoActivado = sonidoActivado; }

    public boolean isVibracionActivada() { return vibracionActivada; }
    public void setVibracionActivada(boolean vibracionActivada) { this.vibracionActivada = vibracionActivada; }
    public void setTiempoInicioMillis(long tiempoInicioMillis) { this.tiempoInicioMillis = tiempoInicioMillis; }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreAlimento, duracionSegundos, categoria, estado, notas, sonido);
    }
}
