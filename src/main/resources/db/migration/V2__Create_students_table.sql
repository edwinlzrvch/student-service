CREATE TABLE students (
    student_id BIGINT PRIMARY KEY,
    date_of_birth DATE,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    enrollment_date DATE,
    CONSTRAINT fk_students_user_id 
        FOREIGN KEY (student_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_student_enrollment_date ON students(enrollment_date);
