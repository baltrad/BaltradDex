-- SQL script creates database schema for BaltradDex

-- drop tables if exist ----------------------------------------------------------------

DROP TABLE IF EXISTS dex_subscriptions;
DROP TABLE IF EXISTS dex_aux_subscriptions;
DROP TABLE IF EXISTS dex_delivery_register;
DROP TABLE IF EXISTS dex_users;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_channels;

DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS channel_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;	
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS aux_subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;

-- create tables -----------------------------------------------------------------------

-- user_id_seq -------------------------------------------------------------------------

CREATE SEQUENCE user_id_seq;

-- dex_users ---------------------------------------------------------------------------

CREATE TABLE dex_users
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('user_id_seq'),
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

-- delivery_register_id_seq

CREATE SEQUENCE delivery_register_id_seq;

-- dex_delivery_register ---------------------------------------------------------------

CREATE TABLE dex_delivery_register
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('delivery_register_id_seq'),
    user_id INT NOT NULL REFERENCES dex_users (id),
    data_id INT NOT NULL REFERENCES bdb_files (id),
    PRIMARY KEY (id)
);
