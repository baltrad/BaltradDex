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

package eu.baltrad.dex.register.model;

import java.util.Date;

/**
 * Class implements data delivery register entry.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class DeliveryRegisterEntry {
//---------------------------------------------------------------------------------------- Constants
    public static final String MSG_SUCCESS = "SUCCESS";
    public static final String MSG_FAILURE = "FAILURE";
//---------------------------------------------------------------------------------------- Variables
    // Record ID
    private int id;
    // User ID
    private int userId;
    // File's identity string
    private String uuid;
    // Recipient / user name
    private String userName;
    // Date of delivery / delivery timestamp
    private Date timeStamp;
    // Delivery status
    private String deliveryStatus;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Default constructor
     */
    public DeliveryRegisterEntry() {}
    /**
     * Constructor sets field values.
     *
     * @param userId User id
     * @param uuid File's identity string
     * @param userName Recipient's name / user name
     * @param timeStamp Delivery timestamp
     * @param deliveryStatus Delivery status tells whether user received a delivery
     */
    public DeliveryRegisterEntry( int userId, String uuid, String userName, Date timeStamp,
            String deliveryStatus ) {
        this.userId = userId;
        this.uuid = uuid;
        this.userName = userName;
        this.timeStamp = timeStamp;
        this.deliveryStatus = deliveryStatus;
    }
    /**
     * Method returns register entry id.
     *
     * @return Register entry id
     */
    public int getId() { return id; }
    /**
     * Method sets register entry id.
     *
     * @param registerEntryID Register entry id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method returns user id.
     *
     * @return User id
     */
    public int getUserId() { return userId; }
    /**
     * Method sets user id.
     *
     * @param userId User id
     */
    public void setUserId( int userId ) { this.userId = userId; }
    /**
     * Method returns file's identity string.
     *
     * @return File's identity string
     */
    public String getUuid() { return uuid; }
    /**
     * Method sets file's identity string.
     *
     * @param uuid File's identity string
     */
    public void setUuid( String uuid ) { this.uuid = uuid; }
    /**
     * Gets user name.
     *
     * @return User name
     */
    public String getUserName() { return userName; }
    /**
     * Sets user name.
     *
     * @param userName User name
     */
    public void setUserName( String userName ) { this.userName = userName; }
    /**
     * Gets delivery timestamp.
     *
     * @return Delivery timestamp
     */
    public Date getTimeStamp() { return timeStamp; }
    /**
     * Sets delivery timestamp.
     *
     * @param timeStamp Delivery timeStamp
     */
    public void setTimeStamp( Date timeStamp ) { this.timeStamp = timeStamp; }
    /**
     * Gets delivery status.
     *
     * @return Delivery status
     */
    public String getDeliveryStatus() { return deliveryStatus; }
    /**
     * Sets delivery status.
     *
     * @param deliveryStatus Selivery status
     */
    public void setDeliveryStatus( String deliveryStatus ) { this.deliveryStatus = deliveryStatus; }
}
//--------------------------------------------------------------------------------------------------
