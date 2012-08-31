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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;

import java.util.List;

import java.sql.SQLException;

/**
 * Node connection manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.0.0
 */
public class NodeConnectionManager implements INodeConnectionManager {

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private NodeConnectionMapper mapper;

    /**
     * Constructor.
     */
    public NodeConnectionManager() {
        this.mapper = new NodeConnectionMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all connections.
     * @return List of all existing connections.
     */
    public List<NodeConnection> load() {
        String sql = "SELECT * FROM dex_node_connections";
	List<NodeConnection> conns = jdbcTemplate.query(sql, mapper);
	return conns;
    }
    
    /**
     * Load connection by id.
     * @param id Connection id
     * @return Connection with a given id
     */
    public NodeConnection load(int id) {
        String sql = "SELECT * FROM dex_node_connections WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load connection by node name.
     * @param nodeName Node name
     * @return Connection with a given node name
     */
    public NodeConnection load(String nodeName) {
        String sql = "SELECT * FROM dex_node_connections WHERE node_name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, nodeName);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store node connection in database.
     * @param conn Node connection to be stored
     * @return Number of records stored
     */
    public int store(NodeConnection conn) {
        return jdbcTemplate.update("INSERT INTO dex_node_connections" +
            "(id, node_name, node_address) VALUES (?,?,?)",
            conn.getId(),
            conn.getNodeName(),
            conn.getNodeAddress());
    }
    
    /**
     * Store node connection in database.
     * @param conn Node connection to be stored
     * @return Number of records stored
     */
    public int storeNoId(NodeConnection conn) {
        return jdbcTemplate.update("INSERT INTO dex_node_connections" +
            "(node_name, node_address) VALUES (?,?)",
            conn.getNodeName(),
            conn.getNodeAddress());
    }
    
    /**
     * Delete node connection with a given id.
     * @param id Node connection id
     * @return Number of deleted records
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_node_connections WHERE " +
                "id = ?", id);
    }
    
    /**
     * Delete all node connections.
     * @return Number of deleted records
     */
    public int delete() {
        return jdbcTemplate.update("DELETE FROM dex_node_connections");
    }
    
    /**
     * Node connection row mapper.
     */
    private static final class NodeConnectionMapper
                            implements ParameterizedRowMapper<NodeConnection> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return Registry entry object
         * @throws SQLException 
         */
        public NodeConnection mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            NodeConnection conn = new NodeConnection(); 
            conn.setId(rs.getInt("id"));
            conn.setNodeName(rs.getString("node_name"));
            conn.setNodeAddress(rs.getString("node_address"));
            return conn;
        }
    }
    
}
