# Bank Microservices Application

## Project Overview
This project is a pseudo-banking application built with a microservices architecture. It comprises three main microservices:
- **Accounts**: Manages customer and customer's account information.
- **Cards**: Manages customer card information.
- **Loans**: Manages loan information for customers.

The application utilizes **Spring Cloud Config Server** for centralized configuration management. Instant configuration updates are managed through GitHub webhook integration and RabbitMQ, ensuring services remain up-to-date without manual intervention. Each microservice interacts via **Eureka** for service registration and discovery, and **Feign Client** for streamlined REST API calls.

## Architecture
- **Spring Cloud Config Server**: Centralized configuration server for all microservices. This setup ensures seamless updates to configurations without restarting services.
- **GitHub Webhook**: Used to monitor changes in the configuration repository and trigger RabbitMQ for updates.
- **RabbitMQ**: Manages the messages and notifications about configuration changes to the microservices.
- **Eureka Server**: Acts as a service registry that allows all microservices to locate each other.
- **Feign Client**: Facilitates easy and declarative HTTP client communication between microservices.

## Technologies & Tools
- **Java 21**
- **Spring Boot 3.x**
- **Spring Cloud (Eureka, Feign, Config Server)**
- **RabbitMQ**
- **GitHub Webhook**
- **Docker (for containerization)**
- **PostgreSQL/MySQL** (as database)

## Installation and Setup
### Requirements
- **Java 21** or above
- **Docker and Docker Compose** (recommended for deployment)
- **RabbitMQ**, **PostgreSQL/MySQL** (can be used via Docker)

### Deployment Instructions
1. **Clone the Repository**:
    ```bash
    git clone https://github.com/andriik7/microservices_bank_app.git
    cd microservices_bank_app
    ```

2. **Set Up Infrastructure** (RabbitMQ, Eureka, Config Server, etc.) using Docker Compose:
    ```bash
    # Use common configuration with different environments
    docker-compose -f docker-compose/default/docker-compose.yml up -d
    ```
    - For **production**:
        ```bash
        docker-compose -f docker-compose/prod/docker-compose.yml up -d
        ```
    - For **QA**:
        ```bash
        docker-compose -f docker-compose/qa/docker-compose.yml up -d
        ```
    - The common configurations are located in the same directory under `common-config` and are included in all environment-specific files.

3. **Running Microservices**:
    Use the following command to run each microservice:
    ```bash
    cd accounts
    mvn spring-boot:run
    ```
    ```bash
    cd cards
    mvn spring-boot:run
    ```
    ```bash
    cd loans
    mvn spring-boot:run
    ```
    **Note:** Ensure Config Server and RabbitMQ are up and running so that all microservices can function correctly.

## Microservices Communication
- **Eureka Server** allows microservices to register and discover each other, providing efficient communication between them (available in a separate branch).
- **Feign Client** is used for interactions between services, such as when the `Accounts` service needs to get loan information from `Loans` (available in a separate branch).
- **RabbitMQ** is utilized to handle asynchronous communication, especially for configuration updates and critical events.

## Docker Deployment
To build Docker images using **JIB**, please follow the instructions below. You must modify the image name in the `pom.xml` to match your Docker Hub username before running the build.

1. **Ensure Docker is logged in to your Docker Hub account**:
    ```bash
    docker login
    ```

2. **Update the Image Name in `pom.xml`**:
    - In each microservice's `pom.xml`, find the following snippet under the `<plugin>` configuration for `JIB`:
    
    ```xml
    <configuration>
        <to>
            <image>akuchera/${project.artifactId}:${project.version}</image>
        </to>
    </configuration>
    ```

    - Replace `akuchera` with your Docker Hub username:
    
    ```xml
    <configuration>
        <to>
            <image>your-username/${project.artifactId}:${project.version}</image>
        </to>
    </configuration>
    ```

3. **Build and Push the Image Using JIB**:
    Navigate to each microservice directory and build the image with JIB using the command below:

    - For **Accounts Service**:
        ```bash
        cd accounts
        mvn compile jib:build
        ```
    
    - For **Cards Service**:
        ```bash
        cd cards
        mvn compile jib:build
        ```
    
    - For **Loans Service**:
        ```bash
        cd loans
        mvn compile jib:build
        ```

    This will create and push the Docker image to your Docker Hub repository without needing Dockerfile or a local Docker daemon.

## Configuration Update with Webhooks
The Config Server is integrated with a GitHub repository. Upon any change in the configuration files, GitHub sends a webhook, which in turn triggers a RabbitMQ message to notify all microservices about the configuration update.

## Additional Branches and Configurations
There are additional branches available that provide extended functionality and configurations:

- **Branch: `mysql_connection_feature`**  
  This branch contains pre-configured Docker files and connections to MySQL databases for each microservice. Each database is pre-populated with data during startup, allowing easy testing and demonstration.

- **Branch: `service_registry_feature`**  
  This branch integrates **Eureka Server** and **Feign Client** to enable service discovery and inter-service communication.
  
  These features are not merged with the `main` branch to keep it lightweight and to avoid unnecessary dependencies for simpler use cases.

### Local Testing Instructions
To test the full feature set locally (including MySQL, Eureka, and Feign Client):
1. **Checkout the Additional Branches**:
    - First, switch to the `mysql_connection_feature` branch and merge it:
      ```bash
      git checkout mysql_connection_feature
      git merge mysql_connection_feature
      ```
    - First, switch to the `service_registry_feature` branch and merge it:
      ```bash
      git checkout service_registry_feature
      git merge service_registry_feature
      ```

2. **Set Up and Run**:
    Follow the similar Docker Compose instructions provided above to set up the environment with MySQL databases and service registry.

This approach allows you to customize and extend the `main` branch with different features, depending on your specific requirements.

## API Documentation
The API documentations for `accounts`, `cards` and `loans` are available at: `http://localhost:{port}/swagger-ui.html` after the service is up.

## Contributing
We welcome contributions! Please create a pull request or open an issue for any discussion.

## Authors
- **Andrii Kuchera** - [GitHub Profile](https://github.com/andriik7)
