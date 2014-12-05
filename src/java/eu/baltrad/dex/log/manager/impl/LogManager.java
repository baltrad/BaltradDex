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

package eu.baltrad.dex.log.manager.impl;

import eu.baltrad.dex.log.manager.ILogManager;
import eu.baltrad.dex.log.model.mapper.LogEntryMapper;
import eu.baltrad.dex.log.model.impl.LogEntry;
import eu.baltrad.dex.log.model.impl.LogParameter;
import static eu.baltrad.dex.util.WebValidator.validate;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.List;


/**
 * Message logger. 
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.6.6
 */
public class LogManager implements ILogManager {
    
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private LogEntryMapper mapper;
    
    private DateFormat format;
    
    /**
     * Constructor.
     */
    public LogManager() {
        this.mapper = new LogEntryMapper();
        this.format = new SimpleDateFormat(DATE_FORMAT); 
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Creates db query based on given log parameters.
     * @param param Log parameter
     * @param count Select /count query toggle  
     * @return Query string
     * @throws Exception    
     */
    public String createQuery(LogParameter param, boolean count) 
            throws Exception{
        String sql = "";
        if (count) {
            sql = "SELECT count(*) FROM dex_messages";
        } else {
            sql = "SELECT * FROM dex_messages";
        }
        boolean hasParameters = false;
        if (validate(param.getLogger()) && !param.getLogger().equals("ALL")) {
            sql += " WHERE logger = '" + param.getLogger() + "'";
            hasParameters = true;
        }
        if (validate(param.getLevel()) && !param.getLevel().equals("ALL")) {
            if (hasParameters) {
                sql += " AND level = '" + param.getLevel() + "'";
            } else {
                sql += " WHERE level = '" + param.getLevel() + "'";
                hasParameters = true;
            }
        }
        if (validate(param.getStartDate())) {
            String startDate = param.getStartDate();
            if (validate(param.getStartHour())) {
                startDate += " " + param.getStartHour() + ":";
            } else {
                startDate += " 00:";
            }
            if (validate(param.getStartMinutes())) {
                startDate += param.getStartMinutes() + ":";
            } else {
                startDate += "00:";
            }
            if (validate(param.getStartSeconds())) {
                startDate += param.getStartSeconds();
            } else {
                startDate += "00";
            }
            if (hasParameters) {
                sql += " AND time_stamp >= '" + 
                        format.parse(startDate).getTime() + "'";
            } else {
                sql += " WHERE time_stamp >= '" + 
                        format.parse(startDate).getTime() + "'";
                hasParameters = true;
            }
        }
        if (validate(param.getEndDate())) {
            String endDate = param.getEndDate();
            if (validate(param.getEndHour())) {
                endDate += " " + param.getEndHour() + ":";
            } else {
                endDate += " 00:";
            }
            if (validate(param.getEndMinutes())) {
                endDate += param.getEndMinutes() + ":";
            } else {
                endDate += "00:";
            }
            if (validate(param.getEndSeconds())) {
                endDate += param.getEndSeconds();
            } else {
                endDate += "00";
            }
            if (hasParameters) {
                sql += " AND time_stamp <= '" + 
                        format.parse(endDate).getTime() + "'";
            } else {
                sql += " WHERE time_stamp <= '" + 
                        format.parse(endDate).getTime() + "'";
                hasParameters = true;
            }
        }
        if (validate(param.getPhrase())) {
            if (hasParameters) {
                sql += " AND message SIMILAR TO '%" + param.getPhrase() + "%'";
            } else {
                sql += " WHERE message SIMILAR TO '%" + param.getPhrase() + "%'";
                hasParameters = true;
            }
        }
        if (hasParameters) {
                sql += " AND level <> 'STICKY'";
            } else {
                sql += " WHERE level <> 'STICKY'";
                hasParameters = true;
            }
        if (!count) {
            sql += " ORDER BY time_stamp DESC OFFSET ? LIMIT ?";
        }
        return sql;
    }
    
    /**
     * Counts entries.
     * @return Total number of entries in the registry
     */
    public long count() {
        String sql = "SELECT count(*) FROM dex_messages WHERE level <> 'STICKY'";
        return jdbcTemplate.queryForLong(sql);
    }
    
    /**
     * Counts entries.
     * @return Total number of entries in the log
     */
    public long count(String sql) {
        return jdbcTemplate.queryForLong(sql);
    }
    
    /**
     * Load all log entries.
     * @return All log entries
     */
    public List<LogEntry> load() {
        String sql = "SELECT * FROM dex_messages WHERE level <> 'STICKY'";
	List<LogEntry> entries = jdbcTemplate.query(sql, mapper);
	return entries;
    }
    
    /**
     * Load log entries.
     * @param limit Limit
     * @return List of log entries
     */
    public List<LogEntry> load(int limit) {
        String sql = "SELECT * FROM dex_messages WHERE level <> 'STICKY'" +
                " ORDER BY time_stamp DESC LIMIT ?";
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
        String sql = "SELECT * FROM dex_messages WHERE level <> 'STICKY'" + 
                " ORDER BY time_stamp DESC OFFSET ? LIMIT ?";
        try {
            return jdbcTemplate.query(sql, mapper, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load log entries matching a given level.
     * @param level Log entry level
     * @return List of log entries matching given level
     */
    public List<LogEntry> load(String level) {
        String sql = "SELECT * FROM dex_messages WHERE level = ?" + 
                " ORDER BY time_stamp DESC;";
        try {
            return jdbcTemplate.query(sql, mapper, level);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load log entries .
     * @param param Log entry parameters
     * @param offset Offset 
     * @param limit Limit
     * @return List of log entries matching given parameters
     */
    public List<LogEntry> load(String sql, int offset, int limit) {
        try {
            return jdbcTemplate.query(sql, mapper, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store log entry in database.
     * @param entry Log entry to store
     * @return Auto-generated record id
     */
    public int store(LogEntry logEntry) {
        final String sql = "INSERT INTO dex_messages " +
            "(time_stamp, logger, level, message) VALUES " +
            "(?,?,?,?)";
        final LogEntry entry = logEntry;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.getJdbcOperations().update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(
                        Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql,
                            new String[] {"id"});
                    ps.setLong(1, entry.getTimeStamp());
                    ps.setString(2, entry.getLogger());
                    ps.setString(3, entry.getLevel());
                    ps.setString(4, entry.getMessage());
                    return ps;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
    }
    
    /**
     * Delete all log entries.
     * @return Number of records deleted
     */
    public int delete() {
        return jdbcTemplate.update("DELETE FROM dex_messages WHERE level" +
                " <> 'STICKY'");
    }
    
    /**
     * Delete log entry with a given id.
     * @return Number of records deleted
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_messages WHERE id = ?", 
                id);
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
    
 }
