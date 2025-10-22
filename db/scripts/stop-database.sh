#!/bin/bash

# Database stop script for Student Service
# This script stops the PostgreSQL database container

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
DB_CONTAINER_NAME="student-service-postgres"

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

# Function to stop container
stop_container() {
    if container_exists; then
        if container_running; then
            print_status "INFO" "Stopping PostgreSQL container..."
            docker stop $DB_CONTAINER_NAME
            print_status "SUCCESS" "PostgreSQL container stopped"
        else
            print_status "INFO" "PostgreSQL container is already stopped"
        fi
    else
        print_status "WARNING" "PostgreSQL container does not exist"
    fi
}

# Function to remove container
remove_container() {
    if container_exists; then
        print_status "INFO" "Removing PostgreSQL container..."
        docker rm $DB_CONTAINER_NAME
        print_status "SUCCESS" "PostgreSQL container removed"
    else
        print_status "INFO" "No container to remove"
    fi
}

# Main execution
main() {
    print_status "INFO" "Stopping database for Student Service..."
    
    # Check prerequisites
    check_docker
    
    # Stop container
    stop_container
    
    # Ask if user wants to remove container
    echo
    read -p "Do you want to remove the container completely? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        remove_container
    else
        print_status "INFO" "Container stopped but not removed. You can start it again with: docker start $DB_CONTAINER_NAME"
    fi
    
    print_status "SUCCESS" "Database stop completed"
}

# Run main function
main "$@"
