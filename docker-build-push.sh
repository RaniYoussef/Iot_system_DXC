#!/bin/bash

docker build -t raniyoussef/iot-backend ./backend
docker build -t raniyoussef/iot-frontend ./frontend

docker push raniyoussef/iot-backend
docker push raniyoussef/iot-frontend
