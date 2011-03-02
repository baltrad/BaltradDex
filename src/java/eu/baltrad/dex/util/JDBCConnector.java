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
import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles JDBC database connection.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class JDBCConnector {
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
//---------------------------------------------------------------------------------------- Variables
    /** Driver class */
    private static String driverClass;
    /** Database URI */
    private static String dbUri;
    /** User name */
    private static String userName;
    /** Password */
    private static String passwd;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public JDBCConnector() {
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                driverClass = props.getProperty( DRIVER_CLASS_PROP );
                dbUri = props.getProperty( DB_URI_PROP );
                userName = props.getProperty( USER_NAME_PROP );
                passwd = props.getProperty( PASSWD_PROP );
                // load driver
                try {
                    Class.forName( driverClass );
                } catch( ClassNotFoundException e ) {
                    System.out.println( "Failed to load JDBC driver: " + e.getMessage() );
                }
            }
        } catch( Exception e ) {
            System.out.println( "Failed to initialize JDBC connector: " + e.getMessage() );
        }
    }
    /**
     * Creates new connection.
     *
     * @return Connection object
     */
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection( dbUri, userName, passwd );
        } catch( SQLException e ) {
            System.out.println( "Failed to establish JDBC connection: " + e.getMessage() );
        }
        return conn;
    }
}
//--------------------------------------------------------------------------------------------------
