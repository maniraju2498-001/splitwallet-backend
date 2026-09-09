# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build the Spring Boot fat JAR
# Uses the official Maven image — no Maven Wrapper (mvnw) files required
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first so Maven dependency downloads are layer-cached.
# Dependencies are only re-downloaded when pom.xml changes.
COPY pom.xml ./

RUN mvn dependency:go-offline -B

# Copy source and build the fat JAR (skip tests — they require a running DB)
COPY src ./src
RUN mvn package -DskipTests -B

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Minimal runtime image
# JRE-only (no compiler/build tools) — keeps the final image small
# Must match the Java version the source was compiled for (Java 21)
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Create a non-root user for security best practice
RUN addgroup -S splitwallet && adduser -S splitwallet -G splitwallet

# Copy only the fat JAR from the build stage
COPY --from=builder /app/target/split-wallet-backend-1.0.0.jar app.jar

# Switch to non-root user
USER splitwallet

# Render sets PORT dynamically; Spring Boot reads it via ${PORT:8080}
EXPOSE 8080

# JVM flags:
#   -XX:+UseContainerSupport  — respect Docker memory limits
#   -XX:MaxRAMPercentage=75   — use 75% of container RAM for heap
#   -Djava.security.egd=...   — faster startup (avoids /dev/random blocking)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
