FROM eclipse-temurin:17-jdk

WORKDIR /app

# Preserving the folder is important, else the app does not start
COPY target/quarkus-app ./quarkus-app

ENTRYPOINT ["java", "-jar", "quarkus-app/quarkus-run.jar"]