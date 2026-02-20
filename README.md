# Feature Toggle System with Spring Cloud Config and AWS Parameter Store

A distributed feature toggle management system built with Spring Boot, Spring Cloud Config, Kafka, and AWS Systems Manager Parameter Store (SSM). This project demonstrates how to implement centralized configuration management with real-time feature toggle updates across microservices.

## Architecture Overview

The system consists of three main components:

1. **Config Server**: Centralized configuration server that fetches configurations from AWS SSM Parameter Store
2. **Meeting Room Service**: A microservice for managing meeting room reservations with dynamic feature toggles
3. **Infrastructure**: LocalStack (AWS emulation), Kafka, and PostgreSQL

### Key Features

- ✅ Centralized configuration management using Spring Cloud Config
- ✅ Dynamic feature toggles stored in AWS SSM Parameter Store
- ✅ Real-time configuration updates via Kafka messaging
- ✅ Chain of Responsibility pattern for validation rules
- ✅ Toggle-based feature activation without deployments
- ✅ LocalStack for local AWS service emulation
- ✅ Flyway database migrations
- ✅ Docker Compose for easy local development

## Technology Stack

### Backend
- **Java 25** with **Kotlin**
- **Spring Boot 3.4.1**
- **Spring Cloud Config**
- **Spring Cloud Stream** with Kafka binder
- **Spring Data JPA**
- **PostgreSQL** database
- **Flyway** for database migrations

### Infrastructure
- **LocalStack**: AWS services emulation (SSM)
- **Apache Kafka 4.1.1**: Message broker for configuration updates
- **PostgreSQL 16**: Relational database
- **Docker & Docker Compose**: Containerization

### AWS Services (via LocalStack)
- **Systems Manager (SSM) Parameter Store**: Configuration storage

## Project Structure

```
feature-toggle/
├── config-server/              # Spring Cloud Config Server
│   ├── src/main/kotlin/
│   │   └── br/com/will/classes/featuretoggle/configserver/
│   │       ├── config/         # AWS and configuration beans
│   │       ├── controller/     # REST endpoints for parameter updates
│   │       └── service/        # Business logic
│   └── Dockerfile
│
├── meeting-room/               # Meeting Room Microservice
│   ├── src/main/kotlin/
│   │   └── br/com/will/classes/featuretoggle/meetingroom/
│   │       ├── application/    # Use cases and services
│   │       ├── domain/         # Domain models and validators
│   │       ├── infrastructure/ # External integrations
│   │       │   ├── config/     # Feature toggle configuration
│   │       │   ├── messaging/  # Kafka consumers
│   │       │   └── persistence/# Database entities and repositories
│   │       └── presentation/   # REST controllers
│   └── Dockerfile
│
├── localstack-init/            # LocalStack initialization scripts
│   └── init-aws.sh            # Creates SSM parameters
│
├── docker-compose.yml          # Docker orchestration
└── README.md
```

## Feature Toggle System

### How Feature Toggles Work

Feature toggles are stored in AWS SSM Parameter Store with the following structure:

```
/config/meeting-room/feature/toggles/<toggle-name>
```

Example toggles:
- `feature.toggles.reservation.capacity-check`: Enable/disable room capacity validation
- `feature.toggles.reservation.time-conflict`: Enable/disable time conflict validation
- `feature.toggles.reservation.advance-booking`: Enable/disable advance booking validation

### Toggle Update Flow

```
┌─────────────────┐
│   Config Server │
│   Update API    │
└────────┬────────┘
         │ 1. Update SSM Parameter
         ▼
┌─────────────────┐
│  AWS SSM Store  │
│   (LocalStack)  │
└────────┬────────┘
         │ 2. Publish to Kafka
         ▼
┌─────────────────┐
│  Kafka Topics   │
│ - toggle-updated│
│ - springCloudBus│
└────────┬────────┘
         │ 3. Consume messages
         ▼
┌─────────────────┐
│  Meeting Room   │
│    Service      │
└─────────────────┘
```

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 25 (for local development)
- Git

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd feature-toggle
   ```

2. **Start all services**
   ```bash
   docker compose up -d
   ```

   This will start:
   - LocalStack (port 4566)
   - PostgreSQL (port 5432)
   - Kafka (port 9092)
   - Config Server (port 8888)
   - Meeting Room Service (port 8082)

3. **Wait for services to be healthy**
   ```bash
   docker compose ps
   ```

4. **Verify the services are running**
   ```bash
   # Check Config Server health
   curl http://localhost:8888/actuator/health
   
   # Check Meeting Room Service health
   curl http://localhost:8082/actuator/health
   ```

### Initialize AWS Parameters

The LocalStack initialization script (`localstack-init/init-aws.sh`) automatically creates the following parameters on startup:

```bash
# Feature toggles
/config/meeting-room/feature/toggles/reservation.capacity-check = true
/config/meeting-room/feature/toggles/reservation.time-conflict = true
/config/meeting-room/feature/toggles/reservation.advance-booking = true

# Database configuration
/config/meeting-room/spring/datasource/url
/config/meeting-room/spring/datasource/username
/config/meeting-room/spring/datasource/password
/config/meeting-room/spring/jpa/hibernate/ddl-auto
```

## API Documentation

### Config Server Endpoints

#### Update a Parameter
```bash
PUT http://localhost:8888/api/parameters/update
Content-Type: application/json

{
  "parameterName": "/config/meeting-room/feature/toggles/reservation.capacity-check",
  "parameterValue": "false"
}
```

This endpoint:
1. Updates the parameter in SSM
2. Publishes a message to Kafka topic `toggle-updated-topic`
3. Notifies all subscribed services

### Meeting Room Service Endpoints

#### Create a Meeting Room
```bash
POST http://localhost:8082/api/rooms
Content-Type: application/json

{
  "name": "Conference Room A",
  "capacity": 10
}
```

#### Create a Reservation
```bash
POST http://localhost:8082/api/reservations
Content-Type: application/json

{
  "roomId": 1,
  "title": "Team Meeting",
  "startTime": "2026-02-20T10:00:00",
  "endTime": "2026-02-20T11:00:00",
  "attendees": 5
}
```

The reservation will be validated based on active feature toggles:
- If `capacity-check` is enabled: validates attendees ≤ room capacity
- If `time-conflict` is enabled: validates no overlapping reservations
- If `advance-booking` is enabled: validates booking is within allowed timeframe

## Testing Feature Toggles

### Scenario 1: Disable Capacity Check

1. **Create a room with capacity 5**
   ```bash
   curl -X POST http://localhost:8082/api/rooms \
     -H "Content-Type: application/json" \
     -d '{"name":"Small Room","capacity":5}'
   ```

2. **Try to book with 10 attendees (should fail)**
   ```bash
   curl -X POST http://localhost:8082/api/reservations \
     -H "Content-Type: application/json" \
     -d '{
       "roomId":1,
       "title":"Big Meeting",
       "startTime":"2026-02-20T14:00:00",
       "endTime":"2026-02-20T15:00:00",
       "attendees":10
     }'
   ```

3. **Disable capacity check**
   ```bash
   curl -X PUT http://localhost:8888/api/parameters/update \
     -H "Content-Type: application/json" \
     -d '{
       "parameterName":"/config/meeting-room/feature/toggles/reservation.capacity-check",
       "parameterValue":"false"
     }'
   ```

4. **Wait a few seconds for the update to propagate**

5. **Try booking again (should succeed now)**

### Scenario 2: Monitor Real-time Updates

1. **Watch Meeting Room Service logs**
   ```bash
   docker logs -f meeting-room
   ```

2. **Update a toggle**
   ```bash
   curl -X PUT http://localhost:8888/api/parameters/update \
     -H "Content-Type: application/json" \
     -d '{
       "parameterName":"/config/meeting-room/feature/toggles/reservation.time-conflict",
       "parameterValue":"false"
     }'
   ```

3. **Observe the log messages**
   - Config Server publishes the update
   - Kafka receives the message
   - Meeting Room Service consumes and applies the change

## Kafka Topics

The system uses two Kafka topics:

1. **toggle-updated-topic**: Feature toggle-specific updates
   - Published by Config Server when a toggle changes
   - Consumed by Meeting Room Service
   - Message format: `FeatureToggleRefreshEvent`

2. **springCloudBus**: Generic configuration refresh events
   - Published by Config Server for configuration changes
   - Consumed by Meeting Room Service
   - Message format: `RefreshRemoteApplicationEvent`

## AWS SSM Parameter Store

### Parameter Naming Convention

```
/config/{application-name}/{property-path}
```

Examples:
- `/config/meeting-room/feature/toggles/reservation.capacity-check`
- `/config/meeting-room/spring/datasource/url`

### Querying Parameters with AWS CLI

```bash
# List all parameters
aws --endpoint-url=http://localhost:4566 ssm describe-parameters

# Get a specific parameter
aws --endpoint-url=http://localhost:4566 ssm get-parameter \
  --name "/config/meeting-room/feature/toggles/reservation.capacity-check"

# Get parameter value only
aws --endpoint-url=http://localhost:4566 ssm get-parameter \
  --name "/config/meeting-room/feature/toggles/reservation.capacity-check" \
  --query 'Parameter.Value' \
  --output text
```

## Development

### Building the Applications

```bash
# Build Config Server
./gradlew :config-server:build

# Build Meeting Room Service
./gradlew :meeting-room:build

# Build both
./gradlew build
```

### Running Locally (without Docker)

1. **Start infrastructure services**
   ```bash
   docker compose up -d localstack kafka postgres
   ```

2. **Run Config Server**
   ```bash
   cd config-server
   ./gradlew bootRun --args='--spring.profiles.active=awsparamstore,aws-local,kafka-local'
   ```

3. **Run Meeting Room Service**
   ```bash
   cd meeting-room
   ./gradlew bootRun
   ```

### Database Migrations

Flyway migrations are located in:
```
meeting-room/src/main/resources/db/migration/
```

Migrations run automatically on application startup.

## Monitoring and Health Checks

### Health Endpoints

- Config Server: `http://localhost:8888/actuator/health`
- Meeting Room: `http://localhost:8082/actuator/health`

### Container Health

```bash
docker compose ps
```

All services should show `healthy` status when properly running.

## Troubleshooting

### Config Server can't connect to LocalStack

**Problem**: `Connection refused` or `UnknownHostException`

**Solution**: Ensure LocalStack is healthy before Config Server starts
```bash
docker compose restart config-server
```

### Meeting Room Service doesn't receive toggle updates

**Problem**: Toggle changes don't apply

**Solutions**:
1. Check Kafka is running: `docker compose ps kafka`
2. Verify topics exist:
   ```bash
   docker exec -it kafka kafka-topics.sh --list --bootstrap-server localhost:9092
   ```
3. Check consumer logs: `docker logs meeting-room | grep toggle`

### Database migrations fail

**Problem**: Flyway errors on startup

**Solution**: Reset the database
```bash
docker compose down -v
docker compose up -d
```

## Architecture Decisions

### Why AWS SSM Parameter Store?

- Hierarchical parameter organization
- Built-in versioning and change history
- IAM-based access control (in production)
- Native AWS integration
- Easy LocalStack emulation for development

### Why Kafka for Configuration Updates?

- Decoupled architecture
- Guaranteed message delivery
- Scalable to multiple service instances
- Event sourcing of configuration changes
- Real-time updates without polling

### Why Chain of Responsibility for Validations?

- Single Responsibility Principle
- Easy to add/remove validations
- Feature toggles control which validations run
- Testable in isolation
- Clear validation flow

## Continuous Integration

The project includes a GitHub Actions CI pipeline that automatically builds and tests the code on every push and pull request.

### CI Pipeline

The pipeline performs the following steps:
1. Checkout code
2. Set up JDK 21
3. Cache Gradle dependencies
4. Build config-server module
5. Build meeting-room module
6. Run all tests

The workflow is defined in `.github/workflows/ci.yml` and runs on:
- Push to `main` or `develop` branches
- Pull requests targeting `main` or `develop` branches

### Local Build

To build the project locally (same as CI):

```bash
./gradlew clean build
```

To skip tests:
```bash
./gradlew clean build -x test
```

## License

This project is licensed under the MIT License.

## Acknowledgments

- Spring Cloud Config for centralized configuration
- LocalStack for AWS service emulation
- Apache Kafka for event streaming

