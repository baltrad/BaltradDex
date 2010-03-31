-- SQL script creates database schema for BaltradDex and baltrad-db 

-- drop tables if exist ----------------------------------------------------------------

DROP TABLE IF EXISTS dex_subscriptions;
DROP TABLE IF EXISTS dex_aux_subscriptions;
DROP TABLE IF EXISTS dex_delivery_register;
DROP TABLE IF EXISTS dex_users;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_channels;

DROP TABLE IF EXISTS source_radars;
DROP TABLE IF EXISTS source_centres;
DROP TABLE IF EXISTS attribute_values_str;
DROP TABLE IF EXISTS attribute_values_time;
DROP TABLE IF EXISTS attribute_values_bool;
DROP TABLE IF EXISTS attribute_values_real;
DROP TABLE IF EXISTS attribute_values_int;
DROP TABLE IF EXISTS attribute_values_date;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS files;
DROP TABLE IF EXISTS sources;
DROP TABLE IF EXISTS attributes;
DROP TABLE IF EXISTS attribute_groups;

DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS channel_id_seq;
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS aux_subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;

-- create tables -----------------------------------------------------------------------

-- dex_users ---------------------------------------------------------------------------

CREATE TABLE dex_users
(
    id SERIAL NOT NULL,
    name VARCHAR(64) NOT NULL UNIQUE,
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
    local_directory VARCHAR(128) NOT NULL,
    PRIMARY KEY (id)
);

-- log_entry_id_seq --------------------------------------------------------------------

CREATE SEQUENCE log_entry_id_seq;

-- dex_messages ------------------------------------------------------------------------

CREATE TABLE dex_messages
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('log_entry_id_seq'),
    date VARCHAR(10) NOT NULL,
    time VARCHAR(8) NOT NULL,
    type VARCHAR(12) NOT NULL,
    message TEXT NOT NULL,
    PRIMARY KEY (id)
);

-- channel_id_seq --------------------------------------------------------------------

CREATE SEQUENCE channel_id_seq;

-- dex_channels ------------------------------------------------------------------------

CREATE TABLE dex_channels
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('channel_id_seq'),
    name VARCHAR(32) NOT NULL,
    wmo_number INT,
    selected BOOLEAN DEFAULT false,
    PRIMARY KEY (id)

);

-- subscription_id_seq -----------------------------------------------------------------

CREATE SEQUENCE subscription_id_seq;

-- dex_subscriptions -------------------------------------------------------------------

CREATE TABLE dex_subscriptions
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('subscription_id_seq'),
    user_id INT NOT NULL REFERENCES dex_users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    channel_id INT NOT NULL REFERENCES dex_channels (id)
                                                    ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id)
);

-- aux_subscription_id_seq -------------------------------------------------------------

CREATE SEQUENCE aux_subscription_id_seq;

-- dex_aux_subscriptions ---------------------------------------------------------------

CREATE TABLE dex_aux_subscriptions
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('aux_subscription_id_seq'),
    user_id INT NOT NULL REFERENCES dex_users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    channel_id INT NOT NULL REFERENCES dex_channels (id)
                                                    ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (id)
);


CREATE TABLE attribute_groups (
    id SERIAL NOT NULL,
    name INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE attributes (
    id SERIAL NOT NULL,
    name TEXT NOT NULL,
    converter TEXT NOT NULL,
    storage_table TEXT NOT NULL,
    storage_column TEXT NOT NULL,
    ignore_in_hash BOOLEAN NOT NULL,
    PRIMARY KEY (id),
        UNIQUE (name)
);

CREATE TABLE sources (
    id SERIAL NOT NULL,
    node_id TEXT NOT NULL,
    PRIMARY KEY (id),
        UNIQUE (node_id)
);

CREATE TABLE files (
    id SERIAL NOT NULL,
    unique_id TEXT NOT NULL,
    path TEXT NOT NULL,
    proposed_filename TEXT NOT NULL,
    filename_version INTEGER NOT NULL,
    object TEXT NOT NULL,
    n_date DATE NOT NULL,
    n_time TIME WITHOUT TIME ZONE NOT NULL,
    source_id INTEGER NOT NULL,
    PRIMARY KEY (id),
        UNIQUE (unique_id),
        UNIQUE (proposed_filename, filename_version),
        UNIQUE (path),
        FOREIGN KEY(source_id) REFERENCES sources (id)
);

CREATE TABLE groups (
    id SERIAL NOT NULL,
    parent_id INTEGER,
    name TEXT NOT NULL,
    product TEXT,
    startdate DATE,
    starttime TIME WITHOUT TIME ZONE,
    enddate DATE,
    endtime TIME WITHOUT TIME ZONE,
    file_id INTEGER NOT NULL,
    PRIMARY KEY (id),
        FOREIGN KEY(parent_id) REFERENCES groups (id),
        FOREIGN KEY(file_id) REFERENCES files (id) ON DELETE CASCADE
);

CREATE TABLE attribute_values_int (
    attribute_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    value BIGINT NOT NULL,
    PRIMARY KEY (attribute_id, group_id),
        FOREIGN KEY(attribute_id) REFERENCES attributes (id),
        FOREIGN KEY(group_id) REFERENCES groups (id) ON DELETE CASCADE
);

CREATE TABLE attribute_values_str (
    attribute_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    value TEXT NOT NULL,
    PRIMARY KEY (attribute_id, group_id),
        FOREIGN KEY(attribute_id) REFERENCES attributes (id),
        FOREIGN KEY(group_id) REFERENCES groups (id) ON DELETE CASCADE
);

CREATE TABLE attribute_values_bool (
    attribute_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    value BOOLEAN NOT NULL,
    PRIMARY KEY (attribute_id, group_id),
        FOREIGN KEY(attribute_id) REFERENCES attributes (id),
        FOREIGN KEY(group_id) REFERENCES groups (id) ON DELETE CASCADE
);

CREATE TABLE attribute_values_time (
    attribute_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    value TIME WITHOUT TIME ZONE NOT NULL,
    PRIMARY KEY (attribute_id, group_id),
        FOREIGN KEY(attribute_id) REFERENCES attributes (id),
        FOREIGN KEY(group_id) REFERENCES groups (id) ON DELETE CASCADE
);

CREATE TABLE attribute_values_real (
    attribute_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    value FLOAT(10) NOT NULL,
    PRIMARY KEY (attribute_id, group_id),
        FOREIGN KEY(attribute_id) REFERENCES attributes (id),
        FOREIGN KEY(group_id) REFERENCES groups (id) ON DELETE CASCADE
);

CREATE TABLE attribute_values_date (
    attribute_id INTEGER NOT NULL,
    group_id INTEGER NOT NULL,
    value DATE NOT NULL,
    PRIMARY KEY (attribute_id, group_id),
        FOREIGN KEY(attribute_id) REFERENCES attributes (id),
        FOREIGN KEY(group_id) REFERENCES groups (id) ON DELETE CASCADE
);

CREATE TABLE source_centres (
    id INTEGER NOT NULL,
    originating_centre INTEGER NOT NULL,
    country_code INTEGER NOT NULL,
    wmo_cccc VARCHAR(4) NOT NULL,
    PRIMARY KEY (id),
        FOREIGN KEY(id) REFERENCES sources (id),
        UNIQUE (country_code),
        UNIQUE (wmo_cccc),
        UNIQUE (originating_centre)
);

CREATE TABLE source_radars (
    id INTEGER NOT NULL,
    centre_id INTEGER NOT NULL,
    radar_site TEXT,
    wmo_code INTEGER,
    place TEXT,
    PRIMARY KEY (id),
        UNIQUE (radar_site),
        UNIQUE (place),
        FOREIGN KEY(centre_id) REFERENCES source_centres (id),
        FOREIGN KEY(id) REFERENCES sources (id),
        UNIQUE (wmo_code)
);

-- delivery_register_id_seq

CREATE SEQUENCE delivery_register_id_seq;

-- dex_delivery_register ---------------------------------------------------------------

CREATE TABLE dex_delivery_register
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('delivery_register_id_seq'),
    user_id INT NOT NULL REFERENCES dex_users (id),
    data_id INT NOT NULL REFERENCES files (id),
    PRIMARY KEY (id)
);
