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

package eu.baltrad.dex.log.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Class implements system message object.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class LogEntry implements Serializable, Comparable< LogEntry > {
//------------------------------------------------------------------------------------------- Fields
    // Log entry ID
    private int id;
    // Object representing current date / log entry timestamp
    private Date timeStamp;
    // Log entry type
    private String type;
    // Actual message text
    private String message;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor
     */
    public LogEntry() {}
    /**
     * Constructor initializes log entry object.
     *
     * @param timeStamp Object representing current date
     * @param type Type of a log entry
     * @param message Actual log entry text
     */
    public LogEntry( Date timeStamp, String type, String message ) {
        this.timeStamp = timeStamp;
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
     * Gets timestamp of a log entry.
     *
     * @return Timestamp of a log entry
     */
    public Date getTimeStamp() { return timeStamp; }
    /**
     * Sets timestamp of a log entry.
     *
     * @param Timestamp of a log entry.
     */
    public void setTimeStamp( Date timeStamp ) { this.timeStamp = timeStamp; }
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
     * Method implementing comparable interface. Allows to sort log entries based on date and time.
     *
     * @param Log entry to compare with current entry
     * @return 0 if objects are equal, 1 if current entry is later than compared entry, -1 otherwise
     */
    public int compareTo( LogEntry logEntry ) {
        return -this.getTimeStamp().compareTo( logEntry.getTimeStamp() );
    }
}
//--------------------------------------------------------------------------------------------------
