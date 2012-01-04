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

import eu.baltrad.dex.util.JDBCConnectionManager;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.List;
import java.util.ArrayList;

/**
 * Class implements custom log message appender.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.6
 * @since 0.6.6
 */
public class LogManager {
//---------------------------------------------------------------------------------------- Constants
    /** Number of log entries per page */
    public final static int ENTRIES_PER_PAGE = 12;
    /** Number of pages in the scroll bar, must be an odd number >= 3 */
    public final static int SCROLL_RANGE = 11;
    /** Trim messages by number trigger */
    public static final String TRIM_MSG_BY_NUMBER_TG = "dex_trim_messages_by_number_tg";
    /** Trim messages by date trigger */
    public static final String TRIM_MSG_BY_AGE_TG = "dex_trim_messages_by_age_tg";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager and Log4j Logger.
     */
    public LogManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX ); // Probably not a good idea to send errors back to myself
    }
    /**
     * Adds log entry to the system log stored in the database
     *
     * @param event LogEntry
     */
    public synchronized void addEntry( LogEntry entry ) {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String msg = entry.getMessage();
            if (msg != null) {
              msg = msg.replace('\'','"');
            }
            String sql = "INSERT INTO dex_messages (timestamp, system, type, message) VALUES ('" +
                entry.getTimeStamp() + "', '" + entry.getSystem() + "', '" + entry.getType() +
                "', '" + msg + "');";
            stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
          // We probably shouldn't send log messages if this goes wrong since we might end up here again
          System.out.println("Failed to add log entry");
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
        } catch( Exception e ) {
            log.error( "Failed to determine number of entries", e );
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
                String system = resultSet.getString( "system" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( timeStamp, system, type, msg );
                entries.add( entry );
            }
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to select log entries", e );
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
                String system = resultSet.getString( "system" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( timeStamp, system, type, msg );
                entries.add( entry );
            }
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to select log entries", e );
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
                String system = resultSet.getString( "system" );
                String type = resultSet.getString( "type" );
                String msg = resultSet.getString( "message" );
                LogEntry entry = new LogEntry( timeStamp, system, type, msg );
                entries.add( entry );
            }
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to select log entries", e );
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
    public int deleteEntries() {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_messages;";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to delete log entries", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Creates trigger on messages table. Trigger activates trimmer function which deletes records
     * from messages table.
     * 
     * @param recordLimit Trimmer function is activated if records number given by this parameter
     * is exceeded
     * @throws Exception
     */
    public void setTrimmer( int recordLimit ) throws Exception {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DROP TRIGGER IF EXISTS dex_trim_messages_by_number_tg ON dex_messages; " +
                "CREATE TRIGGER dex_trim_messages_by_number_tg AFTER INSERT ON " +
                "dex_messages FOR EACH ROW EXECUTE PROCEDURE dex_trim_messages_by_number(" +
                recordLimit + ");";
            stmt.execute( sql );
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to set message log trimmer", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
    /**
     * Creates trigger on messages table. Trigger activates trimmer function which deletes records
     * older than a given age limit.
     *
     * @param maxAgeDays Age limit - number of days
     * @param maxAgeHours Age limit - number of hours
     * @param maxAgeMinutes Age limit - number of minutes
     * @throws Exception
     */
    public void setTrimmer( int maxAgeDays, int maxAgeHours, int maxAgeMinutes )
            throws Exception {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DROP TRIGGER IF EXISTS dex_trim_messages_by_age_tg ON dex_messages; " +
                "CREATE TRIGGER dex_trim_messages_by_age_tg AFTER INSERT ON dex_messages" +
                " FOR EACH ROW EXECUTE PROCEDURE dex_trim_messages_by_age(" + maxAgeDays + ", " +
                maxAgeHours + ", " + maxAgeMinutes + ");";
            stmt.execute( sql );
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to set message log trimmer", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
    /**
     * Removes trigger on message table.
     *
     * @param triggerName Name of the trigger
     * @throws Exception
     */
    public void removeTrimmer( String triggerName ) throws Exception {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DROP TRIGGER IF EXISTS " + triggerName + " ON dex_messages;";
            stmt.execute( sql );
            stmt.close();
        } catch( Exception e ) {
          log.error( "Failed to remove message log trimmer", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
}
//--------------------------------------------------------------------------------------------------
