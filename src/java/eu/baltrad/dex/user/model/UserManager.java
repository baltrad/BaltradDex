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

package eu.baltrad.dex.user.model;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * User manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class UserManager implements IUserManager {

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mappers */
    private UserMapper userMapper;
    private RoleMapper roleMapper;
    
    /**
     * Constructor.
     */
    public UserManager() {
        this.userMapper = new UserMapper();
        this.roleMapper = new RoleMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all users.
     * @return List of all existing users.
     */
    public List<User> load() {
        String sql = "SELECT * FROM dex_users";
	List<User> users = jdbcTemplate.query(sql, userMapper);
	return users;
    }
    
    /**
     * Load user by id.
     * @param id User id
     * @return User with a given id.
     */
    public User load(int id) {
        String sql = "SELECT * FROM dex_users WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load user by name.
     * @param name User name
     * @return User with a given name
     */
    public User load(String name) {
        String sql = "SELECT * FROM dex_users WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, userMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load all roles.
     * @return All existing roles
     */
    public List<Role> loadRoles() {
        String sql = "SELECT * FROM dex_roles";
	List<Role> roles = jdbcTemplate.query(sql, roleMapper);
	return roles;
    }
    
    /**
     * Store user object in the db.
     * @param user User to store
     * @return Number of records stored
     */
    public int store(User user) {
        return jdbcTemplate.update("INSERT INTO dex_users (id, name, role_name,"
                + " password, org_name, org_unit, locality, state, " +
                "country_code, node_address) VALUES (?,?,?,?,?,?,?,?,?,?)",
            user.getId(),
            user.getName(),
            user.getRoleName(),
            user.getPassword(),
            user.getOrganizationName(),
            user.getOrganizationUnit(),
            user.getLocalityName(),
            user.getStateName(),
            user.getCountryCode(),
            user.getNodeAddress());
    }
    
    /**
     * Store user object in the db.
     * @param user User to store
     * @return Number of records stored
     */
    public int storeNoId(User user) {
        return jdbcTemplate.update("INSERT INTO dex_users (name, role_name,"
                + " password, org_name, org_unit, locality, state, " +
                "country_code, node_address) VALUES (?,?,?,?,?,?,?,?,?)",
            user.getName(),
            user.getRoleName(),
            user.getPassword(),
            user.getOrganizationName(),
            user.getOrganizationUnit(),
            user.getLocalityName(),
            user.getStateName(),
            user.getCountryCode(),
            user.getNodeAddress());
    }
    
    /**
     * Update user object in the db. 
     * @param user User to store
     * @return Number of records updated 
     */
    public int update(User user) { 
        return jdbcTemplate.update("UPDATE dex_users SET name = ?, " + 
                "role_name = ?, org_name = ?, org_unit = ?, locality = ?,"
                + "state = ?, country_code = ?, node_address = ? WHERE id = ?",
            user.getName(),
            user.getRoleName(),
            user.getOrganizationName(),
            user.getOrganizationUnit(),
            user.getLocalityName(),
            user.getStateName(),
            user.getCountryCode(),
            user.getNodeAddress(),
            user.getId());
    }           
    
    /**
     * Update user's password.
     * @param id User id
     * @param password Password to set
     * @return Number of records updated.
     */
    public int updatePassword(int id, String password) {
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
    
    /**
     * User row mapper.
     */
    private static final class UserMapper
                                implements ParameterizedRowMapper<User> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return User object
         * @throws SQLException 
         */
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setRoleName(rs.getString("role_name"));
            user.setPassword(rs.getString("password"));
            user.setOrganizationName(rs.getString("org_name"));
            user.setOrganizationUnit(rs.getString("org_unit"));
            user.setLocalityName(rs.getString("locality"));
            user.setStateName(rs.getString("state"));
            user.setCountryCode(rs.getString("country_code"));
            user.setNodeAddress(rs.getString("node_address"));
            return user;
        }
    }
    
    /**
     * Role row mapper.
     */
    private final static class RoleMapper
                                implements ParameterizedRowMapper<Role> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return User object
         * @throws SQLException 
         */
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getInt("id"));
            role.setRole(rs.getString("role"));
            return role;
        }
    }
    
}

