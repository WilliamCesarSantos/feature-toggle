# Build stage
FROM gradle:9-jdk25-alpine as builder

WORKDIR /app

# Copy root gradle files
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle/ gradle/

# Copy meeting-room module
COPY meeting-room/ meeting-room/

# Copy config-server directory (needed for Gradle multi-project structure)
COPY config-server/ config-server/

# Build meeting-room
RUN ./gradlew :meeting-room:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

# Install curl for healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/meeting-room/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

