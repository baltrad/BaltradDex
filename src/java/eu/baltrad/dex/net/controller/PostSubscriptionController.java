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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.INodeConnectionManager;
import eu.baltrad.dex.net.model.ISubscriptionManager;
import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.net.model.NodeConnection;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.log.model.MessageLogger;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

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
public class PostSubscriptionController implements MessageSetter {
    
    /** Initial view */
    private static final String SUBSCRIBE_VIEW = "subscribe";
    
    /** Subscription connection error key */
    private final static String PS_HTTP_CONN_ERROR_KEY = 
            "postsubscription.controller.http_connection_error";        
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
    
    private Authenticator authenticator;
    private IHttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    private INodeConnectionManager nodeConnectionManager;
    private ISubscriptionManager subscriptionManager;    
    private RequestFactory requestFactory;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected String nodeName;
    protected String nodeAddress; 
    
    /**
     * Default constructor.
     */
    public PostSubscriptionController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Initializes controller with current configuration
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir());
        this.httpClient = new HttpClientUtil(
                InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
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
            return IOUtils.toString(is);
        } finally {
            is.close();
        }
        }catch(IOException e) {
            throw new InternalControllerException(e.getMessage());
        }
    }
    
    /**
     * Stores local subscriptions.
     * @param res Http response
     * @param dataSourceString Data sources Json string
     * @return True if subscriptions are successfully saved 
     */
    private boolean storeLocalSubscriptions(HttpResponse response, 
            String dataSourceString) throws InternalControllerException{
        try {
            Set<DataSource> dataSources = jsonUtil
                    .jsonToDataSources(dataSourceString);
            boolean result = true;
            for (DataSource ds : dataSources) {
                Subscription requested = new Subscription(
                    System.currentTimeMillis(), nodeName,
                    ds.getName(), 
                    response.getFirstHeader("Node-Name").getValue(),
                    Subscription.SUBSCRIPTION_DOWNLOAD, true, true,
                    response.getFirstHeader("Node-Address").getValue());
                Subscription existing = subscriptionManager.load(
                    nodeName, ds.getName(), Subscription.SUBSCRIPTION_DOWNLOAD);
                if (existing == null) {
                    if (subscriptionManager.storeNoId(requested) != 1) {
                        result = false;
                    }
                } else {       
                    requested.setId(existing.getId());
                    if (subscriptionManager.update(requested) != 1) {
                        result = false;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            throw new InternalControllerException(e.getMessage());
        }
    }
    
    /**
     * Sends subscription request to the server.
     * @param model Model
     * @param peerName Peer node name
     * @param selectedDataSources Data sources selected for subscription
     * @return View name
     */
    @RequestMapping("/subscribe.htm")
    public String postSubscription(Model model,
            @RequestParam(value="peer_name", required=true) String peerName,
            @RequestParam(value="selected_data_sources", required=true) 
            String[] selectedDataSources) {
        initConfiguration();
        Set<DataSource> selectedPeerDataSources = new HashSet<DataSource>();
        for (int i = 0; i < selectedDataSources.length; i++) {
            String[] parms = selectedDataSources[i].split("_");
            selectedPeerDataSources.add(new DataSource(
                    Integer.parseInt(parms[0]), parms[1], parms[2]));
        }
        String dataSourceString = jsonUtil
                .dataSourcesToJson(selectedPeerDataSources);
        NodeConnection conn = nodeConnectionManager.get(peerName);
        requestFactory = new DefaultRequestFactory(
                URI.create(conn.getNodeAddress()));
        HttpUriRequest req = requestFactory
                .createPostSubscriptionRequest(nodeName, nodeAddress,
                    dataSourceString);
        authenticator.addCredentials(req, nodeName);
        try {
            HttpResponse res = httpClient.post(req);
            if (res.getStatusLine().getStatusCode() == 
                    HttpServletResponse.SC_OK) {
                String okMsg = messages.getMessage(PS_SERVER_SUCCESS_KEY,
                        new String[] {peerName});        
                storeLocalSubscriptions(res, readDataSources(res));
                setMessage(model, SUCCESS_MSG_KEY, okMsg);
                log.warn(okMsg);
            } else if (res.getStatusLine().getStatusCode() == 
                    HttpServletResponse.SC_PARTIAL_CONTENT) {
                String errorMsg = messages.getMessage(
                    PS_SERVER_PARTIAL_SUBSCRIPTION, new String[] {peerName});    
                storeLocalSubscriptions(res, readDataSources(res));
                setMessage(model, ERROR_MSG_KEY, errorMsg);
                log.error(errorMsg);
            } else if (res.getStatusLine().getStatusCode() == 
                    HttpServletResponse.SC_NOT_FOUND) {
                String errorMsg = messages.getMessage(PS_SERVER_ERROR_KEY,
                        new String[] {peerName});
                String errorDetails = res.getStatusLine().getReasonPhrase();
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        errorDetails);      
                log.error(errorMsg + ": " + errorDetails);
            } else {
                String errorMsg = messages.getMessage(PS_SERVER_ERROR_KEY,
                        new String[] {peerName});
                String errorDetails = res.getStatusLine().getReasonPhrase();
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        errorDetails);
                log.error(errorMsg + ": " + errorDetails);
            }  
        } catch (InternalControllerException e) {
            String errorMsg = messages.getMessage(
                    PS_INTERNAL_CONTROLLER_ERROR_KEY, new String[] {peerName}); 
            String errorDetails = e.getMessage();
            setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                    errorDetails);
            log.error(errorMsg + ": " + errorDetails);
        } catch (IOException e) {
            String errorMsg = messages.getMessage(
                    PS_HTTP_CONN_ERROR_KEY, new String[] {peerName});
            String errorDetails = e.getMessage();
            setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                    errorDetails);
            log.error(errorMsg + ": " + errorDetails);
        } catch (Exception e) {
            String errorMsg = messages.getMessage(
                    PS_GENERIC_CONN_ERROR_KEY, new String[] {peerName});
            String errorDetails = e.getMessage();
            setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                    errorDetails);
            log.error(errorMsg + ": " + errorDetails);
        }
        model.addAttribute(PEER_NAME_KEY, peerName);
        return SUBSCRIBE_VIEW;
    }

    /**
     * @param jsonUtil the jsonUtil to set
     */
    @Autowired
    public void setJsonUtil(IJsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * @param nodeConnectionManager the nodeConnectionManager to set
     */
    @Autowired
    public void setNodeConnectionManager(
            INodeConnectionManager nodeConnectionManager) {
        this.nodeConnectionManager = nodeConnectionManager;
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