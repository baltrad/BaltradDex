-- SQL script creates baltrad_db database

-- create user
DROP USER IF EXISTS baltrad;
CREATE USER baltrad WITH PASSWORD 'baltrad';   

-- create database
DROP DATABASE IF EXISTS baltrad;
CREATE DATABASE baltrad
  WITH OWNER = baltrad
       ENCODING = 'UTF8'
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
