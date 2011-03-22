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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

/**
 * Node manager class implementing node connection object handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class NodeConnectionManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public NodeConnectionManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets all existing node connections.
     *
     * @return List of all existing node connections
     */
    public List<NodeConnection> getConnections() {
        Connection conn = null;
        List<NodeConnection> nodeConns = new ArrayList<NodeConnection>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_node_connections" );
            while( resultSet.next() ) {
                int connId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String shortAddress = resultSet.getString( "short_address" );
                String port = resultSet.getString( "port" );
                String userName = resultSet.getString( "user_name" );
                String passwd = resultSet.getString( "password" );
                NodeConnection nodeConn = new NodeConnection( connId, name, shortAddress, port,
                        userName, passwd );
                nodeConns.add( nodeConn );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select node connections: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select node connections: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return nodeConns;
    }
    /**
     * Gets node connection with a given ID.
     *
     * @param id Node connection ID
     * @return Node connection with a given ID
     */
    public NodeConnection getConnection( int id ) {
        Connection conn = null;
        NodeConnection nodeConn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_node_connections WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int connId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String shortAddress = resultSet.getString( "short_address" );
                String port = resultSet.getString( "port" );
                String userName = resultSet.getString( "user_name" );
                String passwd = resultSet.getString( "password" );
                nodeConn = new NodeConnection( connId, name, shortAddress, port, userName, passwd );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select node connections: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select node connections: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return nodeConn;
    }
    /**
     * Gets node connection with a given name.
     *
     * @param connectionName Node connection name
     * @return Node connection with a given name
     */
    public NodeConnection getConnection( String connectionName ) {
        Connection conn = null;
        NodeConnection nodeConn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_node_connections WHERE" +
                    " name = '" + connectionName + "';" );
            while( resultSet.next() ) {
                int connId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String shortAddress = resultSet.getString( "short_address" );
                String port = resultSet.getString( "port" );
                String userName = resultSet.getString( "user_name" );
                String passwd = resultSet.getString( "password" );
                nodeConn = new NodeConnection( connId, name, shortAddress, port, userName, passwd );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select node connections: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select node connections: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return nodeConn;
    }
    /**
     * Saves or updates node connection.
     *
     * @param nodeConn Node connection
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveOrUpdate( NodeConnection nodeConn ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( nodeConn.getId() == 0 ) {
                sql = "INSERT INTO dex_node_connections (name, short_address, port, user_name, " +
                    "password) VALUES ('" + nodeConn.getConnectionName() + "', '" +
                    nodeConn.getShortAddress() + "', '" + nodeConn.getPortNumber() + "', '" +
                    nodeConn.getUserName() + "', '" + nodeConn.getPassword() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_node_connections SET name = '" + nodeConn.getConnectionName() +
                    "', short_address = '" + nodeConn.getShortAddress() + "', port = '" +
                    nodeConn.getPortNumber() + "', user_name = '" + nodeConn.getUserName() + 
                    "', password = '" + nodeConn.getPassword() + "' WHERE id = " +
                    nodeConn.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save node connection: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save node connection: " + e.getMessage() );
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
     * @throws SQLException
     * @throws Exception
     */
    public int deleteConnection( int id ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_node_connections WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete node connection: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete node connection: " + e.getMessage() );
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
     */
    public int deleteConnections() {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_node_connections;";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete node connections: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to delete node connections: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------