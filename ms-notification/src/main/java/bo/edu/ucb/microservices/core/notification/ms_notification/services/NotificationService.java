package bo.edu.ucb.microservices.core.notification.ms_notification.services;

import bo.edu.ucb.microservices.core.notification.ms_notification.mapper.NotificationMapper;
import bo.edu.ucb.microservices.core.notification.ms_notification.model.Notification;
import bo.edu.ucb.microservices.core.notification.ms_notification.repository.NotificationRepository;
import bo.edu.ucb.microservices.dto.notification.NotificationDto;
import bo.edu.ucb.microservices.util.exceptions.InvalidInputException;
import bo.edu.ucb.microservices.util.exceptions.NotFoundException;
import bo.edu.ucb.microservices.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final ServiceUtil serviceUtil;
    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    @Autowired
    public NotificationService(ServiceUtil serviceUtil, NotificationRepository repository, NotificationMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Crea una notificación de forma reactiva.
     * Retorna un Mono<NotificationDto> que emitirá la notificación creada.
     */
    public Mono<NotificationDto> createNotification(NotificationDto notificationDto) {
        if (notificationDto.getNotificationId() < 1) {
            // Se lanza la excepción directamente porque es una validación síncrona.
            throw new InvalidInputException("ID de notificación inválido: " + notificationDto.getNotificationId());
        }

        Notification entity = mapper.dtoToEntity(notificationDto);

        // El pipeline reactivo comienza aquí.
        return repository.save(entity)
                .log(LOGGER.getName(), Level.FINE)
                // Transforma un error de BD en una excepción de dominio.
                .onErrorMap(DuplicateKeyException.class,
                        ex -> new InvalidInputException("Llave duplicada, Notification Id: " + notificationDto.getNotificationId()))
                // Mapea la entidad guardada de vuelta a un DTO.
                .map(mapper::entityToDto);
    }

    /**
     * Obtiene una notificación por su ID de negocio (int) de forma reactiva.
     */
    public Mono<NotificationDto> getNotification(int notificationId) {
        if (notificationId < 1) {
            throw new InvalidInputException("ID de notificación inválido: " + notificationId);
        }

        return repository.findByNotificationId(notificationId)
                // Si el repositorio no emite nada (vacío), se lanza un error de 'NotFoundException'.
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró notificación para notificationId: " + notificationId)))
                .log(LOGGER.getName(), Level.FINE)
                .map(mapper::entityToDto)
                // Se usa un helper para añadir la dirección del servicio, como en el ejemplo.
                .map(this::setServiceAddress);
    }

    /**
     * Actualiza una notificación por su ID de negocio (int) de forma reactiva.
     */
    public Mono<NotificationDto> updateNotification(int notificationId, NotificationDto notificationDto) {
        if (notificationId < 1) {
            throw new InvalidInputException("ID de notificación inválido: " + notificationId);
        }

        return repository.findByNotificationId(notificationId)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró notificación para notificationId: " + notificationId)))
                .flatMap(entity -> {
                    mapper.updateEntityFromDto(notificationDto, entity);
                    return repository.save(entity);
                })
                .log(LOGGER.getName(), Level.FINE)
                .map(mapper::entityToDto);
    }

    /**
     * Elimina una notificación por su ID de negocio (int) de forma reactiva.
     * Retorna Mono<Void> para indicar que la operación ha terminado.
     */
    public Mono<Void> deleteNotification(int notificationId) {
        if (notificationId < 1) {
            throw new InvalidInputException("ID de notificación inválido: " + notificationId);
        }

        LOGGER.debug("deleteNotification: eliminando notificación con ID de negocio: {}", notificationId);

        // Se busca la entidad y luego se pasa al método de borrado del repositorio.
        // .flatMap(repository::delete) es un atajo para .flatMap(entity -> repository.delete(entity))
        return repository.findByNotificationId(notificationId)
                .log(LOGGER.getName(), Level.FINE)
                .flatMap(repository::delete);
    }

    /**
     * Método helper para añadir la dirección del servicio al DTO.
     */
    private NotificationDto setServiceAddress(NotificationDto dto) {
        dto.setServiceAddress(serviceUtil.getServiceAddress());
        return dto;
    }

    /**
     * Obtiene una notificación por su ID de MongoDB (String) de forma reactiva.
     */
    public Mono<NotificationDto> getNotificationByMongoId(String mongoId) {
        return repository.findById(mongoId)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró notificación para el ID de BD: " + mongoId)))
                .map(mapper::entityToDto)
                .map(this::setServiceAddress);
    }

    /**
     * Actualiza una notificación por su ID de MongoDB (String) de forma reactiva.
     */
    public Mono<NotificationDto> updateNotificationByMongoId(String mongoId, NotificationDto notificationDto) {
        return repository.findById(mongoId)
                .switchIfEmpty(Mono.error(new NotFoundException("No se encontró notificación para el ID de BD: " + mongoId)))
                .flatMap(entity -> {
                    // Usamos el método de actualización del mapper que ya tenías
                    mapper.updateEntityFromDto(notificationDto, entity);
                    return repository.save(entity);
                })
                .map(mapper::entityToDto);
    }

    /**
     * Elimina una notificación por su ID de MongoDB (String) de forma reactiva.
     */
    public Mono<Void> deleteNotificationByMongoId(String mongoId) {
        LOGGER.debug("deleteNotificationByMongoId: eliminando notificación con ID de BD: {}", mongoId);
        // deleteById es un método que ya viene en ReactiveMongoRepository
        return repository.deleteById(mongoId);
    }
}