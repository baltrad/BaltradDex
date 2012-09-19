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

package eu.baltrad.dex.log.model;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * Message logger. 
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.6.6
 */
public class LogManager implements ILogManager, InitializingBean {

    /** Number of log entries per page */
    public final static int ENTRIES_PER_PAGE = 12;
    /** Number of pages in the scroll bar, must be an odd number >= 3 */
    public final static int SCROLL_RANGE = 11;
    /** Trim messages by number trigger */
    public static final String TRIM_MSG_BY_NUMBER_TG = 
            "dex_trim_messages_by_number_tg";
    /** Trim messages by date trigger */
    public static final String TRIM_MSG_BY_AGE_TG = 
            "dex_trim_messages_by_age_tg";
    
    /** Static instance */
    private static LogManager instance;
    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private LogEntryMapper mapper;

    /**
     * Constructor.
     */
    public LogManager() {
        this.mapper = new LogEntryMapper();
        
        System.out.println("________________ LogManager()");
        
        if (jdbcTemplate == null) {
            System.out.println("__________________LogManager(): jdbcTemplate is null");
        } else {
            System.out.println("__________________LogManager(): jdbcTemplate OK!!!");
        }
        
        
    }
    
    /**
     * Implements InitializingBean
     */
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }
    
    public static LogManager getInstance() {
        return instance;
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
     * @return Total number of entries in the log
     */
    public long count() {
        String sql = "SELECT count(*) FROM dex_messages";
        return jdbcTemplate.queryForLong(sql);
    }
    
    /**
     * Load all log entries.
     * @return All log entries
     */
    public List<LogEntry> load() {
        String sql = "SELECT * FROM dex_messages";
	List<LogEntry> entries = jdbcTemplate.query(sql, mapper);
	return entries;
    }
    
    /**
     * Load log entries.
     * @param limit Limit
     * @return List of log entries
     */
    public List<LogEntry> load(int limit) {
        String sql = "SELECT * FROM dex_messages ORDER BY timestamp" +
                " DESC LIMIT ?";
        try {
            return jdbcTemplate.query(sql, mapper, limit);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load log entries.
     * @param offset Offset
     * @param limit Limit
     * @return List of log entries
     */
    public List<LogEntry> load(int offset, int limit) {
        String sql = "SELECT * FROM dex_messages ORDER BY timestamp" +
                " DESC OFFSET ? LIMIT ?";
        try {
            return jdbcTemplate.query(sql, mapper, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store log entry in database.
     * @param entry Log entry to store
     * @return Number of records stored
     */
    public int store(LogEntry entry) {
        return jdbcTemplate.update("INSERT INTO dex_messages " +
            "(id, timestamp, system, type, message) VALUES " +
            "(?,?,?,?,?)",
            entry.getId(),
            entry.getTimeStamp(),
            entry.getSystem(),
            entry.getType(),
            entry.getMessage());
    }
    
    /**
     * Store log entry in database.
     * @param entry Log entry to store
     * @return Number of records stored
     */
    public int storeNoId(LogEntry entry) {
        
        
        System.out.println("storeNoId(): Storing ...");
        
        
        int i = jdbcTemplate.update("INSERT INTO dex_messages " +
            "(timestamp, system, type, message) VALUES " +
            "(?,?,?,?)",
            entry.getTimeStamp(),
            entry.getSystem(),
            entry.getType(),
            entry.getMessage());
        
        System.out.println("append(): Number of records stored: " + i);
        
        return i;
    }
    
    /**
     * Delete all log entries.
     * @return Number of records deleted
     */
    public int delete() {
        return jdbcTemplate.update("DELETE FROM dex_messages");
    }
    
    /**
     * Set trigger. Trigger activates trimmer function deleting records when 
     * given number of records is exceeded.
     * @param limit Maximum number of records
     */
    public void setTrimmer(int limit) {
        String sql = "DROP TRIGGER IF EXISTS dex_trim_messages_by_number_tg " +
            "ON dex_messages;" +
            " CREATE TRIGGER dex_trim_messages_by_number_tg AFTER INSERT ON " +
            "dex_messages FOR EACH ROW EXECUTE PROCEDURE " + 
             "dex_trim_messages_by_number(" + limit + ");";
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
        String sql = "DROP TRIGGER IF EXISTS dex_trim_messages_by_age_tg ON " +
            "dex_messages;" +
            " CREATE TRIGGER dex_trim_messages_by_age_tg AFTER INSERT ON " +
            "dex_messages FOR EACH ROW EXECUTE PROCEDURE " + 
            "dex_trim_messages_by_age(" + days + ", " + hours + ", " + 
             minutes + ");";
        jdbcTemplate.update(sql);
    }
    
    /**
     * Removes trigger from registry table.
     * @param name Trigger name
     */
    public void removeTrimmer(String name) {
        String sql = "DROP TRIGGER IF EXISTS " + name + 
                " ON dex_messages;";
        jdbcTemplate.update(sql);
    }
    
    /**
     * Log entry row mapper.
     */
    private static final class LogEntryMapper
                            implements ParameterizedRowMapper<LogEntry> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return Log entry object
         * @throws SQLException 
         */
        public LogEntry mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            LogEntry entry = new LogEntry(); 
            entry.setId(rs.getInt("id"));
            entry.setTimeStamp(rs.getTimestamp("timestamp"));
            entry.setSystem(rs.getString("system"));
            entry.setType(rs.getString("type"));
            entry.setMessage(rs.getString("message"));
            return entry;
        }
    }   
    
 }
