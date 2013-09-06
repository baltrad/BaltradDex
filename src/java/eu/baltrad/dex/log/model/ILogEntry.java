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

package eu.baltrad.dex.log.model;

import java.util.Date;

/**
 * Log entry interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7
 * @since 1.7
 */
public interface ILogEntry {
    
    /* log levels */
    public static final String LEVEL_INFO = "INFO";
    public static final String LEVEL_WARNING = "WARN";
    public static final String LEVEL_ERROR = "ERROR";
    public static final String LEVEL_STICKY = "STICKY";
    
    /**
     * Get record id.
     * @return Record id 
     */
    public int getId();
    
    /**
     * Set record id.
     * @param id Id to set
     */
    public void setId(int id);
    /**
     * Get timestamp.
     * @return Timestamp
     */
    public long getTimeStamp();
    
    /**
     * Set timestamp.
     * @param timeStamp Timestamp to set 
     */
    public void setTimeStamp(long timeStamp );
    
    /**
     * Get date.
     * @return Date
     */
    public Date getDate();
    /**
     * Set date.
     * @param date Date to set
     */
    public void setDate(Date date);
    
    /**
     * Get logger name.
     * @return Logger name
     */
    public String getLogger();
    
    /**
     * Set logger name.
     * @param logger Logger name to set
     */
    public void setLogger(String logger);
    
    /**
     * Get message level.
     * @return Message level
     */
    public String getLevel();
    
    /**
     * Set message level.
     * @param level Message level to set
     */
    public void setLevel(String level);
   
    /**
     * Get message body. 
     * @return Message body
     */
    public String getMessage();
    
    /**
     * Set message body.
     * @param message Message body to set
     */
    public void setMessage(String message);
    
}
