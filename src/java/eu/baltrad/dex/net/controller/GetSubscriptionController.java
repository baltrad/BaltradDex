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
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.log.util.MessageLogger;

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

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Controls subscription process.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class GetSubscriptionController implements MessageSetter {
    
    /** Subscribed peers view */
    private static final String SUBSCRIBED_PEERS_VIEW = "subscribed_peers";
    /** Subscription by peer view */
    private static final String SUBSCRIPTION_BY_PEER_VIEW = 
                                                        "subscription_by_peer";
    /** Selected subscription view */
    private static final String SELECTED_SUBSCRIPTION_VIEW = 
                                                        "selected_subscription";
    /** Subscription status view */
    private static final String SUBSCRIPTION_STATUS_VIEW = 
                                                        "subscription_status";
    
    /** Subscribed peers key */
    private static final String SUBSCRIBED_PEERS_KEY = "subscribed_peers";
    /** Subscription by peer key */
    private static final String SUBSCRIPTION_BY_PEER_KEY = 
                                                        "subscription_by_peer"; 
    /** Selected subscription key */
    private static final String SELECTED_SUBSCRIPION_KEY = 
                                                        "selected_subscription";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    /** Subscription modification status key */
    private static final String STATUS_NOT_CHANGED_KEY = "status_not_changed";
    
    /** Subscription server - success message */
    private static final String GS_SERVER_SUCCESS_KEY = 
            "getsubscription.controller.subscription_server_success";
    /** Subscription server - error message */
    private static final String GS_SERVER_ERROR_KEY = 
            "getsubscription.controller.subscription_server_error";
     /** Subscription server - partial subscription message */
    private static final String GS_SERVER_PARTIAL_SUBSCRIPTION = 
            "getsubscription.controller.subscription_server_partial";
    /** Internal controller error key */
    private static final String GS_INTERNAL_CONTROLLER_ERROR_KEY = 
            "getsubscription.controller.internal_controller_error";
    /** Subscription connection error key */
    private final static String GS_HTTP_CONN_ERROR_KEY = 
            "getsubscription.controller.http_connection_error";  
    /** Generic connection error */
    private static final String GS_GENERIC_CONN_ERROR_KEY = 
            "getsubscription.controller.generic_connection_error";
    
    private ISubscriptionManager subscriptionManager;
    private INodeConnectionManager nodeConnectionManager;
    private MessageResourceUtil messages;
    private Authenticator authenticator;
    private RequestFactory requestFactory;
    private IHttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    private Logger log;
    
    protected String nodeName;
    protected String nodeAddress;
    
    /**
     * Default constructor.
     */
    public GetSubscriptionController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);        
    }
    
    /**
     * Initializes controller with current configuration
     */
    protected void initConfiguration() {
        this.setAuthenticator(new KeyczarAuthenticator(
                 InitAppUtil.getConf().getKeystoreDir()));
        this.httpClient = new HttpClientUtil(
                InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
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
     * Creates subscription request list.
     * @param activeSubscriptionIds Active subscription ids
     * @param inactiveSubscriptionIds Inactive subscription ids
     * @return List of requested subscriptions
     */
    private List<Subscription> createSubscriptionRequest(
            String[] activeSubscriptionIds, String[] inactiveSubscriptionIds) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        if (activeSubscriptionIds != null) {
            for (int i = 0; i < activeSubscriptionIds.length; i++) {
                Subscription s = subscriptionManager.load(Integer.parseInt(
                        activeSubscriptionIds[i]));
                s.setActive(true);
                subscriptions.add(s);
            }
        }
        if (inactiveSubscriptionIds != null) {
            for (int i = 0; i < inactiveSubscriptionIds.length; i++) {
                Subscription s = subscriptionManager.load(Integer.parseInt(
                        inactiveSubscriptionIds[i]));
                s.setActive(false);
                subscriptions.add(s);
            }
        }
        return subscriptions; 
    }
    
    
    /**
     * Checks if subscription status was modified. 
     * @param currentSubscriptionIds Current subscription ids
     * @param selectedSubscriptionIds Selected subscription ids
     * @return True if subscription status was modified 
     */
    private boolean statusModified(String[] currentSubscriptionIds, 
            String[] selectedSubscriptionIds) {
        List<String> current = Arrays.asList(currentSubscriptionIds);
        List<String> selected = new ArrayList<String>();
        if (selectedSubscriptionIds != null) {
            selected = Arrays.asList(selectedSubscriptionIds);
        }
        List<String> unselected = new ArrayList<String>();
        boolean result = false;
        for (String id : current) {
            if (!selected.contains(id)) {
                unselected.add(id);
            }
        }
        for (String id : selected) {
            if (subscriptionManager.load(Integer.parseInt(id)).getActive() 
                    != true ) {
                result = true;
            }
        }
        for (String id : unselected) {
            if (subscriptionManager.load(Integer.parseInt(id)).getActive() 
                    == true ) {
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Reads subscriptions from http response.
     * @param response Http response
     * @return Subscriptions string
     * @throws IOException 
     */
    private String readSubscriptions(HttpResponse response) 
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
     * @param subscriptionString Subscriptions Json string
     * @return True if subscriptions are successfully saved 
     */
    private boolean storeLocalSubscriptions(HttpResponse response,
            String subscriptionString) throws InternalControllerException {
        try {
            List<Subscription> subscriptions = jsonUtil
                    .jsonToSubscriptions(subscriptionString);
            boolean result = true;
            for (Subscription s : subscriptions) {
                Subscription requested = new Subscription(
                    System.currentTimeMillis(), nodeName, s.getDataSourceName(), 
                    response.getFirstHeader("Node-Name").getValue(), 
                    Subscription.SUBSCRIPTION_DOWNLOAD, s.getActive(),
                    s.getSynkronized(), 
                    response.getFirstHeader("Node-Address").getValue());
                Subscription existing = subscriptionManager.load(
                    nodeName, s.getDataSourceName(), 
                    Subscription.SUBSCRIPTION_DOWNLOAD);
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
     * Shows lists of subscribed peer nodes.
     * @param model Model
     * @return Subscribed peers view
     */
    @RequestMapping("/subscribed_peers.htm")
    public String subscribedPeers(Model model) {
        List<Subscription> subscribedPeers = 
                subscriptionManager.loadOperators();
        model.addAttribute(SUBSCRIBED_PEERS_KEY, subscribedPeers);
        return SUBSCRIBED_PEERS_VIEW;
        
    }
    
    /**
     * Shows list of subscriptions for a given peer node. 
     * @param model Model
     * @param peerName Peer node name
     * @return Subscriptions by peer view
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
     * Shows list of selected subscriptions. The list is posted on the remote 
     * node and subscription status is modified accordingly.
     * @param model Model
     * @param peerName Peer node name
     * @param currentSubscriptionIds IDs of current subscriptions
     * @param selectedSubscriptionIds IDs of selected subscriptions  
     * @return Selected subscriptions view
     */
    @RequestMapping("/selected_subscription.htm")
    public String selectedSubscription(Model model,
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
        if (statusModified(currentSubscriptionIds, selectedSubscriptionIds)) {
            List<Subscription> selectedSubscription = 
                new ArrayList<Subscription>(selectedSubscriptionMap.values());
            model.addAttribute(SELECTED_SUBSCRIPION_KEY, selectedSubscription);
            return SELECTED_SUBSCRIPTION_VIEW;
        } else {
            List<Subscription> subscriptionByPeer = subscriptionManager.load(
                peerName, Subscription.SUBSCRIPTION_DOWNLOAD);
            model.addAttribute(SUBSCRIPTION_BY_PEER_KEY, subscriptionByPeer);
            model.addAttribute(STATUS_NOT_CHANGED_KEY, "unchanged");
            return SUBSCRIPTION_BY_PEER_VIEW;
        }
    }
    
    /**
     * Post subscription request on the peer node.
     * @param model Model
     * @param peerName Peer node name
     * @param activeSubscriptionIds List contains IDs of active subscriptions 
     * @param inactiveSubscriptionIds List contains IDs of inactive 
     *                                subscriptions 
     * @return Subscription status view
     */
    @RequestMapping("/subscription_status.htm")
    public String getSubscription(Model model,
            @RequestParam(value="peer_name", required=true) String peerName,
            @RequestParam(value="active_subscription_ids", required=false) 
                String[] activeSubscriptionIds,
            @RequestParam(value="inactive_subscription_ids", required=false)
                String[] inactiveSubscriptionIds) {
        initConfiguration();
        String subscriptionString = jsonUtil.subscriptionsToJson(
                createSubscriptionRequest(activeSubscriptionIds, 
                    inactiveSubscriptionIds));
        NodeConnection conn = nodeConnectionManager.load(peerName);
        requestFactory = new DefaultRequestFactory(
                                            URI.create(conn.getNodeAddress()));
        HttpUriRequest req = requestFactory.createGetSubscriptionRequest(
                nodeName, nodeAddress, subscriptionString);
        authenticator.addCredentials(req, nodeName);
        try {
            HttpResponse res = httpClient.post(req);
            if (res.getStatusLine().getStatusCode() 
                    == HttpServletResponse.SC_OK) {
                String okMsg = messages.getMessage(GS_SERVER_SUCCESS_KEY,
                        new String[] {peerName});
                storeLocalSubscriptions(res, readSubscriptions(res));
                setMessage(model, SUCCESS_MSG_KEY, okMsg);
                log.warn(okMsg);
            } else if (res.getStatusLine().getStatusCode() 
                    == HttpServletResponse.SC_PARTIAL_CONTENT) {
                String errorMsg = messages.getMessage(
                    GS_SERVER_PARTIAL_SUBSCRIPTION, new String[] {peerName});    
                storeLocalSubscriptions(res, readSubscriptions(res));
                setMessage(model, ERROR_MSG_KEY, errorMsg);
                log.error(errorMsg);
            } else if (res.getStatusLine().getStatusCode() 
                    == HttpServletResponse.SC_NOT_FOUND) {
                
                String errorMsg = messages.getMessage(GS_SERVER_ERROR_KEY,
                        new String[] {peerName});
                String errorDetails = res.getStatusLine().getReasonPhrase();
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        errorDetails);      
                log.error(errorMsg + ": " + errorDetails);
            } else {
                String errorMsg = messages.getMessage(GS_SERVER_ERROR_KEY,
                        new String[] {peerName});
                String errorDetails = res.getStatusLine().getReasonPhrase();
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                        errorDetails);
                log.error(errorMsg + ": " + errorDetails);   
            }
        } catch (InternalControllerException e) {
            String errorMsg = messages.getMessage(
                    GS_INTERNAL_CONTROLLER_ERROR_KEY, new String[] {peerName}); 
            String errorDetails = e.getMessage();
            setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                    errorDetails);
            log.error(errorMsg + ": " + errorDetails);
        } catch (IOException e)  {
            String errorMsg = messages.getMessage(
                    GS_HTTP_CONN_ERROR_KEY, new String[] {peerName});
            String errorDetails = e.getMessage();
            setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                    errorDetails);
            log.error(errorMsg + ": " + errorDetails);
        } catch (Exception e) {
            String errorMsg = messages.getMessage(
                    GS_GENERIC_CONN_ERROR_KEY, new String[] {peerName});
            String errorDetails = e.getMessage();
            setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY, errorMsg,
                    errorDetails);
            log.error(errorMsg + ": " + errorDetails);
        }
        model.addAttribute(PEER_NAME_KEY, peerName);
        return SUBSCRIPTION_STATUS_VIEW;
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
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
}
