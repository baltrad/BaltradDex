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

package eu.baltrad.dex.registry.model;

/**
 * Delivery register entry interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
public interface IRegistryEntry {

    /** Delivery success */
    public static final String SUCCESS = "SUCCESS";
    /** Delivery failure */
    public static final String FAILURE = "FAILURE";

    public int getId();
    
    public void setId(int id);
    
    public long getTimeStamp();
    
    public void setTimestamp(long timeStamp);
    
    public String getDateTime();
    
    public void setDateTime(String dateTime);
    
    public String getUuid();
    
    public void setUuid(String uuid);
    
    public String getStatus();
    
    public void setStatus(String status);
    
    public String getUser();
    
    public void setUser(String user);
    
}
