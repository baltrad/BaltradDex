/*******************************************************************************
Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW

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
********************************************************************************
Document : SQL script upgrading existing BaltradDex schema
Created on : Jan 14, 2011, 9:09 AM
*******************************************************************************/

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

CREATE OR REPLACE FUNCTION remove_name_hash_from_dex_users() RETURNS VOID AS $$
BEGIN
    PERFORM true FROM information_schema.columns
        WHERE table_name = 'dex_users' AND column_name = 'name_hash';
    IF FOUND THEN
        RAISE NOTICE 'removing name_hash from dex_users';
        ALTER TABLE dex_users DROP COLUMN name_hash;
    ELSE
        RAISE NOTICE 'column "name_hash" already removed';
    END IF;
END;
$$ LANGUAGE plpgsql;

/*
    It may be necessary to reset users' passwords since we're now using
    spring-security package.
*/  
CREATE OR REPLACE FUNCTION reset_user_passwords() RETURNS void AS $$
DECLARE 
	user_name TEXT;
BEGIN
	FOR user_name IN SELECT NAME FROM dex_users
	LOOP
		UPDATE dex_users SET password = MD5(user_name) WHERE name = user_name; 
	END LOOP;
	RETURN;	
EXCEPTION WHEN OTHERS THEN 
	RAISE NOTICE 'Failed to update user passwords';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION rename_registry_table() RETURNS void AS $$
BEGIN
    PERFORM true FROM information_schema.tables
        WHERE table_name = 'dex_delivery_register';
    IF FOUND THEN
        IF NOT EXISTS (SELECT * FROM information_schema.tables 
                WHERE table_name = 'dex_delivery_registry') THEN  
            RAISE NOTICE 'renaming table dex_delivery_register into dex_delivery_registry';
            ALTER TABLE dex_delivery_register RENAME TO dex_delivery_registry;
	END IF;
    ELSE
        RAISE NOTICE 'table "dex_delivery_register" already renamed';
    END IF;
END;
$$ LANGUAGE plpgsql;

/*
    Update trigger functions with new registry table name. 
*/
CREATE OR REPLACE FUNCTION dex_trim_registry_by_number() RETURNS trigger AS $$ 
    DECLARE
        records_limit INTEGER;
    BEGIN
        records_limit = TG_ARGV[0];
        DELETE FROM dex_delivery_registry WHERE id IN (SELECT id FROM dex_delivery_registry
            ORDER BY timestamp DESC OFFSET records_limit);
        RETURN NEW; 
    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dex_trim_registry_by_age() RETURNS trigger AS $$ 
    DECLARE
        max_age INTERVAL;
    BEGIN
        SELECT (TG_ARGV[0] || ' days ' || TG_ARGV[1] || ' hours ' || TG_ARGV[2] ||
            ' minutes')::INTERVAL INTO max_age;
        DELETE FROM dex_delivery_registry WHERE timestamp IN (SELECT timestamp FROM
            dex_delivery_registry WHERE age(now(), timestamp) > max_age);
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

/*
    Add dex_keys table
*/
CREATE OR REPLACE FUNCTION create_dex_keys_table() RETURNS void AS $$
BEGIN
    PERFORM true FROM information_schema.tables WHERE table_name = 'dex_keys';
    IF NOT FOUND THEN
        CREATE TABLE dex_keys
        (
            id SERIAL NOT NULL PRIMARY KEY,
            name VARCHAR (64) NOT NULL UNIQUE,
            checksum VARCHAR (32),
            authorized BOOLEAN DEFAULT FALSE
        );
    ELSE
        RAISE NOTICE 'table "dex_keys" already exists';
    END IF;
END;
$$ LANGUAGE plpgsql;

/*
    Modify dex_delivery_registry table
*/
CREATE OR REPLACE FUNCTION upgrade_dex_delivery_registry_table() 
    RETURNS void AS $$
BEGIN
    PERFORM true FROM information_schema.columns WHERE table_name = 
        'dex_delivery_registry' AND column_name = 'type';
    IF NOT FOUND THEN
        DECLARE
            rec RECORD;
        BEGIN
            ALTER TABLE dex_delivery_registry ADD COLUMN type VARCHAR(16);
            ALTER TABLE dex_delivery_registry ADD COLUMN status_tmp 
                    BOOLEAN NOT NULL DEFAULT FALSE;
            FOR rec IN SELECT * FROM dex_delivery_registry
            LOOP
                UPDATE dex_delivery_registry SET type = 'upload' 
                    WHERE id = rec.id;
                IF rec.status = 'SUCCESS' THEN
                    UPDATE dex_delivery_registry SET status_tmp = true 
                        WHERE id = rec.id; 
                ELSE
                    UPDATE dex_delivery_registry SET status_tmp = false 
                        WHERE id = rec.id; 
                END IF;
            END LOOP;
            ALTER TABLE dex_delivery_registry ALTER COLUMN type SET NOT NULL;
            ALTER TABLE dex_delivery_registry DROP COLUMN status;
            ALTER TABLE dex_delivery_registry RENAME COLUMN status_tmp TO status; 
        EXCEPTION WHEN OTHERS THEN 
            RAISE NOTICE 'Failed to upgrade dex_delivery_registry';
        END;
    ELSE
        RAISE NOTICE 'table dex_delivery_registry already upgraded';
    END IF;
END
$$ LANGUAGE plpgsql;

/*
    Add dex_dex_delivery_registry_data_sources table
*/
CREATE OR REPLACE FUNCTION create_dex_delivery_registry_data_sources_table() 
    RETURNS void AS $$
BEGIN
    PERFORM true FROM information_schema.tables WHERE 
        table_name = 'dex_delivery_registry_data_sources';
    IF NOT FOUND THEN
        CREATE TABLE dex_delivery_registry_data_sources
        (
            id SERIAL NOT NULL PRIMARY KEY,
            entry_id INT NOT NULL REFERENCES dex_delivery_registry (id) ON DELETE CASCADE,
            data_source_id INT NOT NULL REFERENCES dex_data_sources (id) ON DELETE CASCADE
        );
    ELSE
        RAISE NOTICE 'table "dex_delivery_registry_data_sources" already exists';
    END IF;
END;
$$ LANGUAGE plpgsql;

/*
    Add peer data sources to dex_data_source_users table
*/
CREATE OR REPLACE FUNCTION update_dex_data_source_users_table() 
    RETURNS void AS $$
DECLARE
	rec RECORD;
	num_rec INT;
BEGIN
	CREATE TEMP TABLE peer_data_source_users ON COMMIT DROP AS 
        SELECT 
            u.id AS user_id, ds.id AS data_source_id 
        FROM
            dex_subscriptions s, dex_users u, dex_data_sources ds, 
            dex_subscriptions_users su, dex_subscriptions_data_sources sds 
        WHERE 
            s.id = su.subscription_id AND u.id = su.user_id AND 
            s.type = 'local' AND s.id = sds.subscription_id AND 
            ds.id = sds.data_source_id;
        
        FOR rec IN SELECT * FROM peer_data_source_users LOOP
            SELECT count(*) INTO num_rec FROM dex_data_source_users WHERE 
                data_source_id = rec.data_source_id AND user_id = rec.user_id;
		
		IF num_rec = 0 THEN
			RAISE NOTICE 'inserting row: % %', rec.data_source_id, rec.user_id;
			INSERT INTO dex_data_source_users (data_source_id, user_id) 
                VALUES (rec.data_source_id, rec.user_id);
        ELSE
            RAISE NOTICE 'row already exists: % %', rec.data_source_id, 
                rec.user_id;
		END IF;
	END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT remove_name_hash_from_dex_users();
-- SELECT reset_user_passwords();
SELECT rename_registry_table();
SELECT create_dex_keys_table();
SELECT upgrade_dex_delivery_registry_table();
SELECT create_dex_delivery_registry_data_sources_table();
SELECT update_dex_data_source_users_table(); 

DROP FUNCTION make_plpgsql(); 
DROP FUNCTION remove_name_hash_from_dex_users();
DROP FUNCTION reset_user_passwords();
DROP FUNCTION rename_registry_table();
DROP FUNCTION update_dex_data_source_users_table();
