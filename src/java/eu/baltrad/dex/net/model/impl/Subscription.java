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

package eu.baltrad.dex.net.model.impl;

import eu.baltrad.dex.net.model.ISubscription;

import java.io.Serializable;

import java.util.Date;

/**
 * Class implements data channel subscription object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class Subscription implements ISubscription, Serializable {
    
    private int id;
    private Date date;
    private String type;
    private String operator;
    private String user;
    private String dataSource;
    private boolean active;
    private boolean sync;
    
    /**
     * Constructor.
     */
    public Subscription() {}
    
    /**
     * Constructor.
     * @param id Record id
     * @param timestamp Timestamp
     * @param type Subscription type
     * @param operator Operator's name 
     * @param user User's name 
     * @param dataSource Data source name
     * @param active Active toggle
     * @param sync Synchronized toggle 
     */
    public Subscription(int id, long timeStamp, String type, 
            String operator, String user, String dataSource, 
            boolean active, boolean sync) {
        this.id = id;
        this.date = new Date(timeStamp);
        this.type = type;
        this.operator = operator;
        this.user = user;
        this.dataSource = dataSource;
        this.active = active;
        this.sync = sync;
    } 
    
    /**
     * Constructor.
     * @param timeStamp Timestamp
     * @param type Subscription type
     * @param operator Operator's name
     * @param user User's name 
     * @param dataSource Data source name
     * @param active Active toggle
     * @param sync Synchronized toggle 
     */
    public Subscription(long timeStamp, String type, String operator, 
            String user, String dataSource, boolean active, boolean sync) {
        this.date = new Date(timeStamp);
        this.type = type;
        this.operator = operator;
        this.user = user;
        this.dataSource = dataSource;
        this.active = active;
        this.sync = sync;
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
     * * @return Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date Date to set
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
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
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
    
    /**
     * @return the dataSource
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the sync
     */
    public boolean isSyncronized() {
        return sync;
    }

    /**
     * @param sync the sync to set
     */
    public void setSyncronized(boolean sync) {
        this.sync = sync;
    }
   
}

