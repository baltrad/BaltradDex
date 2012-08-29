/*******************************************************************************
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
        RAISE NOTICE 'renaming table dex_delivery_register into dex_delivery_registry';
        ALTER TABLE dex_delivery_register RENAME TO dex_delivery_registry;
    ELSE
        RAISE NOTICE 'table "dex_delivery_register" already renamed';
    END IF;
END;
$$ LANGUAGE plpgsql;


SELECT remove_name_hash_from_dex_users();
--SELECT reset_user_passwords();
SELECT rename_registry_table();

DROP FUNCTION make_plpgsql(); 
DROP FUNCTION remove_name_hash_from_dex_users();
DROP FUNCTION reset_user_passwords();
DROP FUNCTION rename_registry_table();
