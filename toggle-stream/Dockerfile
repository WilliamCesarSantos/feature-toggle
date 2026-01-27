# Build stage
FROM gradle:9-jdk25 as builder

WORKDIR /app

# Copy root gradle files
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY gradle/ gradle/

# Copy toggle-stream module
COPY toggle-stream/ toggle-stream/

# Build toggle-stream
RUN ./gradlew :toggle-stream:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/toggle-stream/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]

