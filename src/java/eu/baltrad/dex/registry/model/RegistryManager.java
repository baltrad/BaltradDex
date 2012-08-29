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

package eu.baltrad.dex.registry.model;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.List;

/**
 * Class implements data delivery register handling functionality..
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.1.6
 */
public class RegistryManager implements IRegistryManager {

    /** Number of file entries per page */
    public final static int ENTRIES_PER_PAGE = 12;
    /** Number of pages in the scroll bar, must be an odd number >= 3 */
    public final static int SCROLL_RANGE = 11;
    /** Trim registry by number trigger */
    public static final String TRIM_REG_BY_NUMBER_TG = 
            "dex_trim_registry_by_number_tg";
    /** Trim registry by date trigger */
    public static final String TRIM_REG_BY_AGE_TG = 
            "dex_trim_registry_by_age_tg";

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
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
     * Counts entries.
     * @return Total number of entries in the registry
     */
    public long count() {
        String sql = "SELECT count(*) FROM dex_delivery_registry";
        return jdbcTemplate.queryForLong(sql);
    }
    
    /**
     * Load all entries.
     * @return All registry entries
     */
    public List<RegistryEntry> load() {
        String sql = "SELECT * FROM dex_delivery_registry";
	List<RegistryEntry> entries = jdbcTemplate.query(sql, mapper);
	return entries;
    }
    
    /**
     * Load registry entry with a given user id and uuid.
     * @param userId User id
     * @param uuid Uuid
     * @return Matching entry
     */
    public RegistryEntry load(int userId, String uuid) {
        String sql = "SELECT * FROM dex_delivery_registry WHERE " +
                "user_id = ? AND uuid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, userId, uuid);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load registry entries.
     * @param offset Offset
     * @param limit Limit
     * @return List of registry entries
     */
    public List<RegistryEntry> load(int offset, int limit) {
        String sql = "SELECT * FROM dex_delivery_registry ORDER BY timestamp" +
                " DESC OFFSET ? LIMIT ?";
        try {
            return jdbcTemplate.query(sql, mapper, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store registry entry in database.
     * @param entry Registry entry to store
     * @return Number of records stored
     */
    public int store(RegistryEntry entry) {
        return jdbcTemplate.update("INSERT INTO dex_delivery_registry " +
            "(id, user_id, uuid, user_name, timestamp, status) VALUES " +
            "(?,?,?,?,?,?)",
            entry.getId(),
            entry.getUserId(),
            entry.getUuid(),
            entry.getUserName(),
            entry.getTimeStamp(),
            entry.getDeliveryStatus());
    }
    
    /**
     * Store registry entry in database.
     * @param entry Registry entry to store
     * @return Number of records stored
     */
    public int storeNoId(RegistryEntry entry) {
        return jdbcTemplate.update("INSERT INTO dex_delivery_registry " +
            "(user_id, uuid, user_name, timestamp, status) VALUES " +
            "(?,?,?,?,?)",
            entry.getUserId(),
            entry.getUuid(),
            entry.getUserName(),
            entry.getTimeStamp(),
            entry.getDeliveryStatus());
    }
    
    /**
     * Delete all registry entries.
     * @return Number of records deleted
     */
    public int delete() {
        return jdbcTemplate.update("DELETE FROM dex_delivery_registry");
    }
    
    /**
     * Delete registry entry with a given id.
     * @param id Registry entry id
     * @return Number of records deleted
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_delivery_registry WHERE " +
                "id = ?", id);
    }
    
    /**
     * Set trigger. Trigger activates trimmer function deleting records when 
     * given number of records is exceeded.
     * @param limit Maximum number of records
     * @return Number of rows affected
     */
    public int setTrimmer(int limit) {
        String sql = "DROP TRIGGER IF EXISTS dex_trim_registry_by_number_tg " +
            "ON dex_delivery_registry;" +
            " CREATE TRIGGER dex_trim_registry_by_number_tg AFTER INSERT ON " +
            "dex_delivery_registry FOR EACH ROW EXECUTE PROCEDURE " + 
             "dex_trim_registry_by_number(" + limit + ");";
        return jdbcTemplate.update(sql);
    }
    
    /**
     * Set trigger. Trigger activates trimmer function deleting records when 
     * given expiry period is exceeded.
     * @param days Maximum days
     * @param hours Maximum hours 
     * @param minutes Maximum minutes
     * @return Number of rows affected 
     */
    public int setTrimmer(int days, int hours, int minutes) {
        String sql = "DROP TRIGGER IF EXISTS dex_trim_registry_by_age_tg ON " +
            "dex_delivery_registry;" +
            " CREATE TRIGGER dex_trim_registry_by_age_tg AFTER INSERT ON " +
            "dex_delivery_registry FOR EACH ROW EXECUTE PROCEDURE " + 
            "dex_trim_registry_by_age(" + days + ", " + hours + ", " + 
             minutes + ");";
        return jdbcTemplate.update(sql);
    }
    
    /**
     * Removes trigger from registry table.
     * @param name Trigger name
     * @return Number of rows affected
     */
    public int removeTrimmer(String name) {
        String sql = "DROP TRIGGER IF EXISTS " + name + 
                " ON dex_delivery_registry;";
        return jdbcTemplate.update(sql);
    }
    
    /**
     * Registry entry row mapper.
     */
    private static final class RegistryEntryMapper
                            implements ParameterizedRowMapper<RegistryEntry> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return Registry entry object
         * @throws SQLException 
         */
        public RegistryEntry mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            RegistryEntry entry = new RegistryEntry(); 
            entry.setId(rs.getInt("id"));
            entry.setUserId(rs.getInt("user_id"));
            entry.setUuid(rs.getString("uuid"));
            entry.setUserName(rs.getString("user_name"));
            entry.setTimeStamp(rs.getTimestamp("timestamp"));
            entry.setDeliveryStatus(rs.getString("status"));
            return entry;
        }
    }
}
