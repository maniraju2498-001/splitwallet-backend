# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build the Spring Boot fat JAR
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (layer-cached unless dependencies change)
COPY mvnw ./
COPY mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Download dependencies (cached as long as pom.xml doesn't change)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Minimal runtime image
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create non-root user for security
RUN addgroup -S splitwallet && adduser -S splitwallet -G splitwallet

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/split-wallet-backend-1.0.0.jar app.jar

# Use the non-root user
USER splitwallet

# Render sets PORT dynamically; Spring Boot reads it via ${PORT:8080}
EXPOSE 8080

# JVM flags:
#   -XX:+UseContainerSupport  — respect Docker memory limits
#   -XX:MaxRAMPercentage=75   — use 75% of container RAM for heap
#   -Djava.security.egd=...   — faster startup (avoid /dev/random blocking)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
