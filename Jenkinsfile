pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "raniyoussef/iot-system"
        SONARQUBE = 'SonarQube'
    }

    stages {
        stage('Clone Repo') {
            steps {
                git url: 'https://github.com/RaniYoussef/Iot_system_DXC.git', branch: 'rani-sprint4-devops'
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean install'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
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

        stage('Push to Docker Hub') {
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
