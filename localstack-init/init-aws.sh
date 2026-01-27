#!/bin/bash

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
sleep 5

# Set AWS credentials and endpoint
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

# Create SNS topic for feature toggles
echo "Creating SNS topic for feature toggles..."
aws --endpoint-url=http://localhost:4566 sns create-topic \
  --name feature-toggle-topic \
  --output text

# Get topic ARN
TOPIC_ARN=$(aws --endpoint-url=http://localhost:4566 sns list-topics \
  --query 'Topics[?TopicArn.contains(@.TopicArn, `feature-toggle-topic`)] | [0].TopicArn' \
  --output text)

echo "SNS Topic ARN: $TOPIC_ARN"

# Create SQS queue for meeting-room service
echo "Creating SQS queue for meeting-room service..."
aws --endpoint-url=http://localhost:4566 sqs create-queue \
  --queue-name metting-room-feature-toggle-topic.fifo \
  --attributes FifoQueue=true,ContentBasedDeduplication=true \
  --output text

# Get queue URL
QUEUE_URL=$(aws --endpoint-url=http://localhost:4566 sqs get-queue-url \
  --queue-name metting-room-feature-toggle-topic.fifo \
  --query 'QueueUrl' \
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

  # Set queue policy to allow SNS to send messages
  POLICY='{
    "Version": "2012-10-17",
    "Statement": [
      {
        "Effect": "Allow",
        "Principal": {
          "Service": "sns.amazonaws.com"
        },
        "Action": "sqs:SendMessage",
        "Resource": "'$QUEUE_ARN'",
        "Condition": {
          "ArnEquals": {
            "aws:SourceArn": "'$TOPIC_ARN'"
          }
        }
      }
    ]
  }'

  aws --endpoint-url=http://localhost:4566 sqs set-queue-attributes \
    --queue-url "$QUEUE_URL" \
    --attributes Policy="$POLICY" \
    --output text
fi

# Create application parameters in Parameter Store
echo "Creating AWS Parameter Store parameters..."

aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/application/spring/datasource/url" \
  --value "jdbc:postgresql://postgres:5432/meeting_room_db" \
  --type "String" \
  --overwrite 2>/dev/null || true

aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/application/spring/datasource/username" \
  --value "postgres" \
  --type "String" \
  --overwrite 2>/dev/null || true

aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/application/spring/datasource/password" \
  --value "postgres" \
  --type "SecureString" \
  --overwrite 2>/dev/null || true

aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/application/aws/sns/topic-arn" \
  --value "$TOPIC_ARN" \
  --type "String" \
  --overwrite 2>/dev/null || true

aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/toggle-stream/server/port" \
  --value "8081" \
  --type "String" \
  --overwrite 2>/dev/null || true

aws --endpoint-url=http://localhost:4566 ssm put-parameter \
  --name "/config/meeting-room/server/port" \
  --value "8082" \
  --type "String" \
  --overwrite 2>/dev/null || true

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

