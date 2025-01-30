# Product micro service

## Overview
This microservice **handles product CRUD operations and sends product details to Kafka**.


## Components
**Product Service**
- Model
  - Product (id, name, product, description)
- Controller
  - ProductController
  - HomeController
- Service
  - ProductService (business logic)
- Repository
  - ProductRepository (database interaction)
- Kafka
  - ProductProducer (sends product data to Kafka)
- Exception
  - GlobalExceptionHandler (global exception handler to handle exceptions across the application)
  - ProductServiceException (custom exception class to handle specific errors)
  - ProductNotFoundException (custom exception class to handle product not found error)
- Config
  - KafkaConfig
  - SecurityConfig

## Request Body: JSON
```json
{
"name": "Product 1",
"price": 19.99,
"description": "Product 1 description"
}
```

## Technologies Used
- Java 17
- Spring Boot 3.4.2
- Spring WebFlux
- Spring Data JPA
- MySQL
- Kafka
- Docker (for running Kafka)
- Swagger-UI (for API documentation)

## Spring Security
- Username: admin
- Password: password

## Steps to Run the Application

**Set up Kafka using Docker**:
1. Install [Docker](https://www.docker.com/)
2. Verify Docker by executing the following command:
    ```bash
    docker --version
    ```
3. Run Kafka by executing the following command:
    ```bash
    docker-compose up -d
    ```
4. Verify Kafka is running
    ```bash
    docker ps
    ```

To stop and remove Containers, run
    ```bash
    docker-compose down
    ```

**Run the Application**:
- Clone and open the repository in your IDE (e.g., IntelliJ).
- Set up the MySQL database:
  - Create a database named `your_database_name`.
  - Update the `application.yaml` file with your database details.
    Example:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
    spring.datasource.username=your_mysql_username
    spring.datasource.password=your_mysql_password
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```
- Run the Spring Boot application as a Java application.

**Accessing the Application**:
- Open the browser and navigate to `http://localhost:8080` for the application API.
- Use Postman or any API client to interact with the API.

### API Endpoints

#### Create a Product

- **URL**: `/products`
- **Method**: `POST`
- **Request Body**:
    ```json
    {
      "name": "Product 1",
      "price": 19.99,
      "description": "Product 1 description"
    }
    ```
- **Response**:
    - Status: `201 Created`
    - Body:
    ```json
    {
      "id": 1,
      "name": "Product 1",
      "price": 19.99,
      "description": "Product 1 description"
    }
    ```

#### Get All Products

- **URL**: `/products`
- **Method**: `GET`
- **Response**:
    ```json
    [
      {
        "id": 1,
        "name": "Product 1",
        "price": 19.99,
        "description": "Product 1 description"
      },
      {
        "id": 2,
        "name": "Product 2",
        "price": 29.99,
        "description": "Product 2 description"
      }
    ]
    ```

#### Get Product By Id

- **URL**: `/products/1`
- **Method**: `GET`
- **Response**:
    ```json
    [
      {
        "id": 1,
        "name": "Product 1",
        "price": 19.99,
        "description": "Product 1 description"
      }
    ]
    ```

#### Update Product By Id

- **URL**: `/products/2`
- **Method**: `PUT`
- **Response**:
    ```json
    [
      {
        "id": 2,
        "name": "Product 2",
        "price": 29.99,
        "description": "Product 2 description updated"
      }
    ]
    ```

#### Delete Product By Id

- **URL**: `/products/2`
- **Method**: `DELETE`
- **Response**: 204 No Content

### Configuration

- **Database**: Make sure to configure the MySQL database URL, username, and password in the `application.yaml`.
- **Kafka**: Kafka is set up to run locally on port `9092`. If you are using a remote Kafka service, make sure to update the Kafka settings accordingly.

### Swagger UI

Swagger UI is available at the following URL: `http://localhost:8080/swagger-ui/index.html`

