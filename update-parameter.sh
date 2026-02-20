#!/bin/bash

# Script to update AWS SSM Parameter Store parameters and refresh Spring Boot application
# Uses LocalStack for local development

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
AWS_ENDPOINT="${AWS_ENDPOINT:-http://localhost:4566}"
AWS_REGION="${AWS_REGION:-sa-east-1}"
SPRING_REFRESH_URL="${SPRING_REFRESH_URL:-http://localhost:8082/actuator/refresh}"

echo -e "${BLUE}================================================${NC}"
echo -e "${BLUE}   AWS SSM Parameter Store Update Script${NC}"
echo -e "${BLUE}================================================${NC}"
echo ""
echo -e "AWS Endpoint: ${GREEN}${AWS_ENDPOINT}${NC}"
echo -e "AWS Region: ${GREEN}${AWS_REGION}${NC}"
echo -e "Refresh URL: ${GREEN}${SPRING_REFRESH_URL}${NC}"
echo ""

# Ask for parameter name
echo -e "${YELLOW}Enter the parameter name:${NC}"
echo -e "${BLUE}Example: /config/meeting-room/feature/toggles/reservation.capacity-check${NC}"
read -p "> " PARAMETER_NAME

if [ -z "$PARAMETER_NAME" ]; then
    echo -e "${RED}Error: Parameter name cannot be empty${NC}"
    exit 1
fi

# Ask for parameter value
echo ""
echo -e "${YELLOW}Enter the new value:${NC}"
echo -e "${BLUE}Example: true, false, jdbc:postgresql://localhost:5432/mydb${NC}"
read -p "> " PARAMETER_VALUE

if [ -z "$PARAMETER_VALUE" ]; then
    echo -e "${RED}Error: Parameter value cannot be empty${NC}"
    exit 1
fi

# Ask for parameter type
echo ""
echo -e "${YELLOW}Select the parameter type:${NC}"
echo "1) String"
echo "2) SecureString"
echo "3) StringList"
read -p "Enter option (1-3) [default: 1]: " TYPE_OPTION

case $TYPE_OPTION in
    2)
        PARAMETER_TYPE="SecureString"
        ;;
    3)
        PARAMETER_TYPE="StringList"
        ;;
    *)
        PARAMETER_TYPE="String"
        ;;
esac

# Confirm the operation
echo ""
echo -e "${BLUE}================================================${NC}"
echo -e "${YELLOW}Please confirm the following details:${NC}"
echo -e "${BLUE}================================================${NC}"
echo -e "Parameter Name: ${GREEN}${PARAMETER_NAME}${NC}"
echo -e "Parameter Value: ${GREEN}${PARAMETER_VALUE}${NC}"
echo -e "Parameter Type: ${GREEN}${PARAMETER_TYPE}${NC}"
echo ""
read -p "Do you want to proceed? (yes/no) [default: yes]: " CONFIRM

if [[ ! -z "$CONFIRM" && "$CONFIRM" != "yes" && "$CONFIRM" != "y" ]]; then
    echo -e "${YELLOW}Operation cancelled${NC}"
    exit 0
fi

# Update parameter in SSM
echo ""
echo -e "${BLUE}================================================${NC}"
echo -e "${YELLOW}Step 1: Updating parameter in SSM...${NC}"
echo -e "${BLUE}================================================${NC}"

UPDATE_RESULT=$(aws --endpoint-url=${AWS_ENDPOINT} \
    --region=${AWS_REGION} \
    ssm put-parameter \
    --name "${PARAMETER_NAME}" \
    --value "${PARAMETER_VALUE}" \
    --type "${PARAMETER_TYPE}" \
    --overwrite \
    --output json 2>&1)

if [ $? -eq 0 ]; then
    VERSION=$(echo "$UPDATE_RESULT" | grep -o '"Version": [0-9]*' | grep -o '[0-9]*')
    echo -e "${GREEN}✓ Parameter updated successfully!${NC}"
    echo -e "  Version: ${GREEN}${VERSION}${NC}"
else
    echo -e "${RED}✗ Failed to update parameter${NC}"
    echo -e "${RED}${UPDATE_RESULT}${NC}"
    exit 1
fi

# Verify the update
echo ""
echo -e "${YELLOW}Verifying parameter value...${NC}"
VERIFY_RESULT=$(aws --endpoint-url=${AWS_ENDPOINT} \
    --region=${AWS_REGION} \
    ssm get-parameter \
    --name "${PARAMETER_NAME}" \
    --with-decryption \
    --query 'Parameter.Value' \
    --output text 2>&1)

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Current value: ${VERIFY_RESULT}${NC}"
else
    echo -e "${YELLOW}⚠ Could not verify parameter value${NC}"
fi

# Call refresh endpoint
echo ""
echo -e "${BLUE}================================================${NC}"
echo -e "${YELLOW}Step 2: Refreshing Spring Boot application...${NC}"
echo -e "${BLUE}================================================${NC}"

REFRESH_RESULT=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    "${SPRING_REFRESH_URL}" \
    -w "\nHTTP_CODE:%{http_code}" 2>&1)

HTTP_CODE=$(echo "$REFRESH_RESULT" | grep "HTTP_CODE" | cut -d: -f2)

if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "204" ]; then
    echo -e "${GREEN}✓ Application refreshed successfully!${NC}"

    # Extract and display changed properties
    CHANGED_KEYS=$(echo "$REFRESH_RESULT" | grep -v "HTTP_CODE" | grep -o '"[^"]*"' | tr -d '"' | grep -v "^$")

    if [ ! -z "$CHANGED_KEYS" ]; then
        echo -e "${YELLOW}Changed properties:${NC}"
        echo "$CHANGED_KEYS" | while read -r key; do
            echo -e "  - ${GREEN}${key}${NC}"
        done
    else
        echo -e "${BLUE}No properties were refreshed (this is normal if the parameter is not cached)${NC}"
    fi
elif [ "$HTTP_CODE" = "000" ] || [ -z "$HTTP_CODE" ]; then
    echo -e "${YELLOW}⚠ Could not reach the application${NC}"
    echo -e "${BLUE}The parameter was updated, but the application may not be running${NC}"
    echo -e "${BLUE}Please restart the application manually to apply changes${NC}"
else
    echo -e "${YELLOW}⚠ Refresh endpoint returned HTTP ${HTTP_CODE}${NC}"
    echo -e "${BLUE}The parameter was updated, but automatic refresh may have failed${NC}"
    echo -e "${BLUE}Please restart the application manually to apply changes${NC}"
fi

# Summary
echo ""
echo -e "${BLUE}================================================${NC}"
echo -e "${GREEN}Summary${NC}"
echo -e "${BLUE}================================================${NC}"
echo -e "Parameter: ${GREEN}${PARAMETER_NAME}${NC}"
echo -e "New Value: ${GREEN}${PARAMETER_VALUE}${NC}"
echo -e "Type: ${GREEN}${PARAMETER_TYPE}${NC}"
echo -e "Status: ${GREEN}✓ Updated${NC}"
echo ""
echo -e "${BLUE}To view all parameters:${NC}"
echo -e "  aws --endpoint-url=${AWS_ENDPOINT} ssm get-parameters-by-path --path /config/meeting-room --recursive"
echo ""
echo -e "${GREEN}Done!${NC}"

