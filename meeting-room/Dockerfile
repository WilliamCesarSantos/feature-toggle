# Build stage
FROM gradle:9-jdk25-alpine as builder

WORKDIR /app

# Copy root gradle files
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle.kts .
COPY gradle/ gradle/

# Copy only meeting-room module
COPY meeting-room/ meeting-room/

# Create a temporary settings.gradle.kts with only meeting-room
RUN echo 'rootProject.name = "feature-toggle"' > settings.gradle.kts && \
    echo 'include("meeting-room")' >> settings.gradle.kts

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

