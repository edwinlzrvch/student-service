-- Insert initial admin user
-- This script creates the initial admin user for the student service application
-- Password: admin123 (BCrypt hashed)

-- Connect to the database
\c student_service;

-- Insert initial admin user
INSERT INTO users (first_name, last_name, email, password_hash, role, created_at, updated_at) 
VALUES (
    'System', 
    'Administrator', 
    'admin@student-service.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 
    'Admin', 
    NOW(), 
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Insert admin record
INSERT INTO admins (user_id, department, created_at, updated_at)
SELECT 
    u.user_id,
    'System Administration',
    NOW(),
    NOW()
FROM users u 
WHERE u.email = 'admin@student-service.com' 
AND u.role = 'Admin'
ON CONFLICT (user_id) DO NOTHING;

-- Display success message
SELECT 'Initial admin user created successfully' as message;
