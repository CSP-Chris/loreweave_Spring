CREATE SEQUENCE character_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE player_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE if not exists player
(
    id         BIGINT NOT NULL,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    email      VARCHAR(255),
    password   VARCHAR(255),
    CONSTRAINT pk_player PRIMARY KEY (id)
);

CREATE TABLE if not exists character
(
    id          BIGINT NOT NULL,
    name        VARCHAR(255),
    description VARCHAR(255),
    lore_points INT    NOT NULL,
    player_id   BIGINT,
    CONSTRAINT pk_character PRIMARY KEY (id)
);

ALTER TABLE character
    ADD CONSTRAINT uc_character_player UNIQUE (player_id);

ALTER TABLE character
    ADD CONSTRAINT FK_CHARACTER_ON_PLAYER FOREIGN KEY (player_id) REFERENCES player (id);