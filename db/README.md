# Database Setup for Student Service

This directory contains scripts and SQL files for setting up the PostgreSQL database for the Student Service application using Docker.

## Prerequisites

- Docker installed and running
- PostgreSQL client (optional, for direct database access)

## Quick Start

### 1. Initial Database Setup

Run the setup script to create the database, run migrations, and insert initial data:

```bash
./db/scripts/setup-database.sh
```

This script will:
- Start a PostgreSQL container using Docker
- Create the database and user
- Run Spring Boot migrations to create all tables
- Insert initial admin user and sample data

### 2. Reset Database

To completely reset the database (useful for testing):

```bash
./db/scripts/reset-database.sh
```

This script will:
- Stop and remove the existing container
- Create a fresh PostgreSQL container
- Recreate the database with all data

### 3. Stop Database

To stop the database container:

```bash
./db/scripts/stop-database.sh
```

## Database Configuration

### Connection Details

- **Host**: localhost
- **Port**: 5432
- **Database**: student_service
- **Username**: postgres
- **Password**: postgres

### Initial Admin User

- **Email**: admin@student-service.com
- **Password**: password

## Manual Database Access

### Using psql (if installed locally)

```bash
psql -h localhost -p 5432 -U postgres -d student_service
```

### Using Docker

```bash
docker exec -it student-service-postgres psql -U postgres -d student_service
```

## File Structure

```
db/
├── README.md                           # This file
├── sql/
│   ├── 01_create_database.sql         # Database and user creation
│   ├── 02_insert_initial_admin.sql    # Initial admin user
│   └── 03_insert_sample_data.sql      # Sample data for testing
└── scripts/
    ├── setup-database.sh              # Initial database setup
    ├── reset-database.sh              # Reset database completely
    └── stop-database.sh               # Stop database container
```

## SQL Scripts

### 01_create_database.sql
Creates the PostgreSQL database and user with proper permissions.

### 02_insert_initial_admin.sql
Inserts the initial admin user with credentials:
- Email: admin@student-service.com
- Password: password (BCrypt hashed)

### 03_insert_sample_data.sql
Inserts sample data including:
- Sample students
- Sample lecturers
- Sample courses
- Sample enrollments

## Troubleshooting

### Container Already Exists

If you get an error about the container already existing, you can either:
1. Use the reset script: `./db/scripts/reset-database.sh`
2. Manually remove the container: `docker rm -f student-service-postgres`

### Port Already in Use

If port 5432 is already in use, you can:
1. Stop other PostgreSQL services
2. Modify the port in the scripts (change `DB_PORT` variable)

### Migration Issues

If migrations fail, ensure:
1. The Spring Boot application can connect to the database
2. All required environment variables are set
3. The database user has proper permissions

## Environment Variables

The application uses these environment variables for database connection:

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/student_service"
export SPRING_DATASOURCE_USERNAME="student_service_user"
export SPRING_DATASOURCE_PASSWORD="student_service_password"
```

## Sample Data

The sample data includes:

### Users
- 1 Admin user (admin@student-service.com)
- 4 Student users
- 3 Lecturer users

### Students
- John Doe (john.doe@student.edu)
- Jane Smith (jane.smith@student.edu)
- Mike Johnson (mike.johnson@student.edu)
- Sarah Wilson (sarah.wilson@student.edu)

### Lecturers
- Dr. Alice Johnson (Computer Science)
- Prof. Bob Williams (Mathematics)
- Dr. Carol Davis (Physics)

### Courses
- CS101: Introduction to Computer Science
- CS201: Data Structures and Algorithms
- MATH101: Calculus I
- PHYS101: Physics I

### Enrollments
- Sample enrollments linking students to courses

## Development Workflow

1. **First time setup**: Run `./db/scripts/setup-database.sh`
2. **Start application**: `./gradlew bootRun`
3. **Reset database**: `./db/scripts/reset-database.sh` (when needed)
4. **Stop database**: `./db/scripts/stop-database.sh` (when done)

## Notes

- The database container persists data in Docker volumes
- All scripts include error handling and colored output
- Scripts are idempotent (safe to run multiple times)
- The setup script automatically runs Spring Boot migrations
- Sample data is inserted after migrations complete
