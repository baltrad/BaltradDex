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

import eu.baltrad.beast.security.SecurityStorageException;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.MessageSetter;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.keyczar.exceptions.KeyczarException;

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
public class UpdateSubscriptionController  {
    
    /** Subscribed peers view */
    private static final String SUBSCRIBED_PEERS_VIEW = "subscription_peers";
    /** Subscription by peer view */
    private static final String SUBSCRIPTION_BY_PEER_VIEW = 
            "subscription_show";
    /** Selected subscription view */
    private static final String SELECTED_SUBSCRIPTION_VIEW = 
            "subscription_selected";
    /** Subscription status view */
    private static final String SUBSCRIPTION_STATUS_VIEW = 
            "subscription_update_status";
    
    /** Subscribed peers key */
    private static final String SUBSCRIBED_PEERS_KEY = "subscribed_peers";
    /** List of available connections */
    private final static String NODES_KEY = "nodes";    
    /** Subscription by peer key */
    private static final String SUBSCRIPTION_BY_PEER_KEY = 
            "subscription_by_peer"; 
    /** Selected subscription key */
    private static final String SELECTED_SUBSCRIPION_KEY = 
            "subscription_selected";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    /** Subscription modification status key */
    private static final String STATUS_NOT_CHANGED_KEY = 
            "subscription_status_unchanged";
    /** Subscription not changed message key */
    private static final String GS_SUBSCRIPTION_UNCHANGED_KEY = 
            "getsubscription.controller.subscription_unchanged";
    /** Message signer error key */
    private static final String GS_MESSAGE_SIGNER_ERROR_KEY = 
            "getsubscription.controller.message_signer_error";
    /** Subscription server - success message */
    private static final String GS_SERVER_SUCCESS_KEY = 
            "getsubscription.controller.subscription_server_success";
    /** Subscription server - error message */
    private static final String GS_SERVER_ERROR_KEY = 
            "getsubscription.controller.subscription_server_error";
     /** Subscription server - partial subscription message */
    private static final String GS_SERVER_PARTIAL_SUBSCRIPTION_KEY = 
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
    
    private IConfigurationManager confManager;
    private ISubscriptionManager subscriptionManager;
    private INodeStatusManager nodeStatusManager;
    private IUserManager userManager;
    private ModelMessageHelper messageHelper;
    private Authenticator authenticator;
    private IHttpClientUtil httpClient;
    private ProtocolManager protocolManager;
    private Logger log;
    
    protected User localNode;
    
    /**
     * Default constructor.
     */
    public UpdateSubscriptionController() {
        this.log = Logger.getLogger("DEX");       
    }
    
    /**
     * Initializes controller with current configuration
     */
    protected void initConfiguration() {
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
     * Creates subscription request list.
     * @param activeSubscriptionIds Active subscription ids
     * @param inactiveSubscriptionIds Inactive subscription ids
     * @return List of requested subscriptions
     */
    protected List<Subscription> createSubscriptionRequest(String[] activeSubscriptionIds, String[] inactiveSubscriptionIds) {
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
            if (subscriptionManager.load(Integer.parseInt(id))
                    .isActive() != true) {
                result = true;
            }
        }
        for (String id : unselected) {
            if (subscriptionManager.load(Integer.parseInt(id))
                    .isActive() == true) {
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Stores local subscriptions.
     * @param res Http response
     * @param subscriptionString Subscriptions Json string
     * @return True if subscriptions are successfully saved 
     */
  @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
  protected void storeLocalSubscriptions(String nodeName , List<Subscription> subscriptions) throws InternalControllerException {
    try {
        for (Subscription s : subscriptions) {
            Subscription requested = new Subscription(
                System.currentTimeMillis(), Subscription.LOCAL, nodeName,
                s.getDataSource(), s.isActive(), s.isSyncronized());
            Subscription existing = subscriptionManager.load(
                    Subscription.LOCAL, nodeName, s.getDataSource());
            if (existing == null) {
                    int subscriptionId = subscriptionManager.store(requested);
                    // save status
                    int statusId = nodeStatusManager.store(new Status(0, 0, 0));
                    nodeStatusManager.store(statusId, subscriptionId);
            } else {       
                requested.setId(existing.getId());
                subscriptionManager.update(requested);
            }        
        }
    } catch (Exception e) {
        throw new InternalControllerException("Failed to read server response");
    }
  }
    
    /**
     * Shows lists of subscribed peer nodes.
     * @param model Model
     * @return Subscribed peers view
     */
    @RequestMapping("/subscription_peers.htm")
    public String subscribedPeers(Model model) {
        model.addAttribute(SUBSCRIBED_PEERS_KEY, userManager.loadOperators());
        model.addAttribute(NODES_KEY, userManager.loadPeerNames());
        return SUBSCRIBED_PEERS_VIEW;
    }
    
    /**
     * Shows list of subscriptions for a given peer node. 
     * @param model Model
     * @param peerName Peer node name
     * @return Subscriptions by peer view
     */
    @RequestMapping("/subscription_show.htm")
    public String subscriptionByPeer(Model model,
            @RequestParam(value="peer_name", required=true) String peerName) {
        List<Subscription> subscriptionByPeer = subscriptionManager.load(
                Subscription.LOCAL, peerName);
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
    @RequestMapping("/subscription_selected.htm")
    public String selectedSubscription(Model model,
            @RequestParam(value="peer_name", required=true) String peerName,
            @RequestParam(value="current_subscription_ids", required=true) String[] currentSubscriptionIds,
            @RequestParam(value="selected_subscription_ids", required=false) String[] selectedSubscriptionIds) {
      
        List<Subscription> currentSubscription = subscriptionManager.load(Subscription.LOCAL, peerName);
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
                Subscription.LOCAL, peerName);
            model.addAttribute(SUBSCRIPTION_BY_PEER_KEY, subscriptionByPeer);
            model.addAttribute(STATUS_NOT_CHANGED_KEY, 
                    messageHelper.getMessage(GS_SUBSCRIPTION_UNCHANGED_KEY));
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
    @RequestMapping("/subscription_update_status.htm")
    public String updateSubscription(Model model,
            @RequestParam(value="peer_name", required=true) String peerName,
            @RequestParam(value="active_subscription_ids", required=false) 
                String[] activeSubscriptionIds,
            @RequestParam(value="inactive_subscription_ids", required=false)
                String[] inactiveSubscriptionIds) {
        initConfiguration();
        User node = userManager.load(peerName);
        RequestFactory requestFactory = protocolManager.getFactory(node.getNodeAddress());
        List<Subscription> subscriptionRequests = createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds);
        HttpUriRequest req = requestFactory.createUpdateSubscriptionRequest(localNode, subscriptionRequests);
        try {
            authenticator.addCredentials(req, localNode.getName());
            HttpResponse res = httpClient.post(req);
            ResponseParser parser = protocolManager.createParser(res);
            if (parser.getStatusCode() == HttpServletResponse.SC_OK) {
              storeLocalSubscriptions(parser.getNodeName(), parser.getSubscriptions());
              messageHelper.setSuccessMessage(model, GS_SERVER_SUCCESS_KEY, peerName);
            } else if (parser.getStatusCode() == HttpServletResponse.SC_PARTIAL_CONTENT) {
              storeLocalSubscriptions(parser.getNodeName(), parser.getSubscriptions());
              messageHelper.setErrorMessage(model, GS_SERVER_PARTIAL_SUBSCRIPTION_KEY, peerName);
            } else {
              messageHelper.setErrorDetailsMessage(model, GS_SERVER_ERROR_KEY, parser.getReasonPhrase(), peerName);
            }
        } catch (KeyczarException e) {
          messageHelper.setErrorDetailsMessage(model, GS_MESSAGE_SIGNER_ERROR_KEY, e.getMessage());
        } catch (SecurityStorageException e) {
          messageHelper.setErrorDetailsMessage(model, GS_MESSAGE_SIGNER_ERROR_KEY, e.getMessage());
        } catch (InternalControllerException e) {
          messageHelper.setErrorDetailsMessage(model, GS_INTERNAL_CONTROLLER_ERROR_KEY, e.getMessage(), peerName);
        } catch (IOException e)  {
          messageHelper.setErrorDetailsMessage(model, GS_HTTP_CONN_ERROR_KEY, e.getMessage(), peerName);
        } catch (Exception e) {
          messageHelper.setErrorDetailsMessage(model, GS_GENERIC_CONN_ERROR_KEY, e.getMessage(), peerName);
        }
        model.addAttribute(PEER_NAME_KEY, peerName);
        return SUBSCRIPTION_STATUS_VIEW;
    }
    
    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(IConfigurationManager confManager) {
        this.confManager = confManager;
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
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
    
    /**
     * @param httpClient the httpClient to set
     */
    @Autowired
    public void setHttpClient(IHttpClientUtil httpClient) {
        this.httpClient = httpClient;
    }
    
    /**
     * @param log the log to set
     */
    @Autowired
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @param authenticator the authenticator to set
     */
    @Autowired
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }
    
    /**
     * @param messageHelper the message helper
     */
    @Autowired
    public void setMessageHelper(ModelMessageHelper messageHelper) {
      this.messageHelper = messageHelper;
    }

    /**
     * @param protocolManager the protocol manager
     */
    @Autowired
    public void setProtocolManager(ProtocolManager protocolManager) {
      this.protocolManager = protocolManager;
    }
}
