# Student Service

A comprehensive Kotlin Spring Boot REST API for managing students, courses, and enrollments in an educational institution.

## Features

- **REST API** - Complete CRUD operations 
- **Database Management** - PostgreSQL with Flyway migrations
- **API Documentation** - Swagger/OpenAPI integration
- **Validation** - Comprehensive input validation
- **Testing** - HTTP files for API testing
- **Pagination** - Efficient data retrieval
- **Error Handling** - Global exception handling

## Architecture

- **Entities**: User, Student, Lecturer, Admin, Course, Enrollment, AuditLog
- **Repositories**: JPA repositories with custom query methods
- **Services**: Business logic layer with transaction management
- **Controllers**: REST endpoints with comprehensive documentation
- **DTOs**: Data transfer objects with validation
- **Configuration**: Security, OpenAPI, and database configuration

## Prerequisites

- Java 21+
- PostgreSQL 15+
- Docker (optional, for database)
- Gradle 8+

## Setup

### 1. Database Setup

#### Using Docker Compose (Recommended)

```bash
# Start PostgreSQL database
docker-compose up -d
```

The database will be available at:

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `student_service`
- **Username**: `postgres`
- **Password**: `postgres`

### 2. Application Setup

```bash

# Build the application
./gradlew clean build

# Run the application
./gradlew bootRun
```

The application will automatically run Flyway migrations to create all necessary tables.

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Testing

### Using HTTP Files

The project includes ready-to-use HTTP files for testing

### Example API Calls

#### Create a Student

```http
POST http://localhost:8080/api/v1/students
Content-Type: application/json

{
  "firstName": "Alice",
  "lastName": "Johnson",
  "email": "alice.johnson@example.com",
  "password": "password123",
  "dateOfBirth": "2000-05-15",
  "phoneNumber": "+1234567890",
  "address": "123 Main St, City, State 12345",
  "enrollmentDate": "2024-01-15"
}
```

#### Get All Students (Paginated)

```http
GET http://localhost:8080/api/v1/students?page=0&size=10&sort=firstName,asc
```

## Database Schema

### Core Entities

- **User**: Base user information with roles (Student, Lecturer, Admin)
- **Student**: Student-specific information linked to User
- **Lecturer**: Lecturer-specific information linked to User
- **Admin**: Admin-specific information linked to User
- **Course**: Course information with lecturer assignment
- **Enrollment**: Student course enrollments with status tracking
- **AuditLog**: System audit logging


## Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/student_service
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## Development

### Project Structure

```
src/main/kotlin/com/course/studentservice/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data transfer objects
├── entity/         # JPA entities
├── exception/      # Exception handling
├── repository/     # JPA repositories
└── service/        # Business logic services
```

### Building and Running

```bash
# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Create JAR
./gradlew bootJar
```

## API Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2024-01-15T10:30:00"
}
```