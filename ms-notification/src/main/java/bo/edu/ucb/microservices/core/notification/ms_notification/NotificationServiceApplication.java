package bo.edu.ucb.microservices.core.notification.ms_notification;

import bo.edu.ucb.microservices.core.notification.ms_notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations; // CAMBIO: Import reactivo
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations; // CAMBIO: Import reactivo
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

@SpringBootApplication
@ComponentScan({
        "bo.edu.ucb.microservices.core.notification.ms_notification",
        "bo.edu.ucb.microservices.util"
})
public class NotificationServiceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(NotificationServiceApplication.class, args);

        // CAMBIO: Se simplifica el log para que sea igual al de la ingeniera
        String mongoHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
        String mongoPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
        LOGGER.info("---- Connected to MongoDb: {}:{} ----", mongoHost, mongoPort);
    }

    // CAMBIO: Se inyecta la versión REACTIVA de MongoOperations
    @Autowired
    ReactiveMongoOperations mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {
        LOGGER.info("---- Ensuring MongoDB indexes are created ----");

        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate
                .getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        // CAMBIO: Se usa la versión REACTIVA de IndexOperations y se llama a .block()
        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(Notification.class);
        resolver.resolveIndexFor(Notification.class).forEach(index -> indexOps.ensureIndex(index).block());
    }
}