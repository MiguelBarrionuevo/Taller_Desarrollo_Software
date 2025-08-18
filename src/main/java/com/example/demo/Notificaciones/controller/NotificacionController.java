package com.example.demo.Notificaciones.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Notificaciones.dto.NotificacionDto;

@RestController
public class NotificacionController {

    @GetMapping("/api/notificacion")
    public NotificacionDto getNotificacion() {
        // Aquí devolvemos una notificación "de prueba"
        return new NotificacionDto(120, "Aviso de prueba", "Tienes un nuevo mensaje en tu bandeja");
    }
}
