/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

import java.util.Date;
/**
 * Class implements data delivery register entry.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
public class RegistryEntry implements IRegistryEntry {
    
    private int id;
    private int userId;
    private int dataSourceId;
    private long timeStamp;
    private Date date;
    private String type;
    private String uuid;
    private String userName;
    private boolean status;
    
    /**
     * Default constructor.
     */
    public RegistryEntry() {}
    
    /**
     * Constructor.
     * @param id Record id
     * @param userId User id
     * @param dataSourceId Data source id
     * @param type Entry type
     * @param timeStamp Timestamp 
     * @param uuid File's UUID
     * @param userName User name
     * @param status Delivery status
     */
    public RegistryEntry(int id, int userId, int dataSourceId, long timeStamp, 
            String type, String uuid, String userName, boolean status) {
        this.id = id;
        this.userId = userId;
        this.dataSourceId = dataSourceId;
        this.timeStamp = timeStamp;
        this.date = new Date(timeStamp);
        this.type = type;
        this.uuid = uuid;
        this.userName = userName;
        this.status = status;
    }
    
    /**
     * Constructor.
     * @param userId User id
     * @param dataSourceId Data source id
     * @param type Entry type
     * @param timeStamp Timestamp 
     * @param uuid File's UUID
     * @param userName User name
     * @param status Delivery status
     */
    public RegistryEntry(int userId, int dataSourceId, long timeStamp, 
            String type, String uuid, String userName, boolean status) {
        this.userId = userId;
        this.dataSourceId = dataSourceId;
        this.timeStamp = timeStamp;
        this.date = new Date(timeStamp);
        this.type = type;
        this.uuid = uuid;
        this.userName = userName;
        this.status = status;
    }
    
    /**
     * Constructor.
     * @param id Record id
     * @param type Entry type
     * @param timeStamp Timestamp 
     * @param uuid File's UUID
     * @param userName User name
     * @param status Delivery status
     */
    public RegistryEntry(int id, long timeStamp, String type, String uuid, 
            String userName, boolean status) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.date = new Date(timeStamp);
        this.type = type;
        this.uuid = uuid;
        this.userName = userName;
        this.status = status;
    }
    
    /**
     * Compares this object with another.
     * @param obj Object to compare with
     * @return True is tested parameters are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        RegistryEntry entry = (RegistryEntry) obj; 
        return this.getUserId() == entry.getUserId() && 
               this.getDataSourceId() == entry.getDataSourceId() &&
               this.getType() != null && 
               this.getType().equals(entry.getType()) &&
               this.getUuid() != null &&
               this.getUuid().equals(entry.getUuid()) && 
               this.getUserName() != null && 
               this.getUserName().equals(entry.getUserName());
    }
    
    /**
     * Generate hash code.
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int prime = 7;
        int result = 1;
        result = prime * result + this.getId(); 
        result = prime * result + this.getUserId(); 
        result = prime * result + this.getDataSourceId();
        result = prime * result + ((this.getDate() == null) ? 
                0 : this.getDate().hashCode());
        result = prime * result + ((this.getType() == null) ? 
                0 : this.getType().hashCode());
        result = prime * result + ((this.getUuid() == null) ? 
                0 : this.getUuid().hashCode());
        result = prime * result + ((this.getUserName() == null) ? 
                0 : this.getUserName().hashCode());
        return result;
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
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the dataSourceId
     */
    public int getDataSourceId() {
        return dataSourceId;
    }

    /**
     * @param dataSourceId the dataSourceId to set
     */
    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
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
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the status
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(boolean status) {
        this.status = status;
    }
    
}

