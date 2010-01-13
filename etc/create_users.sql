use mysql;
-- First, create the databases
create database baltrad_db;
create database baltrad_testdb;
-- And the users
CREATE USER 'baltrad'@'localhost' IDENTIFIED BY 'baltrad';
grant all on baltrad_db.* to 'baltrad'@'localhost' identified by 'baltrad';
grant all on mysql.baltrad_db to 'baltrad'@'localhost' identified by 'baltrad';
grant all on baltrad_db.baltrad_db to 'baltrad'@'localhost' identified by 'baltrad';
grant all on baltrad_testdb.* to 'baltrad'@'localhost' identified by 'baltrad';
grant all on mysql.baltrad_testdb to 'baltrad'@'localhost' identified by 'baltrad';
grant all on baltrad_testdb.baltrad_testdb to 'baltrad'@'localhost' identified by 'baltrad';
flush privileges;
