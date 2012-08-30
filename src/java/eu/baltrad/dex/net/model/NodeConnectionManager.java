/*******************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
********************************************************************************/

package eu.baltrad.dex.net.model;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.util.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;

/**
 * Node manager class implementing node connection object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class NodeConnectionManager implements INodeConnectionManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public NodeConnectionManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    /**
     * Gets all existing node connections.
     *
     * @return List of all existing node connections
     */
    public List<NodeConnection> get() {
        Connection conn = null;
        List<NodeConnection> nodeConnections = new ArrayList<NodeConnection>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_node_connections";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int connId = resultSet.getInt("id");
                String nodeName = resultSet.getString("node_name");
                String nodeAddress = resultSet.getString("node_address");
                NodeConnection nodeConn = new NodeConnection(connId, nodeName, nodeAddress);
                nodeConnections.add(nodeConn);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select node connections", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return nodeConnections;
    }
    /**
     * Gets node connection with a given ID.
     *
     * @param id Node connection ID
     * @return Node connection with a given ID
     */
    public NodeConnection get(int id) {
        Connection conn = null;
        NodeConnection nodeConnection = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_node_connections WHERE id = " + id + ";";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int connId = resultSet.getInt("id");
                String nodeName = resultSet.getString("node_name");
                String nodeAddress = resultSet.getString("node_address");
                nodeConnection = new NodeConnection(connId, nodeName, nodeAddress);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select node connections", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return nodeConnection;
    }
    /**
     * Gets node connection with a given name.
     *
     * @param nodeName Node name
     * @return Node connection with a given name
     */
    public NodeConnection get(String nodeName) {
        Connection conn = null;
        NodeConnection nodeConnection = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_node_connections WHERE node_name = '" + nodeName + "';";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int connId = resultSet.getInt("id");
                String name = resultSet.getString("node_name");
                String nodeAddress = resultSet.getString("node_address");
                nodeConnection = new NodeConnection(connId, name, nodeAddress);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select node connections", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return nodeConnection;
    }
    /**
     * Saves or updates node connection.
     *
     * @param nodeConn Node connection
     * @return Number of saved or updated records
     * @throws Exception
     */
    public int saveOrUpdate(NodeConnection nodeConn) {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            int connId = 0;
            int addressId = 0;
            // record does not exists, do insert
            if (nodeConn.getId() == 0) {
                sql = "INSERT INTO dex_node_connections (node_name, node_address) VALUES ('" + 
                    nodeConn.getNodeName() + "', '" + nodeConn.getNodeAddress() + "');";
                update = stmt.executeUpdate(sql);
                stmt.close();
            } else {
                // record exists, do update
                sql = "UPDATE dex_node_connections SET node_name = '" + nodeConn.getNodeName() +
                    "', node_address = '" + nodeConn.getNodeAddress() + "' WHERE id = " +
                    nodeConn.getId() + ";";
                update = stmt.executeUpdate(sql);
                stmt.close();
            }
        } catch(Exception e) {
            log.error("Failed to save node connection", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return update;
    }
    /**
     * Deletes node connection with a given ID.
     *
     * @param id Node connection ID
     * @return Number of deleted records
     * @throws Exception
     */
    public int delete(int id) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_node_connections WHERE id = " + id + ";";
            delete = stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to delete node connection", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return delete;
    }
    /**
     * Deletes all node connections.
     *
     * @return Number of deleted entries
     * @throws Exception
     */
    public int delete() throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_node_connections;";
            delete = stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to delete node connections", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------