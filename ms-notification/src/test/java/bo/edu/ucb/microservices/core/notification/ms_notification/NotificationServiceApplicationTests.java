package bo.edu.ucb.microservices.core.notification.ms_notification;

import bo.edu.ucb.microservices.core.notification.ms_notification.repository.NotificationRepository;
import bo.edu.ucb.microservices.dto.notification.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers // Habilita el uso de Testcontainers en esta clase
class NotificationServiceApplicationTests {

    // 1. Inicia un contenedor de MongoDB antes de que comiencen los tests
    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8");

    // 2. Configura Spring dinámicamente para que se conecte a la base de datos del contenedor
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WebTestClient client;

    @Autowired
    private NotificationRepository repository;

    // 3. Antes de CADA test, se asegura de que la base de datos esté vacía
    @BeforeEach
    void setupDb() {
        repository.deleteAll().block();
    }

    private static final int NOTIFICATION_ID_OK = 101;
    private static final int NOTIFICATION_ID_NOT_FOUND = 999;

    @Test
    void getNotification_OK() {
        // ARRANGE: Primero creamos una notificación para poder buscarla
        NotificationDto createdNotification = createTestNotification(NOTIFICATION_ID_OK);

        // ACT & ASSERT: Ahora sí podemos buscar el ID y esperar una respuesta OK
        client.get()
                .uri("/v1/notification/" + NOTIFICATION_ID_OK)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.notificationId").isEqualTo(createdNotification.getNotificationId())
                .jsonPath("$.titulo").isEqualTo(createdNotification.getTitulo());
    }

    @Test
    void getNotification_NotFound() {
        client.get()
                .uri("/v1/notification/" + NOTIFICATION_ID_NOT_FOUND)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("No se encontró notificación para notificationId: " + NOTIFICATION_ID_NOT_FOUND);
    }

    @Test
    void createNotification_OK() {
        NotificationDto request = new NotificationDto(NOTIFICATION_ID_OK, "Test desde Post", "Mensaje de prueba");

        client.post()
                .uri("/v1/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated() // 201 Created es el código correcto para la creación
                .expectBody()
                .jsonPath("$.notificationId").isEqualTo(NOTIFICATION_ID_OK)
                .jsonPath("$.titulo").isEqualTo("Test desde Post");
    }

    @Test
    void updateNotification_OK() {
        // ARRANGE: Primero creamos la notificación que vamos a actualizar
        createTestNotification(NOTIFICATION_ID_OK);

        // Creamos el DTO con los datos actualizados
        NotificationDto updatedDto = new NotificationDto(NOTIFICATION_ID_OK, "Título Actualizado", "Mensaje Actualizado");

        // ACT & ASSERT: Enviamos la petición PUT y verificamos la respuesta
        client.put()
                .uri("/v1/notification/" + NOTIFICATION_ID_OK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedDto)
                .exchange()
                .expectStatus().isOk() // La actualización exitosa devuelve 200 OK
                .expectBody()
                .jsonPath("$.titulo").isEqualTo("Título Actualizado");
    }

    @Test
    void deleteNotification_OK() {
        // ARRANGE: Creamos la notificación que vamos a eliminar
        createTestNotification(NOTIFICATION_ID_OK);

        // ACT & ASSERT
        client.delete()
                .uri("/v1/notification/" + NOTIFICATION_ID_OK)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent(); // 204 No Content es el código para un borrado exitoso
    }

    /**
     * Método de ayuda para crear una notificación de prueba reutilizable.
     */
    private NotificationDto createTestNotification(int notificationId) {
        NotificationDto testDto = new NotificationDto(notificationId, "Título de prueba", "Mensaje de prueba");
        return client.post()
                .uri("/v1/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testDto)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(NotificationDto.class)
                .getResponseBody()
                .blockFirst(); // blockFirst() es aceptable en tests para obtener el resultado
    }
}