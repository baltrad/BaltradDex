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
import java.util.Date;

/**
 * Class implements system message object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class LogEntry implements Serializable {
    
    /** Log entry ID */
    private int id;
    /** Timestamp */
    private Date timeStamp;
    /** Determines the system that generated the message */
    private String system;
    /** Message type */
    private String type;
    /** Actual message string */
    private String message;
    
    /**
     * Default constructor.
     */
    public LogEntry() {}
    
    /**
     * Constructor.
     * @param id Record id
     * @param Timestamp Current time as timestamp
     * @param system Determines system that produced the message
     * @param type Determines type of the message
     * @param message Actual message string
     */
    public LogEntry(int id, Date timeStamp, String system, String type, 
            String message) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.system = system;
        this.type = type;
        this.message = message;
    }
    
    /**
     * Constructor.
     * @param Timestamp Current time as timestamp
     * @param system Determines system that produced the message
     * @param type Determines type of the message
     * @param message Actual message string
     */
    public LogEntry(Date timeStamp, String system, String type, 
            String message) {
        this.timeStamp = timeStamp;
        this.system = system;
        this.type = type;
        this.message = message;
    }
    /**
     * Constructor.
     * @param time Current time in milliseconds
     * @param system Determines system that produced the message
     * @param type Determines type of the message
     * @param message Actual message string
     */
    public LogEntry(long time, String system, String type, String message) {
        this.timeStamp = new Date(time);
        this.system = system;
        this.type = type;
        this.message = message;
    }
    /**
     * Method gets log entry ID.
     *
     * @return Log entry ID
     */
    public int getId() { return id; }
    /**
     * Method sets log entry ID.
     *
     * @param id Log entry ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets timestamp
     *
     * @return TimeStamp
     */
    public Date getTimeStamp() { return timeStamp; }
    /**
     * Sets timstamp
     *
     * @param timeStamp Timestamp to set
     */
    public void setTimeStamp( Date timeStamp ) { this.timeStamp = timeStamp; }
    /**
     * Gets name of the system that generated the message.
     *
     * @return System name
     */
    public String getSystem() { return system; }
    /**
     * Sets name of the system that generated the message.
     *
     * @param source the source to set
     */
    public void setSystem( String system ) { this.system = system; }
    /**
     * Gets message type
     *
     * @return Message type
     */
    public String getType() { return type; }
    /**
     * Sets message type
     *
     * @param type Message type to set
     */
    public void setType( String type ) { this.type = type; }
    /**
     * Gets message
     *
     * @return Message string
     */
    public String getMessage() { return message; }
    /**
     * Sets message
     *
     * @param Message string to set
     */
    public void setMessage( String message ) { this.message = message; }
}
