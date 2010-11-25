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
	BEGIN
    	ALTER TABLE dex_delivery_register DROP COLUMN file_name;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery.register.file_name does not exist';
  	END;
	BEGIN
    	ALTER TABLE dex_delivery_register ADD COLUMN uuid VARCHAR(128) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery.register.uuid already exists';
  	END;
	BEGIN
    	ALTER TABLE dex_delivery_register ADD COLUMN user_name VARCHAR(32) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery.register.user_name already exists';
  	END;
	BEGIN
    	ALTER TABLE dex_delivery_register ADD COLUMN timestamp TIMESTAMP NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery.register.timestamp already exists';
  	END;
	BEGIN
    	ALTER TABLE dex_delivery_register ADD COLUMN status VARCHAR(16) NOT NULL;
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Column dex_delivery.register.status already exists';
  	END;
	BEGIN
    	ALTER TABLE dex_node_configuration ALTER COLUMN time_zone TYPE VARCHAR(128);
  	EXCEPTION
    	WHEN OTHERS THEN RAISE NOTICE 'Failed to alter column dex_node_configuration.time_zone already exists';
  	END;
END;
$$ LANGUAGE plpgsql
;

select upgrade_dex_schema();

drop function make_plpgsql();
drop function upgrade_dex_schema();
