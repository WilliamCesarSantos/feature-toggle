#!/bin/bash

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
sleep 5

# Set AWS credentials and endpoint
export AWS_ACCESS_KEY_ID=guest
export AWS_SECRET_ACCESS_KEY=guest
export AWS_DEFAULT_REGION=sa-east-1

# Create SNS topic for feature toggles
echo "Creating SNS topic for feature toggles..."
TOPIC_ARN=$(aws --endpoint-url=http://localhost:4566 sns create-topic \
  --name feature-toggle-topic \
  --attributes ContentBasedDeduplication=true \
  --output text)

echo "SNS Topic ARN: $TOPIC_ARN"

# Create SQS queue for meeting-room service
echo "Creating SQS queue for meeting-room service..."
QUEUE_URL=$(aws --endpoint-url=http://localhost:4566 sqs create-queue \
  --queue-name metting-room-feature-toggle-topic \
  --attributes ContentBasedDeduplication=true \
  --output text)

echo "SQS Queue URL: $QUEUE_URL"

# Subscribe SQS queue to SNS topic
echo "Subscribing SQS queue to SNS topic..."
if [ ! -z "$TOPIC_ARN" ] && [ ! -z "$QUEUE_URL" ]; then
  # Get queue ARN
  QUEUE_ARN=$(aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes \
    --queue-url "$QUEUE_URL" \
    --attribute-names QueueArn \
    --query 'Attributes.QueueArn' \
    --output text)

  echo "SQS Queue ARN: $QUEUE_ARN"

  # Subscribe
  aws --endpoint-url=http://localhost:4566 sns subscribe \
    --topic-arn "$TOPIC_ARN" \
    --protocol sqs \
    --notification-endpoint "$QUEUE_ARN" \
    --output text
fi

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

# List all SNS topics
echo ""
echo "=== SNS Topics ==="
aws --endpoint-url=http://localhost:4566 sns list-topics --output table

# List all SQS queues
echo ""
echo "=== SQS Queues ==="
aws --endpoint-url=http://localhost:4566 sqs list-queues --output table

# List all subscriptions
echo ""
echo "=== SNS Subscriptions ==="
aws --endpoint-url=http://localhost:4566 sns list-subscriptions --output table

# List all Parameter Store parameters
echo ""
echo "=== AWS Parameter Store Parameters ==="
aws --endpoint-url=http://localhost:4566 ssm get-parameters-by-path \
  --path "/config" \
  --recursive \
  --with-decryption \
  --output table
