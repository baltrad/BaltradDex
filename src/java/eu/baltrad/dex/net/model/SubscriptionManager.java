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

package eu.baltrad.dex.net.model;

import eu.baltrad.dex.log.util.MessageLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * Subscription manager implementing subscription handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionManager implements ISubscriptionManager {

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Logger */
    private Logger log;
    /** Row mapper */
    private Mapper mapper;

    /**
     * Constructor.
     */
    public SubscriptionManager() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
        this.mapper = new Mapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Loads subscription by id.
     * @param id Record id
     * @return Subscription with a given id
     */
    public Subscription load(int id) {
        String sql = "SELECT * FROM dex_subscriptions WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Loads subscriptions by type.
     * @param type Subscription type
     * @return List of subscriptions matching a given type
     */
    public List<Subscription> load(String type) {
        String sql = "SELECT * FROM dex_subscriptions WHERE type = ?";
        try {
            return jdbcTemplate.query(sql, mapper, type);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Loads subscriptions by operator name and type.
     * @param operator
     * @param type
     * @return List of subscriptions matching given parameters
     */
    public List<Subscription> load(String operator, String type) {
        String sql = "SELECT * FROM dex_subscriptions WHERE operator_name = ?" +
                " AND type = ?";
        try {
            return jdbcTemplate.query(sql, mapper, operator, type);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Loads subscriptions by user name, data source name and type.
     * @param userName User name
     * @param dataSourceName Data source name
     * @param type Subscription type
     * @return Subscription object matching given criteria
     */
    public Subscription load(String user, String dataSource, 
            String type) {
        String sql = "SELECT * FROM dex_subscriptions WHERE user_name = ? " + 
                "AND data_source_name = ? AND type = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, user, 
                dataSource, type); 
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Loads distinct users.
     * @return List of distinct users
     */
    public List<Subscription> loadUsers() {
        String sql = "SELECT DISTINCT ON (user_name) * FROM " +
                "dex_subscriptions WHERE type = 'upload'";
        try {
            return jdbcTemplate.query(sql, mapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Loads distinct operators.
     * @return List of distinct operators 
     */
    public List<Subscription> loadOperators() {
        String sql = "SELECT DISTINCT ON (operator_name) * FROM " +
                "dex_subscriptions WHERE type = 'download'";
        try {
            return jdbcTemplate.query(sql, mapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Stores subscription in a database.
     * @param s Subscription to store
     * @return Number of affected records
     */
    public int store(Subscription s) {
        return jdbcTemplate.update("INSERT INTO dex_subscriptions " +
            "(id, timestamp, user_name, data_source_name, operator_name, " + 
            "type, active, synkronized, node_address) " +
            "VALUES (?,?,?,?,?,?,?,?,?)",
            s.getId(),
            s.getTimeStamp(),
            s.getUserName(),
            s.getDataSourceName(),
            s.getOperatorName(),
            s.getType(),
            s.getActive(),
            s.getSynkronized(),
            s.getNodeAddress());
    }
    
    /**
     * Stores subscription record without ID
     * @param s Subscription to store
     * @return Number of affected records
     */
    public int storeNoId(Subscription s) {
        return jdbcTemplate.update("INSERT INTO dex_subscriptions " +
            "(timestamp, user_name, data_source_name, operator_name, " + 
            "type, active, synkronized, node_address) " +
            "VALUES (?,?,?,?,?,?,?,?)",
            s.getTimeStamp(),
            s.getUserName(),
            s.getDataSourceName(),
            s.getOperatorName(),
            s.getType(),
            s.getActive(),
            s.getSynkronized(),
            s.getNodeAddress());
    }
    
    /**
     * Updates subscription.
     * @param s Subscription object
     * @return Number of affected records
     */
    public int update(Subscription s) {
        return jdbcTemplate.update("UPDATE dex_subscriptions SET timestamp = ?,"
            + " user_name = ?, data_source_name = ?, operator_name = ?, " + 
            "type = ?, active = ?, synkronized = ?, node_address = ? " +
            "WHERE id = ?",
            s.getTimeStamp(),
            s.getUserName(),
            s.getDataSourceName(),
            s.getOperatorName(),
            s.getType(),
            s.getActive(),
            s.getSynkronized(),
            s.getNodeAddress(),
            s.getId());
    }
    
    /**
     * Removes subscription from the database.
     * @param s Subscription to remove
     * @return Number of affected records
     */
    public int delete(Subscription s) {
        return jdbcTemplate.update("DELETE FROM dex_subscriptions WHERE id = ?", 
                s.getId());
    }
     
    /**
     * Row mapper.
     */
    private static final class Mapper implements 
                                        ParameterizedRowMapper<Subscription> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return Subscription object
         * @throws SQLException 
         */
        public Subscription mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            Subscription s = new Subscription();
            s.setId(rs.getInt("id"));
            s.setTimeStamp(rs.getTimestamp("timestamp"));
            s.setUserName(rs.getString("user_name"));
            s.setDataSourceName(rs.getString("data_source_name"));
            s.setOperatorName(rs.getString("operator_name"));
            s.setType(rs.getString("type"));
            s.setActive(rs.getBoolean("active"));
            s.setSynkronized(rs.getBoolean("synkronized"));
            s.setNodeAddress(rs.getString("node_address"));
            return s;
        }
    }
    
    /**
     * Compares two subscription lists based on chosen subscription field values.
     *
     * @param s1 First subscription list
     * @param s2 Second subscription list
     * @return True if field values are equal
     */
    public boolean compare(List<Subscription> s1, List<Subscription> s2) {
        boolean res = true;
        try {
            if (s1.size() != s2.size()) {
                res = false;
            } else {
                for (int i = 0; i < s1.size(); i++) {
                    if(!s1.get(i).getDataSourceName().equals(s2.get(i).getDataSourceName()) ||
                            !s1.get(i).getNodeAddress().equals(s2.get(i).getNodeAddress()) ||
                            !s1.get(i).getOperatorName().equals(s2.get(i).getOperatorName()) ||
                            !s1.get(i).getType().equals(s2.get(i).getType()) ||
                            s1.get(i).getActive() != s2.get(i).getActive()) {
                        res = false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to compare subscriptions lists", e);
        }
        return res;
    }
    /**
     * Compares two subscription objects based on chosen field values.
     *
     * @param s1 First subscription object
     * @param s2 Second subscription object
     * @return True if field values are equal
     */
    public boolean compare(Subscription s1, Subscription s2) {
        boolean res = true;
        try {
            if (!s1.getDataSourceName().equals(s2.getDataSourceName()) ||
                    !s1.getNodeAddress().equals(s2.getNodeAddress()) ||
                    !s1.getOperatorName().equals(s2.getOperatorName()) ||
                    !s1.getType().equals( s2.getType()) || s1.getActive() 
                    != s2.getActive()) {
                res = false;
            }
        } catch(Exception e) {
            log.error("Failed to compare subscriptions", e);
        }
        return res;
    }
}

