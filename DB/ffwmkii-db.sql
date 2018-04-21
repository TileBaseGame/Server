CREATE TABLE abilities (ability_id SERIAL PRIMARY KEY NOT NULL, description VARCHAR(255) NOT NULL, unit_usable BOOLEAN,	ability_name VARCHAR(55) NOT NULL);

CREATE TABLE users (user_id SERIAL PRIMARY KEY NOT NULL, email VARCHAR(55), username VARCHAR(55), password TEXT);

CREATE TABLE units (unit_id  SERIAL PRIMARY KEY NOT NULL, unit_type VARCHAR(45), hp INT NOT NULL);

CREATE TABLE user_units (user_unit_id INT PRIMARY KEY NOT NULL, unit_x INT, unit_y INT, unit_id INT REFERENCES units (unit_id), ability_id INT REFERENCES abilities (ability_id), user_id INT REFERENCES users(user_id));

CREATE TABLE game (game_id INT NOT NULL PRIMARY KEY, user_unit_id INT REFERENCES user_units(user_unit_id));

INSERT INTO units (unit_type, hp) values ('melee', 20);

INSERT INTO units (unit_type, hp) values ('range', 17);

INSERT INTO units (unit_type, hp) values ('support', 14);

INSERT INTO abilities (description, unit_usable, ability_name) values ('Poisons an unit doing 50% attack damage over 4 turns', False, 'Poison');

INSERT INTO abilities (description, unit_usable, ability_name) values ('Shoots 6 volleys of missiles, each volley does 25% damage', False, 'Arcane Missiles');

INSERT INTO abilities (description, unit_usable, ability_name) values ('do 200% damage', False, 'Glacial Shard');

INSERT INTO abilities (description, unit_usable, ability_name) values ('Does 150% attack healing', False, 'Restoritive Seronade');