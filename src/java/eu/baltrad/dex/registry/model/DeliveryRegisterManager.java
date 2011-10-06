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

package eu.baltrad.dex.registry.model;

import eu.baltrad.dex.util.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class implements data delivery register handling functionality..
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class DeliveryRegisterManager {
//---------------------------------------------------------------------------------------- Constants
    /** Number of file entries per page */
    public final static int ENTRIES_PER_PAGE = 12;
    /** Number of pages in the scroll bar, must be an odd number >= 3 */
    public final static int SCROLL_RANGE = 11;
    /** SQL script creating trimmer function */
    private static final String SQL_TRIM_REG_BY_NUMBER = "DROP FUNCTION IF EXISTS " +
        "dex_trim_registry_by_number() CASCADE; CREATE OR REPLACE FUNCTION " +
        "dex_trim_registry_by_number() RETURNS trigger AS $$ DECLARE records_limit INTEGER; " + 
        "BEGIN records_limit = TG_ARGV[0]; DELETE FROM dex_delivery_register WHERE id IN " +
        "(SELECT id FROM dex_delivery_register ORDER BY timestamp DESC OFFSET records_limit);" +
        " RETURN NEW; END; $$ LANGUAGE plpgsql;";

    /** SQL script creating trimmer function */
    private static final String SQL_TRIM_REG_BY_AGE = "DROP FUNCTION IF EXISTS " +
        "dex_trim_registry_by_age() CASCADE; CREATE OR REPLACE FUNCTION " +
        "dex_trim_registry_by_age() RETURNS trigger AS $$ DECLARE max_age INTERVAL; BEGIN SELECT " +
        "(TG_ARGV[0] || ' days ' || TG_ARGV[1] || ' hours ' || TG_ARGV[2] || ' " +
        "minutes')::INTERVAL INTO max_age; DELETE FROM dex_delivery_register WHERE timestamp IN " +
        "(SELECT timestamp FROM dex_delivery_register WHERE age(now(), timestamp) > max_age); " +
        "RETURN NEW; END; $$ LANGUAGE plpgsql;";
    /** Trim messages by number function name */
    public static final String TRIM_REG_BY_NUMBER_FUNC = "dex_trim_registry_by_number()";
    /** Trim messages by date function name */
    public static final String TRIM_REG_BY_AGE_FUNC = "dex_trim_registry_by_age()";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public DeliveryRegisterManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets number of delivery register entries stored in a table.
     *
     * @return Number of delivery register entries
     */
    public long countEntries() {
        Connection conn = null;
        long count = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt. executeQuery( "SELECT count(*) FROM dex_delivery_register;"
                    );
            while( resultSet.next() ) {
                count = resultSet.getLong( 1 );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to determine number of delivery register entries: " + 
                    e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to determine number of delivery register entries: " + 
                    e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return count;
    }
    /**
     * Gets unique delivery register entry identified by user's ID and file's UUID.
     *
     * @param userId User id
     * @param uuid File's identity string
     * @return Single entry from data delivery register
     */
    public DeliveryRegisterEntry getEntry( int userId, String uuid ) {
        Connection conn = null;
        DeliveryRegisterEntry entry = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_delivery_register " +
                    "WHERE user_id = " + userId + " AND uuid = '" + uuid + "';" );
            while( resultSet.next() ) {
                int entryId = resultSet.getInt( "id" );
                int entryUserId = resultSet.getInt( "user_id" );
                String entryUuid = resultSet.getString( "uuid" );
                String userName = resultSet.getString( "user_name" );
                Date timestamp = resultSet.getTimestamp( "timestamp" );
                String status = resultSet.getString( "status" );
                entry = new DeliveryRegisterEntry( entryId, entryUserId, entryUuid, userName,
                        timestamp, status );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select delivery register entry: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select delivery register entry: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return entry;
    }
    /**
     * Gets complete delivery register.
     *
     * @return Full data delivery register
     */
    public List<DeliveryRegisterEntry> getEntries() {
        Connection conn = null;
        List<DeliveryRegisterEntry> entries = new ArrayList<DeliveryRegisterEntry>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_delivery_register" );
            while( resultSet.next() ) {
                int entryId = resultSet.getInt( "id" );
                int entryUserId = resultSet.getInt( "user_id" );
                String entryUuid = resultSet.getString( "uuid" );
                String userName = resultSet.getString( "user_name" );
                Date timestamp = resultSet.getTimestamp( "timestamp" );
                String status = resultSet.getString( "status" );
                DeliveryRegisterEntry entry = new DeliveryRegisterEntry( entryId, entryUserId,
                        entryUuid, userName, timestamp, status );
                entries.add( entry );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to fetch delivery register: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to fetch delivery register: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return entries;
    }
    /**
     * Gets given number of entries from the data delivery register.
     *
     * @param offset Number of entries to skip
     * @param limit Number of entries to select
     * @return List containing given number of entries
     */
    public List<DeliveryRegisterEntry> getEntries( int offset, int limit ) {
        Connection conn = null;
        List<DeliveryRegisterEntry> entries = new ArrayList<DeliveryRegisterEntry>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_delivery_register " +
                    "ORDER BY timestamp DESC OFFSET " + offset + " LIMIT " + limit );
            while( resultSet.next() ) {
                int entryId = resultSet.getInt( "id" );
                int entryUserId = resultSet.getInt( "user_id" );
                String entryUuid = resultSet.getString( "uuid" );
                String userName = resultSet.getString( "user_name" );
                Date timestamp = resultSet.getTimestamp( "timestamp" );
                String status = resultSet.getString( "status" );
                DeliveryRegisterEntry entry = new DeliveryRegisterEntry( entryId, entryUserId,
                        entryUuid, userName, timestamp, status );
                entries.add( entry );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data delivery register entries: " +
                    e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select data delivery register entries: " +
                    e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return entries;
    }
    /**
     * Adds entry to the delivery register.
     *
     * @param entry Entry to add
     * @return Number of inserted records
     */
    public synchronized int addEntry( DeliveryRegisterEntry entry ) {
        Connection conn = null;
        int insert = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO dex_delivery_register(user_id, uuid, user_name, timestamp," +
                    " status) VALUES ('" + entry.getUserId() + "', '" + entry.getUuid() + "', '" +
                    entry.getUserName() + "', '" + entry.getTimeStamp() + "', '" +
                    entry.getDeliveryStatus() + "');";
            insert = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to insert delivery register entries: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to insert delivery register entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return insert;
    }
    /**
     * Deletes delivery register entry with a given ID.
     *
     * @return Number of deleted entries
     */
    public int deleteEntry( int id ) {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_delivery_register WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete delivery register entry: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to delete delivery register entry: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Deletes all entries from the delivery register.
     *
     * @return Number of deleted entries
     */
    public int deleteEntries() throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_delivery_register;";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete delivery register entries: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete delivery register entries: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Creates trigger on delivery registry table. Trigger activates trimmer function which
     * deletes records from delivery registry table.
     *
     * @param recordLimit Trimmer function is activated if records number given by this parameter
     * is exceeded
     * @throws SQLException
     * @throws Exception
     */
    public void setTrimmer( int recordLimit ) throws SQLException, Exception {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = SQL_TRIM_REG_BY_NUMBER + " CREATE TRIGGER dex_trim_registry_by_number_tg "
                + "AFTER INSERT ON dex_delivery_register FOR EACH ROW EXECUTE PROCEDURE " +
                "dex_trim_registry_by_number(" + recordLimit + ");";
            stmt.execute( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to set delivery registry trimmer: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to set delivery registry trimmer: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
    /**
     * Creates trigger on delivery registry table. Trigger activates trimmer function which
     * deletes records older than a given age limit.
     *
     * @param maxAgeDays Age limit - number of days
     * @param maxAgeHours Age limit - number of hours
     * @param maxAgeMinutes Age limit - number of minutes
     * @throws SQLException
     * @throws Exception
     */
    public void setTrimmer( int maxAgeDays, int maxAgeHours, int maxAgeMinutes )
            throws SQLException, Exception {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = SQL_TRIM_REG_BY_AGE + " CREATE TRIGGER dex_trim_registry_by_age_tg " +
                "AFTER INSERT ON " + "dex_delivery_register FOR EACH ROW EXECUTE PROCEDURE " +
                "dex_trim_registry_by_age(" + maxAgeDays + ", " + maxAgeHours + ", " + maxAgeMinutes
                + ");";
            stmt.execute( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to set delivery registry trimmer: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to set delivery registry trimmer: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
    /**
     * Removes function and the calling trigger on delivery registry table.
     *
     * @param functionName Name of the function
     * @throws SQLException
     * @throws Exception
     */
    public void removeTrimmer( String functionName ) throws SQLException, Exception {
        Connection conn = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DROP FUNCTION IF EXISTS " + functionName + " CASCADE;";
            stmt.execute( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to remove delivery registry trimmer: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to remove delivery registry trimmer: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
    }
}
//--------------------------------------------------------------------------------------------------
