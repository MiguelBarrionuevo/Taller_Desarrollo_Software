package com.example.demo.Notificaciones.dto;

public class NotificacionDto {
    private int id;
    private String titulo;
    private String mensaje;

    // Constructor
    public NotificacionDto(int id, String titulo, String mensaje) {
        this.id = id;
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
