/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.user.manager.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.baltrad.dex.user.manager.IRoleManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.mapper.UserMapper;

/**
 * User manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class UserManager implements IUserManager {

    /** JDBC template */
    private JdbcOperations jdbcTemplate;
    
    /** Dependent manager */
    private IRoleManager roleManager;
    
    /** Row mapper */
    private UserMapper mapper;
    
    private final static Logger logger = LogManager.getLogger(UserManager.class);
    
    /**
     * Constructor.
     */
    public UserManager() {
        this.mapper = new UserMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * @param roleManager the roleManager to set
     */
    @Autowired
    public void setRoleManager(IRoleManager roleManager) {
        this.roleManager = roleManager;
    }
    
    /**
     * Load all user accounts.
     * @return List of all registered user accounts.
     */
    public List<User> load() {
        String sql = "SELECT u.*, r.name AS role " +
                "FROM dex_users u, dex_roles r, dex_users_roles ur " +
                "WHERE ur.user_id = u.id AND ur.role_id = r.id;";
        return jdbcTemplate.query(sql, mapper);
    }
    
    /**
     * Load user account by id.
     * @param id Account id
     * @return Account with a given id.
     */
    public User load(int id) {
        String sql = "SELECT u.*, r.name AS role " + 
                "FROM dex_users u, dex_roles r, dex_users_roles ur " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id " +
                "AND u.id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load user account by name.
     * @param name Account name
     * @return Account with a given user name
     */
    public User load(String name) {
        String sql = "SELECT u.*, r.name AS role " + 
                "FROM dex_users u, dex_roles r, dex_users_roles ur " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id " +
                "AND u.name = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, name);
        } catch (EmptyResultDataAccessException e) {
          logger.debug("Could not load user", e);
            return null;
        }
    }
    
    /**
     * Returns distinct user names.
     * @return List containing distinct user names
     */
    public List<String> loadPeerNames() {
        String sql = "SELECT DISTINCT u.name AS user_name, r.name AS role " +
                "FROM dex_users u, dex_roles r, dex_users_roles ur " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id " + 
                "AND r.name = 'peer';";			
        return jdbcTemplate.query(sql, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int i) throws SQLException {
                    return rs.getString("user_name");
                }
            });
    }
    
    /**
     * Returns distinct users.
     * @return List containing distinct users
     */
    public List<User> loadPeers() {
        String sql = "SELECT DISTINCT u.*, r.name AS role " +
                "FROM dex_users u, dex_roles r, dex_users_roles ur " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id " + 
                "AND r.name = 'peer';";
        return jdbcTemplate.query(sql, mapper);
    }
    
    /**
     * Returns distinct operators.
     * @return List containing distinct operators.
     */
    public List<User> loadOperators() {
        String sql = "SELECT DISTINCT u.*, r.name AS role " + 
                "FROM dex_users u, dex_roles r, dex_users_roles ur, " + 
                "dex_subscriptions s, dex_subscriptions_users su " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id AND " + 
                "su.subscription_id = s.id AND su.user_id = u.id AND " + 
                "s.type = 'local';";
        return jdbcTemplate.query(sql, mapper);
    }
    
    /**
     * Returns distinct users.
     * @return List containing distinct users.
     */
    public List<User> loadUsers() {
        String sql = "SELECT DISTINCT u.*, r.name AS role " + 
                "FROM dex_users u, dex_roles r, dex_users_roles ur, " + 
                "dex_subscriptions s, dex_subscriptions_users su " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id AND " + 
                "su.subscription_id = s.id AND su.user_id = u.id AND " + 
                "s.type = 'peer';";
        return jdbcTemplate.query(sql, mapper);
    }
    
    /**
     * Store user account object in the db.
     * @param user User account to store
     * @return Auto-generated record id
     * @throw Exception
     */
    @Transactional(propagation= Propagation.REQUIRED,
            rollbackFor=Exception.class)
    public int store(User user) throws Exception {
        
        final String sql = "INSERT INTO dex_users (name, password, " +
                    "org_name, org_unit, locality, state, country_code, " + 
                    "node_address, redirected_address) VALUES (?,?,?,?,?,?,?,?,?)";
        final User usr = user;
        
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(
                            Connection conn) throws SQLException {
                        PreparedStatement ps = conn.prepareStatement(sql,
                                new String[] {"id"});
                        ps.setString(1, usr.getName());
                        ps.setString(2, usr.getPassword());
                        ps.setString(3, usr.getOrgName());
                        ps.setString(4, usr.getOrgUnit());
                        ps.setString(5, usr.getLocality());
                        ps.setString(6, usr.getState());
                        ps.setString(7, usr.getCountryCode());
                        ps.setString(8, usr.getNodeAddress());
                        ps.setString(9, usr.getRedirectedAddress());
                        return ps;
                    }
                }, keyHolder);
            int accountId = keyHolder.getKey().intValue();
            int roleId = roleManager.load(user.getRole()).getId();
            roleManager.store(accountId, roleId);
            return accountId;
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Update user account object in the db. 
     * @param user User account to store
     * @throws Exception 
     */
    @Transactional(propagation= Propagation.REQUIRED,
            rollbackFor=Exception.class)
    public void update(User user) throws Exception { 
        try {
            jdbcTemplate.update("UPDATE dex_users SET name = ?, " + 
                    "org_name = ?, org_unit = ?, locality = ?, "
                    + "state = ?, country_code = ?, node_address = ?, redirected_address = ? "
                    + "WHERE id = ?",
                user.getName(),
                user.getOrgName(),
                user.getOrgUnit(),
                user.getLocality(),
                user.getState(),
                user.getCountryCode(),
                user.getNodeAddress(),
                user.getRedirectedAddress(),
                user.getId());
            int roleId = roleManager.load(user.getRole()).getId();
            roleManager.update(roleId, user.getId());
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }           
    
    /**
     * Update user's password.
     * @param id User id
     * @param password Password to set
     * @return Number of records updated.
     * @throws Exception
     */
    public int updatePassword(int id, String password) throws Exception {
        return jdbcTemplate.update("UPDATE dex_users SET password = ? "
                + "WHERE id = ?", password, id);
    }
    
    /**
     * Delete user with a given id.
     * @param id User id
     * @return Number of deleted records.
     */
    public int delete(int id) {
         return jdbcTemplate.update("DELETE FROM dex_users WHERE id = ?", id);
    }
    
}

