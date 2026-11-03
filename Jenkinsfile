pipeline {
    agent any

    environment {
        IMAGE_NAME = 'acrnotesphere.azurecr.io/notesphere'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out NoteSphere source code'
                checkout scm
            }
        }

	
stage('Verify Jenkins Environment') {
    steps {
        bat '''
            echo === Jenkins Service Account ===
            whoami

            echo === User Profile ===
            echo %USERPROFILE%

            echo === Kubernetes Configuration ===
            if exist "%USERPROFILE%\\.kube\\config" (
                echo Kubeconfig found
            ) else (
                echo Kubeconfig NOT found
                exit /b 1
            )

            echo === Docker ===
            docker info --format "{{.ServerVersion}}"
            if errorlevel 1 exit /b 1

            echo === Azure Authentication ===
            az account show --query name -o tsv
            if errorlevel 1 exit /b 1
        '''
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
                echo 'Building Docker image with Jenkins build number'
                bat '''
                    docker build -t %IMAGE_NAME%:%BUILD_NUMBER% .
                    if errorlevel 1 exit /b 1
                '''
            }
        }

        stage('Push to ACR') {
            steps {
                echo 'Pushing image to Azure Container Registry'
                bat '''
                    az acr login --name acrnotesphere
                    if errorlevel 1 exit /b 1

                    docker push %IMAGE_NAME%:%BUILD_NUMBER%
                    if errorlevel 1 exit /b 1
                '''
            }
        }

        stage('Deploy to AKS using Ansible') {
            steps {
                echo 'Deploying NoteSphere to AKS using Ansible'

                bat '''
                    docker run --rm ^
                      -v "%WORKSPACE%:/ansible" ^
                      --mount "type=bind,source=%USERPROFILE%\\.kube,target=/root/.kube,readonly" ^
                      notesphere-ansible:latest ^
                      ansible-playbook /ansible/ansible/deploy.yml ^
                      -e image_repository=%IMAGE_NAME% ^
                      -e image_tag=%BUILD_NUMBER%
                '''
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
