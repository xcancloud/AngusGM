#!/bin/sh

IMAGE_NAME=xcancloud/config-generator
IMAGE_VERSION=1.0.0

docker build -f ./Dockerfile -t ${IMAGE_NAME}:${IMAGE_VERSION} .
docker push ${IMAGE_NAME}:${IMAGE_VERSION}

docker tag "$IMAGE_NAME:$IMAGE_VERSION" "$IMAGE_NAME:latest"
docker push "$IMAGE_NAME:latest"
