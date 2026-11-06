# NoteSphere – Automated CI/CD Pipeline with Jenkins, Docker and Azure Kubernetes Service

NoteSphere is a Java-based notes management application deployed using an end-to-end DevOps CI/CD pipeline. The project demonstrates how modern DevOps tools can be integrated to automate application building, testing, security scanning, containerization, image publishing, and cloud deployment.

The application is built using Java 21 and Spring Boot, with a simple HTML, CSS, and JavaScript frontend. The CI/CD pipeline is implemented using Jenkins and integrates Maven, Gitleaks, Docker, Azure Container Registry, Ansible, and Azure Kubernetes Service (AKS).

## Features

- Create, view, update, and delete notes.
- RESTful API endpoints for note management.
- Health-check endpoint for application availability.
- Automated application build and testing using Maven.
- Source-code security scanning using Gitleaks.
- Docker-based application containerization.
- Automated Docker image publishing to Azure Container Registry.
- Kubernetes deployment using Ansible.
- Cloud hosting using Azure Kubernetes Service.
- External application access through a Kubernetes LoadBalancer service.

## Technology Stack

| Category | Technology |
|---|---|
| Backend | Java 21, Spring Boot |
| Frontend | HTML, CSS, JavaScript |
| Build Tool | Apache Maven |
| Testing | JUnit |
| Version Control | Git, GitHub |
| CI/CD | Jenkins |
| Security Scanning | Gitleaks |
| Containerization | Docker |
| Container Registry | Azure Container Registry (ACR) |
| Configuration Management | Ansible, Jinja2 |
| Container Orchestration | Kubernetes |
| Cloud Platform | Microsoft Azure |
| Managed Kubernetes | Azure Kubernetes Service (AKS) |

## System Architecture

The project follows the workflow below:

```text
Developer
    |
    v
GitHub Repository
    |
    v
GitHub Webhook
    |
    v
ngrok Tunnel
    |
    v
Jenkins CI/CD Pipeline
    |
    +--> Environment Verification
    |
    +--> Gitleaks Secret Scan
    |
    +--> Maven Build and JUnit Tests
    |
    +--> Docker Image Build
    |
    +--> Push Image to Azure Container Registry
    |
    +--> Ansible Deployment
              |
              v
       Azure Kubernetes Service
              |
              v
       Kubernetes Deployment
              |
              v
       NoteSphere Application Pods
              |
              v
       LoadBalancer Service
              |
              v
       External Application Access
```

AKS uses its managed identity with the `AcrPull` permission to retrieve application images from Azure Container Registry.

## Project Structure

```text
NoteSphere/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   └── resources/
│   │       └── ...
│   │
│   └── test/
│       └── ...
│
├── ansible/
│   ├── deploy.yml
│   └── k8s/
│       ├── deployment.yaml.j2
│       └── service.yaml
│
├── Dockerfile
├── Jenkinsfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Running the Application Locally

### Prerequisites

- Java 21
- Git
- Maven Wrapper (included in the repository)

### 1. Clone the repository

```bash
git clone https://github.com/prannav-r/NoteSphere.git
cd NoteSphere
```

### 2. Build and test the application

On Windows PowerShell:

```powershell
.\mvnw.cmd clean test package
```

On Linux:

```bash
chmod +x mvnw
./mvnw clean test package
```

### 3. Run the application

On Windows:

```powershell
java -jar target/notesphere-0.0.1-SNAPSHOT.jar
```

On Linux:

```bash
java -jar target/notesphere-0.0.1-SNAPSHOT.jar
```

The application runs on:

```text
http://localhost:8080
```

Health-check endpoint:

```text
http://localhost:8080/api/health
```

## REST API Endpoints

| HTTP Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notes` | Retrieve all notes |
| GET | `/api/notes/{id}` | Retrieve a note by ID |
| POST | `/api/notes` | Create a new note |
| PUT | `/api/notes/{id}` | Update an existing note |
| DELETE | `/api/notes/{id}` | Delete a note |
| GET | `/api/health` | Check application health |

## Docker Configuration

The application is containerized using Docker through a multi-stage Dockerfile.

The first stage uses Maven and Java 21 to build the application. The second stage uses a Java 21 runtime image to execute the generated JAR file.

### 1. Build the Docker image

```bash
docker build -t notesphere:latest .
```

### 2. Run the container

```bash
docker run -d --name notesphere -p 8080:8080 notesphere:latest
```

### 3. Verify the container

```bash
docker ps
docker images
```

Access the application:

```text
http://localhost:8080
```

## Jenkins CI/CD Pipeline

Jenkins automates the complete software delivery lifecycle. The pipeline is triggered through GitHub webhooks whenever changes are pushed to the configured repository.

Since Jenkins runs on a local Windows machine, ngrok is used to expose the Jenkins webhook endpoint to GitHub.

### Pipeline Stages

| Stage | Description |
|---|---|
| Environment Verification | Checks required tools, Docker, Azure authentication, and Kubernetes configuration. |
| Gitleaks Secret Scan | Scans the repository for hardcoded secrets. |
| Build and Test | Executes Maven build and JUnit tests. |
| Docker Image Build | Builds the application container image. |
| Push to ACR | Authenticates with Azure and pushes the image to ACR. |
| Deploy to AKS | Executes the Ansible playbook to deploy Kubernetes resources. |

### Image Tagging

Each Docker image is tagged using the Jenkins build number.

```text
acrnotesphere.azurecr.io/notesphere:<BUILD_NUMBER>
```

This makes it possible to identify the image associated with a particular pipeline execution.

## Azure Infrastructure

The application is deployed using the following Azure resources:

| Resource | Name |
|---|---|
| Resource Group | `rg-notesphere` |
| Azure Container Registry | `acrnotesphere` |
| ACR Login Server | `acrnotesphere.azurecr.io` |
| AKS Cluster | `aks-notesphere` |
| Node Pool | `systempool2` |
| Region | Central India |
| Node VM Size | Standard_D2s_v4 |

Azure Container Registry stores the Docker images, while AKS provides the managed Kubernetes environment for running the application.

## Ansible Deployment

Ansible is used to automate Kubernetes deployment operations.

The Ansible playbook is located at:

```text
ansible/deploy.yml
```

It performs the following operations:

1. Generates the Kubernetes Deployment manifest from `deployment.yaml.j2`.
2. Inserts the image repository and tag passed by Jenkins.
3. Applies the Deployment resource to AKS.
4. Applies the LoadBalancer Service resource.
5. Retrieves and displays Deployment information.

The playbook uses the Kubernetes collection `kubernetes.core` to communicate with the AKS API server.

Jenkins executes Ansible inside a Docker container and mounts the project workspace and Kubernetes configuration file into that container.

## Kubernetes Deployment Verification

Use the following commands to verify the AKS deployment.

### Check cluster nodes

```bash
kubectl get nodes
```

### Check application pods

```bash
kubectl get pods -o wide
```

### Check deployments

```bash
kubectl get deployments
```

### Check services

```bash
kubectl get svc
```

The NoteSphere Service uses the `LoadBalancer` type to expose the application externally.

## Application Access

The application can be accessed using the external IP assigned to the Kubernetes LoadBalancer service.

At the time of project verification, the external IP was:

```text
http://52.140.83.103:8080/
```

Health-check endpoint:

```text
http://52.140.83.103:8080/api/health
```

The external IP may change if the LoadBalancer service is recreated. To obtain the current address, execute:

```bash
kubectl get svc notesphere-service
```

## Security and Authentication

The project incorporates the following security mechanisms:

- Gitleaks scans the repository for accidentally committed secrets.
- Jenkins uses Azure CLI authentication to publish Docker images to ACR.
- AKS uses its managed identity with the `AcrPull` permission to retrieve images from ACR.
- Kubernetes configuration is mounted into the Ansible container for deployment access.

Credentials and sensitive configuration should not be committed to the GitHub repository.

## Key DevOps Concepts Demonstrated

- Continuous Integration and Continuous Delivery (CI/CD).
- GitHub webhook-based pipeline triggering.
- Automated build and unit testing.
- Secret detection and security scanning.
- Docker image creation and version tagging.
- Container registry management.
- Infrastructure and deployment automation using Ansible.
- Kubernetes Deployments and Services.
- Managed Kubernetes deployment using Azure AKS.
- Cloud-based application exposure using LoadBalancer services.

## Future Enhancements

- Implement automated rollback for unsuccessful deployments.
- Integrate monitoring and centralized logging.
- Configure Kubernetes Horizontal Pod Autoscaling.
- Add container image vulnerability scanning.
- Integrate a persistent database for note storage.
- Introduce multiple worker nodes and replicas for improved availability.
- Add automated post-deployment integration testing.

## Author

**Prannav R**

GitHub: [prannav-r](https://github.com/prannav-r)

Project Repository: [NoteSphere](https://github.com/prannav-r/NoteSphere)

---

*NoteSphere demonstrates the integration of modern DevOps tools to automate the complete application delivery lifecycle, from source-code changes to cloud deployment.*