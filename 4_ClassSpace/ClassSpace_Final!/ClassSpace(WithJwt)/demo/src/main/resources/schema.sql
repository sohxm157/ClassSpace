CREATE TABLE IF NOT EXISTS teachers (
    teacher_id INT NOT NULL,
    subject VARCHAR(255),
    assigned_classes VARCHAR(255),
    assigned_divisions VARCHAR(255),
    PRIMARY KEY (teacher_id),
    CONSTRAINT fk_teachers_users FOREIGN KEY (teacher_id) REFERENCES users (user_id)
);

ALTER TABLE announcements ADD COLUMN division VARCHAR(50);
