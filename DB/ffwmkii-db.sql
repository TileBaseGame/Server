CREATE TABLE abilities (ability_id INT NOT NULL AUTO_INCREMENT,
						description VARCHAR(255) NOT NULL,
						unit_usable BOOLEAN,
						ability_name VARCHAR(55) NOT NULL,
						PRIMARY KEY(ability_id));

CREATE TABLE users (user_id INT NOT NULL AUTO_INCREMENT,
					email VARCHAR(55),
					username VARCHAR(55),
					password TEXT,
					PRIMARY KEY(user_id));

CREATE TABLE units (unit_id INT NOT NULL AUTO_INCREMENT,
					unit_type VARCHAR(45),
					hp INT NOT NULL,
					PRIMARY KEY(unit_id));

CREATE TABLE user_units (user_unit_id INT NOT NULL,
						unit_x INT,
						unit_y INT,
						PRIMARY KEY (user_unit_id),
						FOREIGN KEY (unit_id) REFERENCES units(unit_id),
						FOREIGN KEY (ability_id) REFERENCES abilities(ability_id),
						FOREIGN KEY (user_id) REFERENCES users(user_id));

CREATE TABLE game (game_id INT NOT NULL,
					PRIMARY KEY (game_id),
					FOREIGN KEY (user_unit_id) REFERENCES user_units(user_unit_id));