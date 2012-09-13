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

package eu.baltrad.dex.net.util;

import eu.baltrad.dex.registry.model.IRegistryManager;
import eu.baltrad.dex.registry.model.RegistryEntry;
import eu.baltrad.dex.log.util.MessageLogger;
import eu.baltrad.dex.user.model.User;

import org.apache.log4j.Logger;
import org.apache.http.HttpResponse;

import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Implements data delivery task executed in a separate thread.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.0
 */
public class PostFileTask implements Runnable {
    
    private IRegistryManager manager;
    private Logger log;
    private IHttpClientUtil httpClient;
    private HttpUriRequest request;
    private String uuid;
    private User user;

    /**
     * Constructor.
     * @param httpClient Http client
     * @param manager Delivery registry manager
     * @param request Data delivery request
     * @param uuid File entry identifier string
     * @param user Recipient
     */
    public PostFileTask(IHttpClientUtil httpClient, IRegistryManager manager,
            HttpUriRequest request, String uuid, User user) {
        this.httpClient = httpClient;
        this.manager = manager;
        this.request = request;
        this.uuid = uuid;
        this.user = user;
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Implements Runnable interface. Runs data delivery task in a 
     * separate thread.
     */
    public void run() {
        try {
            HttpResponse response = httpClient.post(request);
            if (response.getStatusLine().getStatusCode() == 
                    HttpServletResponse.SC_OK) {
                RegistryEntry entry = new RegistryEntry(user.getId(), uuid, 
                        user.getName(), new Date(), 
                        RegistryEntry.MSG_SUCCESS);
                log.info("File " + uuid + " sent to user " + user.getName());
                manager.storeNoId(entry);
            } else {
                // don't retry if file is already delivered 
                if (response.getStatusLine().getStatusCode() 
                        != HttpServletResponse.SC_CONFLICT) {
                    boolean success = false;
                    for (int i = 0; i < 3; i++) {
                        response = httpClient.post(request);
                        if (response.getStatusLine().getStatusCode() ==
                                HttpServletResponse.SC_OK) {
                            success = true;
                            break;
                        }
                    }
                    if (success) {
                        RegistryEntry entry = new RegistryEntry(user.getId(), 
                                uuid, user.getName(), new Date(), 
                                RegistryEntry.MSG_SUCCESS);
                        log.info("File " + uuid + " sent to user " 
                                + user.getName());
                        manager.storeNoId(entry);
                    } else {
                        RegistryEntry entry = new RegistryEntry(user.getId(), 
                                uuid, user.getName(), new Date(), 
                                RegistryEntry.MSG_FAILURE);
                        log.error("Failed to deliver file " + uuid + " to user " 
                                + user.getName());
                        manager.storeNoId(entry);
                    }
                }
            }    
        } catch (Exception e) {
            log.error("Failed to deliver file " + uuid + " to user " 
                    + user.getName(), e);
        }
    }
}

