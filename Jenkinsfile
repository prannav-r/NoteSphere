
pipeline {
    agent any

    environment {
        IMAGE_NAME = 'prannavr/notesphere'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out NoteSphere source code'
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                echo 'Building application and running JUnit tests'
                bat 'mvnw.cmd clean test package'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image'
                bat 'docker build -t %IMAGE_NAME%:latest .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    bat '''
                        @echo off
                        echo %DOCKER_TOKEN%| docker login -u "%DOCKER_USER%" --password-stdin
                        if errorlevel 1 exit /b 1

                        docker push %IMAGE_NAME%:latest
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'NoteSphere CI/CD pipeline completed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the Console Output.'
        }
    }
}
