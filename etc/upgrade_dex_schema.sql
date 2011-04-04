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
Author : szewczenko
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
 EXECUTE 'SELECT MAX('
         || column_name ||
         ') FROM '
         || table_name INTO maxval;
 EXECUTE 'ALTER SEQUENCE '
         || table_name || '_' || column_name || '_seq'
         || ' RESTART WITH '
         || maxval + 1;
 RETURN maxval + 1;
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
END;
$$ LANGUAGE plpgsql;

SELECT split_dex_node_connections_address();
SELECT split_dex_node_configuration_address();
SELECT split_dex_users_node_address();
SELECT restart_seq_with_max('dex_messages', 'id');
SELECT upgrade_dex_schema();

DROP FUNCTION make_plpgsql();
DROP FUNCTION split_dex_node_configuration_address();
DROP FUNCTION split_dex_node_connections_address();
DROP FUNCTION split_dex_users_node_address();
DROP FUNCTION restart_seq_with_max(TEXT, TEXT);
DROP FUNCTION upgrade_dex_schema();