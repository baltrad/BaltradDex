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

package eu.baltrad.dex.log.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Class implements system message object.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class LogEntry implements Serializable {
//---------------------------------------------------------------------------------------- Constants
    /** Date format string */
    private final static String DATE_FORMAT = "yyyy/MM/dd";
    /** Time format string */
    private final static String TIME_FORMAT = "HH:mm:ss";
//---------------------------------------------------------------------------------------- Variables
    /** Log entry ID */
    private int id;
    /** Timestamp */
    private Timestamp timeStamp;
    /** Auxiliary variable storing date as string */
    private String dateStr;
    /** Auxiliary variable storing time as string */
    private String timeStr;
    /** Log entry type */
    private String type;
    /** Actual message text */
    private String message;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor
     */
    public LogEntry() {}
    /**
     * Constructor initializes log entry object.
     *
     * @param timeStamp Time stamp
     * @param type Type of a log entry
     * @param message Actual log entry text
     */
    public LogEntry( Timestamp timeStamp, String type, String message ) {
        this.timeStamp = timeStamp;
        this.type = type;
        this.message = message;
        SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
        SimpleDateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT );
        this.dateStr = dateFormat.format( timeStamp );
        this.timeStr = timeFormat.format( timeStamp );
    }
    /**
     * Constructor initializes log entry object.
     *
     * @param time Current time in milliseconds
     * @param type Type of a log entry
     * @param message Actual log entry text
     */
    public LogEntry( long time, String type, String message ) {
        this.timeStamp = new Timestamp( time );
        this.type = type;
        this.message = message;
        SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
        SimpleDateFormat timeFormat = new SimpleDateFormat( TIME_FORMAT );
        this.dateStr = dateFormat.format( timeStamp );
        this.timeStr = timeFormat.format( timeStamp );
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
     * Gets timestamp of a log entry.
     *
     * @return Timestamp of a log entry
     */
    public Timestamp getTimeStamp() { return timeStamp; }
    /**
     * Sets timestamp of a log entry.
     *
     * @param Timestamp of a log entry.
     */
    public void setTimeStamp( Timestamp timeStamp ) { this.timeStamp = timeStamp; }
    /**
     * Method gets the type of log entry.
     *
     * @return Log entry type
     */
    public String getType() { return type; }
    /**
     * Method sets the type of log entry.
     *
     * @param type Log entry type
     */
    public void setType( String type ) { this.type = type; }
    /**
     * Method gets log entry message.
     *
     * @return Log entry message
     */
    public String getMessage() { return message; }
    /**
     * Method sets log entry message.
     *
     * @param message Log entry message
     */
    public void setMessage( String message ) { this.message = message; }
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
}
//--------------------------------------------------------------------------------------------------
