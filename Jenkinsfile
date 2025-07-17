pipeline {
    agent any

    environment {
        BACKEND_IMAGE = "raniyoussef/iot-backend"
        FRONTEND_IMAGE = "raniyoussef/iot-frontend"
        SONARQUBE = 'SonarQube'
    }

    stages {
        stage('Clone Repo') {
            steps {
                git url: 'https://github.com/RaniYoussef/Iot_system_DXC.git', branch: "${BRANCH_NAME}"
            }
        }

        stage('SonarQube Analysis - Backend') {
            steps {
                dir('backend') {
                    withSonarQubeEnv("${SONARQUBE}") {
                        sh '''
                            chmod +x mvnw
                            ./mvnw clean verify sonar:sonar -DskipTests
                        '''
                    }
                }
            }
        }

        stage('SonarQube Analysis - Frontend') {
            steps {
                dir('frontend') {
                    withSonarQubeEnv("${SONARQUBE}") {
                        sh '''
                            npm install
                            npm run test -- --code-coverage
                            sonar-scanner \
                              -Dsonar.projectKey=iot-frontend \
                              -Dsonar.projectName=iot-frontend \
                              -Dsonar.sources=src \
                              -Dsonar.exclusions=**/node_modules/**,**/*.spec.ts \
                              -Dsonar.typescript.lcov.reportPaths=coverage/lcov.info
                        '''
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
