# =============================================================
# Multi-stage Dockerfile za Quotes API
# =============================================================

# ---- BUILD STAGE ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Cache ovisnosti prije source koda (brži rebuild)
COPY pom.xml .
COPY .mvn .mvn
RUN mvn -B -e -o dependency:resolve dependency:resolve-plugins || true

# Kopiraj source i builduj (preskoči testove — već trče u CI-u)
COPY src ./src
RUN mvn -B -DskipTests -P no-coverage-check package

# ---- RUNTIME STAGE ----
FROM eclipse-temurin:21-jre-alpine

# Non-root user (security best practice)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /opt/app

# Kopiraj JAR iz build stage-a
COPY --from=build --chown=spring:spring /app/target/foodie-hub-0.0.1-SNAPSHOT.jar app.jar

# Render injectira PORT env varijablu
ENV PORT=8080
ENV JAVA_OPTS="-Xmx384m -XX:+UseSerialGC"

EXPOSE 8080

# Healthcheck (Docker razina; Render ima vlastiti)
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
