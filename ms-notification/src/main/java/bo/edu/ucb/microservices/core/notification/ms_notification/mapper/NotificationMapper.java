package bo.edu.ucb.microservices.core.notification.ms_notification.mapper;

import bo.edu.ucb.microservices.core.notification.ms_notification.model.Notification;
import bo.edu.ucb.microservices.dto.notification.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget; // Import necesario

// 1. @Mapper(componentModel = "spring") le dice a MapStruct que genere una implementación
//    de esta interfaz y que la convierta en un Bean de Spring para poder inyectarla en otros servicios.
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    // 2. Mapeo de Entidad (BD) a DTO (lo que se envía al exterior).
    @Mappings({
            // Ignoramos el campo 'serviceAddress' porque no viene de la base de datos,
            // se suele añadir en la capa de servicio.
            @Mapping(target = "serviceAddress", ignore = true)
    })
    NotificationDto entityToDto(Notification entity);

    // 3. Mapeo de DTO (lo que recibimos del exterior) a Entidad (BD).
    @Mappings({
            // Ignoramos campos controlados por la base de datos.
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            // Ignoramos campos que no vienen en el DTO de creación,
            // sino que se asignan internamente en la lógica de negocio.
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "creationDate", ignore = true)
    })
    Notification dtoToEntity(NotificationDto dto);

    // --- NUEVO ---
    // 4. Mapeo para actualizar una Entidad existente desde un DTO.
    // La anotación @MappingTarget le dice a MapStruct que no cree una nueva instancia de Notification,
    // sino que actualice la que le pasamos como parámetro.
    @Mappings({
            // Ignoramos los campos que no deben ser modificados por el usuario durante una actualización.
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "userId", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "creationDate", ignore = true),
            // El notificationId tampoco debería cambiar, ya que es el identificador de negocio.
            @Mapping(target = "notificationId", ignore = true)
    })
    void updateEntityFromDto(NotificationDto dto, @MappingTarget Notification entity);
}