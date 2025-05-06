#!/bin/bash

IMAGE_NAME=raniyoussef/iot-backend
TAG=latest

cd backend
./mvnw clean package -DskipTests || exit 1

docker build -t $IMAGE_NAME:$TAG .
docker push $IMAGE_NAME:$TAG