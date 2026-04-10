FROM eclipse-temurin:21-jdk

WORKDIR /app

# Preserving the folder is important, else the app does not start
COPY target/quarkus-app ./quarkus-app

ENV CATALOG_HOST=0.0.0.0
ENV CATALOG_PORT=10000
ENV CONSUL_URL=http://consul:8500
ENV CONSUL_TOKEN_PATH=/etc/carbonio/catalog/service-discover/token
RUN mkdir -p /etc/carbonio/catalog/service-discover/ \
    && touch /etc/carbonio/catalog/service-discover/token

ENTRYPOINT ["java", "--enable-preview", "--enable-native-access=ALL-UNNAMED", "-jar", "quarkus-app/quarkus-run.jar"]
