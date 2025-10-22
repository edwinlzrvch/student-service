# Student Service

A comprehensive Kotlin Spring Boot REST API for managing students, courses, and enrollments in an educational institution.

## Features

- **REST API** - Complete CRUD operations with JWT authentication
- **Authentication & Authorization** - JWT-based auth with role-based access control
- **Database Management** - PostgreSQL with Flyway migrations
- **API Documentation** - Swagger/OpenAPI integration with security schemes
- **Validation** - Comprehensive input validation
- **Testing** - HTTP files for API testing
- **Pagination** - Efficient data retrieval
- **Error Handling** - Global exception handling
- **User Registration** - Self-service student registration
- **Role-Based Access** - Admin, Lecturer, and Student roles with appropriate permissions

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

## Quick Start

### Option 1: Automated Setup (Recommended)

```bash
# 1. Setup database with sample data
./db/scripts/setup-database.sh

# 2. Start the application
./gradlew bootRun
```

This will:
- Start PostgreSQL using Docker Compose
- Run database migrations
- Insert initial admin user and sample data
- Start the Spring Boot application

### Option 2: Manual Setup

#### 1. Database Setup

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

#### 2. Application Setup

```bash
# Build the application
./gradlew clean build

# Run the application
./gradlew bootRun
```

The application will automatically run Flyway migrations to create all necessary tables.

## Database Management

### Initial Setup

The project includes automated database setup scripts:

```bash
# Complete database setup with sample data
./db/scripts/setup-database.sh
```

This script will:
- Start PostgreSQL using Docker Compose
- Run Spring Boot migrations to create all tables
- Insert initial admin user
- Insert sample students, lecturers, courses, and enrollments

### Reset Database

To completely reset the database (useful for testing):

```bash
# Reset database and start fresh
./db/scripts/reset-database.sh

# Then run setup again
./db/scripts/setup-database.sh
```

### Database Access

#### Using psql (if installed locally)

```bash
psql -h localhost -p 5432 -U postgres -d student_service
```

#### Using Docker

```bash
docker exec -it student-service-postgres psql -U postgres -d student_service
```

## Default Users

After running the setup script, you'll have these users available:

### Admin User
- **Email**: `admin@student-service.com`
- **Password**: `password`

### Sample Students
- **Email**: `john.doe@student.edu`
- **Password**: `password`
- **Email**: `jane.smith@student.edu`
- **Password**: `password`

### Sample Lecturers
- **Email**: `alice.johnson@university.edu`
- **Password**: `password`
- **Email**: `bob.williams@university.edu`
- **Password**: `password`

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Testing

### Database Scripts

The project includes several database management scripts:

#### Setup Database
```bash
./db/scripts/setup-database.sh
```
- Starts PostgreSQL using Docker Compose
- Runs Spring Boot migrations
- Inserts initial admin user
- Inserts sample data (students, lecturers, courses, enrollments)

#### Reset Database
```bash
./db/scripts/reset-database.sh
```
- Stops and removes existing database container
- Starts fresh PostgreSQL container
- Useful for testing and development

### Using HTTP Files

The project includes ready-to-use HTTP files for testing:

- `http/auth.http` - Authentication and authorization tests
- `http/complete_workflow.http` - Complete business workflow tests
- `http/students.http` - Student management tests
- `http/courses.http` - Course management tests
- `http/enrollments.http` - Enrollment management tests
- `http/lecturers.http` - Lecturer management tests
- `http/admin.http` - Admin dashboard tests
- `http/health.http` - Health check tests

### Authentication

The API uses JWT (JSON Web Token) authentication. Most endpoints require authentication:

#### Login
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "email": "admin@student-service.com",
  "password": "password"
}
```

#### Register New Student
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
  "firstName": "New",
  "lastName": "Student",
  "email": "new.student@example.com",
  "password": "password123",
  "dateOfBirth": "2000-01-01"
}
```

#### Using JWT Token
```http
GET http://localhost:8080/api/v1/students
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

### Example API Calls

#### Create a Student (Admin only)

```http
POST http://localhost:8080/api/v1/students
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE

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
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

#### Enroll Student in Course

```http
POST http://localhost:8080/api/v1/enrollments
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN_HERE

{
  "studentId": 1,
  "courseId": 1
}
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
student-service/
├── src/main/kotlin/com/course/studentservice/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/            # Data transfer objects
│   ├── entity/         # JPA entities
│   ├── exception/      # Exception handling
│   ├── repository/     # JPA repositories
│   └── service/        # Business logic services
├── db/                 # Database setup and scripts
│   ├── scripts/        # Database management scripts
│   │   ├── setup-database.sh    # Initial database setup
│   │   └── reset-database.sh    # Reset database
│   └── sql/            # SQL scripts
│       ├── 01_create_database.sql      # Database reference
│       ├── 02_insert_initial_admin.sql # Admin user creation
│       └── 03_insert_sample_data.sql   # Sample data
├── http/               # HTTP test files
└── compose.yaml        # Docker Compose configuration
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