#!/bin/bash

set -e

echo "=== Waiting for LocalStack SSM parameters to be ready ==="

# Set AWS credentials for CLI
export AWS_ACCESS_KEY_ID=guest
export AWS_SECRET_ACCESS_KEY=guest
export AWS_DEFAULT_REGION=sa-east-1

# Check if aws CLI is available
if ! command -v aws &> /dev/null; then
    echo "ERROR: AWS CLI not found!"
    exit 1
fi

echo "AWS CLI version: $(aws --version)"

# Wait for LocalStack to be healthy
echo "Checking LocalStack health..."
MAX_HEALTH_RETRIES=30
HEALTH_RETRY_COUNT=0

while [ $HEALTH_RETRY_COUNT -lt $MAX_HEALTH_RETRIES ]; do
  HEALTH_CHECK=$(curl -s http://localstack:4566/_localstack/health 2>/dev/null)
  if echo "$HEALTH_CHECK" | grep -q '"ssm": "available"'; then
    echo "LocalStack SSM service is available!"
    break
  fi
  echo "LocalStack not ready yet (attempt $((HEALTH_RETRY_COUNT + 1))/$MAX_HEALTH_RETRIES)..."
  HEALTH_RETRY_COUNT=$((HEALTH_RETRY_COUNT + 1))
  sleep 2
done

if [ $HEALTH_RETRY_COUNT -eq $MAX_HEALTH_RETRIES ]; then
  echo "WARNING: LocalStack health check timed out, continuing anyway..."
fi

# Additional wait for init script to complete
echo "Waiting additional 10 seconds for init scripts to complete..."
sleep 10

# Wait for parameters to be created
echo "Checking for SSM parameters..."
MAX_RETRIES=20
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
  echo "Attempting to retrieve parameter (attempt $((RETRY_COUNT + 1))/$MAX_RETRIES)..."

  # Try to list parameters first
  PARAM_COUNT=$(aws --endpoint-url=http://localstack:4566 ssm describe-parameters \
    --parameter-filters "Key=Name,Option=BeginsWith,Values=/config/meeting-room" \
    --query 'length(Parameters)' \
    --output text 2>&1)

  echo "Parameters found: $PARAM_COUNT"

  if [ "$PARAM_COUNT" != "" ] && [ "$PARAM_COUNT" != "0" ] && [ "$PARAM_COUNT" != "None" ]; then
    echo "SUCCESS: Found $PARAM_COUNT parameters in SSM!"

    # List all parameters for debugging
    echo "Listing parameters:"
    aws --endpoint-url=http://localstack:4566 ssm describe-parameters \
      --parameter-filters "Key=Name,Option=BeginsWith,Values=/config/meeting-room" \
      --query 'Parameters[*].Name' \
      --output table 2>&1 || true

    echo "=== Ready to start application ==="
    exit 0
  fi

  RETRY_COUNT=$((RETRY_COUNT + 1))
  sleep 3
done

echo "WARNING: Parameters not found after $MAX_RETRIES attempts"
echo "Attempting to continue anyway..."
exit 0

