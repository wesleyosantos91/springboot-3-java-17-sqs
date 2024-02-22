#!/bin/bash

# Configuration Environment Variables
AWS_REGION="sa-east-1"
AWS_PROFILE="localstack-profile"
AWS_ENDPOINT_URL=http://localhost:4566
QUEUE_NAME="person"

aws sqs create-queue --queue-name $QUEUE_NAME --endpoint-url $AWS_ENDPOINT_URL --profile $AWS_PROFILE --region $AWS_REGION

aws sqs list-queues --endpoint-url $AWS_ENDPOINT_URL --profile $AWS_PROFILE --region $AWS_REGION