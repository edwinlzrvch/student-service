CREATE TABLE lecturers (
    lecturer_id BIGINT PRIMARY KEY,
    specialization VARCHAR(100),
    hire_date DATE,
    phone_number VARCHAR(20),
    CONSTRAINT fk_lecturers_user_id 
        FOREIGN KEY (lecturer_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_lecturer_specialization ON lecturers(specialization);
