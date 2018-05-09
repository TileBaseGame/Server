CREATE TABLE abilities (ability_id SERIAL PRIMARY KEY NOT NULL, ability_name VARCHAR(55) NOT NULL, description VARCHAR(255) NOT NULL, usable BOOLEAN);

CREATE TABLE users (user_id SERIAL PRIMARY KEY NOT NULL, email VARCHAR(55), username VARCHAR(55), password TEXT);

CREATE TABLE units (unit_id  SERIAL PRIMARY KEY NOT NULL, unit_type VARCHAR(45), hp INT NOT NULL);

CREATE TABLE user_units (user_unit_id INT PRIMARY KEY NOT NULL, unit_x INT, unit_y INT, unit_id INT REFERENCES units (unit_id), ability_id INT REFERENCES abilities (ability_id), user_id INT REFERENCES users(user_id));

CREATE TABLE game (game_id INT NOT NULL PRIMARY KEY, user_unit_id INT REFERENCES user_units(user_unit_id));

INSERT INTO units (unit_type, hp) values ('melee', 20);

INSERT INTO units (unit_type, hp) values ('range', 17);

INSERT INTO units (unit_type, hp) values ('support', 14);

INSERT INTO abilities (ability_name, description, usable) values ('Poison', 'Poisons an unit doing 50% attack damage over 4 turns', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Arcane Missiles', 'Shoots 6 volleys of missiles, each volley does 25% damage', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Glacial Shard', 'do 200% damage', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Restorative Seronade', 'Does 150% attack healing', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Teleport', 'Move to an empty tile within 4 tiles', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Fireball', 'Shoots a fireball that deals basic attack damage over a radius of 2 tiles', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Stun', 'Stuns target, skipping thier next turn', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Cover', 'Take damage instead of an ally until warriors next turn', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Charge', 'Charge towards a unit with a range of 3 tiles', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Harpoon', 'Pull an opponents unit to an empty space in front of warrior and root for 1 turn', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Slice and Dice', 'Attack for 20% damage 10 times', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Brutal Blow', 'Do 150%-200% basic attack damage', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Poison Blades', 'Does 50% basic attack damage per turn for 3 turns', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Lament of Fate', 'Buff an allies attack by 50% for 2 turns or Debuff an opponents attack by 50% for 2 turns', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Vicious Mockery', 'Just laughs and taunts an opponent', TRUE);

INSERT INTO abilities (ability_name, description, usable) values ('Marionette', 'Move an allie or enemy unit to a new tile', TRUE);
                  
UPDATE user_units SET unit_id = (SELECT unit_id 
                                 FROM units 
                                 WHERE user_unit_id = unit_id);
                                 
UPDATE user_units SET user_id = (SELECT user_id 
                                 FROM users 
                                 WHERE user_unit_id = user_id);

UPDATE units SET ability_id = (SELECT ability_id 
                                 FROM abilities 
                                 WHERE unit_id = ability_id);