package bo.edu.ucb.microservices.core.notification.ms_notification.repository;

import bo.edu.ucb.microservices.core.notification.ms_notification.model.Notification;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository; // CAMBIO: Import de la versión reactiva
import reactor.core.publisher.Flux; // CAMBIO: Import para múltiples resultados
import reactor.core.publisher.Mono; // CAMBIO: Import para un solo resultado (o ninguno)

// CAMBIO: Se extiende de ReactiveMongoRepository en lugar de MongoRepository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

    // --- 1. Derived Query (Consulta Derivada) ---
    // Spring Data "deriva" la consulta del nombre del método, pero ahora devuelve un 'Flux' o 'Mono'.

    /**
     * Busca todas las notificaciones para un ID de usuario.
     * @param userId El ID del usuario.
     * @return Un Flux que emitirá las notificaciones encontradas.
     */
    Flux<Notification> findByUserId(Integer userId); // CAMBIO: List -> Flux

    /**
     * Busca todas las notificaciones que coincidan con un estado.
     * @param status El estado de la notificación.
     * @return Un Flux que emitirá las notificaciones encontradas.
     */
    Flux<Notification> findByStatus(String status); // CAMBIO: List -> Flux


    // --- 2. Native Query (Consulta Nativa con @Query) ---
    // La sintaxis de @Query es la misma, solo cambia el tipo de retorno.

    /**
     * Busca notificaciones cuyo campo 'mensaje' contenga una palabra clave.
     * @param keyword La palabra a buscar.
     * @return Un Flux que emitirá las notificaciones que coincidan.
     */
    @Query("{'mensaje': { $regex: ?0, $options: 'i' } }")
    Flux<Notification> findByMessageContaining(String keyword); // CAMBIO: List -> Flux


    // --- MÉTODOS POR ID DE NEGOCIO ---

    /**
     * Busca una única notificación por su ID de negocio (el campo notificationId).
     * @param notificationId El ID de negocio.
     * @return Un Mono que emitirá la notificación si se encuentra.
     */
    Mono<Notification> findByNotificationId(int notificationId); // CAMBIO: Optional -> Mono

    /**
     * Elimina una notificación por su ID de negocio.
     * @param notificationId El ID de negocio a eliminar.
     * @return Un Mono<Void> que señala la finalización de la operación de borrado.
     */
    Mono<Void> deleteByNotificationId(int notificationId); // CAMBIO: void -> Mono<Void>
}