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
import eu.baltrad.dex.log.model.MessageLogger;

import eu.baltrad.dex.util.MessageDigestUtil;
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
     * Gets all users.
     *
     * @return List of all registered users
     */
    public List<User> get() {
        Connection conn = null;
        List<User> users = new ArrayList<User>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users;";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String nameHash = resultSet.getString("name_hash" );
                String role = resultSet.getString("role_name");
                String passwd = resultSet.getString("password");
                String orgName = resultSet.getString("org_name");
                String orgUnit = resultSet.getString("org_unit");
                String locality = resultSet.getString("locality");
                String state = resultSet.getString("state");
                String countryCode = resultSet.getString("country_code");
                String nodeAddress = resultSet.getString("node_address");
                User user = new User(userId, name, nameHash, role, passwd, orgName, orgUnit, 
                        locality, state, countryCode, nodeAddress);
                users.add(user);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select users", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return users;
    }
    /**
     * Gets user with a given ID.
     *
     * @param id User ID
     * @return User with a given ID
     */
    public User get(int id) {
        Connection conn = null;
        User user = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users WHERE id = " + id + ";";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String nameHash = resultSet.getString("name_hash" );
                String role = resultSet.getString("role_name");
                String passwd = resultSet.getString("password");
                String orgName = resultSet.getString("org_name");
                String orgUnit = resultSet.getString("org_unit");
                String locality = resultSet.getString("locality");
                String state = resultSet.getString("state");
                String countryCode = resultSet.getString("country_code");
                String nodeAddress = resultSet.getString("node_address");
                user = new User(userId, name, nameHash, role, passwd, orgName, orgUnit, locality, 
                        state, countryCode, nodeAddress);
            }
            stmt.close();
        } catch(Exception e) {
            log.error( "Failed to select user", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return user;
    }
    /**
     * Gets user with a given name.
     *
     * @param name User name
     * @return User with a given name
     */
    public User getByName(String name) {
        Connection conn = null;
        User user = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users WHERE name = '" + name + "';";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String userName = resultSet.getString("name");
                String nameHash = resultSet.getString("name_hash" );
                String role = resultSet.getString("role_name");
                String passwd = resultSet.getString("password");
                String orgName = resultSet.getString("org_name");
                String orgUnit = resultSet.getString("org_unit");
                String locality = resultSet.getString("locality");
                String state = resultSet.getString("state");
                String countryCode = resultSet.getString("country_code");
                String nodeAddress = resultSet.getString("node_address");
                user = new User(id, userName, nameHash, role, passwd, orgName, orgUnit, locality, 
                        state, countryCode, nodeAddress);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select user", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return user;
    }
    /**
     * Gets user with a given name hash.
     *
     * @param hash User name hash
     * @return User with a given name hash
     */
    public User getByHash(String hash) {
        Connection conn = null;
        User user = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_users WHERE name_hash = '" + hash + "';";
            ResultSet resultSet = stmt.executeQuery( sql );
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String nameHash = resultSet.getString("name_hash" );
                String role = resultSet.getString("role_name");
                String passwd = resultSet.getString("password");
                String orgName = resultSet.getString("org_name");
                String orgUnit = resultSet.getString("org_unit");
                String locality = resultSet.getString("locality");
                String state = resultSet.getString("state");
                String countryCode = resultSet.getString("country_code");
                String nodeAddress = resultSet.getString("node_address");
                user = new User(id, name, nameHash, role, passwd, orgName, orgUnit, locality, 
                        state, countryCode, nodeAddress);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select user", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return user;
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
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM dex_roles");
            while (resultSet.next()) {
                int roleId = resultSet.getInt("id");
                String roleName = resultSet.getString("role");
                Role role = new Role(roleId, roleName);
                // Peer accounts can't be established by administrator
                if (!role.getRole().matches("peer")) {
                    roles.add(role);
                }
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select user role", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
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
    public int saveOrUpdate(User user) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if (user.getId() == 0) {
                sql = "INSERT INTO dex_users (name, name_hash, role_name, password, org_name, " +
                    "org_unit, locality, state, country_code, node_address) VALUES ('" +
                    user.getName() + "', '" + MessageDigestUtil.createHash(user.getName()) + 
                    "', '" + user.getRoleName() + "', '" + user.getPassword() + "', '" + 
                    user.getOrganizationName() + "', '" + user.getOrganizationUnit() + "', '" + 
                    user.getLocalityName() + "', '" + user.getStateName() + "', '" + 
                    user.getCountryCode() + "', '" + user.getNodeAddress() + "');";
                update = stmt.executeUpdate(sql);
            } else {
                // record exists, do update
                sql = "UPDATE dex_users SET name = '" + user.getName() + "', name_hash = '" +
                    MessageDigestUtil.createHash(user.getName()) + "', role_name = '" + 
                    user.getRoleName() + "', " + "password = '" + user.getPassword() + 
                    "', org_name = '" + user.getOrganizationName() + "', org_unit = '" + 
                    user.getOrganizationUnit() + "', locality = '" + user.getLocalityName() + 
                    "', state = '" + user.getStateName() + "', country_code = '" + 
                    user.getCountryCode() + "', node_address = '" + user.getNodeAddress() + 
                    "' WHERE id = " + user.getId() + ";";
                update = stmt.executeUpdate(sql) ;
                stmt.close();
            }
        } catch(Exception e) {
            log.error("Failed to save user account", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
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
    public int deleteUser(int id) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_users WHERE id = " + id + ";";
            delete = stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to delete user account", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
