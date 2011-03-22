/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

/**
 * Class implements data channel subscription object.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class Subscription implements Serializable, Comparable< Subscription > {
//---------------------------------------------------------------------------------------- Constants
    public static final String LOCAL_SUBSCRIPTION = "local";
    public static final String REMOTE_SUBSCRIPTION = "remote";
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String userName;
    private String channelName;
    private String nodeAddress;
    private String operatorName;
    private String type;
    private boolean active;
    private boolean synkronized;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Subscription() {}
    /**
     * Constructor creating new subscription object with given field values.
     *
     * @param userName User name
     * @param channelName Channel name
     * @param nodeAddress Node address
     * @param operatorName Operator name
     * @param type Subscription type
     * @param active Subscription activation toggle
     * @param synkronized Synchronization toggle
     */
    public Subscription( String userName, String channelName, String nodeAddress,
            String operatorName, String type, boolean active, boolean synkronized ) {
        this.userName = userName;
        this.channelName = channelName;
        this.nodeAddress = nodeAddress;
        this.operatorName = operatorName;
        this.type = type;
        this.active = active;
        this.synkronized = synkronized;
    }
    /**
     * Constructor creating new subscription object with given field values.
     *
     * @param id Subscription ID
     * @param userName User name
     * @param channelName Channel name
     * @param nodeAddress Node address
     * @param operatorName Operator name
     * @param type Subscription type
     * @param active Subscription activation toggle
     * @param synkronized Synchronization toggle
     */
    public Subscription( int id, String userName, String channelName, String nodeAddress,
            String operatorName, String type, boolean active, boolean synkronized ) {
        this.id = id;
        this.userName = userName;
        this.channelName = channelName;
        this.nodeAddress = nodeAddress;
        this.operatorName = operatorName;
        this.type = type;
        this.active = active;
        this.synkronized = synkronized;
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
     * Gets channel name.
     *
     * @return Channel name
     */
    public String getChannelName() { return channelName; }
    /**
     * Sets channel name.
     *
     * @param channelName Channel name
     */
    public void setChannelName( String channelName ) { this.channelName = channelName; }
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
     * on channel name.
     *
     * @param s Subscription
     * @return 0 if objects are equal
     */
    public int compareTo( Subscription s ) {
        return getChannelName().compareTo( s.getChannelName() );
    }
}
//--------------------------------------------------------------------------------------------------