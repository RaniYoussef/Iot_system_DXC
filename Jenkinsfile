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
            steps {
                dir('frontend') {
                    withSonarQubeEnv("${SONARQUBE}") {
                        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                            sh '''
                                curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
                                apt-get install -y nodejs
                                npm install -g npm@latest
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

        stage('Build Backend Image') {
            steps {
                sh 'docker build -t $BACKEND_IMAGE:${BUILD_NUMBER} ./backend'
            }
        }

        stage('Build Frontend Image') {
            steps {
                sh 'docker build -t $FRONTEND_IMAGE:${BUILD_NUMBER} ./frontend'
            }
        }

        stage('Push Docker Images') {
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
