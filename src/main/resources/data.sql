INSERT INTO player (id, first_name, last_name, email, password) VALUES
(1, 'Chris', 'Test', 'chris@example.com', 'password'),
(2, 'Jamie', 'Test', 'jamie@example.com', 'password'),
(3, 'Wyatt', 'Test', 'wyatt@example.com', 'password');

INSERT INTO character (id, name, description, lore_points, player_id) VALUES
(101, 'Frodo Baggins', 'A Hobbit of the Shire', 1, 1),
(102, 'Gandalf', 'A wise wizard of the Istari', 1, 2),
(103, 'Aragorn', 'Heir of Isildur', 1, 3);