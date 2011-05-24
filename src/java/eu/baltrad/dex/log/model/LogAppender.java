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

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Implements custom log message appender.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.6
 * @since 0.6.6
 */
public class LogAppender extends AppenderSkeleton {
//---------------------------------------------------------------------------------------- Variables
    /** Log manager object */
    private LogManager logManger;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     *
     * @param name Appender's name
     */
    public LogAppender( String name ) {
        setName( name );
        logManger = new LogManager();
    }
    /**
     * Determines whether this log appender requires a Layout object.
     *
     * @return True
     */
    public boolean requiresLayout() { return true; }
    /**
     * Closes this log appender.
     */
    public void close() {}
    /**
     * Custom append method used to store log entries in the database.
     * 
     * @param event Logging event
     */
    public void append( LoggingEvent event ) {
        logManger.addEntry( new LogEntry( event.getTimeStamp(), getName(),
                event.getLevel().toString(), event.getRenderedMessage() ) );

    }
}
//--------------------------------------------------------------------------------------------------
