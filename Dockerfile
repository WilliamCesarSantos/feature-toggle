# Build stage
FROM gradle:9-jdk25-alpine as builder

WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Build application
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

# Install curl, bash, and AWS CLI for healthcheck and LocalStack waiting
RUN apk add --no-cache curl bash aws-cli

WORKDIR /app

# Copy built JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy wait script
COPY wait-for-localstack.sh /app/wait-for-localstack.sh
RUN chmod +x /app/wait-for-localstack.sh

EXPOSE 8082

# Wait for LocalStack to initialize before starting the application
ENTRYPOINT ["/bin/bash", "-c", "/app/wait-for-localstack.sh && java -jar app.jar"]

