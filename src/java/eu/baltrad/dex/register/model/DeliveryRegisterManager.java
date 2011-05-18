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

package eu.baltrad.dex.register.model;

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
    public int deleteEntries() {
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
        } catch( Exception e ) {
            System.err.println( "Failed to delete delivery register entries: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
