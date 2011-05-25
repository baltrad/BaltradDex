/***************************************************************************************************
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

This file is part of the BaltradDex software.

BaltradDex is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

BaltradDex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex software. If not, see http://www.gnu.org/licenses.
****************************************************************************************************
Document : SQL script upgrading existing BaltradDex schema
Created on : Jan 14, 2011, 9:09 AM
***************************************************************************************************/


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

CREATE OR REPLACE FUNCTION restart_seq_with_max(table_name TEXT, column_name TEXT)
 RETURNS BIGINT AS $$
DECLARE
 maxval BIGINT;
BEGIN
 EXECUTE 'SELECT COALESCE(MAX(' || column_name || '), 0) + 1 FROM '
         || table_name INTO maxval;
 EXECUTE 'ALTER SEQUENCE '
         || table_name || '_' || column_name || '_seq'
         || ' RESTART WITH '
         || maxval;
 RETURN maxval;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION split_dex_node_connections_address() RETURNS VOID AS $$
BEGIN
  PERFORM true FROM information_schema.columns
    WHERE table_name = 'dex_node_connections' AND column_name = 'address';
  IF FOUND THEN
    RAISE NOTICE 'splitting column "address" to "short_address" and "port"';
    ALTER TABLE dex_node_connections ADD COLUMN short_address VARCHAR(64);
    ALTER TABLE dex_node_connections ADD COLUMN port VARCHAR(16);
    UPDATE dex_node_connections SET
      short_address = substring(address from E'://([\\w\.-]+):'),
      port = substring(address from E'://[\\w\.-]+:(\\d+)/');
    ALTER TABLE dex_node_connections ALTER short_address SET NOT NULL;
    ALTER TABLE dex_node_connections ALTER port SET NOT NULL;
    ALTER TABLE dex_node_connections DROP COLUMN address;
  ELSE
    RAISE NOTICE 'column "address" already split to "short_address" and "port"';
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION split_dex_node_configuration_address() RETURNS VOID AS $$
BEGIN
  PERFORM true FROM information_schema.columns
    WHERE table_name = 'dex_node_configuration' AND column_name = 'address';
  IF FOUND THEN
    RAISE NOTICE 'splitting column "address" to "short_address" and "port"';
    ALTER TABLE dex_node_configuration ADD COLUMN short_address VARCHAR(64);
    ALTER TABLE dex_node_configuration ADD COLUMN port VARCHAR(16);
    UPDATE dex_node_configuration SET
      short_address = substring(address from E'://([\\w\.-]+):'),
      port = substring(address from E'://[\\w\.-]+:(\\d+)/');
    ALTER TABLE dex_node_configuration ALTER short_address SET NOT NULL;
    ALTER TABLE dex_node_configuration ALTER port SET NOT NULL;
    ALTER TABLE dex_node_configuration DROP COLUMN address;
  ELSE
    RAISE NOTICE 'column "address" already split to "short_address" and "port"';
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION split_dex_users_node_address() RETURNS VOID AS $$
BEGIN
  PERFORM true FROM information_schema.columns
    WHERE table_name = 'dex_users' AND column_name = 'node_address';
  IF FOUND THEN
    RAISE NOTICE 'splitting column "node_address" to "short_address" and "port"';
    ALTER TABLE dex_users ADD COLUMN short_address VARCHAR(64);
    ALTER TABLE dex_users ADD COLUMN port VARCHAR(16);
    UPDATE dex_users SET
      short_address = substring(node_address from E'://([\\w\.-]+):'),
      port = substring(node_address from E'://[\\w\.-]+:(\\d+)/');
    ALTER TABLE dex_users ALTER short_address SET NOT NULL;
    ALTER TABLE dex_users ALTER port SET NOT NULL;
    ALTER TABLE dex_users DROP COLUMN node_address;
  ELSE
    RAISE NOTICE 'column "node_address" already split to "short_address" and "port"';
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION upgrade_dex_schema() RETURNS VOID AS $$
BEGIN
    BEGIN
        ALTER TABLE dex_users DROP COLUMN ret_password;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Column dex_users.ret_password does not exist';
    END;
    BEGIN
        ALTER TABLE dex_users DROP COLUMN selected;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Column dex_users.selected does not exist';
    END;
    BEGIN
        ALTER TABLE dex_users ADD CONSTRAINT dex_users_name_hash_key UNIQUE (name_hash);
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Column dex_users.name_hash does not exist';
    END;
    BEGIN
        ALTER TABLE dex_subscriptions RENAME COLUMN selected TO active;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Column dex_subcriptions.selected does not exist';
    END;
    BEGIN
        ALTER TABLE dex_subscriptions ADD COLUMN timestamp TIMESTAMP DEFAULT now();
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to insert column "timestamp" into table "dex_subscriptions"';
    END;
    BEGIN
	ALTER TABLE dex_subscriptions ALTER COLUMN timestamp SET NOT NULL;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to modify column "timestamp" of table "dex_subscriptions"';
    END;
    BEGIN
        ALTER TABLE dex_messages ADD COLUMN system VARCHAR(16) NOT NULL DEFAULT 'DEX';
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to add column "system" to table "dex_messages"';
    END;
    BEGIN
        CREATE SEQUENCE file_object_id_seq;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create sequence "file_object_id_seq"';
    END;
    BEGIN
        CREATE TABLE dex_file_objects
        (
            id INT NOT NULL UNIQUE DEFAULT NEXTVAL('file_object_id_seq'),
            file_object VARCHAR(64) NOT NULL UNIQUE,
            description TEXT NOT NULL,
            PRIMARY KEY (id)
        );
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_file_objects"';
    END;
    BEGIN
        CREATE SEQUENCE data_quantity_id_seq;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create sequence "data_quantity_id_seq"';
    END;
    BEGIN
        CREATE TABLE dex_data_quantities
        (
            id INT NOT NULL UNIQUE DEFAULT NEXTVAL('data_quantity_id_seq'),
            data_quantity VARCHAR(64) NOT NULL UNIQUE,
            unit VARCHAR(32) NOT NULL,
            description TEXT NOT NULL,
            PRIMARY KEY (id)
        );
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_quantities"';
    END;
    BEGIN
        CREATE SEQUENCE product_id_seq;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create sequence "product_id_seq"';
    END;
    BEGIN
        CREATE TABLE dex_products
        (
            id INT NOT NULL UNIQUE DEFAULT NEXTVAL('product_id_seq'),
            product VARCHAR(32) NOT NULL UNIQUE,
            description TEXT NOT NULL,
            PRIMARY KEY (id)
        );
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_products"';
    END;
    BEGIN 
        CREATE SEQUENCE product_parameter_id_seq;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create sequence "product_parameter_id_seq"';
    END;
    BEGIN
        CREATE TABLE dex_product_parameters
        (
            id INT NOT NULL UNIQUE DEFAULT NEXTVAL('product_parameter_id_seq'),
            parameter VARCHAR(32) NOT NULL UNIQUE,
            description TEXT NOT NULL,
            PRIMARY KEY (id)
        );
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_product_parameters"';
    END;
    BEGIN
        ALTER SEQUENCE channel_id_seq RENAME TO radar_id_seq;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Failed to rename sequence "channel_id_seq"';
    END;
    BEGIN
        ALTER TABLE dex_channels RENAME TO dex_radars;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Failed to rename table "dex_channels"';
    END;
    BEGIN
        ALTER TABLE dex_radars ALTER COLUMN id SET DEFAULT nextval('radar_id_seq');
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Failed to alter table "dex_radars"';
    END;
    BEGIN
	ALTER TABLE dex_channel_permissions ADD CONSTRAINT dex_channel_permissions_channel_id_fkey
		FOREIGN KEY (channel_id) REFERENCES dex_radars (id) MATCH SIMPLE;
    EXCEPTION
	WHEN OTHERS THEN RAISE NOTICE 'Failed to alter column "dex_channel_permissions.channel_id"';
    END;
    BEGIN
        ALTER TABLE dex_channel_permissions ALTER COLUMN channel_id SET NOT NULL;
    EXCEPTION
        WHEN OTHERS THEN RAISE NOTICE 'Failed to alter column "dex_channel_permissions.channel_id"';
    END;
END;
$$ LANGUAGE plpgsql;

--
-- creates data sources based on records existing in dex_radars table
--
CREATE OR REPLACE FUNCTION create_data_sources() RETURNS integer AS $$
BEGIN
    --
    -- check if data sources exist
    --
    PERFORM true FROM information_schema.tables where table_name = 'dex_data_sources';
    IF NOT FOUND THEN
        --
        -- upgrade schema
        --
        BEGIN
            CREATE SEQUENCE data_source_id_seq;
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create sequence "data_source_id_seq"';
        END;
        BEGIN
            CREATE TABLE dex_data_sources
            (
                id INT NOT NULL UNIQUE DEFAULT NEXTVAL('data_source_id_seq'),
                name VARCHAR(128) UNIQUE NOT NULL,
                description TEXT,
                PRIMARY KEY (id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_sources"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_quantities
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                data_quantity_id INT NOT NULL REFERENCES dex_data_quantities(id) ON DELETE CASCADE,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_quantities"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_file_objects
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                file_object_id INT NOT NULL REFERENCES dex_file_objects(id) ON DELETE CASCADE,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_file_objects"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_products
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                product_id INT NOT NULL REFERENCES dex_products(id) ON DELETE CASCADE,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_products"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_product_parameters
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                product_parameter_id INT NOT NULL REFERENCES dex_product_parameters(id) ON DELETE CASCADE,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_product_parameters"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_radars
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                radar_id INT NOT NULL REFERENCES dex_radars(id) ON DELETE CASCADE,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_radars"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_users
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                user_id INT NOT NULL REFERENCES dex_users(id) ON DELETE CASCADE,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_users"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_product_parameter_values
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                parameter_id INT NOT NULL REFERENCES dex_product_parameters(id) ON DELETE CASCADE,
                parameter_value VARCHAR(64) NOT NULL,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_product_parameter_values"';
        END;
        BEGIN
            ALTER TABLE dex_subscriptions RENAME COLUMN channel_name TO data_source_name;
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'Failed to rename column "dex_subscriptions.channel_name"';
        END;
            BEGIN
                    ALTER TABLE dex_subscriptions ALTER COLUMN data_source_name TYPE VARCHAR(128);
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'Failed to modify data type of column "dex_subscriptions.data_source_name"';
        END;
        BEGIN
            CREATE TABLE dex_data_source_filters
            (
                id SERIAL NOT NULL,
                data_source_id INT NOT NULL REFERENCES dex_data_sources(id) ON DELETE CASCADE,
                filter_id INT NOT NULL,
                PRIMARY KEY(id)
            );
        EXCEPTION
            WHEN OTHERS THEN RAISE NOTICE 'failed to create table "dex_data_source_filters"';
        END;
        --
        -- create data sources
	--
        INSERT INTO dex_data_sources (name, description) SELECT name, name FROM dex_radars
            WHERE 1 = 1;
        DECLARE
            perm RECORD;
            radar RECORD;
            dataSource RECORD;
            userId INTEGER;
            filterId INTEGER;
            wmoNumber VARCHAR;
        BEGIN
                --
                -- create data source users based on records existing in dex_channel_permissions table
                --
                FOR perm IN SELECT * FROM dex_channel_permissions LOOP
                        userId = perm.user_id;
                        FOR radar IN SELECT * FROM dex_radars WHERE id = perm.channel_id LOOP
                                FOR dataSource IN SELECT * FROM dex_data_sources WHERE name = radar.name LOOP
                                        INSERT INTO dex_data_source_users (data_source_id, user_id) VALUES
                                                (dataSource.id, userId);
                                END LOOP;
                        END LOOP;
                END LOOP;
                --
                -- create data source radars parameters based or records existing in dex_radars table
                --
                FOR dataSource IN SELECT * FROM dex_data_sources LOOP
                        FOR radar IN SELECT * FROM dex_radars WHERE name = dataSource.name LOOP
                                INSERT INTO dex_data_source_radars (data_source_id, radar_id) VALUES
                                        (dataSource.id, radar.id);
                        END LOOP;
                END LOOP;
                --
                -- create data source filters parameter
                --
                FOR dataSource IN SELECT * FROM dex_data_sources LOOP
                        INSERT INTO beast_filters (type) VALUES ('attr') RETURNING filter_id INTO filterId;
                        SELECT wmo_number FROM dex_radars WHERE name = dataSource.name INTO wmoNumber;
                        INSERT INTO beast_attr_filters (filter_id, attr, op, value_type, value, negated) VALUES
                                (filterId, 'what/source:WMO', 'EQ', 'STRING', wmoNumber, false);
                        INSERT INTO dex_data_source_filters (data_source_id, filter_id) VALUES
                                (dataSource.id, filterId);
                END LOOP;

                RETURN 0;
        END;
    END IF;
    RETURN 0;
END;
$$ LANGUAGE plpgsql;

SELECT split_dex_node_connections_address();
SELECT split_dex_node_configuration_address();
SELECT split_dex_users_node_address();
SELECT restart_seq_with_max('dex_messages', 'id');
SELECT upgrade_dex_schema();
SELECT create_data_sources();

DROP FUNCTION make_plpgsql();
DROP FUNCTION split_dex_node_configuration_address();
DROP FUNCTION split_dex_node_connections_address();
DROP FUNCTION split_dex_users_node_address();
DROP FUNCTION restart_seq_with_max(TEXT, TEXT);
DROP FUNCTION upgrade_dex_schema();
DROP FUNCTION create_data_sources();
