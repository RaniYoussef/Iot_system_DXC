pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-pat-amrkamoun')
        BACKEND_IMAGE = 'amrkamoun/iot-backend'
        FRONTEND_IMAGE = 'amrkamoun/iot-frontend'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            agent {
                docker {
                    image 'eclipse-temurin:21-jdk-alpine'
                    args '-v /root/.m2:/root/.m2' // optional: cache Maven repo
                }
            }
            steps {
                dir('backend') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Test Backend') {
            agent {
                docker {
                    image 'eclipse-temurin:21-jdk-alpine'
                    args '-v /root/.m2:/root/.m2' // optional: cache Maven repo
                }
            }
            steps {
                dir('backend') {
                    sh 'chmod +x ./mvnw'
                    sh './mvnw test'
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                dir('backend') {
                    sh "docker build -t $BACKEND_IMAGE:$IMAGE_TAG ."
                }
            }
        }

        stage('Build Frontend') {
            agent {
                docker {
                    image 'node:18-alpine'
                    args '-v /root/.npm:/root/.npm' // optional: cache npm packages
                }
            }
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'  // Adjust if you have a different build command
                }
            }
        }

        stage('Test Frontend') {
            agent {
                docker {
                    image 'node:18-alpine'
                    args '-v /root/.npm:/root/.npm' // optional: cache npm packages
                }
            }
            steps {
                dir('frontend') {
                    sh 'npm test -- --watch=false'  // Run tests once, no watch mode
                }
            }
        }

        stage('Build Frontend Docker Image') {
            steps {
                dir('frontend') {
                    sh "docker build -t $FRONTEND_IMAGE:$IMAGE_TAG ."
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry('', 'dockerhub-pat-amrkamoun') {
                        sh "docker push $BACKEND_IMAGE:$IMAGE_TAG"
                        sh "docker push $FRONTEND_IMAGE:$IMAGE_TAG"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploy stage - add your deployment commands here'
                // e.g., ssh to server and pull new images, restart services
            }
        }
    }
}
