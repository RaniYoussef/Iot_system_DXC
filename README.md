#  IoT Monitoring System – Full Stack Dockerized Application

This repository contains a full-stack IoT monitoring application composed of:

- **Backend**: Spring Boot (Java 21) RESTful API
- **Frontend**: Angular application (served via `ng serve` on port 4200)
- **Database**: MySQL 8.0
- **Cache**: Redis

> This project is containerized using **Docker Compose** (no Docker Desktop or Docker Rootless).

---

## Prerequisites

Make sure the following are installed on your system:

- Docker Engine (CLI-based)
- Docker Compose plugin
- Git
- WSL2 (for Windows users)
- Node.js (if you run frontend outside Docker – optional)

---

##  How to Run the Project

### Step 1: Start Docker daemon manually

In one terminal:
```bash
sudo dockerd
then enter your password 

Wait until you see:
INFO[...]: API listen on /var/run/docker.sock

Step 2: In a new terminal, set the Docker socket path
export DOCKER_HOST=unix:///var/run/docker.sock

Step 3: Start the application using Docker Compose
docker compose up --build

This will:

Build and start all services (backend, frontend, mysql, redis)

Serve Angular on http://localhost:4200

Serve Spring Boot backend on http://localhost:8080

To build and push the latest frontend and backend images to Docker Hub, use the provided shell script:
chmod +x build-and-push.sh
./build-and-push.sh
This will:

Build new Docker images for backend and frontend

Tag them as raniyoussef/iot-backend and raniyoussef/iot-frontend

For any future update (new features or bug fixes), follow this process:

 Apply your code changes (frontend or backend)

 Rebuild Docker images using:
 ./build-and-push.sh

Push the new version to Docker Hub (they’ll overwrite the previous latest tag)

Run docker compose up --pull always --build to ensure latest images are used

Maintainer
Name: Rani Youssef

Docker Hub: https://hub.docker.com/u/raniyoussef


////////////////////////////////////////////////////////////////////
Equivalent Manual Commands
1. Build the Docker Images
Manually build the frontend and backend images:
# Backend
docker build -t raniyoussef/iot-backend ./backend

# Frontend
docker build -t raniyoussef/iot-frontend ./frontend


2. Create Network and Volume (if not already created)
# Create network (only once)
docker network create iot_system_dxc_iot-net

# Create volume for MySQL (only once)
docker volume create iot_system_dxc_mysql-data


3. Start Each Container
Start the containers manually and connect them to the shared network:
# Start MySQL
docker run -d \
  --name mysql \
  --network iot_system_dxc_iot-net \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=iotdb \
  -e MYSQL_USER=iotuser \
  -e MYSQL_PASSWORD=iotpass \
  -v iot_system_dxc_mysql-data:/var/lib/mysql \
  -p 3306:3306 \
  mysql:8.0

# Start Redis
docker run -d \
  --name iot_redis \
  --network iot_system_dxc_iot-net \
  -p 6379:6379 \
  redis:7

# Start Backend
docker run -d \
  --name iot-backend \
  --network iot_system_dxc_iot-net \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/iotdb \
  -e SPRING_DATASOURCE_USERNAME=iotuser \
  -e SPRING_DATASOURCE_PASSWORD=iotpass \
  -e SPRING_REDIS_HOST=iot_redis \
  -e SPRING_REDIS_PORT=6379 \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  -p 8080:8080 \
  raniyoussef/iot-backend

# Start Frontend (Angular dev mode on port 4200)
docker run -d \
  --name iot-frontend \
  --network iot_system_dxc_iot-net \
  -p 4200:4200 \
  raniyoussef/iot-frontend
///////////////////////////////////////////////////////////////////////////////




