pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "raniyoussef/iot-system"
        SONARQUBE = 'SonarQube'  // this name must match the one in Jenkins > Manage Jenkins > Configure System
    }

    tools {
        maven 'Maven 3.8.1'
        nodejs 'NodeJS 18'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git url: 'https://github.com/RaniYoussef/Iot_system_DXC.git', branch: 'dev'
            }
        }

        stage('Build Backend') {
            dir('backend') {
                steps {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Build Frontend') {
            dir('frontend') {
                steps {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE}") {
                    sh 'sonar-scanner'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:${BUILD_NUMBER} .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $DOCKER_IMAGE:${BUILD_NUMBER}
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
