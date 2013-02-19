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

package eu.baltrad.dex.net.model;

import java.util.Date;

/**
 * Baltrad subscription interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public interface ISubscription {
    
    /** Subscription types */
    public static final String LOCAL = "local";
    public static final String PEER = "peer";
    
    public int getId();
    
    public void setId(int id);
    
    public Date getDate();
    
    public void setDate(Date date);
    
    public String getType();
    
    public void setType(String type);
    
    public String getOperator();

    public void setOperator(String operator);
    
    public String getUser();

    public void setUser(String user);
    
    public String getDataSource();
    
    public void setDataSource(String dataSource);
        
    public boolean isActive();
    
    public void setActive(boolean active);
    
    public boolean isSyncronized();
    
    public void setSyncronized(boolean sync);
    
}
