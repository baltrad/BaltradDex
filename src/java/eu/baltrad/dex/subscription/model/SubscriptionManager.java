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

package eu.baltrad.dex.subscription.model;

import eu.baltrad.dex.util.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Subscription manager inplementing subscription handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public SubscriptionManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets all existing subscriptions.
     *
     * @return List of all existing subscriptions
     */
    public List<Subscription> getSubscriptions() {
        Connection conn = null;
        List<Subscription> subs = new ArrayList<Subscription>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_subscriptions" );
            while( resultSet.next() ) {
                int subId = resultSet.getInt( "id" );
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                String userName = resultSet.getString( "user_name" );
                String channelName = resultSet.getString( "channel_name" );
                String nodeAddress = resultSet.getString( "node_address" );
                String operator = resultSet.getString( "operator_name" );
                String type = resultSet.getString( "type" );
                boolean active = resultSet.getBoolean( "active" );
                boolean synkronized = resultSet.getBoolean( "synkronized" );
                Subscription sub = new Subscription( subId, timeStamp, userName, channelName,
                        nodeAddress, operator, type, active, synkronized );
                subs.add( sub );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return subs;
    }
    /**
     * Gets subsciptions by type.
     *
     * @param subscriptionType Subscription type
     * @return List of subscriptions of a given type
     */
    public List<Subscription> getSubscriptions( String subscriptionType ) {
        Connection conn = null;
        List<Subscription> subs = new ArrayList<Subscription>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_subscriptions " +
                    "WHERE type = '" + subscriptionType + "';" );
            while( resultSet.next() ) {
                int subId = resultSet.getInt( "id" );
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                String userName = resultSet.getString( "user_name" );
                String channelName = resultSet.getString( "channel_name" );
                String nodeAddress = resultSet.getString( "node_address" );
                String operator = resultSet.getString( "operator_name" );
                String type = resultSet.getString( "type" );
                boolean active = resultSet.getBoolean( "active" );
                boolean synkronized = resultSet.getBoolean( "synkronized" );
                Subscription sub = new Subscription( subId, timeStamp, userName, channelName,
                        nodeAddress, operator, type, active, synkronized );
                subs.add( sub );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return subs;
    }
    /**
     * Gets unique subsciption identified by data channel name and subscription type.
     *
     * @param channelName Channel name
     * @param subscriptionType Subscription type
     * @return Subscription object
     */
    public Subscription getSubscription( String channelName, String subscriptionType ) {
        Connection conn = null;
        Subscription sub = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_subscriptions " +
                    "WHERE channel_name = '" + channelName + "' AND type = '" +
                    subscriptionType + "';" );
            while( resultSet.next() ) {
                int subId = resultSet.getInt( "id" );
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                String userName = resultSet.getString( "user_name" );
                String channel = resultSet.getString( "channel_name" );
                String nodeAddress = resultSet.getString( "node_address" );
                String operator = resultSet.getString( "operator_name" );
                String type = resultSet.getString( "type" );
                boolean active = resultSet.getBoolean( "active" );
                boolean synkronized = resultSet.getBoolean( "synkronized" );
                sub = new Subscription( subId, timeStamp, userName, channel, nodeAddress,
                        operator, type, active, synkronized );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return sub;
    }
    /**
     * Gets unique subsciption identified by user name, data channel name and subscription type.
     *
     * @param userName User name
     * @param channelName Channel name
     * @param subscriptionType Subscription type
     * @return Subscription object
     */
    public Subscription getSubscription( String userName, String channelName,
            String subscriptionType ) {
        Connection conn = null;
        Subscription sub = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_subscriptions " +
                    "WHERE channel_name = '" + channelName + "' AND type = '" +
                    subscriptionType + "';" );
            while( resultSet.next() ) {
                int subId = resultSet.getInt( "id" );
                Timestamp timeStamp = resultSet.getTimestamp( "timestamp" );
                String user = resultSet.getString( "user_name" );
                String channel = resultSet.getString( "channel_name" );
                String nodeAddress = resultSet.getString( "node_address" );
                String operator = resultSet.getString( "operator_name" );
                String type = resultSet.getString( "type" );
                boolean active = resultSet.getBoolean( "active" );
                boolean synkronized = resultSet.getBoolean( "synkronized" );
                sub = new Subscription( subId, timeStamp, user, channel, nodeAddress, operator,
                        type, active, synkronized );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select subscriptions: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return sub;
    }
    /**
     * Gets distinct field values.
     *
     * @param fieldName Name of the field the value of which will be selected
     * @param subscriptionType Subscription type
     * @return List of strings representing field values
     */
    public List<String> getDistinct( String fieldName, String subscriptionType ) {
        Connection conn = null;
        List<String> fieldValues = new ArrayList<String>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT DISTINCT " + fieldName +
                    " FROM dex_subscriptions WHERE type = '" + subscriptionType + "';" );
            while( resultSet.next() ) {
                String fieldValue = resultSet.getString( fieldName );
                fieldValues.add( fieldValue );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select distinct field values: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select distinct field values: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return fieldValues;
    }
    /**
     * Saves subscription object.
     *
     * @param sub Subscription to be saved
     * @return Number of inserted records
     * @throws SQLException
     * @throws Exception
     */
    public int saveSubscription( Subscription sub ) throws SQLException, Exception {
        Connection conn = null;
        int insert = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO dex_subscriptions (timestamp, user_name, channel_name, " +
                    "node_address, operator_name, type, active, synkronized ) VALUES ('" +
                    sub.getTimeStamp() + "', '" +sub.getUserName() + "', '" + sub.getChannelName() +
                    "', '" + sub.getNodeAddress() + "', '" + sub.getOperatorName() + "', '" +
                    sub.getType() + "', '" + sub.getActive() + "', '" + sub.getSynkronized() +
                    "');";
            insert = stmt.executeUpdate( sql ) ;
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
        return insert;
    }
    /**
     * Updates subscription object.
     *
     * @param sub Subscription to be saved
     * @return Number of inserted records
     * @throws SQLException
     * @throws Exception
     */
    public int updateSubscription( String channelName, String subscriptionType, boolean active )
            throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "UPDATE dex_subscriptions SET active = " + active  + " WHERE " +
                    "channel_name = '" + channelName + "' AND type = '" + subscriptionType + "';";
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
     * Deletes subscription with a given ID.
     *
     * @param id Subscription ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteSubscription( int id ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_subscriptions WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete subscription: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete subscription: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Deletes subscription identified by a given channel name and type.
     *
     * @param channelName Name of subscribed cannel
     * @param subscriptionType Subscription type
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteSubscription( String channelName, String subscriptionType )
            throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_subscriptions WHERE channel_name = '" +
                    channelName + "' AND type = '" + subscriptionType + "';";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete subscription: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete subscription: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Deletes subscription identified by a given user name, channel name and type.
     *
     * @param userName Name of the subscriber
     * @param channelName Name of subscribed cannel
     * @param subscriptionType Subscription type
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteSubscription( String userName, String channelName, String subscriptionType )
            throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_subscriptions WHERE user_name = '" + userName + 
                    "' AND channel_name = '" + channelName + "' AND type = '" + subscriptionType +
                    "';";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete subscription: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete subscription: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    } 
    /**
     * Compares two subscription lists based on chosen subscription field values.
     *
     * @param s1 First subscription list
     * @param s2 Second subscription list
     * @return True if field values are equal
     */
    public boolean compareSubscriptionLists( List< Subscription > s1, List< Subscription > s2 ) {
        boolean res = true;
        if( s1.size() != s2.size() ) {
            res = false;
        } else {
            for( int i = 0; i < s1.size(); i++ ) {
                if( !s1.get( i ).getChannelName().equals( s2.get( i ).getChannelName() ) ||
                        !s1.get( i ).getNodeAddress().equals( s2.get( i ).getNodeAddress() ) ||
                        !s1.get( i ).getOperatorName().equals( s2.get( i ).getOperatorName() ) ||
                        !s1.get( i ).getType().equals( s2.get( i ).getType() ) ||
                        s1.get( i ).getActive() != s2.get( i ).getActive() ) {
                    res = false;
                }
            }
        }
        return res;
    }
    /**
     * Compares two subscription objects based on chosen field values.
     *
     * @param s1 First subscription object
     * @param s2 Second subscription object
     * @return True if field values are equal
     */
    public boolean compareSubscriptions( Subscription s1, Subscription s2 ) {
        boolean res = true;
        if( !s1.getChannelName().equals( s2.getChannelName() ) ||
                !s1.getNodeAddress().equals( s2.getNodeAddress() ) ||
                !s1.getOperatorName().equals( s2.getOperatorName() ) ||
                !s1.getType().equals( s2.getType() ) || s1.getActive() != s2.getActive() ) {
            res = false;
        }
        return res;
    }
}
//--------------------------------------------------------------------------------------------------
