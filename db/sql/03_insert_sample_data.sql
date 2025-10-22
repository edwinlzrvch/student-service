-- Insert sample data for testing
-- This script creates sample users, students, lecturers, courses, and enrollments

-- Connect to the database
\c student_service;

-- Insert sample users
INSERT INTO users (first_name, last_name, email, password_hash, role) VALUES
-- Students
('John', 'Doe', 'john.doe@student.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Student'),
('Jane', 'Smith', 'jane.smith@student.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Student'),
('Mike', 'Johnson', 'mike.johnson@student.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Student'),
('Sarah', 'Wilson', 'sarah.wilson@student.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Student'),
-- Lecturers
('Dr. Alice', 'Johnson', 'alice.johnson@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lecturer'),
('Prof. Bob', 'Williams', 'bob.williams@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lecturer'),
('Dr. Carol', 'Davis', 'carol.davis@university.edu', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lecturer')
ON CONFLICT (email) DO NOTHING;

-- Insert students (using student_id as both primary key and foreign key to users.user_id)
INSERT INTO students (student_id, date_of_birth, phone_number, address, enrollment_date)
SELECT 
    u.user_id,
    '2000-01-15'::date,
    '+1234567890',
    '123 Main St, Campus City',
    '2023-09-01'::date
FROM users u WHERE u.email = 'john.doe@student.edu'
UNION ALL
SELECT 
    u.user_id,
    '2000-03-22'::date,
    '+1234567891',
    '456 Oak Ave, Campus City',
    '2023-09-01'::date
FROM users u WHERE u.email = 'jane.smith@student.edu'
UNION ALL
SELECT 
    u.user_id,
    '1999-11-08'::date,
    '+1234567892',
    '789 Pine St, Campus City',
    '2023-09-01'::date
FROM users u WHERE u.email = 'mike.johnson@student.edu'
UNION ALL
SELECT 
    u.user_id,
    '2001-05-14'::date,
    '+1234567893',
    '321 Elm St, Campus City',
    '2023-09-01'::date
FROM users u WHERE u.email = 'sarah.wilson@student.edu'
ON CONFLICT (student_id) DO NOTHING;

-- Insert lecturers (using lecturer_id as both primary key and foreign key to users.user_id)
INSERT INTO lecturers (lecturer_id, specialization, hire_date, phone_number)
SELECT 
    u.user_id,
    'Computer Science',
    '2020-01-15'::date,
    '+1987654321'
FROM users u WHERE u.email = 'alice.johnson@university.edu'
UNION ALL
SELECT 
    u.user_id,
    'Mathematics',
    '2019-08-20'::date,
    '+1987654322'
FROM users u WHERE u.email = 'bob.williams@university.edu'
UNION ALL
SELECT 
    u.user_id,
    'Physics',
    '2021-03-10'::date,
    '+1987654323'
FROM users u WHERE u.email = 'carol.davis@university.edu'
ON CONFLICT (lecturer_id) DO NOTHING;

-- Insert courses
INSERT INTO courses (course_code, title, description, credits, lecturer_id, start_date, end_date, capacity)
SELECT 
    'CS101',
    'Introduction to Computer Science',
    'Basic concepts of computer science and programming',
    3,
    l.lecturer_id,
    '2023-09-01'::date,
    '2023-12-15'::date,
    30
FROM lecturers l 
JOIN users u ON l.lecturer_id = u.user_id 
WHERE u.email = 'alice.johnson@university.edu'
UNION ALL
SELECT 
    'CS201',
    'Data Structures and Algorithms',
    'Advanced programming concepts and algorithm design',
    4,
    l.lecturer_id,
    '2023-09-01'::date,
    '2023-12-15'::date,
    25
FROM lecturers l 
JOIN users u ON l.lecturer_id = u.user_id 
WHERE u.email = 'alice.johnson@university.edu'
UNION ALL
SELECT 
    'MATH101',
    'Calculus I',
    'Differential and integral calculus',
    4,
    l.lecturer_id,
    '2023-09-01'::date,
    '2023-12-15'::date,
    35
FROM lecturers l 
JOIN users u ON l.lecturer_id = u.user_id 
WHERE u.email = 'bob.williams@university.edu'
UNION ALL
SELECT 
    'PHYS101',
    'Physics I',
    'Mechanics and thermodynamics',
    4,
    l.lecturer_id,
    '2023-09-01'::date,
    '2023-12-15'::date,
    30
FROM lecturers l 
JOIN users u ON l.lecturer_id = u.user_id 
WHERE u.email = 'carol.davis@university.edu'
ON CONFLICT (course_code) DO NOTHING;

-- Insert sample enrollments
INSERT INTO enrollments (student_id, course_id, enrollment_date, status)
SELECT 
    s.student_id,
    c.course_id,
    '2023-09-01'::date,
    'Active'
FROM students s
JOIN users u ON s.student_id = u.user_id
CROSS JOIN courses c
WHERE u.email = 'john.doe@student.edu' AND c.course_code = 'CS101'
UNION ALL
SELECT 
    s.student_id,
    c.course_id,
    '2023-09-01'::date,
    'Active'
FROM students s
JOIN users u ON s.student_id = u.user_id
CROSS JOIN courses c
WHERE u.email = 'jane.smith@student.edu' AND c.course_code = 'CS101'
UNION ALL
SELECT 
    s.student_id,
    c.course_id,
    '2023-09-01'::date,
    'Active'
FROM students s
JOIN users u ON s.student_id = u.user_id
CROSS JOIN courses c
WHERE u.email = 'mike.johnson@student.edu' AND c.course_code = 'MATH101'
UNION ALL
SELECT 
    s.student_id,
    c.course_id,
    '2023-09-01'::date,
    'Active'
FROM students s
JOIN users u ON s.student_id = u.user_id
CROSS JOIN courses c
WHERE u.email = 'sarah.wilson@student.edu' AND c.course_code = 'PHYS101'
ON CONFLICT (student_id, course_id) DO NOTHING;

-- Display success message
SELECT 'Sample data inserted successfully' as message;