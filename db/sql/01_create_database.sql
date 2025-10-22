-- Database setup for student service
-- This script is run after the database is created by Docker Compose
-- The database 'student_service' already exists from compose.yaml
-- Spring Boot migrations will handle table creation

-- Connect to the existing database
\c student_service;

-- This script is mainly for reference
-- The actual database setup is handled by:
-- 1. Docker Compose (creates database)
-- 2. Spring Boot migrations (creates tables)
-- 3. Application startup (runs migrations automatically)