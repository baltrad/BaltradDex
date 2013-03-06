/*******************************************************************************
Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW

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
********************************************************************************
Document   : SQL script drops BaltradDex schema
Created on : Jun 22, 2010, 11:57:02 AM
Author     : szewczenko
*******************************************************************************/

DROP TABLE IF EXISTS dex_roles CASCADE;
DROP TABLE IF EXISTS dex_users CASCADE;
DROP TABLE IF EXISTS dex_users_roles;
DROP TABLE IF EXISTS dex_keys;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_radars CASCADE;
DROP TABLE IF EXISTS dex_subscriptions CASCADE;
DROP TABLE IF EXISTS dex_subscriptions_users;
DROP TABLE IF EXISTS dex_subscriptions_data_sources;
DROP TABLE IF EXISTS dex_delivery_registry CASCADE;
DROP TABLE IF EXISTS dex_delivery_registry_users;
DROP TABLE IF EXISTS dex_data_sources CASCADE;
DROP TABLE IF EXISTS dex_file_objects CASCADE;
DROP TABLE IF EXISTS dex_data_quantities CASCADE;
DROP TABLE IF EXISTS dex_products CASCADE;
DROP TABLE IF EXISTS dex_product_parameters CASCADE;
DROP TABLE IF EXISTS dex_data_source_quantities;
DROP TABLE IF EXISTS dex_data_source_file_objects;
DROP TABLE IF EXISTS dex_data_source_products;
DROP TABLE IF EXISTS dex_data_source_product_parameters;
DROP TABLE IF EXISTS dex_data_source_product_parameter_values;
DROP TABLE IF EXISTS dex_data_source_radars;
DROP TABLE IF EXISTS dex_data_source_users;
DROP TABLE IF EXISTS dex_data_source_filters;

DROP FUNCTION IF EXISTS dex_trim_messages_by_number() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_messages_by_age() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_registry_by_number() CASCADE;
DROP FUNCTION IF EXISTS dex_trim_registry_by_age() CASCADE;