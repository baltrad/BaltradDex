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
Document   : SQL script upgrading existing BaltradDex schema
Created on : Nov 25, 2010, 8:59 AM
Author     : szewczenko
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

CREATE OR REPLACE FUNCTION upgrade_dex_schema() RETURNS VOID AS $$
BEGIN
--	BEGIN
--    	ALTER TABLE dex_delivery_register DROP COLUMN file_name;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery_register.file_name does not exist';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_delivery_register ADD COLUMN uuid VARCHAR(128) NOT NULL;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery_register.uuid already exists';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_delivery_register ADD COLUMN user_name VARCHAR(32) NOT NULL;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery_register.user_name already exists';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_delivery_register ADD COLUMN timestamp TIMESTAMP NOT NULL;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery_register.timestamp already exists';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_delivery_register ADD COLUMN status VARCHAR(16) NOT NULL;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery_register.status already exists';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_node_configuration ALTER COLUMN time_zone TYPE VARCHAR(128);
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Failed to alter column dex_node_configuration.time_zone already exists';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_messages DROP COLUMN date;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_messages.date does not exist';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_messages DROP COLUMN time;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_messages.time does not exist';
--  	END;
--	BEGIN
--    	ALTER TABLE dex_messages ADD COLUMN timestamp TIMESTAMP NOT NULL;
--  	EXCEPTION
--    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_messages.timestamp already exists';
--  	END;

        BEGIN
    	ALTER TABLE dex_node_connections DROP COLUMN address;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_node_connections.address does not exist';
  	END;
	BEGIN
    	ALTER TABLE dex_node_connections ADD COLUMN short_address VARCHAR(64) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_node_connections.short_address already exists';
  	END;
        BEGIN
    	ALTER TABLE dex_node_connections ADD COLUMN port VARCHAR(16) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_node_connections.port already exists';
  	END;

        BEGIN
    	ALTER TABLE dex_node_configuration DROP COLUMN address;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_node_configuration.address does not exist';
  	END;
	BEGIN
    	ALTER TABLE dex_node_configuration ADD COLUMN short_address VARCHAR(64) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_node_configuration.short_address already exists';
  	END;
        BEGIN
    	ALTER TABLE dex_node_configuration ADD COLUMN port VARCHAR(16) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_node_configuration.port already exists';
  	END;

        BEGIN
    	ALTER TABLE dex_users DROP COLUMN node_address;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_users.node_address does not exist';
  	END;
	BEGIN
    	ALTER TABLE dex_users ADD COLUMN short_address VARCHAR(64) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_users.short_address already exists';
  	END;
        BEGIN
    	ALTER TABLE dex_users ADD COLUMN port VARCHAR(16) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_users.port already exists';
  	END;


END;
$$ LANGUAGE plpgsql
;

select upgrade_dex_schema();

drop function make_plpgsql();
drop function upgrade_dex_schema();
