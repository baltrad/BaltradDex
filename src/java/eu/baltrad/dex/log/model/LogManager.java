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

package eu.baltrad.dex.log.model;

import eu.baltrad.dex.util.JDBCConnector;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class implements system message manager.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class LogManager {
//---------------------------------------------------------------------------------------- Constants
    /** Informative message identifier */
    public final static String MSG_INFO = "INFO";
    /** Warning message identifier */
    public final static String MSG_WRN = "WARNING";
    /** Error message identifier */
    public final static String MSG_ERR = "ERROR";
    /** Log entry list size limit*/
    public final static int PAGE_LIMIT = 12;
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnector jdbcConnector;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Gets reference to JDBCConnector class object
     * 
     * @return Reference to JDBCConnector class object
     * @see JDBCConnector
     */
    public JDBCConnector getJdbcConnector() { return jdbcConnector; }
    /**
     * Sets reference to JDBCConnector class object.
     *
     * @param jdbcConnector Reference to JDBCConnector class object
     * @see JDBCConnector
     */
    public void setJdbcConnector( JDBCConnector jdbcConnector ) {
        this.jdbcConnector = jdbcConnector;
    }
    /**
     * Gets number of log entries stored in a table.
     *
     * @return Number of log entries
     */
    public int countEntries() {
        int count = 0;
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt. executeQuery( "SELECT count(*) FROM dex_messages" );
            while( resultSet.next() ) {
                count = resultSet.getInt( 1 );
            }
            stmt.close();
            conn. close();
        } catch( SQLException e ) {
            System.err.println( "Failed to determine number of entries: " + e.getMessage() );
        }
        return count;
    }
    /**
     * Gets all entries available in the system log.
     *
     * @return List of all available log entries
     */
    public List<LogEntry> getEntries() {
        List<LogEntry> entries = new ArrayList<LogEntry>();
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_messages ORDER BY " +
                "timestamp DESC" );
            while( resultSet.next() ) {
                Date timeStamp = resultSet.getTimestamp( "timestamp" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( timeStamp, type, msg );
                entries.add( entry );
            }
            stmt.close();
            conn. close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        }
        return entries;
    }
    /**
     * Gets given number of entries from the system log.
     * 
     * @param limit Number of entries to select
     * @return List containing given number of entries
     */
    public List<LogEntry> getEntries( int limit ) {
        List<LogEntry> entries = new ArrayList<LogEntry>();
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_messages ORDER BY " +
                "timestamp DESC LIMIT " + limit );
            while( resultSet.next() ) {
                Date timeStamp = resultSet.getTimestamp( "timestamp" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( timeStamp, type, msg );
                entries.add( entry );
            }
            stmt.close();
            conn.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        }
        return entries;
    }
    /**
     * Gets given number of entries from the system log.
     *
     * @param offset Number of entries to skip
     * @param limit Number of entries to select
     * @return List containing given number of entries
     */
    public List<LogEntry> getEntries( int offset, int limit ) {
        List<LogEntry> entries = new ArrayList<LogEntry>();
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_messages ORDER BY " +
                "timestamp DESC OFFSET " + offset + " LIMIT " + limit );
            while( resultSet.next() ) {
                Date timeStamp = resultSet.getTimestamp( "timestamp" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( timeStamp, type, msg );
                entries.add( entry );
            }
            stmt.close();
            conn.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        }
        return entries;
    }
    /**
     * Adds entry to the system log.
     *
     * @param entry Entry to add
     * @return Number of inserted records
     */
    public synchronized int addEntry( LogEntry entry ) {
        int insert = 0;
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO dex_messages (timestamp, type, message) VALUES ('" +
                    entry.getTimeStamp() + "', '" + entry.getType() + "', '" + entry.getMessage() +
                    "');";
            insert = stmt.executeUpdate( sql );
            stmt.close();
            conn.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete log entries: " + e.getMessage() );
        }
        return insert;
    }
    /**
     * Adds entry to the system log.
     *
     * @param timestamp Log entry timestamp
     * @param type Log entry type
     * @param message Log entry message
     * @return Number of inserted records
     */
    public synchronized int addEntry( Date timestamp, String type, String message ) {
        int insert = 0;
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO dex_messages (timestamp, type, message) VALUES ('" +
                    timestamp + "', '" + type + "', '" + message + "');";
            insert = stmt.executeUpdate( sql );
            stmt.close();
            conn.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete log entries: " + e.getMessage() );
        }
        return insert;
    }
    /**
     * Deletes all entries from the system log.
     *
     * @return Number of deleted entries
     */
    public int deleteEntries() {
        int delete = 0;
        try {
            Connection conn = JDBCConnector.connect();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_messages";
            delete = stmt.executeUpdate( sql );
            stmt.close();
            conn.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete log entries: " + e.getMessage() );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
