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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import eu.baltrad.dex.util.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;
import java.util.ArrayList;

/**
 * Class implements custom log message appender.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.6
 * @since 0.6.6
 *
 * <pre>
 * This class should be of interest to you if you want to append messages to the system log
 * stored in the database. In order store a message in the system log, custom <b>append()</b>
 * method should be used:
 *
 * <b>public void append( LoggingEvent event ) {}</b>
 *
 * The method accepts LoggingEvent as a call parameter. Use either <b>LogEntry</b> class or your
 * own implementation of LoggingEvent. The following is an example of how this method should be
 * used with <b>LogEntry</b> object.
 * 
 * To append LogEntry using default logger available in <b>LogManager</b> class:
 *
 * <b>
 * logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_INFO, "A test message" ) );
 * </b>
 *
 * To append LogEntry using logger of your choice:
 *
 * <b>
 * logManager.append( new LogEntry( LogEntry.LOG_SRC_PGF, 
 *          Logger.getLogger( "eu.baltrad.test.class" ), LogEntry.LEVEL_INFO, "A test message" ) );
 * </b>
 * Note that <b>LogEntry</b> should be used in order to keep compliance with DEX message display
 * functionality.
 * Refer to the documentation of <b>LogEntry</b> class for details.
 * </pre>
 */
public class LogManager extends AppenderSkeleton {
//---------------------------------------------------------------------------------------- Constants
    /** Number of log entries per page */
    public final static int ENTRIES_PER_PAGE = 12;
    /** Number of pages in the scroll bar, must be an odd number >= 3 */
    public final static int SCROLL_RANGE = 11;
    /** References logges object */
    private static Logger logger;
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager and Log4j Logger.
     */
    public LogManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        logger = Logger.getLogger( LogManager.class );
    }
    /**
     * Determines if the appender requires a layout
     * 
     * @return True
     */
    public boolean requiresLayout() { return true; }
    /**
     *
     */
    public void close() {};
    /**
     * Gets reference to logger object
     *
     * @return Reference to logger object
     */
    public static Logger getLogger() { return logger; }
    /**
     * Appends a log entry to the system log stored in the database
     *
     * @param event LoggingEvent
     */
    public void append( LoggingEvent event ) {
        LogEntry entry = ( LogEntry )event;
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            Timestamp timestamp = new Timestamp( entry.getTimeStamp() );
            String sql = "INSERT INTO dex_messages (timestamp, system, type, message) VALUES ('" +
                timestamp + "', '" + entry.getSystem() + "', '" +
                entry.getLevel() + "', '" + entry.getMessage() + "');";
            stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to append log entry: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to append log entry: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
    /**
     * Gets number of log entries stored in a table.
     *
     * @return Number of log entries
     */
    public long countEntries() {
        Connection conn = null;
        long count = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt. executeQuery( "SELECT count(*) FROM dex_messages;" );
            while( resultSet.next() ) {
                count = resultSet.getLong( 1 );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to determine number of entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to determine number of entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return count;
    }
    /**
     * Gets all entries available in the system log.
     *
     * @return List of all available log entries
     */
    public List<LogEntry> getEntries() {
        Connection conn = null;
        List<LogEntry> entries = new ArrayList<LogEntry>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_messages ORDER BY " +
                "timestamp DESC;" );
            while( resultSet.next() ) {
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                long time = timeStamp.getTime();
                String system = resultSet.getString( "system" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( system, time, Level.toLevel( type ), msg );
                entries.add( entry );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
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
        Connection conn = null;
        List<LogEntry> entries = new ArrayList<LogEntry>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_messages ORDER BY " +
                "timestamp DESC LIMIT " + limit + ";");
            while( resultSet.next() ) {
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                long time = timeStamp.getTime();
                String system = resultSet.getString( "system" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( system, time, Level.toLevel( type ), msg );
                entries.add( entry );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
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
        Connection conn = null;
        List<LogEntry> entries = new ArrayList<LogEntry>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_messages ORDER BY " +
                "timestamp DESC OFFSET " + offset + " LIMIT " + limit );
            while( resultSet.next() ) {
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                long time = timeStamp.getTime();
                String system = resultSet.getString( "system" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( system, time, Level.toLevel( type ), msg );
                entries.add( entry );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select log entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return entries;
    }
    /**
     * Deletes all entries from the system log.
     *
     * @return Number of deleted entries
     */
    public int deleteEntries() throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_messages;";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete log entries: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete log entries: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
