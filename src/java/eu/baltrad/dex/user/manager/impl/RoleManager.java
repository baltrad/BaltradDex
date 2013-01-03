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

import eu.baltrad.dex.user.manager.IRoleManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.mapper.RoleMapper;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Role manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class RoleManager implements IRoleManager {
    
    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private RoleMapper mapper;
    
    /**
     * Constructor.
     */
    public RoleManager() {
        this.mapper = new RoleMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all roles.
     * @return All existing roles
     */
    public List<Role> load() {
	return jdbcTemplate.query("SELECT * FROM dex_roles", mapper);
    }
    
    /**
     * Load role by name.
     * @param name Role name
     * @return Role by name
     */
    public Role load(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM dex_roles WHERE name = ?", mapper, name);
    }
    
    /**
     * Store record in dex_users_roles table.
     * @param userId User id
     * @param roleId Role id
     * @return Number of records stored
     */
    public int store(int userId, int roleId) {
        return jdbcTemplate.update(
                "INSERT INTO dex_users_roles (user_id, role_id) " +
                "VALUES (?, ?)", userId, roleId);
    }
    
    /**
     * Update record in dex_users_roles table.
     * @param roleId Role id
     * @param userId User id
     * @return Number of records updated
     */
    public int update(int roleId, int userId) {
        return jdbcTemplate.update(
                "UPDATE dex_users_roles SET role_id = ? WHERE user_id = ?", 
                roleId, userId);
    }
    
}
