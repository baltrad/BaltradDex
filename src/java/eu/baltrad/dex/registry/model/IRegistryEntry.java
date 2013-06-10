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

package eu.baltrad.dex.registry.model;

/**
 * Delivery register entry interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
public interface IRegistryEntry {

    /** Upload entry */
    public static final String UPLOAD = "upload";
    /** Download entry */
    public static final String DOWNLOAD = "download";

    public int getId();
    
    public void setId(int id);
    
    public int getUserId();
    
    public void setUserId(int userId);
    
    public int getDataSourceId();
    
    public void setDataSourceId(int dataSourceId);
    
    public long getTimeStamp();
    
    public void setTimestamp(long timeStamp);
    
    public String getType();
    
    public void setType(String type);
    
    public String getUuid();
    
    public void setUuid(String uuid);
    
    public String getUserName();
    
    public void setUserName(String userName);
    
    public boolean getStatus();
    
    public void setStatus(boolean status);
    
}
