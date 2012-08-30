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

import java.util.List;

/**
 * Message logger interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.6.6
 */
public interface ILogManager {
    
    public long count();
    
    public List<LogEntry> load();
    
    public List<LogEntry> load(int limit); 
    
    public List<LogEntry> load(int offset, int limit);
    
    public int store(LogEntry entry);
    
    public int storeNoId(LogEntry entry);
    
    public int delete();
    
    public void setTrimmer(int limit);
    
    public void setTrimmer(int days, int hours, int minutes);
    
    public void removeTrimmer(String name);
    
}
