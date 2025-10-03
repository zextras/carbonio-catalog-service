FROM maven:3-eclipse-temurin-17-alpine AS builder

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

RUN groupadd -r carbonio-catalog && useradd -r -g carbonio-catalog carbonio-catalog

WORKDIR /app

COPY --from=builder /app/target/quarkus-app /app

RUN mkdir -p /var/log/carbonio/catalog && \
    mkdir -p /etc/carbonio/catalog/service-discover && \
    chown -R carbonio-catalog:carbonio-catalog /app /var/log/carbonio/catalog /etc/carbonio/catalog

USER carbonio-catalog

EXPOSE 10000

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:10000/health/ || exit 1

ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Djava.awt.headless=true" \
    QUARKUS_HTTP_HOST=0.0.0.0 \
    QUARKUS_HTTP_PORT=10000

CMD ["java", "-jar", "quarkus-run.jar"]
