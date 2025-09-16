-- ==========================================
-- File Name:    data.sql
-- Created By:   Jamie Coker
-- Created On:   2025-09-15
-- Purpose:      Seed initial database with users and characters
--
-- Updated By:
-- Updated By:
-- ==========================================

DROP TABLE IF EXISTS character;
DROP TABLE IF EXISTS "user";

CREATE TABLE "user" (
    id INT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE character (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    lore_points INT,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

INSERT INTO "user" (id, first_name, last_name, email, password) VALUES
    (1, 'Chris', 'Test', 'chris@example.com', '$2a$10$Dow1bY2vyq3uLZr2n1hC7OVH8RHxJ3XxF1CK9C6my92iDLmIUyYie'),
    (2, 'Jamie', 'Test', 'jamie@example.com', '$2a$10$Dow1bY2vyq3uLZr2n1hC7OVH8RHxJ3XxF1CK9C6my92iDLmIUyYie'),
    (3, 'Wyatt', 'Test', 'wyatt@example.com', '$2a$10$Dow1bY2vyq3uLZr2n1hC7OVH8RHxJ3XxF1CK9C6my92iDLmIUyYie');

INSERT INTO character (id, name, description, lore_points, user_id) VALUES
    (101, 'Frodo Baggins', 'A Hobbit of the Shire', 1, 1),
    (102, 'Gandalf', 'A wise wizard of the Istari', 1, 2),
    (103, 'Aragorn', 'Heir of Isildur', 1, 3);
