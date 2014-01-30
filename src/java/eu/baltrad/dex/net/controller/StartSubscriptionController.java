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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.request.factory.RequestFactory;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.MessageSetter;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.IFilterManager;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;

import java.util.Set;
import java.util.HashSet;

import java.io.InputStream;
import java.io.IOException;

/**
 * Controls data source subscription process.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class StartSubscriptionController implements MessageSetter {
    
    /** Initial view */
    private static final String SUBSCRIBE_VIEW = "subscription_start_status";
    /** Selected data sources model key */
    private static final String DS_SELECTED = "selected_data_sources";
    /** Message signer error key */
    private static final String PS_MESSAGE_SIGNER_ERROR_KEY = 
            "postsubscription.controller.message_signer_error";
    /** Subscription connection error key */
    private final static String PS_HTTP_CONN_ERROR_KEY = 
            "postsubscription.controller.http_connection_error";
    /** Peer account not found error key */
    private final static String PS_PEER_NOT_FOUND_ERROR_KEY =
            "postsubscription.controller.peer_not_found_error";
    /** Generic connection error */
    private static final String PS_GENERIC_CONN_ERROR_KEY = 
            "postsubscription.controller.generic_connection_error"; 
    /** Internal controller error key */
    private static final String PS_INTERNAL_CONTROLLER_ERROR_KEY = 
            "postsubscription.controller.internal_controller_error";
    /** Subscription server - error message */
    private static final String PS_SERVER_ERROR_KEY = 
            "postsubscription.controller.subscription_server_error";
    /** Subscription server - success message */
    private static final String PS_SERVER_SUCCESS_KEY = 
            "postsubscription.controller.subscription_server_success";
    /** Subscription server - partial subscription message */
    private static final String PS_SERVER_PARTIAL_SUBSCRIPTION = 
            "postsubscription.controller.subscription_server_partial";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    
    private IConfigurationManager confManager;
    private Authenticator authenticator;
    private IHttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    
    private IDataSourceManager dataSourceManager;
    private ISubscriptionManager subscriptionManager;
    private INodeStatusManager nodeStatusManager;
    private IFilterManager filterManager;
    private IUserManager userManager;
    private RequestFactory requestFactory;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected User localNode;
    
    /**
     * Default constructor.
     */
    public StartSubscriptionController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Initializes controller with current configuration
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                confManager.getAppConf().getKeystoreDir());
        this.httpClient = new HttpClientUtil(
                Integer.parseInt(confManager.getAppConf().getConnTimeout()), 
                Integer.parseInt(confManager.getAppConf().getSoTimeout()));
        this.localNode = new User(confManager.getAppConf().getNodeName(),
                Role.NODE, null, confManager.getAppConf().getOrgName(),
                confManager.getAppConf().getOrgUnit(),
                confManager.getAppConf().getLocality(),
                confManager.getAppConf().getState(),
                confManager.getAppConf().getCountryCode(),
                confManager.getAppConf().getNodeAddress());
    }
    
    /**
     * Sets message.
     * @param model Model
     * @param messageKey Message key 
     * @param message Message 
     */
    public void setMessage(Model model, String messageKey, String message) {
        model.addAttribute(messageKey, message);
    }
    
    /**
     * Sets detailed message.
     * @param model Model
     * @param messageKey Message key
     * @param detailsKey Details key
     * @param message Message
     * @param details Detailed message
     */
    public void setMessage(Model model, String messageKey, String detailsKey,
            String message, String details) 
    {
        model.addAttribute(messageKey, message);
        model.addAttribute(detailsKey, details);
    }
    
    /**
     * Reads data sources from http response.
     * @param response Http response
     * @return Data source string
     * @throws IOException 
     */
    private String readDataSources(HttpResponse response) 
            throws InternalControllerException {
        try {
            InputStream is = null;
            try {
                is = response.getEntity().getContent();
                return IOUtils.toString(is, "UTF-8");
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new InternalControllerException("Failed to read server "
                    + "response");
        }
    }
    
    /**
     * Stores local subscriptions.
     * @param res Http response
     * @param dataSourceString Data sources Json string
     * @param peerName Peer node name
     * @return True if subscriptions are successfully saved 
     */
    @Transactional(propagation=Propagation.REQUIRED, 
            rollbackFor=Exception.class)
    private void storeLocalSubscriptions(HttpResponse response, 
            String dataSourceString, String peerName) 
                throws InternalControllerException {
        try {
            Set<DataSource> dataSources = jsonUtil
                    .jsonToDataSources(dataSourceString);
            for (DataSource ds : dataSources) {
                // save peer data sources
                DataSource dataSource = dataSourceManager.load(ds.getName(), 
                        DataSource.PEER);
                User user = userManager.load(peerName);
                if (dataSource == null) {
                    int id = dataSourceManager.store(ds);
                    // save peer data source user (operator) 
                    dataSourceManager.storeUser(id, user.getId());
                    // save peer data source filter
                    IFilter filter = dataSourceManager
                            .createFilter(ds.getSource(), ds.getFileObject());
                    filterManager.store(filter);
                    dataSourceManager.storeFilter(id, filter.getId());
                } else {
                    ds.setId(dataSource.getId());
                    dataSourceManager.update(ds);
                }
                // save subscriptions
                Subscription requested = new Subscription(
                        System.currentTimeMillis(), Subscription.LOCAL,
                        response.getFirstHeader("Node-Name").getValue(), 
                        ds.getName(), true, true);
                Subscription existing = subscriptionManager.load(
                    Subscription.LOCAL, peerName, ds.getName());
                if (existing == null) {
                    int subscriptionId = subscriptionManager.store(requested);
                    // save status
                    int statusId = nodeStatusManager.store(new Status(0, 0, 0));
                    nodeStatusManager.store(statusId, subscriptionId);
                } else {
                    requested.setId(existing.getId());       
                    int subId = requested.getId();
                    subscriptionManager.update(requested);
                    // update status - reset downloads
                    Status s = nodeStatusManager.load(subId);
                    s.setDownloads(0);
                    nodeStatusManager.update(s, subId);
                }
            }
        } catch (Exception e) {
            throw new InternalControllerException("Failed to read server "
                    + "response");
        }
    }
    
    /**userManager
     * Sends subscription request to the server.
     * @param model Model
     * @param peerName Peer node name
     * @param selectedDataSources Data sources selected for subscription
     * @return View name
     */
    @RequestMapping("/subscription_start_status.htm")
    public String startSubscription(HttpServletRequest request, Model model,
            @RequestParam(value="peer_name", required=true) String peerName) {
        initConfiguration();
        Set<DataSource> selectedPeerDataSources = (HashSet<DataSource>) 
                request.getSession().getAttribute(DS_SELECTED);
        request.getSession().removeAttribute(DS_SELECTED);
        User node = userManager.load(peerName);
        if (node == null) {
            String errorMsg = messages.getMessage(PS_PEER_NOT_FOUND_ERROR_KEY,
                            new String[] {peerName});
            setMessage(model, ERROR_MSG_KEY, errorMsg);      
            log.error(errorMsg + ": " + errorMsg );
        } else {
            requestFactory = new DefaultRequestFactory(
                URI.create(node.getNodeAddress()));
            HttpUriRequest req = requestFactory
                    .createStartSubscriptionRequest(localNode, 
                                                    selectedPeerDataSources);
            try {
                authenticator.addCredentials(req, localNode.getName());
                HttpResponse res = httpClient.post(req);
                if (res.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_OK) {
                    String okMsg = messages.getMessage(PS_SERVER_SUCCESS_KEY,
                            new String[] {peerName});
                    storeLocalSubscriptions(res, readDataSources(res), 
                            peerName);
                    setMessage(model, SUCCESS_MSG_KEY, okMsg);
                    log.warn(okMsg);
                } else if (res.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_PARTIAL_CONTENT) {
                    String errorMsg = messages.getMessage(
                        PS_SERVER_PARTIAL_SUBSCRIPTION, 
                        new String[] {peerName});
                    storeLocalSubscriptions(res, readDataSources(res), 
                            peerName);
                    setMessage(model, ERROR_MSG_KEY, errorMsg);
                    log.error(errorMsg);
                } else if (res.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_NOT_FOUND) {
                    String errorMsg = messages.getMessage(PS_SERVER_ERROR_KEY,
                            new String[] {peerName});
                    String errorDetails = res.getStatusLine().getReasonPhrase();
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, 
                            errorMsg, errorDetails);      
                    log.error(errorMsg + ": " + errorDetails);
                } else {
                    String errorMsg = messages.getMessage(PS_SERVER_ERROR_KEY,
                            new String[] {peerName});
                    String errorDetails = res.getStatusLine().getReasonPhrase();
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, 
                            errorMsg, errorDetails);
                    log.error(errorMsg + ": " + errorDetails);
                }  
            } catch (KeyczarException e) { 
                String errorMsg = messages
                        .getMessage(PS_MESSAGE_SIGNER_ERROR_KEY);     
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            } catch (InternalControllerException e) {
                String errorMsg = messages.getMessage(
                        PS_INTERNAL_CONTROLLER_ERROR_KEY, 
                        new String[] {peerName});
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            } catch (IOException e) {
                String errorMsg = messages.getMessage(
                        PS_HTTP_CONN_ERROR_KEY, new String[] {peerName});
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            } catch (Exception e) {
                String errorMsg = messages.getMessage(
                        PS_GENERIC_CONN_ERROR_KEY, new String[] {peerName});
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            }
            model.addAttribute(PEER_NAME_KEY, peerName);   
        }
        return SUBSCRIBE_VIEW;
    }
    
    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(IConfigurationManager confManager) {
        this.confManager = confManager;
    }

    /**
     * @param jsonUtil the jsonUtil to set
     */
    @Autowired
    public void setJsonUtil(IJsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
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
     * @param nodeStatusManager the nodeStatusManager to set
     */
    @Autowired
    public void setNodeStatusManager(INodeStatusManager nodeStatusManager) {
        this.nodeStatusManager = nodeStatusManager;
    }
    
    /**
     * @param filterManager the IFilterManager to set
     */
    public void setFilterManager(IFilterManager filterManager) {
        this.filterManager = filterManager;
    }
    
    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * @param httpClient the httpClient to set
     */
    public void setHttpClient(IHttpClientUtil httpClient) {
        this.httpClient = httpClient;
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
    
}