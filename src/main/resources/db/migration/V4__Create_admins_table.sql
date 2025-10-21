CREATE TABLE admins (
    admin_id BIGINT PRIMARY KEY,
    department VARCHAR(100),
    CONSTRAINT fk_admins_user_id 
        FOREIGN KEY (admin_id) 
        REFERENCES users(user_id) 
        ON DELETE CASCADE
);
