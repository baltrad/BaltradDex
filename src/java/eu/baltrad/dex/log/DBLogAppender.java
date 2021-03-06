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
 ******************************************************************************/

package eu.baltrad.dex.log;

import eu.baltrad.dex.log.model.impl.LogEntry;
import eu.baltrad.dex.log.manager.impl.LogManager;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Implements custom log message appender.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class DBLogAppender extends AppenderSkeleton {
    
    private static LogManager logManager = null;
    
    /**
     * Constructor. 
     */
    public DBLogAppender() {}
    
    /**
     * @param manager 
     */
    public synchronized void setLogManager(LogManager manager) {
	    if (logManager == null) {
	        logManager = manager;
        }
    }       
    
    public boolean requiresLayout() {
        return false;
    }
    
    public void close() {}

    /**
     * Custom appender implementation.
     * @param event Logging event
     */
    public void append(LoggingEvent event) {
        if (logManager != null) {
            logManager.store(new LogEntry(event.getTimeStamp(),
                event.getLoggerName(), event.getLevel().toString(), 
                event.getRenderedMessage()));
        }
    }
    
}
