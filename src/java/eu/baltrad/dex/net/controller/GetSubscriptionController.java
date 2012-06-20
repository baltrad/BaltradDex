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
import eu.baltrad.dex.datasource.model.IDataSourceManager;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

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
    private static final String SELECTED_SUBSCRIPION_KEY = "selected_subscription";
    private static final String PEER_NAME_KEY = "peer_name";
    private static final String STATUS_NOT_CHANGED_KEY = "status_not_changed";
    
    
    private static final String SELECTED_DATA_SOURCES_KEY = "selected_data_sources";
    
    private ISubscriptionManager subscriptionManager;
    
    private IDataSourceManager dataSourceManager;
    
    private MessageResourceUtil messages;
    
    private Logger log;
    
    
    
    /**
     * Default constructor.
     */
    public GetSubscriptionController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);        
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
     * 
     * @param model
     * @return 
     */
    @RequestMapping("/subscribed_peers.htm")
    public String subscribedPeers(Model model) {
        List<Subscription> subscribedPeers = subscriptionManager.loadOperators(
                Subscription.SUBSCRIPTION_DOWNLOAD);
        model.addAttribute(SUBSCRIBED_PEERS_KEY, subscribedPeers);
        return SUBSCRIBED_PEERS_VIEW;
        
    }
    
    /**
     * 
     * @param model
     * @param peerName
     * @return 
     */
    @RequestMapping("/subscription_by_peer.htm")
    public String subscriptionByPeer(Model model,
            @RequestParam(value="peer_name", required=true) String peerName) {
        List<Subscription> subscriptionByPeer = subscriptionManager.load(
                peerName, Subscription.SUBSCRIPTION_DOWNLOAD);
        model.addAttribute(SUBSCRIPTION_BY_PEER_KEY, subscriptionByPeer);
        model.addAttribute(PEER_NAME_KEY, peerName);
        return SUBSCRIPTION_BY_PEER_VIEW;
    }
    
    /**
     * 
     * @param model
     * @param peerName
     * @param currentSubscriptionIds
     * @param selectedSubscriptionIds
     * @return 
     */
    @RequestMapping("/selected_subscription.htm")
    public String getSubscription(Model model,
            @RequestParam(value="peer_name", required=true) String peerName,
            @RequestParam(value="current_subscription_ids", required=true) 
                String[] currentSubscriptionIds,
            @RequestParam(value="selected_subscription_ids", required=false)
                String[] selectedSubscriptionIds) {
        
        List<Subscription> currentSubscription = subscriptionManager.load(
                peerName, Subscription.SUBSCRIPTION_DOWNLOAD);
        Map currentSubscriptionMap = new HashMap<Integer, Subscription>();
        for (Subscription s : currentSubscription) {
            currentSubscriptionMap.put(s.getId(), s);
        }
        Map selectedSubscriptionMap = new HashMap<Integer, Subscription>();
        if (selectedSubscriptionIds != null) {
            for (int i = 0; i < selectedSubscriptionIds.length; i++) {
                Subscription s = subscriptionManager.load(Integer.parseInt(
                        selectedSubscriptionIds[i]));
                s.setActive(true);
                selectedSubscriptionMap.put(s.getId(), s);
            }
        }
        Set<Integer> currentKeys = currentSubscriptionMap.keySet();
        Iterator it = currentKeys.iterator();
        while (it.hasNext()) {
            int key = (Integer) it.next();
            Subscription s = (Subscription) currentSubscriptionMap.get(key);
            if (!selectedSubscriptionMap.containsKey(key)) {
                s.setActive(false);
                selectedSubscriptionMap.put(key, s);
            }
        }
        model.addAttribute(PEER_NAME_KEY, peerName);
        
        if (currentSubscriptionIds.length == selectedSubscriptionIds.length) {
            List<Subscription> subscriptionByPeer = subscriptionManager.load(
                peerName, Subscription.SUBSCRIPTION_DOWNLOAD);
            model.addAttribute(SUBSCRIPTION_BY_PEER_KEY, subscriptionByPeer);
            model.addAttribute(STATUS_NOT_CHANGED_KEY, "unchanged");
            return SUBSCRIPTION_BY_PEER_VIEW;
        } else {
            List<Subscription> selectedSubscription = 
                new ArrayList<Subscription>(selectedSubscriptionMap.values());
            model.addAttribute(SELECTED_SUBSCRIPION_KEY, selectedSubscription);
            return SELECTED_SUBSCRIPTION_VIEW;
        }
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
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }
    
}
