#!/bin/sh

VERSION=1.0.0

docker build -f ./Dockerfile -t xcancloud/config-generator:${VERSION} .
docker push xcancloud/config-generator:${VERSION}
