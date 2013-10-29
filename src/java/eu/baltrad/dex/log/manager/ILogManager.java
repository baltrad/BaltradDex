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

package eu.baltrad.dex.log.manager;

import eu.baltrad.dex.log.model.impl.LogEntry;

import java.util.List;

/**
 * Message logger interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.6.6
 */
public interface ILogManager {
    
    /** Trim messages by number trigger */
    public static final String TRIM_MSG_BY_NUMBER_TG = 
            "dex_trim_messages_by_number_tg";
    /** Trim messages by date trigger */
    public static final String TRIM_MSG_BY_AGE_TG = 
            "dex_trim_messages_by_age_tg";
    /** SQL query to select sticky-level messages */
    public static final String SQL_SELECT_STICKY = "SELECT count(*) FROM " + 
            "dex_messages WHERE level = 'STICKY';";
    
    public long count();
    
    public long count(String sql);
    
    public List<LogEntry> load();
    
    public List<LogEntry> load(int limit); 
    
    public List<LogEntry> load(int offset, int limit);
    
    public List<LogEntry> load(String level);
    
    public List<LogEntry> load(String sql, int offset, int limit);
    
    public int store(LogEntry entry);
    
    public int delete();
    
    public int delete(int id);
    
    public void setTrimmer(int limit);
    
    public void setTrimmer(int days, int hours, int minutes);
    
    public void removeTrimmer(String name);
    
}
