
-- Drop tables for clean test runs
DROP TABLE IF EXISTS character;
DROP TABLE IF EXISTS user;

-- Create the user table
CREATE TABLE user (
                      id INT PRIMARY KEY,
                      first_name VARCHAR(255) NOT NULL,
                      last_name VARCHAR(255) NOT NULL,
                      email VARCHAR(255) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL
);

-- Create the character table with a foreign key to the user table
CREATE TABLE character (
                           id INT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           description VARCHAR(255),
                           lore_points INT,
                           user_id INT NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Insert data into the user table
INSERT INTO user (id, first_name, last_name, email, password) VALUES
                                                                  (1, 'Chris', 'Test', 'chris@example.com', 'password'),
                                                                  (2, 'Jamie', 'Test', 'jamie@example.com', 'password'),
                                                                  (3, 'Wyatt', 'Test', 'wyatt@example.com', 'password');

-- Insert data into the character table
INSERT INTO character (id, name, description, lore_points, user_id) VALUES
                                                                        (101, 'Frodo Baggins', 'A Hobbit of the Shire', 1, 1),
                                                                        (102, 'Gandalf', 'A wise wizard of the Istari', 1, 2),
                                                                        (103, 'Aragorn', 'Heir of Isildur', 1, 3);