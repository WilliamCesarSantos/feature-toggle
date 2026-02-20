# Build stage
FROM gradle:9-jdk25-alpine as builder

WORKDIR /app

# Copy root gradle files
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle.kts .
COPY gradle/ gradle/

# Copy only config-server module
COPY config-server/ config-server/

# Create a temporary settings.gradle.kts with only config-server
RUN echo 'rootProject.name = "feature-toggle"' > settings.gradle.kts && \
    echo 'include("config-server")' >> settings.gradle.kts

# Build config-server
RUN ./gradlew :config-server:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

# Install curl for healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/config-server/build/libs/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]

