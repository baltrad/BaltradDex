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

package eu.baltrad.dex.net.manager.impl;

import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.user.manager.IAccountManager;
import eu.baltrad.dex.net.manager.INodeManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.model.mapper.SubscriptionMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    /** Dependent managers */
    private IAccountManager accountManager;
    private IDataSourceManager dataSourceManager;
    private INodeManager nodeManager;
    
    /** Row mapper */
    private SubscriptionMapper mapper;

    /**
     * Constructor.
     */
    public SubscriptionManager() {
        this.mapper = new SubscriptionMapper();
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
    public void setAccountManager(IAccountManager accountManager) {
        this.accountManager = accountManager;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * @param nodeManager the nodeManager to set
     */
    @Autowired
    public void setNodeManager(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }
    
    /**
     * Load subscription.
     * @param id Subscription id
     * @return Subscription matching given id
     */
    public Subscription load(int id) {
        String sql = "SELECT dex_subscriptions.*, dex_users.name " + 
            "AS user_name, dex_nodes.name AS operator_name, " +
            "dex_data_sources.name AS datasource FROM dex_subscriptions, " + 
            "dex_users, dex_nodes, dex_data_sources, dex_subscriptions_users," +
            " dex_subscriptions_nodes, dex_subscriptions_data_sources WHERE " + 
            "dex_subscriptions_users.subscription_id = dex_subscriptions.id " +
            "AND dex_subscriptions_users.user_id = dex_users.id AND " + 
            "dex_subscriptions_nodes.subscription_id = dex_subscriptions.id " + 
            "AND dex_subscriptions_nodes.node_id = dex_nodes.id AND " +
            "dex_subscriptions_data_sources.subscription_id = " + 
            "dex_subscriptions.id " + 
            "AND dex_subscriptions_data_sources.data_source_id = " + 
            "dex_data_sources.id AND dex_subscriptions.id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load subscriptions.
     * @param type Subscription type
     * @return List of subscriptions matching given type
     */
    public List<Subscription> load(String type) {
        String sql = "SELECT dex_subscriptions.*, dex_users.name " + 
            "AS user_name, dex_nodes.name AS operator_name, " +
            "dex_data_sources.name AS datasource FROM dex_subscriptions, " + 
            "dex_users, dex_nodes, dex_data_sources, dex_subscriptions_users," +
            " dex_subscriptions_nodes, dex_subscriptions_data_sources WHERE " + 
            "dex_subscriptions_users.subscription_id = dex_subscriptions.id " +
            "AND dex_subscriptions_users.user_id = dex_users.id AND " + 
            "dex_subscriptions_nodes.subscription_id = dex_subscriptions.id " + 
            "AND dex_subscriptions_nodes.node_id = dex_nodes.id AND " +
            "dex_subscriptions_data_sources.subscription_id = " + 
            "dex_subscriptions.id " + 
            "AND dex_subscriptions_data_sources.data_source_id = " + 
            "dex_data_sources.id AND dex_subscriptions.type = ?";
        try {
            return jdbcTemplate.query(sql, mapper, type);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load subscriptions.
     * @param type Subscription type
     * @param operator Operator name
     * @return List of subscriptions matching given parameters 
     */
    public List<Subscription> load(String type, String operator) {
        String sql = "SELECT dex_subscriptions.*, dex_users.name " + 
            "AS user_name, dex_nodes.name AS operator_name, " +
            "dex_data_sources.name AS datasource FROM dex_subscriptions, " + 
            "dex_users, dex_nodes, dex_data_sources, dex_subscriptions_users," +
            " dex_subscriptions_nodes, dex_subscriptions_data_sources WHERE " + 
            "dex_subscriptions_users.subscription_id = dex_subscriptions.id " +
            "AND dex_subscriptions_users.user_id = dex_users.id AND " + 
            "dex_subscriptions_nodes.subscription_id = dex_subscriptions.id " + 
            "AND dex_subscriptions_nodes.node_id = dex_nodes.id AND " +
            "dex_subscriptions_data_sources.subscription_id = " + 
            "dex_subscriptions.id " + 
            "AND dex_subscriptions_data_sources.data_source_id = " + 
            "dex_data_sources.id AND dex_subscriptions.type = ? AND " +
            "dex_nodes.name = ?";
        try {
            return jdbcTemplate.query(sql, mapper, type, operator);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load subscriptions.
     * @param type Subscription type
     * @param user User name
     * @param dataSource Data source name
     * @return List of subscriptions matching given parameters
     */
    public Subscription load(String type, String user, String dataSource) {
        String sql = "SELECT dex_subscriptions.*, dex_users.name " + 
            "AS user_name, dex_nodes.name AS operator_name, " +
            "dex_data_sources.name AS datasource FROM dex_subscriptions, " + 
            "dex_users, dex_nodes, dex_data_sources, dex_subscriptions_users," +
            " dex_subscriptions_nodes, dex_subscriptions_data_sources WHERE " + 
            "dex_subscriptions_users.subscription_id = dex_subscriptions.id " +
            "AND dex_subscriptions_users.user_id = dex_users.id AND " + 
            "dex_subscriptions_nodes.subscription_id = dex_subscriptions.id " + 
            "AND dex_subscriptions_nodes.node_id = dex_nodes.id AND " +
            "dex_subscriptions_data_sources.subscription_id = " + 
            "dex_subscriptions.id " + 
            "AND dex_subscriptions_data_sources.data_source_id = " + 
            "dex_data_sources.id AND dex_subscriptions.type = ? AND " + 
            "dex_users.name = ? AND dex_data_sources.name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, type, user, 
                    dataSource);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store subscription.
     * @param subscription Subscription to store
     * @return Auto-generated record id
     * @throws Exception 
     */
    @Transactional(propagation=Propagation.REQUIRED, 
            rollbackFor=Exception.class)
    public int store(Subscription subscription) throws Exception {
        final String sql = "INSERT INTO dex_subscriptions (time_stamp, " + 
                    "type, active, sync) VALUES (?,?,?,?)";
        final Subscription s = subscription;
        
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.getJdbcOperations().update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(
                            Connection conn) throws SQLException {
                        PreparedStatement ps = conn.prepareStatement(sql,
                                new String[] {"id"});
                        ps.setLong(1, s.getDate().getTime());
                        ps.setString(2, s.getType());
                        ps.setBoolean(3, s.isActive());
                        ps.setBoolean(4, s.isSyncronized());
                        return ps;
                    }
                }, keyHolder);
            int subId = keyHolder.getKey().intValue();
            int userId = accountManager.load(s.getUser()).getId();
            jdbcTemplate.update("INSERT INTO dex_subscriptions_users " +
                    "(subscription_id, user_id) VALUES (?,?)", 
                    subId, userId);
            int dataSourceId = dataSourceManager.load(
                    s.getDataSource()).getId();
            jdbcTemplate.update("INSERT INTO dex_subscriptions_data_sources " +
                    "(subscription_id, data_source_id) VALUES (?,?)", 
                    subId, dataSourceId);
            int nodeId = nodeManager.load(s.getOperator()).getId();
            jdbcTemplate.update("INSERT INTO dex_subscriptions_nodes " +
                    "(subscription_id, node_id) VALUES (?,?)", 
                    subId, nodeId);
            return subId;
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Update subscription.
     * @param s Subscription to update
     * @throws Exception
     */
    public void update(Subscription s) throws Exception {
        try {
            jdbcTemplate.update(
                    "UPDATE dex_subscriptions SET time_stamp = ?, type = ?, " + 
                    "active = ?, sync = ? WHERE id = ?",
                s.getDate().getTime(),
                s.getType(),
                s.isActive(),
                s.isSyncronized(),
                s.getId());
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }    
    }
    
    /**
     * Delete subscription by id.
     * @param id Subscription id
     * @throws Exception
     */
    public void delete(int id) throws Exception {
        try {
            jdbcTemplate.update("DELETE FROM dex_subscriptions WHERE id = ?", 
                id);
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }    
    }
    
}

