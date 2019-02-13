/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.status.manager;

import eu.baltrad.dex.status.model.Status;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;

/**
 * Node status manager interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public interface INodeStatusManager {
    
    public List<String> loadNodeNames() throws DataAccessException;
    
    public Status load(int subscriptionId) throws DataAccessException;
    
    public List<Status> load(String peerName, String subscriptionType) 
            throws DataAccessException;
    
    public int store(Status status) throws DataAccessException;
    
    public int store(int statusId, int subscriptionId) 
            throws DataAccessException;
    
    public int update(Status status, int subscriptionId) 
            throws DataAccessException;
    
    public int delete(int subscriptionId) throws DataAccessException;
    
    
    /**
     * @return names of nodes that has stored their status
     */
    public Set<String> getRuntimeNodeNames();
    
    /**
     * @param nodeName the node name setting the runtime status
     * @param httpStatus the http status
     * @param outgoing if outgoing or incomming communication
     */
    public void setRuntimeNodeStatus(String nodeName, int httpStatus);
    
    /**
     * @param nodeName the node name that is queried for status
     * @return the status
     * @throws RuntimeException if node hasn't stored any status 
     */
    public int getRuntimeNodeStatus(String nodeName);
    
    /**
     * @param nodeName the node name that is queried for it's last updated status
     * @return the date the last update for specified node occured
     * @throws RuntimeException if node hasn't stored any status 
     */
    public Date getRuntimeNodeDate(String nodeName);
}
