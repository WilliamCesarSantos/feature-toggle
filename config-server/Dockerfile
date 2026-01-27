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

# Build config-server
RUN ./gradlew :config-server:build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/config-server/build/libs/*.jar app.jar

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]

