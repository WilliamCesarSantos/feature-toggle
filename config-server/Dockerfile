# Build stage
FROM gradle:9-jdk25 as builder

WORKDIR /app

# Copy root gradle files
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle/ gradle/

# Copy config-server module
COPY config-server/ config-server/

# Copy meeting-room directory (needed for Gradle multi-project structure)
COPY meeting-room/ meeting-room/

# Build config-server
RUN ./gradlew :config-server:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jdk

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/config-server/build/libs/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]

