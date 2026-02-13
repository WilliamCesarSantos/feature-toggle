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

# Create all parameters
create_parameter "/config/application/spring/datasource/url" "jdbc:postgresql://postgres:5432/meeting_room_db" "String"
create_parameter "/config/application/spring/datasource/username" "postgres" "String"
create_parameter "/config/application/spring/datasource/password" "postgres" "SecureString"
create_parameter "/config/application/aws/sns/topic-arn" "$TOPIC_ARN" "String"

echo "Initialization completed!"

# List all Parameter Store parameters
echo ""
echo "=== AWS Parameter Store Parameters ==="
aws --endpoint-url=http://localhost:4566 ssm get-parameters-by-path \
  --path "/config" \
  --recursive \
  --with-decryption \
  --output table
