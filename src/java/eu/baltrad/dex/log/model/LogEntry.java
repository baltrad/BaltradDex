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

package eu.baltrad.dex.log.model;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Class implements system message object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class LogEntry implements Serializable {
    
    private final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    /** Log entry ID */
    private int id;
    /** Current time in milliseconds */
    private long timeStamp;
    /** Date and time in a given format */
    private String dateTime;
    /** Logger that generated the message */
    private String logger;
    /** Message level: INFO, WARN, ERROR */
    private String level;
    /** Message body */
    private String message;
    /** Date format */
    private DateFormat format = new SimpleDateFormat(DATE_FORMAT);
    
    /**
     * Constructor.
     * @param id Record id
     * @param timeStamp Current time in milliseconds
     * @param logger Logger that generated the message
     * @param level Message level
     * @param message Message body
     */
    public LogEntry(int id, long timeStamp, String logger, String level, 
            String message) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.dateTime = format.format(new Date(timeStamp));
        this.logger = logger;
        this.level = level;
        this.message = message;
    }
    
    /**
     * Constructor.
     * @param timeStamp Current time in milliseconds
     * @param logger Logger that generated the message
     * @param level Message level
     * @param message Message body
     */
    public LogEntry(long timeStamp, String logger, String level, String message) {
        this.timeStamp = timeStamp;
        this.dateTime = format.format(new Date(timeStamp));
        this.logger = logger;
        this.level = level;
        this.message = message;
    }
    
    /**
     * Get record id.
     * @return Record id 
     */
    public int getId() { return id; }
    
    /**
     * Set record id.
     * @param id Id to set
     */
    public void setId( int id ) { this.id = id; }
    
    /**
     * Get timestamp.
     * @return Timestamp
     */
    public long getTimeStamp() { return timeStamp; }
    
    /**
     * Set timestamp.
     * @param timeStamp Timestamp to set 
     */
    public void setTimeStamp(long timeStamp ) { this.timeStamp = timeStamp; }
    
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
     * Get logger name.
     * @return Logger name
     */
    public String getLogger() { return logger; }
    
    /**
     * Set logger name.
     * @param logger Logger name to set
     */
    public void setLogger( String logger ) { this.logger = logger; }
    
    /**
     * Get message level.
     * @return Message level
     */
    public String getLevel() { return level; }
    
    /**
     * Set message level.
     * @param level Message level to set
     */
    public void setLevel( String level ) { this.level = level; }
   
    /**
     * Get message body. 
     * @return Message body
     */
    public String getMessage() { return message; }
    
    /**
     * Set message body.
     * @param message Message body to set
     */
    public void setMessage( String message ) { this.message = message; }
    
}
