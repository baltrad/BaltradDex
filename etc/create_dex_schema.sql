/*******************************************************************************
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
********************************************************************************
Document   : SQL script creates BaltradDex schema
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
*******************************************************************************/

DROP TABLE IF EXISTS dex_roles CASCADE;
DROP TABLE IF EXISTS dex_users CASCADE;
DROP TABLE IF EXISTS dex_users_roles;
DROP TABLE IF EXISTS dex_keys;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_radars CASCADE;
DROP TABLE IF EXISTS dex_subscriptions CASCADE;
DROP TABLE IF EXISTS dex_subscriptions_users;
DROP TABLE IF EXISTS dex_subscriptions_data_sources;
DROP TABLE IF EXISTS dex_delivery_registry CASCADE;
DROP TABLE IF EXISTS dex_delivery_registry_users;
DROP TABLE IF EXISTS dex_delivery_registry_data_sources;
DROP TABLE IF EXISTS dex_data_sources CASCADE;
DROP TABLE IF EXISTS dex_file_objects CASCADE;
DROP TABLE IF EXISTS dex_data_quantities CASCADE;
DROP TABLE IF EXISTS dex_products CASCADE;
DROP TABLE IF EXISTS dex_product_parameters CASCADE;
DROP TABLE IF EXISTS dex_data_source_quantities;
DROP TABLE IF EXISTS dex_data_source_file_objects;
DROP TABLE IF EXISTS dex_data_source_products;
DROP TABLE IF EXISTS dex_data_source_product_parameters;
DROP TABLE IF EXISTS dex_data_source_product_parameter_values;
DROP TABLE IF EXISTS dex_data_source_radars;
DROP TABLE IF EXISTS dex_data_source_users;
DROP TABLE IF EXISTS dex_data_source_filters;
DROP TABLE IF EXISTS dex_status CASCADE;
DROP TABLE IF EXISTS dex_status_subscriptions;

DROP FUNCTION IF EXISTS dex_trim_messages_by_number() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_messages_by_age() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_registry_by_number() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_registry_by_age() CASCADE;


CREATE TABLE dex_roles
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR (32) NOT NULL UNIQUE
);

CREATE TABLE dex_users
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE,
    node_address VARCHAR (256),
    redirected_address VARCHAR(256),    
    password VARCHAR (32),
    org_name VARCHAR (256),
    org_unit VARCHAR (256),
    locality VARCHAR (64),
    state VARCHAR (64),
    country_code VARCHAR (2)
);

CREATE TABLE dex_users_roles
(
    id SERIAL NOT NULL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES dex_users (id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES dex_roles (id) ON DELETE CASCADE
);

CREATE TABLE dex_keys
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR (64) NOT NULL UNIQUE,
    checksum VARCHAR (32),
    authorized BOOLEAN DEFAULT FALSE,
    injector BOOLEAN DEFAULT FALSE
);

CREATE TABLE dex_messages
(
    id SERIAL NOT NULL PRIMARY KEY,
    time_stamp BIGINT NOT NULL,
    logger VARCHAR (16) NOT NULL,
    level VARCHAR(16) NOT NULL,
    message TEXT NOT NULL
);

CREATE UNIQUE INDEX dex_messages_timestamp_idx ON dex_messages (time_stamp);

CREATE TABLE dex_radars
(
    id SERIAL NOT NULL PRIMARY KEY,
    country_code VARCHAR (16) NOT NULL, 
    center_code VARCHAR (16) NOT NULL,
    center_number INT NOT NULL,
    rad_place VARCHAR (64) NOT NULL,
    rad_code VARCHAR (16) NOT NULL UNIQUE,
    rad_wmo VARCHAR (16) NOT NULL UNIQUE
);

CREATE TABLE dex_subscriptions
(
    id SERIAL NOT NULL PRIMARY KEY,
    time_stamp BIGINT NOT NULL,
    type VARCHAR(16),
    active BOOLEAN DEFAULT false,
    sync BOOLEAN DEFAULT false
);

CREATE TABLE dex_data_sources
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(128) UNIQUE NOT NULL,
    type VARCHAR(16) NOT NULL,
    source VARCHAR(256),
    file_object VARCHAR(256),
    description TEXT
);

CREATE TABLE dex_subscriptions_users
(
    id SERIAL NOT NULL PRIMARY KEY,
    subscription_id INT NOT NULL REFERENCES dex_subscriptions (id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES dex_users (id) ON DELETE CASCADE
);

CREATE TABLE dex_subscriptions_data_sources
(
    id SERIAL NOT NULL PRIMARY KEY,
    subscription_id INT NOT NULL REFERENCES dex_subscriptions (id) ON DELETE CASCADE,
    data_source_id INT NOT NULL REFERENCES dex_data_sources (id) ON DELETE CASCADE
);

CREATE TABLE dex_delivery_registry
(
    id SERIAL NOT NULL PRIMARY KEY,
    time_stamp BIGINT NOT NULL,
    type VARCHAR(16) NOT NULL,
    uuid VARCHAR(128) NOT NULL,
    status BOOLEAN DEFAULT FALSE
);

CREATE UNIQUE INDEX dex_delivery_registry_timestamp_idx ON dex_delivery_registry (time_stamp);

CREATE TABLE dex_delivery_registry_users
(
    id SERIAL NOT NULL PRIMARY KEY,
    entry_id INT NOT NULL REFERENCES dex_delivery_registry (id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES dex_users (id) ON DELETE CASCADE
);

CREATE TABLE dex_delivery_registry_data_sources
(
    id SERIAL NOT NULL PRIMARY KEY,
    entry_id INT NOT NULL REFERENCES dex_delivery_registry (id) ON DELETE CASCADE,
    data_source_id INT NOT NULL REFERENCES dex_data_sources (id) ON DELETE CASCADE
);

CREATE TABLE dex_file_objects
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE TABLE dex_data_quantities
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE,
    unit VARCHAR(32) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE dex_products
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE TABLE dex_product_parameters
(
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(32) NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE TABLE dex_data_source_quantities
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    data_quantity_id INT NOT NULL REFERENCES dex_data_quantities(id) ON DELETE CASCADE
);

CREATE TABLE dex_data_source_file_objects
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    file_object_id INT NOT NULL REFERENCES dex_file_objects(id) ON DELETE CASCADE
);

CREATE TABLE dex_data_source_products
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES dex_products(id) ON DELETE CASCADE
);

CREATE TABLE dex_data_source_product_parameters
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    product_parameter_id INT NOT NULL REFERENCES dex_product_parameters(id) ON DELETE CASCADE
);

CREATE TABLE dex_data_source_product_parameter_values
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    parameter_id INT NOT NULL REFERENCES dex_product_parameters(id) ON DELETE CASCADE,
    parameter_value VARCHAR(64) NOT NULL
);

CREATE TABLE dex_data_source_radars
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    radar_id INT NOT NULL REFERENCES dex_radars(id) ON DELETE CASCADE
);

CREATE TABLE dex_data_source_users
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES dex_users(id) ON DELETE CASCADE
);

CREATE TABLE dex_data_source_filters
(
    id SERIAL NOT NULL PRIMARY KEY,
    data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
    filter_id INT NOT NULL
);

CREATE TABLE dex_status
(
    id SERIAL NOT NULL PRIMARY KEY,
    downloads BIGINT DEFAULT 0,
    uploads BIGINT DEFAULT 0,
    upload_failures BIGINT DEFAULT 0
);

CREATE TABLE dex_status_subscriptions
(
    id SERIAL NOT NULL PRIMARY KEY,
    status_id INT NOT NULL REFERENCES dex_status(id) ON DELETE CASCADE,
    subscription_id INT NOT NULL REFERENCES dex_subscriptions(id) ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION make_plpgsql()
RETURNS VOID
  LANGUAGE SQL
AS $$
  CREATE LANGUAGE plpgsql;
$$;
SELECT
  CASE
    WHEN EXISTS (
      SELECT 1 from pg_catalog.pg_language where lanname='plpgsql'
    ) THEN
      NULL
    ELSE make_plpgsql()
  END;

CREATE OR REPLACE FUNCTION dex_trim_messages_by_number() RETURNS trigger AS $$
    DECLARE
        records_limit INTEGER;
    BEGIN
        records_limit = TG_ARGV[0];
        DELETE FROM dex_messages WHERE id IN (SELECT id FROM dex_messages 
            WHERE  level <> 'STICKY' ORDER BY time_stamp DESC OFFSET 
            records_limit);
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dex_trim_messages_by_age() RETURNS trigger AS $$
    DECLARE
	MILLIS_PER_DAY BIGINT = 86400000;
	MILLIS_PER_HOUR BIGINT= 3600000;
        MILLIS_PER_MINUTE BIGINT= 60000;
                
        now_date TIMESTAMP WITHOUT TIME ZONE;
        now_epoch BIGINT;
	max_age BIGINT;
		
        days INT;
	hours INT;
	minutes INT;	
    BEGIN
        SELECT now() INTO now_date;
        SELECT (EXTRACT (epoch FROM now_date)::BIGINT) * 1000 INTO now_epoch;
        SELECT TG_ARGV[0]::BIGINT INTO days;
        SELECT TG_ARGV[1]::BIGINT INTO hours;
        SELECT TG_ARGV[2]::BIGINT INTO minutes;

        max_age = now_epoch - (days * MILLIS_PER_DAY + hours * 
                    MILLIS_PER_HOUR + minutes * MILLIS_PER_MINUTE);
		
        DELETE FROM dex_messages WHERE time_stamp < max_age 
                    AND level <> 'STICKY'; 
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dex_trim_registry_by_number() RETURNS trigger AS $$ 
    DECLARE
        records_limit INTEGER;
    BEGIN
        records_limit = TG_ARGV[0];
        DELETE FROM dex_delivery_registry WHERE id IN (SELECT id 
            FROM dex_delivery_registry ORDER BY time_stamp DESC OFFSET 
                records_limit);
        RETURN NEW; 
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dex_trim_registry_by_age() RETURNS trigger AS $$
    DECLARE
	MILLIS_PER_DAY BIGINT = 86400000;
	MILLIS_PER_HOUR BIGINT= 3600000;
        MILLIS_PER_MINUTE BIGINT= 60000;
                
        now_date TIMESTAMP WITHOUT TIME ZONE;
        now_epoch BIGINT;
	max_age BIGINT;
		
        days INT;
	hours INT;
	minutes INT;	
    BEGIN
        SELECT now() INTO now_date;
        SELECT (EXTRACT (epoch FROM now_date)::BIGINT) * 1000 INTO now_epoch;
        SELECT TG_ARGV[0]::BIGINT INTO days;
        SELECT TG_ARGV[1]::BIGINT INTO hours;
        SELECT TG_ARGV[2]::BIGINT INTO minutes;

        max_age = now_epoch - (days * MILLIS_PER_DAY + hours * 
                    MILLIS_PER_HOUR + minutes * MILLIS_PER_MINUTE);
		
        DELETE FROM dex_delivery_registry WHERE time_stamp < max_age; 
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

