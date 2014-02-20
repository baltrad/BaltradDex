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

package eu.baltrad.dex.net.servlet;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.keyczar.exceptions.KeyczarException;

import java.util.List;
import java.util.ArrayList;

/**
 * Receives and handles subscription requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@SuppressWarnings("serial")
@Controller
public class UpdateSubscriptionServlet extends HttpServlet {
    private static final String GS_INTERNAL_SERVER_ERROR_KEY = 
            "getsubscription.server.internal_server_error";
    private static final String GS_MESSAGE_VERIFIER_ERROR_KEY = 
            "postsubscription.server.message_verifier_error";    
    private static final String GS_UNAUTHORIZED_REQUEST_KEY =
            "getsubscription.server.unauthorized_request";
    private static final String GS_SUBSCRIPTION_START_SUCCESS_KEY = 
            "getsubscription.server.subscription_start_success";
    private static final String GS_SUBSCRIPTION_CANCEL_SUCCESS_KEY = 
            "getsubscription.server.subscription_cancel_success";
    private static final String GS_SUBSCRIPTION_FAILURE_KEY = 
            "getsubscription.server.subscription_failure";
    private static final String GS_GENERIC_SUBSCRIPTION_ERROR = 
            "getsubscription.server.generic_subscription_error";
    
    private IConfigurationManager confManager;
    private Authenticator authenticator;
    private ISubscriptionManager subscriptionManager;
    private INodeStatusManager nodeStatusManager;
    private MessageResourceUtil messages;
    private Logger log;
    protected User localNode;
    private ProtocolManager protocolManager = null;
    private final static Logger logger = LogManager.getLogger(PostMessageServlet.class); 

    /**
     * Default constructor.
     */
    public UpdateSubscriptionServlet() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Initializes servlet with current configuration.
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                confManager.getAppConf().getKeystoreDir());
        this.localNode = new User(confManager.getAppConf().getNodeName(),
                Role.NODE, null, confManager.getAppConf().getOrgName(),
                confManager.getAppConf().getOrgUnit(),
                confManager.getAppConf().getLocality(),
                confManager.getAppConf().getState(),
                confManager.getAppConf().getCountryCode(),
                confManager.getAppConf().getNodeAddress());
    }
    
    /**
     * Stores subscriptions requested by peer. 
     * @param req Node request
     * @param requestedSubscription Requested subscriptions
     * @return Updated peer subscriptions
     */
    @Transactional(propagation=Propagation.REQUIRED, 
            rollbackFor=Exception.class)
    protected List<Subscription> storePeerSubscriptions(String nodeName,
                List<Subscription> requestedSubscription) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        try {
            for (Subscription requested : requestedSubscription) {
                Subscription current = subscriptionManager.load(
                        Subscription.PEER, nodeName, 
                        requested.getDataSource());
                if (current != null) {
                    String[] messageArgs = {current.getUser(), 
                        current.getDataSource()};
                    if (requested.isActive()) {
                        current.setActive(requested.isActive());
                        current.setSyncronized(true);
                        subscriptionManager.update(current);
                        subscriptions.add(current);
                        log.warn(messages.getMessage(
                            GS_SUBSCRIPTION_START_SUCCESS_KEY, messageArgs));
                    } else {
                        current.setActive(requested.isActive());
                        current.setSyncronized(true);
                        subscriptionManager.delete(current.getId());
                        subscriptions.add(current);
                        // delete status
                        nodeStatusManager.delete(current.getId());
                        log.warn(messages.getMessage(
                            GS_SUBSCRIPTION_CANCEL_SUCCESS_KEY, messageArgs));
                    }
                } else {
                    if (requested.isActive()) {
                        current = new Subscription(System.currentTimeMillis(), 
                                Subscription.PEER, nodeName, 
                                requested.getDataSource(), requested.isActive(), 
                                true);
                        int subscriptionId = subscriptionManager.store(current);
                        subscriptions.add(current);
                        // save status
                        int statusId = nodeStatusManager.store(new Status(0, 0, 0));
                        nodeStatusManager.store(statusId, subscriptionId);
                        String[] messageArgs = {current.getUser(), 
                            current.getDataSource()};
                        log.warn(messages.getMessage(
                            GS_SUBSCRIPTION_START_SUCCESS_KEY, messageArgs));
                    }
                }
            }
        } catch (Exception e) {
            log.error(messages.getMessage(GS_SUBSCRIPTION_FAILURE_KEY));
        }
        return subscriptions;
    }

    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/update_subscription.htm")
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        initConfiguration();
        @SuppressWarnings("unused")
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
      RequestParser requestParser = protocolManager.createParser(request);
      logger.debug("Request arrived using protocol version '" + requestParser.getProtocolVersion() + "'");
      try {
        if (requestParser.isAuthenticated(authenticator)) {
          List<Subscription> requestedSubscriptions = requestParser.getSubscriptions();
          List<Subscription> subscriptions = storePeerSubscriptions(requestParser.getNodeName(), requestedSubscriptions);
          ResponseWriter responseWriter = requestParser.getWriter(response);
          
          if (subscriptions.size() == requestedSubscriptions.size()) {
            responseWriter.subscriptionResponse(localNode.getName(), subscriptions, HttpServletResponse.SC_OK);
          } else if (subscriptions.size() > 0) {
            responseWriter.subscriptionResponse(localNode.getName(), subscriptions, HttpServletResponse.SC_PARTIAL_CONTENT);
          } else {
            responseWriter.messageResponse(messages.getMessage(GS_GENERIC_SUBSCRIPTION_ERROR), HttpServletResponse.SC_NOT_FOUND);
          }
        } else {
          requestParser.getWriter(response).messageResponse(
              messages.getMessage(GS_UNAUTHORIZED_REQUEST_KEY), HttpServletResponse.SC_UNAUTHORIZED);
        }
      } catch (KeyczarException e) {
        requestParser.getWriter(response).messageResponse(
            messages.getMessage(GS_MESSAGE_VERIFIER_ERROR_KEY), HttpServletResponse.SC_UNAUTHORIZED);
      } catch (Exception e) {
        requestParser.getWriter(response).messageResponse(
            messages.getMessage(GS_INTERNAL_SERVER_ERROR_KEY), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
    
    
    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(IConfigurationManager confManager) {
        this.confManager = confManager;
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
     * @param nodeStatusManager the nodeStatusManager to set
     */
    @Autowired
    public void setNodeStatusManager(INodeStatusManager nodeStatusManager) {
        this.nodeStatusManager = nodeStatusManager;
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
    
    /**
     * @param protocolManager the protocol manager to use
     */
    @Autowired
    public void setProtocolManager(ProtocolManager protocolManager) {
      this.protocolManager = protocolManager;
    }
}
