#!/bin/bash

# Database setup script for Student Service
# This script sets up the PostgreSQL database using Docker

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
DB_CONTAINER_NAME="student-service-postgres"
DB_NAME="student_service"
DB_USER="postgres"
DB_PASSWORD="postgres"
DB_PORT="5432"
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="postgres"

# Function to print colored output
print_status() {
    local status=$1
    local message=$2
    case $status in
        "INFO")
            echo -e "${BLUE}[INFO]${NC} $message"
            ;;
        "SUCCESS")
            echo -e "${GREEN}[SUCCESS]${NC} $message"
            ;;
        "WARNING")
            echo -e "${YELLOW}[WARNING]${NC} $message"
            ;;
        "ERROR")
            echo -e "${RED}[ERROR]${NC} $message"
            ;;
    esac
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_status "ERROR" "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    print_status "SUCCESS" "Docker is running"
}

# Function to check if container exists
container_exists() {
    docker ps -a --format "table {{.Names}}" | grep -q "^${DB_CONTAINER_NAME}$"
}

# Function to check if container is running
container_running() {
    docker ps --format "table {{.Names}}" | grep -q "^${DB_CONTAINER_NAME}$"
}

# Function to start PostgreSQL using Docker Compose
start_postgres() {
    print_status "INFO" "Starting PostgreSQL using Docker Compose..."
    
    # Get the directory of this script
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    local project_root="$(dirname "$(dirname "$script_dir")")"
    
    # Start PostgreSQL using Docker Compose
    cd "$project_root"
    docker-compose up -d postgres
    
    # Wait for PostgreSQL to be ready
    print_status "INFO" "Waiting for PostgreSQL to be ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker exec $DB_CONTAINER_NAME pg_isready -U $POSTGRES_USER > /dev/null 2>&1; then
            print_status "SUCCESS" "PostgreSQL is ready"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            print_status "ERROR" "PostgreSQL failed to start after $max_attempts attempts"
            exit 1
        fi
        
        print_status "INFO" "Attempt $attempt/$max_attempts - waiting for PostgreSQL..."
        sleep 2
        ((attempt++))
    done
}

# Function to execute SQL script
execute_sql() {
    local script_path=$1
    local description=$2
    
    print_status "INFO" "Executing: $description"
    
    if [ ! -f "$script_path" ]; then
        print_status "ERROR" "SQL script not found: $script_path"
        exit 1
    fi
    
    if docker exec -i $DB_CONTAINER_NAME psql -U $POSTGRES_USER -d postgres < "$script_path"; then
        print_status "SUCCESS" "$description completed"
    else
        print_status "ERROR" "$description failed"
        exit 1
    fi
}

# Function to run database migrations
run_migrations() {
    print_status "INFO" "Running database migrations..."
    
    # Get the directory of this script
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    local project_root="$(dirname "$(dirname "$script_dir")")"
    
    # Run Spring Boot application to execute migrations
    print_status "INFO" "Starting Spring Boot application to run migrations..."
    cd "$project_root"
    
    # Set database URL for the application
    export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:$DB_PORT/student_service"
    export SPRING_DATASOURCE_USERNAME="$DB_USER"
    export SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"
    
    # Run migrations in background
    ./gradlew bootRun > /dev/null 2>&1 &
    local app_pid=$!
    
    # Wait for application to start and run migrations
    print_status "INFO" "Waiting for migrations to complete..."
    sleep 30
    
    # Stop the application
    kill $app_pid 2>/dev/null || true
    wait $app_pid 2>/dev/null || true
    
    print_status "SUCCESS" "Database migrations completed"
}

# Function to insert initial data
insert_initial_data() {
    local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
    
    # Insert initial admin user
    execute_sql "$script_dir/../sql/02_insert_initial_admin.sql" "Initial admin user creation"
    
    # Insert sample data
    execute_sql "$script_dir/../sql/03_insert_sample_data.sql" "Sample data insertion"
}

# Function to display connection info
display_connection_info() {
    print_status "INFO" "Database setup completed successfully!"
    echo
    echo "Connection Information:"
    echo "======================"
    echo "Host: localhost"
    echo "Port: $DB_PORT"
    echo "Database: $DB_NAME"
    echo "Username: $DB_USER"
    echo "Password: $DB_PASSWORD"
    echo
    echo "Admin User:"
    echo "Email: admin@student-service.com"
    echo "Password: admin123"
    echo
    echo "To connect using psql:"
    echo "psql -h localhost -p $DB_PORT -U $DB_USER -d $DB_NAME"
    echo
    echo "To connect using Docker:"
    echo "docker exec -it $DB_CONTAINER_NAME psql -U $DB_USER -d $DB_NAME"
    echo
    echo "To start the application:"
    echo "./gradlew bootRun"
}

# Main execution
main() {
    print_status "INFO" "Starting database setup for Student Service..."
    
    # Check prerequisites
    check_docker
    
    # Start PostgreSQL
    start_postgres
    
    # Run migrations (this will create all tables)
    run_migrations
    
    # Insert initial data
    insert_initial_data
    
    # Display connection info
    display_connection_info
}

# Run main function
main "$@"
