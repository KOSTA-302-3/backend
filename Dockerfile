FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.properties"]