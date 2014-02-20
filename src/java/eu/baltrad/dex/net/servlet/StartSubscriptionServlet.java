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
import eu.baltrad.dex.datasource.model.DataSource;
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

import org.keyczar.exceptions.KeyczarException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;

/**
 * Receives and handles subscription requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@SuppressWarnings("serial")
@Controller
public class StartSubscriptionServlet extends HttpServlet {
 
    private static final String PS_UNAUTHORIZED_REQUEST_KEY =
            "postsubscription.server.unauthorized_request";
    private static final String PS_MESSAGE_VERIFIER_ERROR_KEY = 
            "postsubscription.server.message_verifier_error";    
    private static final String PS_INTERNAL_SERVER_ERROR_KEY = 
            "postsubscription.server.internal_server_error";
    private static final String PS_SUBSCRIPTION_SUCCESS_KEY = 
            "postsubscription.server.subscription_success";
    private static final String PS_SUBSCRIPTION_FAILURE_KEY = 
            "postsubscription.server.subscription_failure";
    private static final String PS_GENERIC_SUBSCRIPTION_ERROR = 
            "postsubscription.server.generic_subscription_error";
    
    private IConfigurationManager confManager;
    private Authenticator authenticator;
    private ISubscriptionManager subscriptionManager;
    private INodeStatusManager nodeStatusManager;
    private MessageResourceUtil messages;
    private ProtocolManager protocolManager;
    
    private Logger log;
    
    private final static Logger logger = LogManager.getLogger(StartSubscriptionServlet.class);
    
    protected User localNode;;
    
    /**
     * Default constructor.
     */
    public StartSubscriptionServlet() {
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
     * @param requestedDataSources Requested data sources
     * @return Subscribed data sources
     */
    @Transactional(propagation=Propagation.REQUIRED, 
            rollbackFor=Exception.class)
    protected Set<DataSource> storePeerSubscriptions(String nodeName, Set<DataSource> requestedDataSources) {
        Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
        try {
            for (DataSource ds : requestedDataSources) {
                // Save or update depending on whether subscription exists
                Subscription requested = new Subscription(System.currentTimeMillis(), Subscription.PEER, nodeName, ds.getName(), true, true);
                Subscription existing = subscriptionManager.load(Subscription.PEER, nodeName, ds.getName());
                String[] msgArgs = {ds.getName(), localNode.getName(), nodeName};
                if (existing == null) {
                    int subscriptionId = subscriptionManager.store(requested);
                    // save status
                    int statusId = nodeStatusManager.store(new Status(0, 0, 0));
                    nodeStatusManager.store(statusId, subscriptionId);
                    subscribedDataSources.add(new DataSource(
                            ds.getName(), ds.getType(), ds.getDescription(),
                            ds.getSource(), ds.getFileObject()));
                    log.warn(messages.getMessage(PS_SUBSCRIPTION_SUCCESS_KEY, msgArgs));
                } else {
                    int subId = existing.getId(); 
                    requested.setId(subId);
                    subscriptionManager.update(requested);
                    subscribedDataSources.add(new DataSource(
                            ds.getName(), ds.getType(), ds.getDescription(),
                            ds.getSource(), ds.getFileObject()));
                    // update status - reset uploads
                    Status s = nodeStatusManager.load(subId);
                    s.setUploads(0);
                    nodeStatusManager.update(s, subId);
                    
                    log.warn(messages.getMessage(PS_SUBSCRIPTION_SUCCESS_KEY, 
                            msgArgs));
                }
            }
        } catch (Exception e) {
            log.error(messages.getMessage(PS_SUBSCRIPTION_FAILURE_KEY));
        }    
        return subscribedDataSources;
    }
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/start_subscription.htm")
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
      RequestParser parser = protocolManager.createParser(request);
      logger.debug("Request arrived using protocol version '" + parser.getProtocolVersion() + "'");

      try {
        if (parser.isAuthenticated(authenticator)) {
          Set<DataSource> requestedDataSources = parser.getDataSources();
          Set<DataSource> subscribedDataSources = storePeerSubscriptions(parser.getNodeName(), requestedDataSources);
          ResponseWriter responseWriter = parser.getWriter(response);
          if (subscribedDataSources.equals(requestedDataSources)) {
            responseWriter.dataSourcesResponse(localNode.getName(), 
                subscribedDataSources, HttpServletResponse.SC_OK);
          } else if (subscribedDataSources.size() > 0) {
            responseWriter.dataSourcesResponse(localNode.getName(), 
                subscribedDataSources, HttpServletResponse.SC_PARTIAL_CONTENT);
          } else {
            responseWriter.messageResponse(
                messages.getMessage(PS_GENERIC_SUBSCRIPTION_ERROR), HttpServletResponse.SC_NOT_FOUND);
          }
        } else {
          parser.getWriter(response).messageResponse(
              messages.getMessage(PS_UNAUTHORIZED_REQUEST_KEY), HttpServletResponse.SC_UNAUTHORIZED);
        }
      } catch (KeyczarException e) {
        parser.getWriter(response).messageResponse(
            messages.getMessage(PS_MESSAGE_VERIFIER_ERROR_KEY), HttpServletResponse.SC_UNAUTHORIZED);
      } catch (Exception e) {
        parser.getWriter(response).messageResponse(
            messages.getMessage(PS_INTERNAL_SERVER_ERROR_KEY), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
     * @param protocolManager the protocol manager to use
     */
    @Autowired
    public void setProtocolManager(ProtocolManager protocolManager) {
      this.protocolManager = protocolManager;
    }
}