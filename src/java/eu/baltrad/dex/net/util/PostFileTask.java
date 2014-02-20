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

package eu.baltrad.dex.net.util;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.model.User;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

/**
 * Implements data delivery task executed in a separate thread.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.0
 */
public class PostFileTask implements Runnable {
    
    protected IRegistryManager registryManager;
    protected ISubscriptionManager subscriptionManager;
    protected INodeStatusManager nodeStatusManager;
    protected Logger log;
    protected IHttpClientUtil httpClient;
    protected HttpUriRequest request;
    protected String uuid;
    protected User user;
    protected DataSource dataSource;

    public PostFileTask() {}
    
    /**
     * Constructor.
     * @param registryManager Delivery registry manager
     * @param subscriptionManager Subscription manager
     * @param nodeStatusManager Node status manager
     * @param request Data delivery request
     * @param uuid File entry identifier string
     * @param user Recipient
     * @param dataSource Data source
     * @param connTimeout Connection timeout
     * @param soTimeout Socket timeout
     */
    public PostFileTask(IRegistryManager registryManager, 
            ISubscriptionManager subscriptionManager, 
            INodeStatusManager nodeStatusManager, HttpUriRequest request, 
            String uuid, User user, DataSource dataSource,
            int connTimeout, int soTimeout) {
        this.log = Logger.getLogger("DEX");
        this.registryManager = registryManager;
        this.subscriptionManager = subscriptionManager;
        this.nodeStatusManager = nodeStatusManager;
        this.request = request;
        this.uuid = uuid;
        this.user = user;
        this.dataSource = dataSource;
        this.httpClient = new HttpClientUtil(connTimeout, soTimeout);
    }
    
    /**
     * Implements Runnable interface. Runs data delivery task in a 
     * separate thread.
     */
    public void run() {
        try {
            Subscription s = subscriptionManager.load(Subscription.PEER, 
                    user.getName(), dataSource.getName());
            int subscriptionId = s.getId();
            Status status = nodeStatusManager.load(subscriptionId);
            try {
                HttpResponse response = httpClient.post(request);
                if (response.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_OK) {
                    RegistryEntry entry = new RegistryEntry(
                            user.getId(), dataSource.getId(), 
                            System.currentTimeMillis(), RegistryEntry.UPLOAD,
                            uuid, user.getName(), true);
                    log.info("File " + uuid + " sent to user " + 
                            user.getName());
                    registryManager.store(entry);
                    // update status
                    status.incrementUploads();
                    nodeStatusManager.update(status, subscriptionId);
                } else if (response.getStatusLine().getStatusCode() ==
                        HttpServletResponse.SC_FORBIDDEN) {
                    // remove invalid subscription
                    subscriptionManager.delete(subscriptionId);
                    // remove respective status
                    nodeStatusManager.delete(subscriptionId);
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
                            RegistryEntry entry = new RegistryEntry(
                                user.getId(), dataSource.getId(), 
                                System.currentTimeMillis(), 
                                RegistryEntry.UPLOAD, uuid, user.getName(), 
                                true);
                            log.info("File " + uuid + " sent to user " 
                                    + user.getName());
                            registryManager.store(entry);
                            // update status
                            status.incrementUploads();
                            nodeStatusManager.update(status, subscriptionId);
                        } else {
                            RegistryEntry entry = new RegistryEntry(
                                user.getId(), dataSource.getId(), 
                                System.currentTimeMillis(), 
                                RegistryEntry.UPLOAD, uuid, user.getName(), 
                                false);
                            log.error("Failed to send file " + uuid + " to user " 
                                    + user.getName() + ": " + 
                                    response.getStatusLine().getStatusCode() 
                                    + " - " + response.getStatusLine()
                                        .getReasonPhrase());
                            registryManager.store(entry);
                            // update status
                            status.incrementUploadFailures();
                            nodeStatusManager.update(status, subscriptionId);
                        }
                    }    
                }
            } finally {
                httpClient.shutdown();
            }
        } catch (Exception e) {
            log.error("Problems encountered while sending file " + uuid + 
                    " to user " + user.getName() + ": " + e.getMessage(), e);
        }
    }
}

