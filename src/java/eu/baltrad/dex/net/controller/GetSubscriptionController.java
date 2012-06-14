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
import eu.baltrad.dex.net.model.ISubscriptionManager;
import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.net.model.INodeConnectionManager;
import eu.baltrad.dex.net.model.NodeConnection;
import eu.baltrad.dex.datasource.model.DataSource;
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


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.IOException;

/**
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class GetSubscriptionController implements MessageSetter {
    
    /** Current view */
    //private static final String GET_SUBSCRIPTION_VIEW = "getsubscription";
    /** Show subscription view */
    //private static final String SHOW_SUBSCRIPTION_VIEW = "showsubscription";
    
    /** Data sources selected for subscription */
    //private static final String SUBSCRIPTIONS_KEY = "subscriptions_key";
    /** URL of the target node */
    //private static final String TARGET_NODE_URL = "target_node_url";

    /** Message keys */
    //private static final String INVALID_URL_MSG = "node.url.invalid";
    //private static final String SUBSCRIPTION_READ_ERROR_MSG = 
    //        "datasource.read.error";
    //private static final String SUBSCRIPTION_SERVER_ERROR_MSG = 
    //        "subscription.server.error";
    
    private static final String SUBSCRIBED_PEERS_VIEW = "subscribed_peers";
    
    private static final String SUBSCRIPTION_BY_PEER_VIEW = "subscription_by_peer";
    private static final String SELECTED_SUBSCRIPTION_VIEW = "selected_subscription";
    
    
    private static final String SUBSCRIBED_PEERS_KEY = "subscribed_peers";
    private static final String SUBSCRIPTION_BY_PEER_KEY = "subscription_by_peer"; 
    private static final String PEER_NAME_KEY = "peer_name";
    
    private ISubscriptionManager subscriptionManager;
    private INodeConnectionManager nodeConnectionManager;
    
    private UrlValidatorUtil urlValidator;
    private MessageResourceUtil messages;
    private Authenticator authenticator;
    private RequestFactory requestFactory;
    private HttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    private Logger log;
    
    private String nodeName;
    private String nodeAddress;
    
    /**
     * Default constructor.
     */
    public GetSubscriptionController() {
        this.authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir());
        this.httpClient = new HttpClientUtil(
                InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);        
    }
    
    /**
     * Constructor.
     * @param nodeName Node name
     * @param nodeAdress Node address
     */
    public GetSubscriptionController(String nodeName, String nodeAddress) {
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
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
    
    
    @RequestMapping("/subscribed_peers.htm")
    public String subscribedPeers(Model model) {
        List<Subscription> subscribedPeers = subscriptionManager.loadOperators(
                Subscription.SUBSCRIPTION_DOWNLOAD);
        model.addAttribute(SUBSCRIBED_PEERS_KEY, subscribedPeers);
        return SUBSCRIBED_PEERS_VIEW;
        
    }
    
    @RequestMapping("/subscription_by_peer.htm")
    public String subscriptionByPeer(Model model,
            @RequestParam(value="peer_name", required=true) String peerName) {
        List<Subscription> subscriptionByPeer = subscriptionManager.load(
                peerName, Subscription.SUBSCRIPTION_DOWNLOAD);
        model.addAttribute(SUBSCRIPTION_BY_PEER_KEY, subscriptionByPeer);
        model.addAttribute(PEER_NAME_KEY, peerName);
        return SUBSCRIPTION_BY_PEER_VIEW;
    }
    
    @RequestMapping("/selected_subscription.htm")
    public String getSubscription(Model model,
            @RequestParam(value="peer_name", required=true) String peerName,
            @RequestParam(value="selected_data_sources", required=false)
                String[] selectedDataSources) {
        
        Set<DataSource> selectedPeerDataSources = new HashSet<DataSource>();
        if (selectedDataSources != null) {
            for (int i = 0; i < selectedDataSources.length; i++) {
                selectedPeerDataSources.add(
                        new DataSource(0, selectedDataSources[i], ""));
            }
        } 
        String dataSourceString = jsonUtil
                .dataSourcesToJson(selectedPeerDataSources);
        NodeConnection conn = nodeConnectionManager.get(peerName);
        requestFactory = new DefaultRequestFactory(
                URI.create(conn.getNodeAddress()));
        HttpUriRequest req = requestFactory
                .createGetSubscriptionRequest(nodeName, nodeAddress, 
                    dataSourceString);
        authenticator.addCredentials(req, nodeName);
        try {
            HttpResponse res = httpClient.post(req);
            if (res.getStatusLine().getStatusCode() == 
                    HttpServletResponse.SC_OK) {
                
            } else {
                
            }
            
        } catch (IOException e) {
            
        } catch (Exception e) {
            
        }
        
        
        
        
        System.out.println("_________________________ds string = " + dataSourceString);
        
       
        
        return SELECTED_SUBSCRIPTION_VIEW;
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
     * @return the urlValidator
     *
    public UrlValidatorUtil getUrlValidator() {
        return urlValidator;
    }

    /**
     * @param urlValidator the urlValidator to set
     * void setUrlValidator(UrlValidatorUtil urlValidator) {
        this.urlValidator = urlValidator;
    }

    /**
     * @return the messages
     *
    public MessageResourceUtil getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     *
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }

    /**
     * @return the authenticator
     *
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     *
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * @return the httpClient
     *
    public HttpClientUtil getHttpClient() {
        return httpClient;
    }

    /**
     * @param httpClient the httpClient to set
     *
    public void setHttpClient(HttpClientUtil httpClient) {
        this.httpClient = httpClient;
    }*/

   

    

    /**
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(ISubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }
    
}
