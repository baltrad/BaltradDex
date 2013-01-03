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

import eu.baltrad.dex.log.model.LogEntry;
import eu.baltrad.dex.net.manager.INodeManager;
import eu.baltrad.dex.net.model.impl.Node;
import eu.baltrad.dex.net.model.mapper.NodeMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

/**
 * Node manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class NodeManager implements INodeManager {
    
    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private NodeMapper mapper;
    
    /**
     * Constructor.
     */
    public NodeManager() {
        this.mapper = new NodeMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all nodes.
     * @return All available nodes 
     */
    public List<Node> load() {
        String sql = "SELECT * FROM dex_nodes";
        try {
            return jdbcTemplate.query(sql, mapper);
        } catch (DataAccessException e) {
            return null;
        }    
    }
    
    /**
     * Load operators.
     * @return List of operators
     */
    public List<Node> loadOperators() {
        String sql = "SELECT DISTINCT dex_nodes.name, dex_nodes.address, " + 
            "dex_nodes.id FROM dex_nodes, dex_subscriptions, " + 
            "dex_subscriptions_nodes WHERE " + 
            "dex_subscriptions_nodes.subscription_id = dex_subscriptions.id " + 
            "AND dex_subscriptions_nodes.node_id = dex_nodes.id AND " + 
            "dex_subscriptions.type = 'download';";
        try {
            return jdbcTemplate.query(sql, mapper);
        } catch (DataAccessException e) {
            return null;
        }    
    }
    
    /**
     * Load peers.
     * @return List of peer nodes
     */
    public List<Node> loadPeers() {
        String sql = "SELECT DISTINCT dex_nodes.name, dex_nodes.address, " + 
            "dex_nodes.id FROM dex_nodes, dex_subscriptions, " + 
            "dex_subscriptions_nodes WHERE " + 
            "dex_subscriptions_nodes.subscription_id = dex_subscriptions.id " + 
            "AND dex_subscriptions_nodes.node_id = dex_nodes.id AND " + 
            "dex_subscriptions.type = 'upload';";
        try {
            return jdbcTemplate.query(sql, mapper);
        } catch (DataAccessException e) {
            return null;
        }    
    }
    
    /**
     * Load node by id.
     * @param id Node id
     * @return Node with a given id.
     */
    public Node load(int id) {
        String sql = "SELECT * FROM dex_nodes WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load by user id
     * @param id User id
     * @return Node assigned with a given user
     */
    public Node loadByUser(int id) {
        String sql = "SELECT * FROM dex_nodes WHERE id IN (SELECT id FROM " + 
                "dex_users_nodes WHERE user_id = ?)";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (DataAccessException e) {
            return null;
        }
        
    }
    
    /**
     * Load node by name.
     * @param name Node name
     * @return Node with a given name.
     */
    public Node load(String name) {
        String sql = "SELECT * FROM dex_nodes WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store node object.
     * @param node Node object
     * @return Auto-generated record id
     */
    public int store(Node node) {
        final String sql = "INSERT INTO dex_nodes (name, address) VALUES (?,?)";
        final Node nd = node;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.getJdbcOperations().update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(
                        Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql,
                            new String[] {"id"});
                    ps.setString(1, nd.getName());
                    ps.setString(2, nd.getAddress());
                    return ps;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
    }
    
    /**
     * Store record in dex_users_nodes table.
     * @param userId User id
     * @param nodeId Role id
     * @return Number of records stored
     */
    public int store(int userId, int nodeId) {
        return jdbcTemplate.update(
                "INSERT INTO dex_users_nodes (user_id, node_id) " +
                "VALUES (?, ?)", userId, nodeId);
    }
    
    /**
     * Update record in dex_users_roles table.
     * @param roleId Role id
     * @param userId User id
     * @return Number of records updated
     */
    public int update(int userId, int nodeId) {
        return jdbcTemplate.update(
                "UPDATE dex_users_nodes SET user_id = ? WHERE node_id = ?", 
                userId, nodeId);
    }
    
    /**
     * Delete node with a given id.
     * @param id Node id
     * @return Number of deleted records.
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_nodes WHERE id = ?", id);
    }
    
}
