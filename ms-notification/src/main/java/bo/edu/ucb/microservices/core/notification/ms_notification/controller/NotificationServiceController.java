package bo.edu.ucb.microservices.core.notification.ms_notification.controller;

import bo.edu.ucb.microservices.core.notification.ms_notification.services.NotificationService;
import bo.edu.ucb.microservices.dto.notification.NotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/v1/notification")
@Tag(name = "Notification", description = "REST API para notificaciones")
public class NotificationServiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceController.class);

    private final NotificationService notificationService;

    @Autowired
    public NotificationServiceController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // --- ENDPOINTS QUE OPERAN CON EL ID DE NEGOCIO (int) ---

    @Operation(summary = "Crea una nueva notificación usando su ID de negocio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<NotificationDto> createNotification(@Valid @RequestBody NotificationDto dto) {
        LOGGER.info("POST /notification, creando notificación con ID de negocio: {}", dto.getNotificationId());
        return notificationService.createNotification(dto);
    }

    @Operation(summary = "Obtiene una notificación por su ID de negocio (int)")
    @GetMapping(value = "/{notificationId}", produces = "application/json")
    public Mono<NotificationDto> getNotificationByBusinessId(
            @Parameter(description = "ID de negocio de la notificación", required = true, example = "101")
            @PathVariable("notificationId") int notificationId) {
        LOGGER.info("GET /notification/{}", notificationId);
        return notificationService.getNotification(notificationId);
    }

    @Operation(summary = "Actualiza una notificación por su ID de negocio (int)")
    @PutMapping(value = "/{notificationId}", consumes = "application/json", produces = "application/json")
    public Mono<NotificationDto> updateNotificationByBusinessId(
            @PathVariable int notificationId, @RequestBody NotificationDto dto) {
        LOGGER.info("PUT /notification/{}, actualizando con: {}", notificationId, dto);
        return notificationService.updateNotification(notificationId, dto);
    }

    @Operation(summary = "Elimina una notificación por su ID de negocio (int)")
    @DeleteMapping(value = "/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteNotificationByBusinessId(@PathVariable("notificationId") int notificationId) {
        LOGGER.info("DELETE /notification/{}", notificationId);
        return notificationService.deleteNotification(notificationId);
    }


    // --- ENDPOINTS QUE OPERAN CON EL ID DE LA BASE DE DATOS (String de MongoDB) ---

    @Operation(summary = "Obtiene una notificación por su ID de base de datos (String)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    // Se añade /db/ para diferenciarlo del endpoint que usa un 'int'
    @GetMapping(value = "/db/{mongoId}", produces = "application/json")
    public Mono<NotificationDto> getNotificationByMongoId(
            @Parameter(description = "ID de la notificación generado por MongoDB", required = true, example = "6724b1a434c3a3795388a10d")
            @PathVariable("mongoId") String mongoId
    ) {
        LOGGER.info("GET /notification/db/{}", mongoId);
        // Se necesita un nuevo método en el servicio que opere con el String id
        return notificationService.getNotificationByMongoId(mongoId);
    }

    @Operation(summary = "Actualiza una notificación por su ID de base de datos (String)")
    @PutMapping(value = "/db/{mongoId}", consumes = "application/json", produces = "application/json")
    public Mono<NotificationDto> updateNotificationByMongoId(
            @PathVariable String mongoId, @RequestBody NotificationDto dto) {
        LOGGER.info("PUT /notification/db/{}, actualizando con: {}", mongoId, dto);
        // Se necesita un nuevo método en el servicio
        return notificationService.updateNotificationByMongoId(mongoId, dto);
    }

    @Operation(summary = "Elimina una notificación por su ID de base de datos (String)")
    @DeleteMapping(value = "/db/{mongoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteNotificationByMongoId(@PathVariable("mongoId") String mongoId) {
        LOGGER.info("DELETE /notification/db/{}", mongoId);
        // Se necesita un nuevo método en el servicio
        return notificationService.deleteNotificationByMongoId(mongoId);
    }
}