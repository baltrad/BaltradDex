-- SQL script drops database schema for BaltradDex

-- drop tables if exist ----------------------------------------------------------------

DROP TABLE IF EXISTS dex_subscriptions;
DROP TABLE IF EXISTS dex_delivery_register;
DROP TABLE IF EXISTS dex_users;
DROP TABLE IF EXISTS dex_roles;
DROP TABLE IF EXISTS dex_messages;
DROP TABLE IF EXISTS dex_channels;

-- just for testing ---
DROP TABLE IF EXISTS dex_data;

DROP SEQUENCE IF EXISTS log_entry_id_seq;
DROP SEQUENCE IF EXISTS channel_id_seq;
DROP SEQUENCE IF EXISTS user_id_seq;	
DROP SEQUENCE IF EXISTS subscription_id_seq;
DROP SEQUENCE IF EXISTS delivery_register_id_seq;
