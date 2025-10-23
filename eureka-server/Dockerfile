# ====== Build (extrae capas) ======
FROM eclipse-temurin:21-jre-alpine AS builder
WORKDIR /extracted
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ====== Runtime ======
FROM eclipse-temurin:21-jre-alpine
WORKDIR /application

# Usuario no root (opcional)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar capas
COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/application/ ./

EXPOSE 8761

# ¡Sin -cp!
ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]
