/***************************************************************************************************
Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
****************************************************************************************************
Document   : SQL script creates BaltradDex schema
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
***************************************************************************************************/

-- drop tables if exist ----------------------------------------------------------------------------

DROP TABLE IF EXISTS dex_subscriptions;
DROP TABLE IF EXISTS dex_delivery_register;
DROP TABLE IF EXISTS dex_channel_permissions CASCADE;
DROP TABLE IF EXISTS dex_users;
DROP TABLE IF EXISTS dex_roles;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_channels;
DROP TABLE IF EXISTS dex_node_connections;
DROP TABLE IF EXISTS dex_node_configuration;

DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS channel_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;	
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;
DROP SEQUENCE IF EXISTS node_connection_id_seq;
DROP SEQUENCE IF EXISTS configuration_id_seq;
DROP SEQUENCE IF EXISTS channel_permission_id_seq;

-- create tables -----------------------------------------------------------------------------------

-- dex_roles ---------------------------------------------------------------------------------------
CREATE TABLE dex_roles
(
    id SERIAL NOT NULL,
    role VARCHAR(32) PRIMARY KEY
);

-- user_id_seq -------------------------------------------------------------------------------------
CREATE SEQUENCE user_id_seq;
-- dex_users ---------------------------------------------------------------------------------------
CREATE TABLE dex_users
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('user_id_seq'),
    name VARCHAR(64) NOT NULL UNIQUE,
    name_hash VARCHAR(32) NOT NULL,
    role_name VARCHAR(32) NOT NULL REFERENCES dex_roles (role),
    password VARCHAR(32) NOT NULL,
    ret_password VARCHAR(32),
    node_address VARCHAR(64) NOT NULL,
    factory VARCHAR(256) NOT NULL,
    country VARCHAR(64) NOT NULL,
    city VARCHAR(64) NOT NULL,
    city_code VARCHAR(12) NOT NULL,
    street VARCHAR(64) NOT NULL,
    number VARCHAR(12) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    email VARCHAR(64) NOT NULL,
    selected BOOLEAN DEFAULT false,
    PRIMARY KEY (id)
);

-- log_entry_id_seq --------------------------------------------------------------------------------
CREATE SEQUENCE log_entry_id_seq;
-- dex_messages ------------------------------------------------------------------------------------
CREATE TABLE dex_messages
(
    id SERIAL NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    type VARCHAR(12) NOT NULL,
    message TEXT NOT NULL,
    PRIMARY KEY (id)
);

-- channel_id_seq ----------------------------------------------------------------------------------
CREATE SEQUENCE channel_id_seq;
-- dex_channels ------------------------------------------------------------------------------------
CREATE TABLE dex_channels
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('channel_id_seq'),
    name VARCHAR(32) NOT NULL,
    wmo_number VARCHAR(16),
    PRIMARY KEY (id)
);

-- channel_permission_id_seq -----------------------------------------------------------------------
CREATE SEQUENCE channel_permission_id_seq;
-- dex_channel_permissions -------------------------------------------------------------------------
CREATE TABLE dex_channel_permissions
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('channel_permission_id_seq'),
    channel_id INT NOT NULL REFERENCES dex_channels (id),
    user_id INT NOT NULL REFERENCES dex_users (id),
    PRIMARY KEY (id)
);

-- subscription_id_seq -----------------------------------------------------------------------------
CREATE SEQUENCE subscription_id_seq;
-- dex_subscriptions -------------------------------------------------------------------------------
CREATE TABLE dex_subscriptions
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('subscription_id_seq'),
    user_name VARCHAR(32),
    channel_name VARCHAR(32),
    node_address VARCHAR(64),
    operator_name VARCHAR(64),
    type VARCHAR(16),
    selected BOOLEAN DEFAULT false,
    synkronized BOOLEAN DEFAULT false,
    PRIMARY KEY (id)
);

-- delivery_register_id_seq ------------------------------------------------------------------------
CREATE SEQUENCE delivery_register_id_seq;
-- dex_delivery_register ---------------------------------------------------------------------------
CREATE TABLE dex_delivery_register
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('delivery_register_id_seq'),
    user_id INT NOT NULL REFERENCES dex_users (id),
    uuid VARCHAR(128) NOT NULL,
    user_name VARCHAR(32) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    status VARCHAR(16) NOT NULL,
    PRIMARY KEY (id)
);

-- node id sequence --------------------------------------------------------------------------------
CREATE SEQUENCE node_connection_id_seq;
-- dex_node connections-----------------------------------------------------------------------------
CREATE TABLE dex_node_connections
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('node_connection_id_seq'),
    name VARCHAR(64) NOT NULL UNIQUE,
    address VARCHAR(64) NOT NULL,
    user_name VARCHAR(64) NOT NULL,
    password VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);
-- configuration id sequence -----------------------------------------------------------------------
CREATE SEQUENCE configuration_id_seq;
-- dex_node_configuration --------------------------------------------------------------------------
CREATE TABLE dex_node_configuration
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('configuration_id_seq'),
    name VARCHAR(64) NOT NULL,
    type VARCHAR(16) NOT NULL,
    address VARCHAR(128) NOT NULL,
    org_name VARCHAR(128) NOT NULL,
    org_address VARCHAR(128) NOT NULL,
    time_zone VARCHAR(128) NOT NULL,
    temp_dir VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);
----------------------------------------------------------------------------------------------------









