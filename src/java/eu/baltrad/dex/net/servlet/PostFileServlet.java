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

package eu.baltrad.dex.net.servlet;

import eu.baltrad.dex.net.model.NodeRequest;
import eu.baltrad.dex.net.model.NodeResponse;
import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.net.model.ISubscriptionManager;
import eu.baltrad.dex.db.model.IBltFileManager;
import eu.baltrad.dex.registry.model.IDeliveryRegistryManager;
import eu.baltrad.dex.user.model.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.net.util.RequestFactory;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.db.DuplicateEntry;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.util.FileEntryNamer;
import eu.baltrad.bdb.oh5.MetadataMatcher;

import eu.baltrad.beast.message.mo.BltDataMessage;
import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.db.IFilter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.net.URI;
import java.util.UUID;

/**
 * Receives and handles post file requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class PostFileServlet extends HttpServlet {
 
    private static final String PF_UNAUTHORIZED_REQUEST_KEY =
            "postfile.server.unauthorized_request";
    private static final String PF_INTERNAL_SERVER_ERROR_KEY = 
            "postfile.server.internal_server_error";
    private static final String PF_DUPLICATE_ENTRY_ERROR_KEY = 
            "postfile.server.duplicate_entry_error";
    private static final String PF_DATABASE_ERROR_KEY = 
            "postfile.server.database_error";
    private static final String PF_GENERIC_POST_FILE_ERROR_KEY = 
            "postfile.server.generic_post_file_error";
    
    private Authenticator authenticator;
    private FileCatalog catalog;
    private FileEntryNamer namer;
    private MetadataMatcher matcher;
    private IBltMessageManager messageManager;
    private IBltFileManager fileManager;
    private IDeliveryRegistryManager registryManager;
    private IUserManager userManager;
    private IHttpClientUtil httpClient;
    private FramePublisherManager framePublisherManager;
    private ISubscriptionManager subscriptionManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected String nodeName;
    protected String nodeAddress;
    
    /**
     * Default constructor.
     */
    public PostFileServlet() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Initializes servlet with current configuration.
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir());
        this.setHttpClient(new HttpClientUtil(
                 InitAppUtil.getConf().getConnTimeout(), 
                 InitAppUtil.getConf().getSoTimeout()));
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
    }
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/post_file.htm")
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        initConfiguration();
        HttpSession session = request.getSession(true);
        doPost(request, response);
        return new ModelAndView();
    }
    
    /**
     * Actual mathod processing requests.
     * @param request Http servlet request
     * @param response Http servlet response 
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    {
        NodeRequest req = new NodeRequest(request);
        NodeResponse res = new NodeResponse(response);
        try {
            if (authenticator.authenticate(req.getMessage(), 
                    req.getSignature(), req.getNodeName())) {
                log.info("New data file received from " + req.getNodeName());
                FileEntry entry = catalog.store(req.getInputStream());
                if (entry != null) {
                    String name = namer.name(entry);
                    UUID uuid = entry.getUuid();
                    log.info(name + " stored with UUID " + uuid.toString());
                    BltDataMessage msg = new BltDataMessage();
                    msg.setFileEntry(entry);
                    messageManager.manage(msg);
                    List<Subscription> uploads = subscriptionManager.load(
                            Subscription.SUBSCRIPTION_UPLOAD);
                    for (Subscription s : uploads) {
                        IFilter filter = fileManager
                                .getFilter(s.getDataSourceName());                        
                        boolean match = matcher.match(entry.getMetadata(), 
                                filter.getExpression());
                        User receiver = userManager.getByName(s.getUserName());
                        if (match && !registryManager.entryExists(
                                receiver.getId(), uuid.toString())) {
                            RequestFactory requestFactory = 
                                new DefaultRequestFactory(
                                    URI.create(receiver.getNodeAddress()));
                            HttpUriRequest deliveryRequest = requestFactory
                                .createPostFileRequest(nodeName, 
                                        nodeAddress, entry.getContentStream());
                            authenticator.addCredentials(deliveryRequest, 
                                    nodeName);
                            PostFileTask task = new PostFileTask(httpClient, 
                                    deliveryRequest, entry.getUuid().toString(), 
                                    receiver);
                            framePublisherManager.getFramePublisher(
                                    receiver.getName()).addTask(task);   
                        }  
                    }
                } else {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND, 
                    messages.getMessage(PF_GENERIC_POST_FILE_ERROR_KEY));
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                    messages.getMessage(PF_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (DuplicateEntry e) {
            res.setStatus(HttpServletResponse.SC_CONFLICT,
                    messages.getMessage(PF_DUPLICATE_ENTRY_ERROR_KEY));
        } catch (DatabaseError e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(PF_DATABASE_ERROR_KEY));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(PF_INTERNAL_SERVER_ERROR_KEY));
        }
    }

     /**
     * @param catalog the catalog to set
     */
    @Autowired
    public void setCatalog(FileCatalog catalog) {
        this.catalog = catalog;
    }
    
    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }

    /**
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(ISubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @param namer the namer to set
     */
    @Autowired
    public void setNamer(FileEntryNamer namer) {
        this.namer = namer;
    }

    /**
     * @param messageManager the messageManager to set
     */
    @Autowired
    public void setMessageManager(IBltMessageManager messageManager) {
        this.messageManager = messageManager;
    }

    /**
     * @param matcher the matcher to set
     */
    @Autowired
    public void setMatcher(MetadataMatcher matcher) {
        this.matcher = matcher;
    }

    /**
     * @param fileManager the fileManager to set
     */
    @Autowired
    public void setFileManager(IBltFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * @param registryManager the registryManager to set
     */
    @Autowired
    public void setRegistryManager(IDeliveryRegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * @param framePublisherManager the framePublisherManager to set
     */
    @Autowired
    public void setFramePublisherManager(FramePublisherManager 
            framePublisherManager) {
        this.framePublisherManager = framePublisherManager;
    }

    /**
     * @param httpClient the httpClient to set
     */
    public void setHttpClient(IHttpClientUtil httpClient) {
        this.httpClient = httpClient;
    }
    
}