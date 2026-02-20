#!/bin/bash

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
sleep 5

# Set AWS credentials and endpoint
export AWS_ACCESS_KEY_ID=guest
export AWS_SECRET_ACCESS_KEY=guest
export AWS_DEFAULT_REGION=sa-east-1

# Create application parameters in Parameter Store
echo "Creating AWS Parameter Store parameters..."

# Function to create parameter with validation
create_parameter() {
  local name=$1
  local value=$2
  local type=$3

  echo "Creating parameter: $name"
  aws --endpoint-url=http://localhost:4566 ssm put-parameter \
    --name "$name" \
    --value "$value" \
    --type "$type" \
    --overwrite 2>/dev/null

  if [ $? -eq 0 ]; then
    echo "✓ Parameter created: $name"
  else
    echo "✗ Failed to create parameter: $name"
  fi
}

# Database Configuration
echo "=== Database Configuration ==="
create_parameter "/config/meeting-room/spring/datasource/url" "jdbc:postgresql://postgres:5432/meeting_room_db" "String"
create_parameter "/config/meeting-room/spring/datasource/username" "postgres" "String"
create_parameter "/config/meeting-room/spring/datasource/password" "postgres" "SecureString"
create_parameter "/config/meeting-room/spring/datasource/driver-class-name" "org.postgresql.Driver" "String"

# JPA Configuration
echo "=== JPA Configuration ==="
create_parameter "/config/meeting-room/spring/jpa/hibernate/ddl-auto" "none" "String"
create_parameter "/config/meeting-room/spring/jpa/show-sql" "false" "String"
create_parameter "/config/meeting-room/spring/jpa/properties/hibernate/dialect" "org.hibernate.dialect.PostgreSQLDialect" "String"
create_parameter "/config/meeting-room/spring/jpa/properties/hibernate/format_sql" "true" "String"

# Kafka Binder Configuration
echo "=== Kafka Binder Configuration ==="
create_parameter "/config/meeting-room/spring/cloud/stream/kafka/binder/brokers" "kafka:9092" "String"
create_parameter "/config/meeting-room/spring/cloud/stream/kafka/binder/auto-create-topics" "false" "String"
create_parameter "/config/meeting-room/spring/cloud/stream/kafka/binder/replication-factor" "1" "String"

# Kafka Configuration
echo "=== Kafka Configuration ==="
create_parameter "/config/meeting-room/spring/kafka/bootstrap-servers" "kafka:9092" "String"

# Feature Toggles Configuration
echo "=== Feature Toggles Configuration ==="
create_parameter "/config/meeting-room/feature/toggles/reservation.capacity-check" "true" "String"
create_parameter "/config/meeting-room/feature/toggles/reservation.schedule-conflict-check" "true" "String"

echo "Initialization completed!"

# List all Parameter Store parameters
echo ""
echo "=== AWS Parameter Store Parameters for meeting-room ==="
aws --endpoint-url=http://localhost:4566 ssm get-parameters-by-path \
  --path "/config/meeting-room" \
  --recursive \
  --with-decryption \
  --output table

echo ""
echo "=== Total Parameters Created ==="
aws --endpoint-url=http://localhost:4566 ssm get-parameters-by-path \
  --path "/config" \
  --recursive \
  --query 'Parameters[*].Name' \
  --output table
