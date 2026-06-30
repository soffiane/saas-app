FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/saas-app-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /app/certs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
