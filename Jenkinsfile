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
                git url: 'https://github.com/RaniYoussef/Iot_system_DXC.git', branch: 'rani-sprint4-devops'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE}") {
                    // Scan backend Java code
                    dir('backend') {
                        sh 'sonar-scanner'
                    }
                }
            }
        }

        stage('Build Backend Docker Image') {
            steps {
                sh 'docker build -t $BACKEND_IMAGE:${BUILD_NUMBER} ./backend'
            }
        }

        stage('Build Frontend Docker Image') {
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
