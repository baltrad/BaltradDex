DROP TABLE IF EXISTS baltrad_data;
DROP TABLE IF EXISTS baltrad_data_channels;
DROP TABLE IF EXISTS baltrad_users;
DROP TABLE IF EXISTS baltrad_groups;
DROP TABLE IF EXISTS baltrad_subscriptions;
DROP TABLE IF EXISTS baltrad_roles;
DROP TABLE IF EXISTS baltrad_delivery_register;
DROP TABLE IF EXISTS baltrad_logs;

CREATE TABLE baltrad_data
(
	data_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	node VARCHAR(64) NOT NULL,
	station VARCHAR(32) NOT NULL,
	absolute_path VARCHAR(255) NOT NULL,
	file_name VARCHAR(64) NOT NULL,
	date DATE NOT NULL,
	time TIME NOT NULL,
	datasets INT NOT NULL
);

CREATE TABLE baltrad_data_channels
(
	data_channel_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(32) NOT NULL,
	wmo_number INT,
	checked VARCHAR(8) DEFAULT ''
);

CREATE TABLE baltrad_users
(
	user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_name VARCHAR(64) NOT NULL,
	password VARCHAR(64) NOT NULL,
	role VARCHAR(32) NOT NULL,
	factory VARCHAR(256) NOT NULL,
	country VARCHAR(64) NOT NULL,
	city VARCHAR(64) NOT NULL,
	zip_code VARCHAR(12) NOT NULL,
	street VARCHAR(64) NOT NULL,
	number VARCHAR(12) NOT NULL,
	phone VARCHAR(32) NOT NULL,
	email VARCHAR(64) NOT NULL,
	node_address VARCHAR(64) NOT NULL,
	local_directory VARCHAR(128) NOT NULL
);

CREATE TABLE baltrad_groups
(
	group_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(64) NOT NULL
);

CREATE TABLE baltrad_roles
(
	role_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	group_id INT NOT NULL,
	CONSTRAINT FOREIGN KEY (user_id)
		REFERENCES baltrad_users (user_id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FOREIGN KEY (group_id)
		REFERENCES baltrad_groups (group_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE baltrad_subscriptions
(
	subscription_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	data_channel_id INT NOT NULL,
	CONSTRAINT FOREIGN KEY (user_id)
		REFERENCES baltrad_users (user_id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT FOREIGN KEY (data_channel_id)
		REFERENCES baltrad_data_channels (data_channel_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE baltrad_delivery_register
(
	register_entry_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL,
	data_id INT NOT NULL,
	CONSTRAINT FOREIGN KEY (user_id) REFERENCES baltrad_users (user_id),
	CONSTRAINT FOREIGN KEY (data_id) REFERENCES baltrad_data (data_id)
);

CREATE TABLE baltrad_logs
(
	log_entry_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	log_entry_rank VARCHAR(16) NOT NULL,
	log_entry_date DATE NOT NULL,
	log_entry_time TIME NOT NULL,
	log_entry_text TEXT NOT NULL
);
