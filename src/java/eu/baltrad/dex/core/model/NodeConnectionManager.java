/***************************************************************************************************
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
***************************************************************************************************/

package eu.baltrad.dex.core.model;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Node manager class implementing node connection object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class NodeConnectionManager {
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
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all existing node connections.
     *
     * @return List of all existing node connections
     */
    public List<NodeConnection> getConnections() {
        Connection conn = null;
        List<NodeConnection> nodeConnections = new ArrayList<NodeConnection>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_node_connections conn, dex_node_address addr, " +
                    "dex_node_connection_address conn_addr WHERE conn.id = conn_addr.connection_id"
                    + " AND addr.id = conn_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int connId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String userName = resultSet.getString( "user_name" );
                String passwd = resultSet.getString( "password" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                NodeConnection nodeConn = new NodeConnection( connId, name, scheme, hostAddress,
                        port, appCtx, entryAddress, userName, passwd );
                nodeConnections.add( nodeConn );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select node connections", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return nodeConnections;
    }
    /**
     * Gets node connection with a given ID.
     *
     * @param id Node connection ID
     * @return Node connection with a given ID
     */
    public NodeConnection getConnection( int id ) {
        Connection conn = null;
        NodeConnection nodeConnection = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_node_connections conn, dex_node_address addr, " +
                    "dex_node_connection_address conn_addr WHERE conn.id = " + id + " AND conn.id ="
                    + " conn_addr.connection_id AND addr.id = conn_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int connId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String userName = resultSet.getString( "user_name" );
                String passwd = resultSet.getString( "password" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                nodeConnection = new NodeConnection( connId, name, scheme, hostAddress, port,
                        appCtx, entryAddress, userName, passwd );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select node connections", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return nodeConnection;
    }
    /**
     * Gets node connection with a given name.
     *
     * @param connName Node connection name
     * @return Node connection with a given name
     */
    public NodeConnection getConnection( String connName ) {
        Connection conn = null;
        NodeConnection nodeConnection = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_node_connections conn, dex_node_address addr, " +
                    "dex_node_connection_address conn_addr WHERE conn.name = '" + connName +
                    "' AND conn.id = conn_addr.connection_id AND addr.id = conn_addr.address_id";
            ResultSet resultSet = stmt.executeQuery( sql );
            while( resultSet.next() ) {
                int connId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String userName = resultSet.getString( "user_name" );
                String passwd = resultSet.getString( "password" );
                String scheme = resultSet.getString( "scheme" );
                String hostAddress = resultSet.getString( "host_address" );
                int port = resultSet.getInt( "port" );
                String appCtx = resultSet.getString( "app_context" );
                String entryAddress = resultSet.getString( "entry_address" );
                nodeConnection = new NodeConnection( connId, name, scheme, hostAddress, port,
                        appCtx, entryAddress, userName, passwd );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select node connections", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
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
    public int saveOrUpdate( NodeConnection nodeConn ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            ResultSet generatedKeys = null;
            int connId = 0;
            int addressId = 0;
            // record does not exists, do insert
            if( nodeConn.getId() == 0 ) {
                sql = "INSERT INTO dex_node_connections (name, user_name, password) VALUES ('" + 
                    nodeConn.getConnectionName() + "', '" + nodeConn.getUserName() + "', '" +
                    nodeConn.getPassword() + "');";
                update = stmt.executeUpdate( sql, Statement.RETURN_GENERATED_KEYS ) ;
                generatedKeys = stmt.getGeneratedKeys();
                if( generatedKeys.next() ) {
                    connId = generatedKeys.getInt( 1 );
                }
                sql = "INSERT INTO dex_node_address (scheme, host_address, port, app_context, " +
                    "entry_address) VALUES ('" + nodeConn.getScheme() + "', '" +
                    nodeConn.getHostAddress() + "', " + nodeConn.getPort() + ", '" +
                    nodeConn.getAppCtx() + "', '" + nodeConn.getEntryAddress() + "');";
                update = stmt.executeUpdate( sql, Statement.RETURN_GENERATED_KEYS ) ;
                generatedKeys = stmt.getGeneratedKeys();
                if( generatedKeys.next() ) {
                    addressId = generatedKeys.getInt( 1 );
                }
                sql = "INSERT INTO dex_node_connection_address (connection_id, address_id) VALUES ("
                        + connId + ", " + addressId + ");";
                update = stmt.executeUpdate( sql );
                stmt.close();
            } else {
                // record exists, do update
                sql = "UPDATE dex_node_connections SET name = '" + nodeConn.getConnectionName() +
                    "', user_name = '" + nodeConn.getUserName() + 
                    "', password = '" + nodeConn.getPassword() + "' WHERE id = " +
                    nodeConn.getId() + ";";
                update = stmt.executeUpdate( sql ) ;
                sql = "UPDATE dex_node_address SET scheme = '" + nodeConn.getScheme() +
                        "', host_address = '" + nodeConn.getHostAddress() + "', port = " +
                        nodeConn.getPort() + ", app_context = '" + nodeConn.getAppCtx() +
                        "', entry_address = '" + nodeConn.getEntryAddress() + "' WHERE id IN " +
                        "(SELECT address_id FROM dex_node_connection_address WHERE connection_id = "
                        + nodeConn.getId() + " );";
                update = stmt.executeUpdate( sql );
                stmt.close();
            }
        } catch( Exception e ) {
            log.error( "Failed to save node connection", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
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
    public int deleteConnection( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_node_connections WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete node connection", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Deletes all node connections.
     *
     * @return Number of deleted entries
     * @throws Exception
     */
    public int deleteConnections() throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_node_connections;";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete node connections", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------