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

package eu.baltrad.dex.net.util.json;

import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;

import java.util.Set;
import java.util.List;

/**
 * Json utility interface.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public interface IJsonUtil {
    
    public String userAccountToJson(Account account) throws RuntimeException;
    
    public Account jsonToUserAccount(String json) throws RuntimeException;
    
    public String dataSourcesToJson(Set<DataSource> dataSources)
            throws RuntimeException;
     
    public Set<DataSource> jsonToDataSources(String json) 
            throws RuntimeException;
     
    public String subscriptionsToJson(List<Subscription> subscriptions)
            throws RuntimeException;
     
    public List<Subscription> jsonToSubscriptions(String json) 
            throws RuntimeException;
     
}
