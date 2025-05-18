#!/bin/bash

# === SETTINGS ===
DOCKER_USERNAME="raniyoussef"
BACKEND_IMAGE_NAME="iot-backend"
FRONTEND_IMAGE_NAME="iot-frontend"

# === BUILD BACKEND ===
echo "🚀 Building backend image..."
docker build -t $DOCKER_USERNAME/$BACKEND_IMAGE_NAME ./backend
if [ $? -ne 0 ]; then
  echo "❌ Backend build failed"
  exit 1
fi

# === BUILD FRONTEND ===
echo "🚀 Building frontend image..."
docker build -t $DOCKER_USERNAME/$FRONTEND_IMAGE_NAME ./frontend
if [ $? -ne 0 ]; then
  echo "❌ Frontend build failed"
  exit 1
fi

# === PUSH BACKEND ===
echo "📤 Pushing backend image to Docker Hub..."
docker push $DOCKER_USERNAME/$BACKEND_IMAGE_NAME
if [ $? -ne 0 ]; then
  echo "❌ Backend push failed"
  exit 1
fi

# === PUSH FRONTEND ===
echo "📤 Pushing frontend image to Docker Hub..."
docker push $DOCKER_USERNAME/$FRONTEND_IMAGE_NAME
if [ $? -ne 0 ]; then
  echo "❌ Frontend push failed"
  exit 1
fi

echo "✅ All images built and pushed successfully!"
