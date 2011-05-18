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

import java.io.Serializable;
import java.text.SimpleDateFormat;

import java.sql.Timestamp;

/**
 * Class implements data channel subscription object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class Subscription implements Serializable, Comparable< Subscription > {
//---------------------------------------------------------------------------------------- Constants
    /** Local subscriptions key */
    public static final String LOCAL_SUBSCRIPTION = "local";
    /** Remote subscriptions key */
    public static final String REMOTE_SUBSCRIPTION = "remote";
    /** Date format string */
    private final static String DATE_FORMAT = "yyyy/MM/dd";
    /** Time format string */
    private final static String TIME_FORMAT = "HH:mm:ss";
//---------------------------------------------------------------------------------------- Variables
    /** Subscription ID */
    private int id;
    /** Timestamp */
    private Timestamp timeStamp;
    /** Auxiliary variable storing date as string */
    private String dateStr;
    /** Auxiliary variable storing time as string */
    private String timeStr;
    /** Subscriber's name */
    private String userName;
    /** Data source name */
    private String dataSourceName;
    /** Node's address */
    private String nodeAddress;
    /** Node's operator name */
    private String operatorName;
    /** Subscription type */
    private String type;
    /** Subscription's state determines whether subscription is active or deactivated */
    private boolean active;
    /** Subscription's state determines whether subscription is synchronized or unsynchronized */
    private boolean synkronized;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Subscription() {}
    /**
     * Constructor creating new subscription object with given field values.
     *
     * @param time Current time in milliseconds
     * @param userName User name
     * @param dataSourceName Data source name
     * @param nodeAddress Node address
     * @param operatorName Operator name
     * @param type Subscription type
     * @param active Subscription activation toggle
     * @param synkronized Synchronization toggle
     */
    public Subscription( long time, String userName, String dataSourceName, String nodeAddress,
            String operatorName, String type, boolean active, boolean synkronized ) {
        this.timeStamp = new Timestamp( time );
        this.userName = userName;
        this.dataSourceName = dataSourceName;
        this.nodeAddress = nodeAddress;
        this.operatorName = operatorName;
        this.type = type;
        this.active = active;
        this.synkronized = synkronized;
        SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
        SimpleDateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT );
        this.dateStr = dateFormat.format( timeStamp );
        this.timeStr = timeFormat.format( timeStamp );
    }
    /**
     * Constructor creating new subscription object with given field values.
     *
     * @param id Subscription ID
     * @param time Current time in milliseconds
     * @param userName User name
     * @param dataSourceName Data source name
     * @param nodeAddress Node address
     * @param operatorName Operator name
     * @param type Subscription type
     * @param active Subscription activation toggle
     * @param synkronized Synchronization toggle
     */
    public Subscription( int id, long time, String userName, String dataSourceName,
            String nodeAddress, String operatorName, String type, boolean active,
            boolean synkronized ) {
        this.id = id;
        this.timeStamp = new Timestamp( time );
        this.userName = userName;
        this.dataSourceName = dataSourceName;
        this.nodeAddress = nodeAddress;
        this.operatorName = operatorName;
        this.type = type;
        this.active = active;
        this.synkronized = synkronized;
        SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
        SimpleDateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT );
        this.dateStr = dateFormat.format( timeStamp );
        this.timeStr = timeFormat.format( timeStamp );
    }
    /**
     * Constructor creating new subscription object with given field values.
     *
     * @param id Subscription ID
     * @param timeStamp Timestamp
     * @param userName User name
     * @param dataSourceName Data source name
     * @param nodeAddress Node address
     * @param operatorName Operator name
     * @param type Subscription type
     * @param active Subscription activation toggle
     * @param synkronized Synchronization toggle
     */
    public Subscription( int id, Timestamp timeStamp, String userName, String dataSourceName,
            String nodeAddress, String operatorName, String type, boolean active,
            boolean synkronized ) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.userName = userName;
        this.dataSourceName = dataSourceName;
        this.nodeAddress = nodeAddress;
        this.operatorName = operatorName;
        this.type = type;
        this.active = active;
        this.synkronized = synkronized;
        SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
        SimpleDateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT );
        this.dateStr = dateFormat.format( timeStamp );
        this.timeStr = timeFormat.format( timeStamp );
    }
    /**
     * Method gets subscription id.
     *
     * @return Subscription id
     */
    public int getId() { return id; }
    /**
     * Method sets subscription id.
     *
     * @param id Dubscription id
     */
    public void setId( int id ) { this.id = id; }
     /**
     * Gets subscription's timestamp.
     *
     * @return Subscription's timestamp
     */
    public Timestamp getTimeStamp() { return timeStamp; }
    /**
     * Sets subscription's timestamp.
     *
     * @param Subscription's timestamp.
     */
    public void setTimeStamp( Timestamp timeStamp ) { this.timeStamp = timeStamp; }
    /**
     * Gets date string.
     *
     * @return Date string
     */
    public String getDateStr() { return dateStr; }
    /**
     * Sets date string.
     *
     * @param dateStr Date string to set
     */
    public void setDateStr( String dateStr ) { this.dateStr = dateStr; }
    /**
     * Gets time string.
     *
     * @return Time string
     */
    public String getTimeStr() { return timeStr; }
    /**
     * Sets time string.
     *
     * @param timeStr Time string to set
     */
    public void setTimeStr( String timeStr ) { this.timeStr = timeStr; }
    /**
     * Method gets user name.
     *
     * @return User name
     */
    public String getUserName() { return userName; }
    /**
     * Method sets user name.
     *
     * @param userName User name
     */
    public void setUserName( String userName ) { this.userName = userName; }
     /**
     * Gets data source name.
     *
     * @return Data source name
     */
    public String getDataSourceName() { return dataSourceName; }
    /**
     * Sets data source name.
     *
     * @param dataSourceName Data source name
     */
    public void setDataSourceName( String dataSourceName ) { this.dataSourceName = dataSourceName; }
    /**
     * Gets subscription type
     *
     * @return Subscription type
     */
    public String getType() { return type; }
    /**
     * Sets subscription type
     * 
     * @param type Subscription type
     */
    public void setType( String type ) { this.type = type; }
    /**
     * Gets node address
     *
     * @return Node address
     */
    public String getNodeAddress() { return nodeAddress; }
    /**
     * Sets node address.
     *
     * @param nodeAddress Node address
     */
    public void setNodeAddress( String nodeAddress ) { this.nodeAddress = nodeAddress; }
    /**
     * Gets operator name.
     *
     * @return Operator name
     */
    public String getOperatorName() { return operatorName; }
    /**
     * Sets operator name.
     *
     * @param operatorName Operator name
     */
    public void setOperatorName( String operatorName ) { this.operatorName = operatorName; }
    /**
     * Method gets subscription activation toggle state.
     *
     * @return Subscription activation toggle state
     */
    public boolean getActive() { return active; }
    /**
     * Method sets subscription activation toggle state.
     *
     * @param active Subscription activation toggle state
     */
    public void setActive( boolean active ) { this.active = active; }
    /**
     * Method gets subscription synchronization toggle state.
     *
     * @return Subscription synchronization toggle state
     */
    public boolean getSynkronized() { return synkronized; }
    /**
     * Method sets subscription synchronization toggle state.
     *
     * @param synkronized Subscription synchronization toggle state
     */
    public void setSynkronized( boolean synkronized ) { this.synkronized = synkronized; }
    /**
     * Method implementing comparable interface. Sorts subscription objects based
     * on data source name.
     *
     * @param s Subscription
     * @return 0 if objects are equal
     */
    public int compareTo( Subscription s ) {
        return getDataSourceName().compareTo( s.getDataSourceName() );
    }
}
//--------------------------------------------------------------------------------------------------