/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.registry.model.impl;

import eu.baltrad.dex.registry.model.IRegistryEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Class implements data delivery register entry.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
public class RegistryEntry implements IRegistryEntry {
    
    private final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    private int id;
    private long timeStamp;
    private String dateTime;
    private String uuid;
    private String status;
    private String user;
    private DateFormat format = new SimpleDateFormat(DATE_FORMAT);
    
    /**
     * Default constructor.
     */
    public RegistryEntry() {}
    
    /**
     * Constructor.
     * @param id Record id
     * @param timestamp Timestamp 
     * @param uuid File's UUID
     * @param status Delivery status
     * @param user User name
     */
    public RegistryEntry(int id, long timeStamp, String uuid,
            String status, String user) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.dateTime = format.format(new Date(timeStamp));
        this.uuid = uuid;
        this.status = status;
        this.user = user;
    }
    
    /**
     * Constructor.
     * @param time Time in milliseconds
     * @param uuid File's UUID
     * @param status Delivery status
     * @param user User name
     */
    public RegistryEntry(long timeStamp, String uuid, String status, 
            String user) {
        this.timeStamp = timeStamp;
        this.dateTime = format.format(new Date(timeStamp));
        this.uuid = uuid;
        this.status = status;
        this.user = user;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the timestamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    
    /**
     * Get formatted date and time.
     * @return Formatted date and time
     */
    public String getDateTime() {
        return dateTime;
    }

    /**
     * Set formatted date and time.
     * @param dateTime Formatted date and time
     */
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }
    
}

