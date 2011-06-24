/***************************************************************************************************
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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
DROP TABLE IF EXISTS dex_users CASCADE;
DROP TABLE IF EXISTS dex_roles;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_radars CASCADE;
DROP TABLE IF EXISTS dex_node_connections;
DROP TABLE IF EXISTS dex_node_configuration;
DROP TABLE IF EXISTS dex_file_objects CASCADE;
DROP TABLE IF EXISTS dex_data_quantities CASCADE;
DROP TABLE IF EXISTS dex_products CASCADE;
DROP TABLE IF EXISTS dex_product_parameters CASCADE;
DROP TABLE IF EXISTS dex_data_sources CASCADE;
DROP TABLE IF EXISTS dex_data_source_quantities CASCADE;
DROP TABLE IF EXISTS dex_data_source_file_objects CASCADE;
DROP TABLE IF EXISTS dex_data_source_products CASCADE;
DROP TABLE IF EXISTS dex_data_source_product_parameters CASCADE;
DROP TABLE IF EXISTS dex_data_source_product_parameter_values CASCADE;
DROP TABLE IF EXISTS dex_data_source_radars CASCADE;
DROP TABLE IF EXISTS dex_data_source_users CASCADE;
DROP TABLE IF EXISTS dex_data_source_filters;
-- drop sequences if exist -------------------------------------------------------------------------
DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS radar_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;	
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;
DROP SEQUENCE IF EXISTS node_connection_id_seq;
DROP SEQUENCE IF EXISTS configuration_id_seq;
DROP SEQUENCE IF EXISTS channel_permission_id_seq;
DROP SEQUENCE IF EXISTS file_object_id_seq;
DROP SEQUENCE IF EXISTS data_quantity_id_seq CASCADE;
DROP SEQUENCE IF EXISTS product_id_seq;
DROP SEQUENCE IF EXISTS product_parameter_id_seq;
DROP SEQUENCE IF EXISTS data_source_id_seq;

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
    name_hash VARCHAR(32) NOT NULL UNIQUE,
    role_name VARCHAR(32) NOT NULL REFERENCES dex_roles (role),
    password VARCHAR(32) NOT NULL,
    short_address VARCHAR(64) NOT NULL,
    port VARCHAR(16) NOT NULL,
    factory VARCHAR(256) NOT NULL,
    country VARCHAR(64) NOT NULL,
    city VARCHAR(64) NOT NULL,
    city_code VARCHAR(12) NOT NULL,
    street VARCHAR(64) NOT NULL,
    number VARCHAR(12) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    email VARCHAR(64) NOT NULL,
    PRIMARY KEY (id)
);

-- log_entry_id_seq --------------------------------------------------------------------------------
CREATE SEQUENCE log_entry_id_seq;
-- dex_messages ------------------------------------------------------------------------------------
CREATE TABLE dex_messages
(
    id SERIAL NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    system VARCHAR(16) NOT NULL,
    type VARCHAR(16) NOT NULL,
    message TEXT NOT NULL,
    PRIMARY KEY (id)
);

-- channel_id_seq ----------------------------------------------------------------------------------
CREATE SEQUENCE radar_id_seq;
-- dex_channels ------------------------------------------------------------------------------------
CREATE TABLE dex_radars
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('radar_id_seq'),
    name VARCHAR(32) UNIQUE NOT NULL,
    wmo_number VARCHAR(16) UNIQUE,
    PRIMARY KEY (id)
);

-- channel_permission_id_seq -----------------------------------------------------------------------
CREATE SEQUENCE channel_permission_id_seq;
-- dex_channel_permissions -------------------------------------------------------------------------
CREATE TABLE dex_channel_permissions
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('channel_permission_id_seq'),
    channel_id INT NOT NULL REFERENCES dex_radars (id),
    user_id INT NOT NULL REFERENCES dex_users (id),
    PRIMARY KEY (id)
);

-- subscription_id_seq -----------------------------------------------------------------------------
CREATE SEQUENCE subscription_id_seq;
-- dex_subscriptions -------------------------------------------------------------------------------
CREATE TABLE dex_subscriptions
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('subscription_id_seq'),
    timestamp TIMESTAMP NOT NULL,
    user_name VARCHAR(32),
    data_source_name VARCHAR(64),
    node_address VARCHAR(64),
    operator_name VARCHAR(64),
    type VARCHAR(16),
    active BOOLEAN DEFAULT false,
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
    short_address VARCHAR(64) NOT NULL,
    port VARCHAR(16) NOT NULL,
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
    short_address VARCHAR(64) NOT NULL,
    port VARCHAR(16) NOT NULL,
    org_name VARCHAR(128) NOT NULL,
    org_address VARCHAR(128) NOT NULL,
    time_zone VARCHAR(128) NOT NULL,
    temp_dir VARCHAR(32) NOT NULL,
    email VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);
-- file object id sequence -------------------------------------------------------------------------
CREATE SEQUENCE file_object_id_seq;
-- dex_file_objects --------------------------------------------------------------------------------
CREATE TABLE dex_file_objects
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('file_object_id_seq'),
    file_object VARCHAR(64) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    PRIMARY KEY (id)
);

-- data quantity id sequence -----------------------------------------------------------------------
CREATE SEQUENCE data_quantity_id_seq;
-- dex_data_quantities -------------------------------------------------------------------------------
CREATE TABLE dex_data_quantities
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('data_quantity_id_seq'),
    data_quantity VARCHAR(64) NOT NULL UNIQUE,
    unit VARCHAR(32) NOT NULL,
    description TEXT NOT NULL,
    PRIMARY KEY (id)
);
-- data product id sequence ------------------------------------------------------------------------
CREATE SEQUENCE product_id_seq;
-- dex_products ------------------------------------------------------------------------------------
CREATE TABLE dex_products
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('product_id_seq'),
    product VARCHAR(32) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    PRIMARY KEY (id)
);
-- data product parameter id sequence --------------------------------------------------------------
CREATE SEQUENCE product_parameter_id_seq;
-- dex_products ------------------------------------------------------------------------------------
CREATE TABLE dex_product_parameters
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('product_parameter_id_seq'),
    parameter VARCHAR(32) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    PRIMARY KEY (id)
);
-- data source id sequence -------------------------------------------------------------------------
CREATE SEQUENCE data_source_id_seq;
-- dex_data_sources --------------------------------------------------------------------------------
CREATE TABLE dex_data_sources
(
    id INT NOT NULL UNIQUE DEFAULT NEXTVAL('data_source_id_seq'),
    name VARCHAR(128) UNIQUE NOT NULL,
    description TEXT,
    PRIMARY KEY (id)
);
-- dex_data_source_quantities ----------------------------------------------------------------------
CREATE TABLE dex_data_source_quantities
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    data_quantity_id INT NOT NULL REFERENCES dex_data_quantities(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
);
-- dex_data_source_file_objects --------------------------------------------------------------------
CREATE TABLE dex_data_source_file_objects
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    file_object_id INT NOT NULL REFERENCES dex_file_objects(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
);
-- dex_data_source_products ------------------------------------------------------------------------
CREATE TABLE dex_data_source_products
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES dex_products(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
);
-- dex_data_source_product_parameters --------------------------------------------------------------
CREATE TABLE dex_data_source_product_parameters
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    product_parameter_id INT NOT NULL REFERENCES dex_product_parameters(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
);
-- dex_data_source_product_parameter_values --------------------------------------------------------
CREATE TABLE dex_data_source_product_parameter_values
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    parameter_id INT NOT NULL REFERENCES dex_product_parameters(id) ON DELETE CASCADE,
    parameter_value VARCHAR(64) NOT NULL,
    PRIMARY KEY(id)
);
-- dex_data_source_radars --------------------------------------------------------------------------
CREATE TABLE dex_data_source_radars
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    radar_id INT NOT NULL REFERENCES dex_radars(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
);
-- dex_data_source_users ---------------------------------------------------------------------------
CREATE TABLE dex_data_source_users
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES dex_users(id) ON DELETE CASCADE,
    PRIMARY KEY(id)
);
-- dex_data_source_filters -------------------------------------------------------------------------
CREATE TABLE dex_data_source_filters
(
    id SERIAL NOT NULL,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    filter_id INT NOT NULL,
    PRIMARY KEY(id)
);
----------------------------------------------------------------------------------------------------
