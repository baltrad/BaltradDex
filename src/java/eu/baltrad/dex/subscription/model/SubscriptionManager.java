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
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * Subscription manager inplementing subscription handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public SubscriptionManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all existing subscriptions.
     *
     * @return List of all existing subscriptions
     */
    public List<Subscription> get() {
        Connection conn = null;
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_subscriptions;";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int subId = resultSet.getInt("id");
                Timestamp timeStamp = resultSet.getTimestamp("timestamp");
                String userName = resultSet.getString("user_name");
                String dataSourceName = resultSet.getString("data_source_name");
                String operator = resultSet.getString("operator_name");
                String type = resultSet.getString("type");
                boolean active = resultSet.getBoolean("active");
                boolean synkronized = resultSet.getBoolean("synkronized");
                String nodeAddress = resultSet.getString("node_address");
                Subscription sub = new Subscription(subId, timeStamp, userName, dataSourceName,
                        operator, type, active, synkronized, nodeAddress);
                subscriptions.add(sub);
            }
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to select subscriptions", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return subscriptions;
    } 
    /**
     * Gets subsciptions by type.
     *
     * @param type Subscription type
     * @return List of subscriptions of a given type
     */
    public List<Subscription> get(String type) {
        Connection conn = null;
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_subscriptions WHERE type = '" + type + "';";
            ResultSet resultSet = stmt.executeQuery(sql );
            while (resultSet.next()) {
                int subId = resultSet.getInt("id");
                Timestamp timeStamp = resultSet.getTimestamp("timestamp");
                String userName = resultSet.getString("user_name");
                String dataSourceName = resultSet.getString("data_source_name");
                String operator = resultSet.getString("operator_name");
                String subType = resultSet.getString("type");
                boolean active = resultSet.getBoolean("active");
                boolean synkronized = resultSet.getBoolean("synkronized");
                String nodeAddress = resultSet.getString("node_address");
                Subscription sub = new Subscription(subId, timeStamp, userName, dataSourceName,
                        operator, subType, active, synkronized, nodeAddress);
                subscriptions.add(sub);
            }
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to select subscriptions", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return subscriptions;
    }
    /**
     * Gets unique subsciption identified by data source name and subscription type.
     *
     * @param dsName Data source name
     * @param type Subscription type
     * @return Subscription object
     */
    public Subscription get(String dsName, String type) {
        Connection conn = null;
        Subscription subscription = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_subscriptions WHERE data_source_name = '" + dsName +
                    "' AND type = '" + type + "';";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int subId = resultSet.getInt("id");
                Timestamp timeStamp = resultSet.getTimestamp("timestamp");
                String userName = resultSet.getString("user_name");
                String dataSourceName = resultSet.getString("data_source_name");
                String operator = resultSet.getString("operator_name");
                String subType = resultSet.getString("type");
                boolean active = resultSet.getBoolean("active");
                boolean synkronized = resultSet.getBoolean("synkronized");
                String nodeAddress = resultSet.getString("node_address");
                subscription = new Subscription( subId, timeStamp, userName, dataSourceName,
                        operator, subType, active, synkronized, nodeAddress);
            }
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to select subscriptions", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return subscription;
    }
    /**
     * Gets unique subsciption identified by user name, data source name and subscription type.
     *
     * @param usrName User name
     * @param dsName Data source name
     * @param type Subscription type
     * @return Subscription object
     */
    public Subscription get(String usrName, String dsName, String type) {
        Connection conn = null;
        Subscription subscription = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM dex_subscriptions WHERE user_name = '" + usrName +
                    "' AND data_source_name = '" + dsName + "' AND type = '" + type + "';";
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                int subId = resultSet.getInt("id");
                Timestamp timeStamp = resultSet.getTimestamp("timestamp");
                String user = resultSet.getString("user_name");
                String dataSourceName = resultSet.getString("data_source_name");
                String operator = resultSet.getString("operator_name");
                String subType = resultSet.getString("type");
                boolean active = resultSet.getBoolean("active");
                boolean synkronized = resultSet.getBoolean("synkronized");
                String nodeAddress = resultSet.getString("node_address");
                subscription = new Subscription(subId, timeStamp, user, dataSourceName, operator,
                        subType, active, synkronized, nodeAddress);
            }
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to select subscriptions", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return subscription;
    }
    /**
     * Gets distinct field values.
     *
     * @param fieldName Name of the field the value of which will be selected
     * @param subscriptionType Subscription type
     * @return List of strings representing field values
     */
    public List<String> getDistinct(String fieldName, String type) {
        Connection conn = null;
        List<String> fieldValues = new ArrayList<String>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT DISTINCT " + fieldName +
                    " FROM dex_subscriptions WHERE type = '" + type + "';");
            while (resultSet.next()) {
                String fieldValue = resultSet.getString(fieldName);
                fieldValues.add(fieldValue);
            }
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to select subscriptions", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return fieldValues;
    }
    /**
     * Saves subscription object.
     *
     * @param sub Subscription to be saved
     * @return Number of inserted records
     * @throws Exception
     */
    public int save(Subscription sub) throws Exception {
        Connection conn = null;
        int insert = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO dex_subscriptions (timestamp, user_name, data_source_name, " +
                    "operator_name, type, active, synkronized, node_address) VALUES ('" + 
                    sub.getTimeStamp() + "', '" + sub.getUserName() + "', '" + 
                    sub.getDataSourceName() + "', '" + sub.getOperatorName() + "', '" + 
                    sub.getType() + "', " + sub.getActive() + ", " + sub.getSynkronized() + ", '" +
                    sub.getNodeAddress() + "');";
            insert = stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to save subscription", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return insert;
    }
    /**
     * Updates subscription object.
     *
     * @param dataSourceName Data source name
     * @param subscriptionType Subscription type
     * @param active Active toggle
     * @return Number of inserted records
     * @throws Exception
     */
    public int update(String dataSourceName, String subscriptionType, boolean active) 
            throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "UPDATE dex_subscriptions SET active = " + active  + " WHERE " +
                    "data_source_name = '" + dataSourceName + "' AND type = '" + subscriptionType +
                    "';";
            update = stmt.executeUpdate(sql) ;
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to update subscription", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
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
    public int delete(int id) {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_subscriptions WHERE id = " + id + ";";
            delete = stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to delete subscription", e);
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return delete;
    }
    /**
     * Deletes subscription identified by a given data source name and type.
     *
     * @param dataSourceName Name of subscribed data source
     * @param subscriptionType Subscription type
     * @return Number of deleted records
     * @throws Exception
     */
    public int delete(String dataSourceName, String subscriptionType)
            throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_subscriptions WHERE data_source_name = '" +
                    dataSourceName + "' AND type = '" + subscriptionType + "';";
            delete = stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            log.error("Failed to delete subscription", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
        }
        return delete;
    }
    /**
     * Deletes subscription identified by a given user name, channel name and type.
     *
     * @param userName Name of the subscriber
     * @param dataSourceName Name of subscribed data source
     * @param subscriptionType Subscription type
     * @return Number of deleted records
     * @throws Exception
     */
    public int delete(String userName, String dataSourceName, String subscriptionType) 
            throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_subscriptions WHERE user_name = '" + userName + 
                    "' AND data_source_name = '" + dataSourceName + "' AND type = '" +
                    subscriptionType + "';";
            delete = stmt.executeUpdate(sql);
            stmt.close();
        } catch(Exception e) {
            log.error("Failed to delete subscription", e);
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection(conn);
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
    public boolean compare(List<Subscription> s1, List<Subscription> s2) {
        boolean res = true;
        try {
            if (s1.size() != s2.size()) {
                res = false;
            } else {
                for (int i = 0; i < s1.size(); i++) {
                    if(!s1.get(i).getDataSourceName().equals(s2.get(i).getDataSourceName()) ||
                            !s1.get(i).getNodeAddress().equals(s2.get(i).getNodeAddress()) ||
                            !s1.get(i).getOperatorName().equals(s2.get(i).getOperatorName()) ||
                            !s1.get(i).getType().equals(s2.get(i).getType()) ||
                            s1.get(i).getActive() != s2.get(i).getActive()) {
                        res = false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to compare subscriptions lists", e);
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
    public boolean compare(Subscription s1, Subscription s2) {
        boolean res = true;
        try {
            if (!s1.getDataSourceName().equals(s2.getDataSourceName()) ||
                    !s1.getNodeAddress().equals(s2.getNodeAddress()) ||
                    !s1.getOperatorName().equals(s2.getOperatorName()) ||
                    !s1.getType().equals( s2.getType()) || s1.getActive() != s2.getActive()) {
                res = false;
            }
        } catch(Exception e) {
            log.error("Failed to compare subscriptions", e);
        }
        return res;
    }
}
//--------------------------------------------------------------------------------------------------
