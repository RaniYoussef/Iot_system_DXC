pipeline {
    agent any

    environment {
        BACKEND_IMAGE = "raniyoussef/iot-backend"
        FRONTEND_IMAGE = "raniyoussef/iot-frontend"
        SONARQUBE = 'SonarQube'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/RaniYoussef/Iot_system_DXC.git', branch: "${BRANCH_NAME}"
            }
        }

        stage('SonarQube Analysis - Backend') {
            steps {
                dir('backend') {
                    withSonarQubeEnv("${SONARQUBE}") {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh '''
                                chmod +x mvnw
                                ./mvnw clean verify sonar:sonar -DskipTests -Dsonar.login=$SONAR_TOKEN
                            '''
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis - Frontend') {
            agent {
                docker {
                    image 'node:20'
                    args '-u 0:0'  // Run as root for permission safety
                }
            }
            steps {
                dir('frontend') {
                    withSonarQubeEnv("${SONARQUBE}") {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh '''
                                npm install --legacy-peer-deps
                                npx sonar-scanner \
                                  -Dsonar.projectKey=iot-frontend \
                                  -Dsonar.projectName=iot-frontend \
                                  -Dsonar.sources=src \
                                  -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts \
                                  -Dsonar.login=$SONAR_TOKEN
                            '''
                        }
                    }
                }
            }
        }

        stage('Build Backend Image (Tagged)') {
            steps {
                sh 'docker build -t $BACKEND_IMAGE:${BUILD_NUMBER} ./backend'
            }
        }

        stage('Build Frontend Image (Tagged)') {
            steps {
                sh 'docker build -t $FRONTEND_IMAGE:${BUILD_NUMBER} ./frontend'
            }
        }

        stage('Push Images to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $BACKEND_IMAGE:${BUILD_NUMBER}
                        docker push $FRONTEND_IMAGE:${BUILD_NUMBER}
                    '''
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
