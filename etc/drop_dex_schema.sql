/***************************************************************************************************
Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW

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

-- drop tables -------------------------------------------------------------------------------------
DROP TABLE IF EXISTS dex_subscriptions CASCADE;
DROP TABLE IF EXISTS dex_delivery_register CASCADE;
DROP TABLE IF EXISTS dex_channel_permissions CASCADE;
DROP TABLE IF EXISTS dex_channels CASCADE;
DROP TABLE IF EXISTS dex_users CASCADE;
DROP TABLE IF EXISTS dex_roles CASCADE;
DROP TABLE IF EXISTS dex_messages CASCADE;
DROP TABLE IF EXISTS dex_radars CASCADE;
DROP TABLE IF EXISTS dex_node_connections CASCADE;
DROP TABLE IF EXISTS dex_node_configuration CASCADE;
DROP TABLE IF EXISTS dex_log_configuration;
DROP TABLE IF EXISTS dex_file_objects CASCADE;
DROP TABLE IF EXISTS dex_data_quantities CASCADE;
DROP TABLE IF EXISTS dex_products CASCADE;
DROP TABLE IF EXISTS dex_product_parameters CASCADE;
DROP TABLE IF EXISTS dex_data_sources CASCADE;
DROP TABLE IF EXISTS dex_data_source_quantities CASCADE;
DROP TABLE IF EXISTS dex_data_source_file_objects CASCADE;
DROP TABLE IF EXISTS dex_data_source_products CASCADE;
DROP TABLE IF EXISTS dex_data_source_product_parameters CASCADE;
DROP TABLE IF EXISTS dex_data_source_product_parameter_values CASCADE;
DROP TABLE IF EXISTS dex_data_source_radars CASCADE;
DROP TABLE IF EXISTS dex_data_source_users CASCADE;
DROP TABLE IF EXISTS dex_data_source_filters;
DROP TABLE IF EXISTS dex_certificates;
-- drop sequences ----------------------------------------------------------------------------------
DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS radar_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;	
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;
DROP SEQUENCE IF EXISTS node_connection_id_seq;
DROP SEQUENCE IF EXISTS configuration_id_seq;
DROP SEQUENCE IF EXISTS channel_permission_id_seq;
DROP SEQUENCE IF EXISTS file_object_id_seq;
DROP SEQUENCE IF EXISTS data_quantity_id_seq;
DROP SEQUENCE IF EXISTS product_id_seq;
DROP SEQUENCE IF EXISTS product_parameter_id_seq;
DROP SEQUENCE IF EXISTS data_source_id_seq;
-- drop functions ----------------------------------------------------------------------------------
DROP FUNCTION IF EXISTS dex_trim_messages_by_number() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_messages_by_age() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_registry_by_number() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_registry_by_age() CASCADE;
----------------------------------------------------------------------------------------------------