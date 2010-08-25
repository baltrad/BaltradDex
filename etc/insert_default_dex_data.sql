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
Document   : SQL script inserts default mandatory data into BaltradDex tables
Created on : Aug 19, 2010, 10:36:42 AM
Author     : szewczenko
***************************************************************************************************/

-- insert default roles into dex_roles table -------------------------------------------------------
INSERT INTO dex_roles (id, role) VALUES (1, 'admin'), (2, 'operator'),
    (3, 'peer'), (4, 'user');

-- insert default user admin into dex_users table --------------------------------------------------
INSERT INTO dex_users (name, name_hash, role_name, password, node_address, factory, country, city,
    city_code, street, number, phone, email)
    VALUES ('admin', MD5('admin'), 'admin', MD5('baltrad'),
        'http://localhost:8084/BaltradDex/dispatch.htm', 'Company', 'Country', 'City', 'Code',
        'Street', 'Number', 'Phone', 'email address');
-- create default node configuration ---------------------------------------------------------------
INSERT INTO dex_node_configuration (name, type, address, org_name, org_address, time_zone, temp_dir,
    email ) VALUES('Your node name', 'Primary', 'http://localhost:8084/BaltradDex/dispatch.htm',
    'Your organization name', 'Your organization address', 'Time zone',
    'Relative temporary directory', 'Node administrator email');

