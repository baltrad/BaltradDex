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
Document   : SQL script drops BaltradDex schema
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
***************************************************************************************************/

-- drop tables if exist ----------------------------------------------------------------

DROP TABLE IF EXISTS dex_subscriptions CASCADE;
DROP TABLE IF EXISTS dex_delivery_register CASCADE;
DROP TABLE IF EXISTS dex_channel_permissions CASCADE;
DROP TABLE IF EXISTS dex_users CASCADE;
DROP TABLE IF EXISTS dex_roles CASCADE;
DROP TABLE IF EXISTS dex_messages CASCADE;
DROP TABLE IF EXISTS dex_channels CASCADE;
DROP TABLE IF EXISTS dex_node_connections CASCADE;
DROP TABLE IF EXISTS dex_node_configuration CASCADE;

DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS channel_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;	
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;
DROP SEQUENCE IF EXISTS node_connection_id_seq;
DROP SEQUENCE IF EXISTS configuration_id_seq;
DROP SEQUENCE IF EXISTS channel_permission_id_seq;
----------------------------------------------------------------------------------------------------