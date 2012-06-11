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

ALTER TABLE dex_users DROP COLUMN name_hash;
-- reset passwords to user names 
CREATE OR REPLACE FUNCTION update_user_passwords() RETURNS void AS $$
DECLARE 
	user_name TEXT;
BEGIN
	FOR user_name IN SELECT NAME FROM dex_users
	LOOP
		UPDATE dex_users SET password = user_name WHERE name = user_name; 
	END LOOP;
	RETURN;	
EXCEPTION WHEN OTHERS THEN 
	RAISE NOTICE 'Failed to update user passwords';
END;
$$ LANGUAGE plpgsql;

-- SELECT update_user_passwords();

