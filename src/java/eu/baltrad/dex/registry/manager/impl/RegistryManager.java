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

package eu.baltrad.dex.registry.manager.impl;

import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.registry.model.mapper.RegistryEntryMapper;
import eu.baltrad.dex.user.manager.impl.UserManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Class implements data delivery register handling functionality..
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.1.6
 */
public class RegistryManager implements IRegistryManager {
    
    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** User account manager */
    private UserManager accountManager;
    /** Row mapper */
    private RegistryEntryMapper mapper;
    
    /**
     * Constructor.
     */
    public RegistryManager() {
        this.mapper = new RegistryEntryMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * @param accountManager the accountManager to set
     */
    @Autowired
    public void setAccountManager(UserManager accountManager) {
        this.accountManager = accountManager;
    }
    
    /**
     * Counts entries.
     * @return Total number of entries in the registry
     */
    public long count() {
        String sql = "SELECT count(*) FROM dex_delivery_registry";
        return jdbcTemplate.queryForLong(sql);
    }
    
    /**
     * Load registry entries.
     * @param offset Offset
     * @param limit Limit
     * @return List of registry entries
     */
    public List<RegistryEntry> load(int offset, int limit) {
        String sql = "SELECT dex_delivery_registry.*, dex_users.name AS " + 
            "user_name FROM dex_delivery_registry, dex_users, " + 
            "dex_delivery_registry_users WHERE " +
            "dex_delivery_registry_users.entry_id = dex_delivery_registry.id " +
            "AND dex_delivery_registry_users.user_id = dex_users.id " + 
            "ORDER BY time_stamp DESC OFFSET ? LIMIT ?";
        try {
            return jdbcTemplate.query(sql, mapper, offset, limit);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store entry in delivery registry. 
     * @param entry Entry to store
     * @return Auto-generated record id
     * @throws Exception 
     */
    @Transactional(propagation=Propagation.REQUIRED,
            rollbackFor=Exception.class)
    public int store(RegistryEntry registryEntry) throws Exception {
        
        final String sql = "INSERT INTO dex_delivery_registry " +
                "(time_stamp, uuid, status) VALUES (?,?,?)";
        final RegistryEntry entry = registryEntry;
        
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(
                            Connection conn) throws SQLException {
                        PreparedStatement ps = conn.prepareStatement(sql,
                                new String[] {"id"});
                        ps.setLong(1, entry.getTimeStamp());
                        ps.setString(2, entry.getUuid());
                        ps.setString(3, entry.getStatus());
                        return ps;
                    }
                }, keyHolder);
            int entryId = keyHolder.getKey().intValue(); 
            int userId = accountManager.load(entry.getUser()).getId();
            jdbcTemplate.update("INSERT INTO dex_delivery_registry_users " +
                    "(entry_id, user_id) VALUES (?,?)", entryId, userId);
            return entryId;
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Delete all registry entries.
     * @return Number of records deleted
     */
    public int delete() throws Exception {
        try {
            return jdbcTemplate.update("DELETE FROM dex_delivery_registry");
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }    
    }
    
    /**
     * Set trigger. Trigger activates trimmer function deleting records when 
     * given number of records is exceeded.
     * @param limit Maximum number of records
     */
    public void setTrimmer(int limit) {
        String sql = "DROP TRIGGER IF EXISTS dex_trim_registry_by_number_tg " +
            "ON dex_delivery_registry;" +
            " CREATE TRIGGER dex_trim_registry_by_number_tg AFTER INSERT ON " +
            "dex_delivery_registry FOR EACH ROW EXECUTE PROCEDURE " + 
             "dex_trim_registry_by_number(" + limit + ");";
        jdbcTemplate.update(sql);
    }
    
    /**
     * Set trigger. Trigger activates trimmer function deleting records when 
     * given expiry period is exceeded.
     * @param days Maximum days
     * @param hours Maximum hours 
     * @param minutes Maximum minutes
     */
    public void setTrimmer(int days, int hours, int minutes) {
        String sql = "DROP TRIGGER IF EXISTS dex_trim_registry_by_age_tg ON " +
            "dex_delivery_registry;" +
            " CREATE TRIGGER dex_trim_registry_by_age_tg AFTER INSERT ON " +
            "dex_delivery_registry FOR EACH ROW EXECUTE PROCEDURE " + 
            "dex_trim_registry_by_age(" + days + ", " + hours + ", " + 
             minutes + ");";
        jdbcTemplate.update(sql);
    }
    
    /**
     * Removes trigger from registry table.
     * @param name Trigger name
     */
    public void removeTrimmer(String name) {
        String sql = "DROP TRIGGER IF EXISTS " + name + 
                " ON dex_delivery_registry;";
        jdbcTemplate.update(sql);
    }
    
}
