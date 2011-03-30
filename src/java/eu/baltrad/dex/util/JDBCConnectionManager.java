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

package eu.baltrad.dex.util;

import java.util.Properties;
import java.util.Vector;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Handles JDBC connection pooling. Implemented as sigleton in order to keep control over the
 * number of created connections.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class JDBCConnectionManager {
//---------------------------------------------------------------------------------------- Constants
    /** Properties file name */
    private static final String PROPS_FILE_NAME = "dex.jdbc.properties";
    /** Driver class property */
    private static final String DRIVER_CLASS_PROP = "jdbc.connection.driver_class";
    /** Database URI property */
    private static final String DB_URI_PROP = "jdbc.connection.dburi";
    /** User name */
    private static final String USER_NAME_PROP = "jdbc.connection.username";
    /** Password */
    private static final String PASSWD_PROP = "jdbc.connection.password";
    /** Connection pool size prop */
    private static final String POOL_SIZE_PROP = "jdbc.connection.pool_size";
    /** Test query used to validate connection */
    private static final String TEST_QUERY = "SELECT count(*) FROM dex_users;";
//---------------------------------------------------------------------------------------- Variables
    /** Driver class */
    private static String driverClass;
    /** Database URI */
    private static String dbUri;
    /** User name */
    private static String userName;
    /** Password */
    private static String passwd;
    /** Connection pool size */
    private static int poolSize;
    /** Connection pool */
    private static Vector<Connection> connectionPool;
    /** Reference to the object of this class */
    private static JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Initializes object of this class in case it is null, otherwise returns existing object.
     *
     * @return Reference to the object of this class
     */
    public static synchronized JDBCConnectionManager getInstance() {
        if( jdbcConnectionManager == null ) {
            jdbcConnectionManager = new JDBCConnectionManager();
        }
        return jdbcConnectionManager;
    }
    /**
     * Private constructor can be invoked by getInstance() method only.
     */
    private JDBCConnectionManager() {
        init();
    }
    /**
     * Reads properties from stream, loads driver and initializes connection pool.
     */
    private void init() {
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                driverClass = props.getProperty( DRIVER_CLASS_PROP );
                dbUri = props.getProperty( DB_URI_PROP );
                userName = props.getProperty( USER_NAME_PROP );
                passwd = props.getProperty( PASSWD_PROP );
                poolSize = Integer.parseInt( props.getProperty( POOL_SIZE_PROP ) );
                // load driver
                try {
                    Class.forName( driverClass );
                     // Initialize connection pool
                    connectionPool = new Vector<Connection>();
                    initializeConnectionPool();

                } catch( ClassNotFoundException e ) {
                    System.out.println( "Failed to load JDBC driver: " + e.getMessage() );
                }
            }
        } catch( Exception e ) {
            System.out.println( "Failed to initialize JDBC connector: " + e.getMessage() );
        }
    }
    /**
     * Initializes connection pool
     */
    private void initializeConnectionPool() {
        while( !connectionPoolInitialized() ) {
            connectionPool.addElement( createNewConnection() );
        }
    }
    /**
     * Checks connection pool status.
     *
     * @return True if connection pool is initialized, false otherwise.
     */
    private synchronized boolean connectionPoolInitialized() {
        if( connectionPool.size() < poolSize ) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Creates new JDBC connection.
     *
     * @return JDBC connection
     */
    private Connection createNewConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection( dbUri, userName, passwd );
        } catch( SQLException e ) {
            System.out.println( "Failed to establish JDBC connection: " + e.getMessage() );
        }
        return conn;
    }
    /**
     * Gets first connection from the pool and removes the first element from the pool.
     * 
     * @return JDBC connection
     */
    public synchronized Connection getConnection() {
        Connection connection = null;
        if( connectionPool.size() > 0 ) {
            connection = connectionPool.firstElement();
            if( !validateConnection( connection ) ) {
                recoverConnectionPool();
                connection = connectionPool.firstElement();
            }
            connectionPool.removeElementAt( 0 );
        }
        return connection;
    }
    /**
     * Returns connection to the pool.
     *
     * @param connection JDBC connection to be returned to the pool
     */
    public synchronized void returnConnection( Connection connection ) {
        connectionPool.addElement( connection );
    }
    /**
     * Validates a connection.
     *
     * @param conn Connection to be validated
     * @return True if connection is valid, false otherwise
     */
    private boolean validateConnection( Connection conn ) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( TEST_QUERY );
            while( resultSet.next() ) {
                long count = resultSet.getLong( 1 );
            }
            return true;
        } catch( Exception e ) {
            return false;
        }
    }
    /**
     * Closes existing connections and initializes connection pool.
     */
    private void recoverConnectionPool() {
        for( int i = 0; i < poolSize; i++ ) {
            if( connectionPool.get( i ) != null ) {
                Connection c = connectionPool.get( i );
                try {
                    c.close();
                } catch( SQLException e ) {
                    System.out.println( "Failed to close invalid connection: " + e.getMessage() );
                }
            }
        }
        connectionPool.removeAllElements();
        for( int i = 0; i < poolSize; i++ ) {
            connectionPool.addElement( createNewConnection() );
        }
    }
}
//--------------------------------------------------------------------------------------------------
