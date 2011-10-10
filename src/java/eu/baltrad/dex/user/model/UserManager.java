/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.user.model;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.util.MessageDigestUtil;
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * User manager class implementing user object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class UserManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public UserManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets user with a given ID.
     *
     * @param id User ID
     * @return User with a given ID
     */
    public User getUserById( int id ) {
        Connection conn = null;
        User user = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users usr, dex_node_address addr, dex_user_address " +
                    "usr_addr WHERE usr.id = " + id + " AND usr.id = usr_addr.user_id " +
                    "AND addr.id = usr_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int userId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String nameHash = resultSet.getString( "name_hash" );
                String role = resultSet.getString( "role_name" );
                String passwd = resultSet.getString( "password" );
                String factory = resultSet.getString( "factory" );
                String country = resultSet.getString( "country" );
                String city = resultSet.getString( "city" );
                String cityCode = resultSet.getString( "city_code" );
                String street = resultSet.getString( "street" );
                String number = resultSet.getString( "number" );
                String phone = resultSet.getString( "phone" );
                String email = resultSet.getString( "email" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                user = new User( userId, name, nameHash, role, passwd, factory, country, city,
                        cityCode, street, number, phone, email, scheme, hostAddress, port, appCtx,
                        entryAddress );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select user", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return user;
    }
    /**
     * Gets user with a given name.
     *
     * @param name User name
     * @return User with a given name
     */
    public User getUserByName( String userName ) {
        Connection conn = null;
        User user = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users usr, dex_node_address addr, dex_user_address " +
                    "usr_addr WHERE usr.name = '" + userName + "' AND usr.id = usr_addr.user_id " +
                    "AND addr.id = usr_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int userId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String nameHash = resultSet.getString( "name_hash" );
                String role = resultSet.getString( "role_name" );
                String passwd = resultSet.getString( "password" );
                String factory = resultSet.getString( "factory" );
                String country = resultSet.getString( "country" );
                String city = resultSet.getString( "city" );
                String cityCode = resultSet.getString( "city_code" );
                String street = resultSet.getString( "street" );
                String number = resultSet.getString( "number" );
                String phone = resultSet.getString( "phone" );
                String email = resultSet.getString( "email" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                user = new User( userId, name, nameHash, role, passwd, factory, country, city,
                        cityCode, street, number, phone, email, scheme, hostAddress, port, appCtx,
                        entryAddress );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select user", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return user;
    }
    /**
     * Gets user with a given name hash.
     *
     * @param userNameHash User name hash
     * @return User with a given name hash
     */
    public User getUserByNameHash( String userNameHash ) {
        Connection conn = null;
        User user = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users usr, dex_node_address addr, dex_user_address " +
                    "usr_addr WHERE usr.name_hash = '" + userNameHash + "' AND usr.id = " +
                    "usr_addr.user_id AND addr.id = usr_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int userId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String nameHash = resultSet.getString( "name_hash" );
                String role = resultSet.getString( "role_name" );
                String passwd = resultSet.getString( "password" );
                String factory = resultSet.getString( "factory" );
                String country = resultSet.getString( "country" );
                String city = resultSet.getString( "city" );
                String cityCode = resultSet.getString( "city_code" );
                String street = resultSet.getString( "street" );
                String number = resultSet.getString( "number" );
                String phone = resultSet.getString( "phone" );
                String email = resultSet.getString( "email" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                user = new User( userId, name, nameHash, role, passwd, factory, country, city,
                        cityCode, street, number, phone, email, scheme, hostAddress, port, appCtx,
                        entryAddress );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select user", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return user;
    }
    /**
     * Gets all users.
     *
     * @return List of all registered users
     */
    public List<User> getUsers() {
        Connection conn = null;
        List<User> allUsers = new ArrayList<User>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users usr, dex_node_address addr, dex_user_address " +
                    "usr_addr WHERE usr.id = usr_addr.user_id AND addr.id = usr_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int userId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String nameHash = resultSet.getString( "name_hash" );
                String role = resultSet.getString( "role_name" );
                String passwd = resultSet.getString( "password" );
                String factory = resultSet.getString( "factory" );
                String country = resultSet.getString( "country" );
                String city = resultSet.getString( "city" );
                String cityCode = resultSet.getString( "city_code" );
                String street = resultSet.getString( "street" );
                String number = resultSet.getString( "number" );
                String phone = resultSet.getString( "phone" );
                String email = resultSet.getString( "email" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                User user = new User( userId, name, nameHash, role, passwd, factory, country, city,
                        cityCode, street, number, phone, email, scheme, hostAddress, port, appCtx,
                        entryAddress );
                allUsers.add( user );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select users", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return allUsers;
    }
    /**
     * Gets all roles.
     *
     * @return List of all roles defined in the system
     */
    public List<Role> getRoles() {
        Connection conn = null;
        List<Role> roles = new ArrayList<Role>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_roles" );
            while( resultSet.next() ) {
                int roleId = resultSet.getInt( "id" );
                String roleName = resultSet.getString( "role" );
                Role role = new Role( roleId, roleName );
                roles.add( role );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select user role", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return roles;
    }
    /**
     * Saves or updates user account.
     *
     * @param user User account
     * @return Number of saved or updated records
     * @throws Exception
     */
    public int saveOrUpdate( User user ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            ResultSet generatedKeys = null;
            int userId = 0;
            int addressId = 0;
            // record does not exists, do insert
            if( user.getId() == 0 ) {
                sql = "INSERT INTO dex_users (name, name_hash, role_name, password, factory, " +
                    "country, city, city_code, street, number, phone, email ) VALUES ('" +
                    user.getName() + "', '" + MessageDigestUtil.createHash( user.getName() ) +
                    "', '" + user.getRoleName() + "', '" + MessageDigestUtil.createHash(
                    user.getPassword() ) + "', '" + user.getFactory() + "', '" + user.getCountry() +
                    "', '" + user.getCity() + "', '" + user.getCityCode() + "', '" +
                    user.getStreet() + "', '" + user.getNumber() + "', '" + user.getPhone() +
                    "', '" + user.getEmail() + "');";
                update = stmt.executeUpdate( sql, Statement.RETURN_GENERATED_KEYS ) ;
                generatedKeys = stmt.getGeneratedKeys();
                if( generatedKeys.next() ) {
                    userId = generatedKeys.getInt( 1 );
                }
                sql = "INSERT INTO dex_node_address (scheme, host_address, port, app_context, " +
                    "entry_address) VALUES ('" + user.getScheme() + "', '" + user.getHostAddress() +
                    "', " + user.getPort() + ", '" + user.getAppCtx() + "', '" +
                    user.getEntryAddress() + "');";
                update = stmt.executeUpdate( sql, Statement.RETURN_GENERATED_KEYS ) ;
                generatedKeys = stmt.getGeneratedKeys();
                if( generatedKeys.next() ) {
                    addressId = generatedKeys.getInt( 1 );
                }
                sql = "INSERT INTO dex_user_address (user_id, address_id) VALUES (" + userId + ", "
                        + addressId + ");";
                update = stmt.executeUpdate( sql );
                stmt.close();
            } else {
                // record exists, do update
                sql = "UPDATE dex_users SET name = '" + user.getName() + "', name_hash = '" +
                    MessageDigestUtil.createHash( user.getName() ) + "', role_name = '" +
                    user.getRoleName() + "', " + "password = '" + MessageDigestUtil.createHash(
                    user.getPassword() ) + "', factory = '" + user.getFactory() + "', country = '" +
                    user.getCountry() + "', city = '" + user.getCity() + "', city_code = '" +
                    user.getCityCode() + "', street = '" + user.getStreet() + "', number = '" +
                    user.getNumber() + "', phone = '" + user.getPhone() + "', email = '" +
                    user.getEmail() + "' WHERE id = '" + user.getId() + "';";
                update = stmt.executeUpdate( sql ) ;
                sql = "UPDATE dex_node_address SET scheme = '" + user.getScheme() +
                        "', host_address = '" + user.getHostAddress() + "', port = " +
                        user.getPort() + ", app_context = '" + user.getAppCtx() +
                        "', entry_address = '" + user.getEntryAddress() + "' WHERE id IN (SELECT " +
                        "address_id FROM dex_user_address WHERE user_id = " + user.getId() + " );";
                update = stmt.executeUpdate( sql );
                stmt.close();
            }
        } catch( Exception e ) {
            log.error( "Failed to save user account", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes user account with a given ID.
     *
     * @param id User account ID
     * @return Number of deleted records
     * @throws Exception
     */
    public int deleteUser( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_users WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete user account", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
