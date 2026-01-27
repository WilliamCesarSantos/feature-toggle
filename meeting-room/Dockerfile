# Build stage
FROM gradle:9-jdk25 as builder

WORKDIR /app

# Copy root gradle files
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle/ gradle/

# Copy meeting-room module
COPY meeting-room/ meeting-room/

# Build meeting-room
RUN ./gradlew :meeting-room:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/meeting-room/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

