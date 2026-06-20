# Prep stage runs on BUILDPLATFORM (amd64 CI builder) — no QEMU. Creates the
# arch-independent service-discover dir/token so the final image needs no RUN.
FROM --platform=$BUILDPLATFORM docker.io/library/busybox AS prep
RUN mkdir -p /staging/etc/carbonio/catalog/service-discover/ \
    && touch /staging/etc/carbonio/catalog/service-discover/token

FROM docker.io/library/eclipse-temurin:24-jdk

WORKDIR /app

# Preserving the folder is important, else the app does not start
COPY target/quarkus-app ./quarkus-app

ENV CATALOG_HOST=0.0.0.0
ENV CATALOG_PORT=10000
ENV CONSUL_URL=http://consul:8500
ENV CONSUL_TOKEN_PATH=/etc/carbonio/catalog/service-discover/token
COPY --from=prep /staging/etc /etc

ENV JDK_JAVA_OPTIONS="-Xms256m -Xmx512m"

ENTRYPOINT ["java", "--enable-preview", "--enable-native-access=ALL-UNNAMED", "-jar", "quarkus-app/quarkus-run.jar"]
