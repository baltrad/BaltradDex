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

import java.sql.Connection;
import java.sql.DriverManager;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Tests JDBC databse connection
 *
 * @author szewczenko
 * @version 1.6
 * @since 1.6
 */
public class JDBCConnectionTest extends TestCase {
//---------------------------------------------------------------------------------------- Variables
    private static Connection conn;
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URI = "jdbc:postgresql://localhost:5432/baltrad";
    private static final String DB_USER = "baltrad";
    private static final String DB_PASSWD = "baltrad";
//------------------------------------------------------------------------------------------ Methods
    public void testLoadDriver() {
        try {
            Class.forName( DB_DRIVER );
        } catch( ClassNotFoundException e ) {
            fail( "Failed to load driver: " + e.getMessage() );
        }
    }

    public void testConnectJDBC() {
        try {
            conn = DriverManager.getConnection( DB_URI, DB_USER, DB_PASSWD );
            assertNotNull( conn );
        } catch( SQLException e ) {
            fail( "Failed to connect to database: " + e.getMessage() );
        }
    }

    public void testSelectRecords() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_radars" );

            while( resultSet.next() ) {
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                //System.out.println( "Radar name: " + name );
                //System.out.println( "WMO number: " + wmoNumber );
            }
            stmt.close();
            conn.close();
        } catch( SQLException e ) {
            fail( "Failed to execute query: " + e.getMessage() );
        }
    }
}
//--------------------------------------------------------------------------------------------------
