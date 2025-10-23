package bo.edu.ucb.microservices.core.notification.ms_notification.config;

import bo.edu.ucb.microservices.core.notification.ms_notification.controller.NotificationServiceController;
import bo.edu.ucb.microservices.dto.notification.NotificationDto;
import bo.edu.ucb.microservices.util.events.Event;
import bo.edu.ucb.microservices.util.exceptions.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final NotificationServiceController notificationServiceController;

    @Autowired
    public MessageProcessorConfig(NotificationServiceController notificationServiceController) {
        this.notificationServiceController = notificationServiceController;
    }

    @Bean("messageProcessor")
    public Function<Flux<Event<Integer, NotificationDto>>, Flux<Void>> messageProcessor() {
        return events -> events.flatMap(event -> {
            LOGGER.info("Procesando evento para la clave: {}", event.getKey());

            try {
                switch (event.getEventType()) {
                    case CREATE:
                        return notificationServiceController.createNotification(event.getData())
                                .doOnNext(dto -> LOGGER.info("Notificación creada con ID: {}", dto.getNotificationId()))
                                .then(); // Mono<Void> -> Flux<Void> mediante flatMap

                    case DELETE:
                        return notificationServiceController.deleteNotificationByBusinessId(event.getKey())
                                .doOnSuccess(v -> LOGGER.info("Notificación eliminada con ID: {}", event.getKey()))
                                .then();

                    default:
                        String errorMessage = "Tipo de evento incorrecto: " + event.getEventType() + ", se espera CREATE o DELETE";
                        LOGGER.warn(errorMessage);
                        return Flux.error(new EventProcessingException(errorMessage));
                }
            } catch (Exception e) {
                LOGGER.error("Error procesando evento: {}", event.getKey(), e);
                return Flux.error(e);
            }
        });
    }
}

