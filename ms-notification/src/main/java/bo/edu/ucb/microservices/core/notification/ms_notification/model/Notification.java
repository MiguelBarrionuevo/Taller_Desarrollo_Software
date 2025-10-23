package bo.edu.ucb.microservices.core.notification.ms_notification.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

// 1. @Document le dice a Spring que esta clase representa un documento en la colección "notifications" de MongoDB.
@Document(collection = "notifications")
public class Notification {

    // 2. @Id marca este campo como la clave primaria del documento. MongoDB lo generará automáticamente.
    @Id
    private String id;

    // 3. @Version se usa para el "optimistic locking", una técnica para evitar conflictos si
    //    dos usuarios intentan modificar la misma notificación a la vez. Spring lo maneja por ti.
    @Version
    private Integer version;

    // 4. @Indexed(unique = true) crea un índice en este campo y asegura que no puede haber
    //    dos notificaciones con el mismo 'notificationId'. Es nuestro identificador de negocio.
    @Indexed(unique = true)
    private int notificationId;

    // --- Campos de tu NotificationDto ---
    private String titulo;
    private String mensaje;

    // --- Campos adicionales útiles para un sistema de notificación ---
    private Integer userId; // Para saber a qué usuario pertenece la notificación
    private String status;  // Podría ser "ENVIADO", "LEIDO", "ARCHIVADO", etc.
    private Date creationDate; // Para saber cuándo se creó

    // --- Constructores ---
    public Notification() {
    }

    public Notification(int notificationId, String titulo, String mensaje, Integer userId, String status, Date creationDate) {
        this.notificationId = notificationId;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.userId = userId;
        this.status = status;
        this.creationDate = creationDate;
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}