/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.net.manager.INodeManager;
import eu.baltrad.dex.net.model.impl.Node;
import eu.baltrad.dex.user.manager.IRoleManager;
import eu.baltrad.dex.user.manager.IAccountManager;
import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.user.model.mapper.AccountMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * User manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class AccountManager implements IAccountManager {

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    
    /** Dependent managers */
    private IRoleManager roleManager;
    private INodeManager nodeManager;
    
    /** Row mapper */
    private AccountMapper mapper;
    
    /**
     * Constructor.
     */
    public AccountManager() {
        this.mapper = new AccountMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
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
     * @param nodeManager the nodeManager to set
     */
    @Autowired
    public void setNodeManager(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }
    
    /**
     * Load all user accounts.
     * @return List of all registered user accounts.
     */
    public List<Account> load() {
        String sql = "SELECT dex_users.*, dex_roles.name AS role_name, " + 
                "dex_nodes.address AS node_address FROM dex_users, " + 
                "dex_roles, dex_nodes, dex_users_roles, dex_users_nodes " + 
                "WHERE dex_users_roles.user_id = dex_users.id AND " +
                "dex_users_roles.role_id = dex_roles.id AND " + 
                "dex_users_nodes.user_id = dex_users.id AND " + 
                "dex_users_nodes.node_id = dex_nodes.id;";
        List<Account> accounts = jdbcTemplate.query(sql, mapper);
	return accounts;
    }
    
    /**
     * Load account by id.
     * @param id Account id
     * @return Account with a given id.
     */
    public Account load(int id) {
        String sql = "SELECT dex_users.*, dex_roles.name AS role_name, " +
                "dex_nodes.address AS node_address FROM dex_users, " + 
                "dex_roles, dex_nodes, dex_users_roles, dex_users_nodes " + 
                "WHERE dex_users_roles.user_id = dex_users.id AND " + 
                "dex_users_roles.role_id = dex_roles.id AND " + 
                "dex_users_nodes.user_id = dex_users.id AND " + 
                "dex_users_nodes.node_id = dex_nodes.id AND " + 
                "dex_users.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load account by name.
     * @param name Account name
     * @return Account with a given user name
     */
    public Account load(String name) {
        String sql = "SELECT dex_users.*, dex_roles.name AS role_name, " +
                "dex_nodes.address AS node_address FROM dex_users, " + 
                "dex_roles, dex_nodes, dex_users_roles, dex_users_nodes " + 
                "WHERE dex_users_roles.user_id = dex_users.id AND " + 
                "dex_users_roles.role_id = dex_roles.id AND " + 
                "dex_users_nodes.user_id = dex_users.id AND " + 
                "dex_users_nodes.node_id = dex_nodes.id AND " +
                "dex_users.name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Returns distinct names of users.
     * @return List containing distinct user names
     */
    public List<String> loadUsers() {
        String sql = "SELECT DISTINCT dex_users.name AS user_name FROM " + 
                "dex_users, dex_subscriptions, dex_subscriptions_users " +
                "WHERE dex_subscriptions_users.subscription_id = " + 
                "dex_subscriptions.id AND dex_subscriptions_users.user_id = " +
                "dex_users.id AND dex_subscriptions.type = 'upload';";			

        return jdbcTemplate.query(sql, new ParameterizedRowMapper<String>() {
                public String mapRow(ResultSet rs, int i) throws SQLException {
                    return rs.getString("user_name");
                }
            });
    }
    
    /**
     * Store account object in the db.
     * @param account User to store
     * @return Auto-generated record id
     * @throw Exception
     */
    @Transactional(propagation= Propagation.REQUIRED,
            rollbackFor=Exception.class)
    public int store(Account account) throws Exception {
        
        final String sql = "INSERT INTO dex_users (name, password, " +
                    "org_name, org_unit, locality, state, country_code) "
                    + "VALUES (?,?,?,?,?,?,?)";
        final Account accnt = account;
        
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(
                            Connection conn) throws SQLException {
                        PreparedStatement ps = conn.prepareStatement(sql,
                                new String[] {"id"});
                        ps.setString(1, accnt.getName());
                        ps.setString(2, accnt.getPassword());
                        ps.setString(3, accnt.getOrgName());
                        ps.setString(4, accnt.getOrgUnit());
                        ps.setString(5, accnt.getLocality());
                        ps.setString(6, accnt.getState());
                        ps.setString(7, accnt.getCountryCode());
                        return ps;
                    }
                }, keyHolder);
            int accountId = keyHolder.getKey().intValue();
            int roleId = roleManager.load(account.getRoleName()).getId();
            roleManager.store(accountId, roleId);
            int nodeId = nodeManager.store(new Node(account.getName(), 
                    account.getNodeAddress()));
            nodeManager.store(accountId, nodeId);
            return accountId;
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Update user object in the db. 
     * @param user User to store
     * @throws Exception 
     */
    @Transactional(propagation= Propagation.REQUIRED,
            rollbackFor=Exception.class)
    public void update(Account account) throws Exception { 
        try {
            jdbcTemplate.update("UPDATE dex_users SET name = ?, " + 
                    "org_name = ?, org_unit = ?, locality = ?,"
                    + "state = ?, country_code = ? WHERE id = ?",
                account.getName(),
                account.getOrgName(),
                account.getOrgUnit(),
                account.getLocality(),
                account.getState(),
                account.getCountryCode(),
                account.getId());
            int roleId = roleManager.load(account.getRoleName()).getId();
            roleManager.update(roleId, account.getId());
            int nodeId = nodeManager.loadByUser(account.getId()).getId();
            nodeManager.update(account.getId(), nodeId);
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

