CREATE TABLE courses (
    course_id BIGSERIAL PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(100),
    description TEXT,
    credits INTEGER,
    lecturer_id BIGINT,
    start_date DATE,
    end_date DATE,
    capacity INTEGER,
    course_metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_courses_lecturer_id 
        FOREIGN KEY (lecturer_id) 
        REFERENCES lecturers(lecturer_id) 
        ON DELETE SET NULL
);

CREATE UNIQUE INDEX idx_course_code ON courses(course_code);
CREATE INDEX idx_course_lecturer ON courses(lecturer_id);
CREATE INDEX idx_course_start_date ON courses(start_date);
