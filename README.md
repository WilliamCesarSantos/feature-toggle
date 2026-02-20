# Meeting Room Service with AWS Parameter Store Feature Toggles

A Spring Boot microservice for managing meeting room reservations with dynamic feature toggles stored in AWS Systems Manager Parameter Store (SSM). This project demonstrates how to implement feature toggles that can be changed at runtime without redeployment.

## Architecture Overview

The system is a single microservice that directly connects to AWS SSM Parameter Store for configuration management:

- **Meeting Room Service**: A microservice for managing meeting room reservations with dynamic feature toggles
- **Infrastructure**: LocalStack (AWS emulation) and PostgreSQL

### Key Features

- ✅ Dynamic feature toggles stored in AWS SSM Parameter Store
- ✅ Direct AWS Parameter Store integration with Spring Cloud AWS
- ✅ Chain of Responsibility pattern for validation rules
- ✅ Toggle-based feature activation without deployments
- ✅ LocalStack for local AWS service emulation
- ✅ Flyway database migrations
- ✅ Docker Compose for easy local development

## Technology Stack

### Backend
- **Java 25** with **Kotlin**
- **Spring Boot 3.4.1**
- **Spring Cloud AWS 3.2.1**
- **Spring Data JPA**
- **PostgreSQL** database
- **Flyway** for database migrations

### Infrastructure
- **LocalStack**: AWS services emulation (SSM)
- **PostgreSQL 16**: Relational database
- **Docker & Docker Compose**: Containerization

### AWS Services (via LocalStack)
- **Systems Manager (SSM) Parameter Store**: Configuration and feature toggle storage

## Project Structure

```
meeting-room/
├── src/main/kotlin/
│   └── br/com/will/classes/featuretoggle/meetingroom/
│       ├── application/         # Use cases and services
│       │   ├── port/           # Input/Output ports
│       │   └── service/        # Business logic
│       ├── domain/             # Domain models and validators
│       │   ├── entity/         # Domain entities
│       │   ├── exception/      # Domain exceptions
│       │   └── validation/     # Validation chain
│       └── infrastructure/     # External integrations
│           ├── config/         # Configuration beans
│           ├── featuretoggle/  # Feature toggle management
│           ├── persistence/    # Database entities and repositories
│           └── web/            # REST controllers and DTOs
├── src/main/resources/
│   ├── application.yml         # Application configuration
│   └── db/migration/           # Flyway migrations
├── localstack-init/            # LocalStack initialization scripts
│   └── init-aws.sh            # Creates SSM parameters
├── Dockerfile
├── docker-compose.yml          # Docker orchestration
├── build.gradle.kts
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
- `feature.toggles.reservation.schedule-conflict-check`: Enable/disable schedule conflict validation

### Toggle Configuration Flow

```
┌─────────────────┐
│  AWS SSM Store  │
│  (LocalStack)   │
└────────┬────────┘
         │
         │ On Startup
         ▼
┌─────────────────┐
│ Meeting Room    │
│ Service         │
│ (Reads toggles) │
└─────────────────┘
```

The application reads configuration from AWS Parameter Store on startup using Spring Cloud AWS.

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Java 25 (for local development)
- Git

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd meeting-room
   ```

2. **Start all services**
   ```bash
   docker compose up -d --build
   ```

   This will start:
   - LocalStack (port 4566) - AWS services emulation
   - PostgreSQL (port 5432) - Database
   - Meeting Room Service (port 8082) - Main application

3. **Wait for services to be healthy**
   ```bash
   docker compose ps
   ```

4. **Verify the service is running**
   ```bash
   # Check Meeting Room Service health
   curl http://localhost:8082/actuator/health
   ```

### Initialize AWS Parameters

The LocalStack initialization script (`localstack-init/init-aws.sh`) automatically creates the following parameters on startup:

```bash
# Feature toggles
/config/meeting-room/feature/toggles/reservation.capacity-check = true
/config/meeting-room/feature/toggles/reservation.schedule-conflict-check = true

# Database configuration
/config/meeting-room/spring/datasource/url = jdbc:postgresql://postgres:5432/meeting_room_db
/config/meeting-room/spring/datasource/username = postgres
/config/meeting-room/spring/datasource/password = postgres
/config/meeting-room/spring/jpa/hibernate/ddl-auto = none
```

### Local Development

To run the application locally without Docker:

1. **Start LocalStack and PostgreSQL**
   ```bash
   docker compose up localstack postgres -d
   ```

2. **Set environment variables**
   ```bash
   export AWS_ACCESS_KEY_ID=guest
   export AWS_SECRET_ACCESS_KEY=guest
   export AWS_REGION=sa-east-1
   export AWS_ENDPOINT=http://localhost:4566
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

## API Documentation

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

Response:
```json
{
  "id": 1,
  "name": "Conference Room A",
  "capacity": 10
}
```

#### Get All Meeting Rooms
```bash
GET http://localhost:8082/api/rooms
```

#### Create a Reservation
```bash
POST http://localhost:8082/api/reservations
Content-Type: application/json

{
  "roomId": 1,
  "requester": "John Doe",
  "startTime": "2026-02-20T10:00:00",
  "endTime": "2026-02-20T11:00:00",
  "numberOfParticipants": 5
}
```

Response:
```json
{
  "id": 1,
  "roomId": 1,
  "requester": "John Doe",
  "startTime": "2026-02-20T10:00:00",
  "endTime": "2026-02-20T11:00:00",
  "numberOfParticipants": 5
}
```

The reservation will be validated based on active feature toggles:
- If `capacity-check` is enabled: validates numberOfParticipants ≤ room capacity
- If `schedule-conflict-check` is enabled: validates no overlapping reservations

## Testing Feature Toggles

### Scenario 1: Capacity Check Toggle

1. **Create a room with capacity 5**
   ```bash
   curl -X POST http://localhost:8082/api/rooms \
     -H "Content-Type: application/json" \
     -d '{"name":"Small Room","capacity":5}'
   ```

2. **Try to book with 10 participants (should fail with capacity-check enabled)**
   ```bash
   curl -X POST http://localhost:8082/api/reservations \
     -H "Content-Type: application/json" \
     -d '{
       "roomId":1,
       "requester":"John Doe",
       "startTime":"2026-02-20T14:00:00",
       "endTime":"2026-02-20T15:00:00",
       "numberOfParticipants":10
     }'
   ```
   
   Expected error: "Number of participants exceeds room capacity"

3. **Disable capacity check via AWS CLI**
   ```bash
   aws --endpoint-url=http://localhost:4566 ssm put-parameter \
     --name "/config/meeting-room/feature/toggles/reservation.capacity-check" \
     --value "false" \
     --type "String" \
     --overwrite
   ```

4. **Restart the application to load new configuration**
   ```bash
   docker compose restart meeting-room
   ```

5. **Try booking again (should succeed now)**

### Scenario 2: Schedule Conflict Check Toggle

1. **Create a room**
   ```bash
   curl -X POST http://localhost:8082/api/rooms \
     -H "Content-Type: application/json" \
     -d '{"name":"Meeting Room","capacity":10}'
   ```

2. **Create a reservation**
   ```bash
   curl -X POST http://localhost:8082/api/reservations \
     -H "Content-Type: application/json" \
     -d '{
       "roomId":1,
       "requester":"Alice",
       "startTime":"2026-02-20T10:00:00",
       "endTime":"2026-02-20T11:00:00",
       "numberOfParticipants":5
     }'
   ```

3. **Try to create an overlapping reservation (should fail)**
   ```bash
   curl -X POST http://localhost:8082/api/reservations \
     -H "Content-Type: application/json" \
     -d '{
       "roomId":1,
       "requester":"Bob",
       "startTime":"2026-02-20T10:30:00",
       "endTime":"2026-02-20T11:30:00",
       "numberOfParticipants":5
     }'
   ```
   
   Expected error: "Room already reserved for this time slot"

4. **Disable schedule conflict check**
   ```bash
   aws --endpoint-url=http://localhost:4566 ssm put-parameter \
     --name "/config/meeting-room/feature/toggles/reservation.schedule-conflict-check" \
     --value "false" \
     --type "String" \
     --overwrite
   ```

5. **Restart and try again (should allow overlapping reservations)**

## Accessing AWS Parameter Store

### Interactive Parameter Update Script

The easiest way to update parameters and refresh the application:

```bash
./update-parameter.sh
```

This interactive script will:
1. Prompt you for the parameter name, value, and type
2. Update the parameter in AWS SSM Parameter Store
3. Automatically call the Spring Boot `/actuator/refresh` endpoint
4. Verify the update was successful

See [UPDATE-PARAMETER-GUIDE.md](UPDATE-PARAMETER-GUIDE.md) for detailed usage instructions.

### Manual AWS CLI Commands

#### View all parameters
```bash
aws --endpoint-url=http://localhost:4566 ssm get-parameters-by-path \
  --path "/config/meeting-room" \
  --recursive \
  --with-decryption
```

#### Get a specific parameter
```bash
aws --endpoint-url=http://localhost:4566 ssm get-parameter \
  --name "/config/meeting-room/feature/toggles/reservation.capacity-check" \
  --with-decryption
```

#### Update a parameter
```bash
aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/meeting-room/feature/toggles/reservation.capacity-check" \
  --value "true" \
  --type "String" \
  --overwrite
```

#### Refresh application after manual update
```bash
curl -X POST http://localhost:8082/actuator/refresh
```

## Development

### Building the Application

```bash
# Build the application
./gradlew build

# Skip tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

### Database Migrations

Flyway migrations are located in:
```
src/main/resources/db/migration/
```

Migrations run automatically on application startup. Current migrations:
- `V1__Create_meeting_rooms_and_reservations_tables.sql`: Creates initial schema

## Monitoring and Health Checks

### Health Endpoint

- Meeting Room Service: `http://localhost:8082/actuator/health`

### Container Health

```bash
docker compose ps
```

All services should show `healthy` status when properly running.

## Troubleshooting

### Application can't connect to LocalStack

**Problem**: `Connection refused` or `UnknownHostException`

**Solution**: Ensure LocalStack is healthy before the application starts
```bash
docker compose restart meeting-room
```

### Toggle changes don't apply

**Problem**: Updated parameters in SSM don't reflect in the application

**Solution**: The application reads parameters on startup. Restart to load new values:
```bash
docker compose restart meeting-room
```

### Database migrations fail

**Problem**: Flyway errors on startup

**Solution**: Reset the database
```bash
docker compose down -v
docker compose up -d
```

### LocalStack parameters not created

**Problem**: Parameters missing in SSM

**Solution**: Check LocalStack logs and re-run initialization
```bash
docker compose logs localstack
docker compose restart localstack
```

## Architecture Decisions

### Why AWS SSM Parameter Store?

- **Hierarchical organization**: Parameters organized by application and feature
- **Built-in versioning**: Track parameter changes over time
- **IAM-based access control**: Secure parameter access in production
- **Native AWS integration**: Seamless integration with Spring Cloud AWS
- **Easy local development**: LocalStack provides full SSM emulation

### Why Chain of Responsibility for Validations?

- **Single Responsibility Principle**: Each validator handles one concern
- **Easy to extend**: Add new validations without modifying existing code
- **Feature toggle control**: Enable/disable validations dynamically
- **Testable in isolation**: Unit test each validator independently
- **Clear validation flow**: Validators execute in a defined order

### Why Direct Parameter Store Integration?

- **Simplicity**: No intermediate config server needed
- **Reduced latency**: Direct AWS SDK calls
- **Fewer moving parts**: Less infrastructure to maintain
- **Standard Spring Boot**: Uses Spring Cloud AWS standard features

## Continuous Integration

The project includes a GitHub Actions CI pipeline that automatically builds and tests the code on every push and pull request.

### CI Pipeline

The pipeline performs the following steps:
1. Checkout code
2. Set up JDK 25
3. Cache Gradle dependencies
4. Build the application
5. Run all tests

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
