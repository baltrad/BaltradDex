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

import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.net.response.impl.NodeResponse;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.user.model.Account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;

import org.apache.log4j.Logger;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.keyczar.exceptions.KeyczarException;

/**
 * Receives and handles subscription requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller

public class UpdateSubscriptionServlet extends HttpServlet {
    
    private static final String GS_INTERNAL_SERVER_ERROR_KEY = 
            "getsubscription.server.internal_server_error";
    private static final String GS_MESSAGE_VERIFIER_ERROR_KEY = 
            "postsubscription.server.message_verifier_error";    
    private static final String GS_UNAUTHORIZED_REQUEST_KEY =
            "getsubscription.server.unauthorized_request";
    private static final String GS_SUBSCRIPTION_SUCCESS_KEY = 
            "getsubscription.server.subscription_success";
    private static final String GS_SUBSCRIPTION_FAILURE_KEY = 
            "getsubscription.server.subscription_failure";
    private static final String GS_GENERIC_SUBSCRIPTION_ERROR = 
            "getsubscription.server.generic_subscription_error";
    
    private Authenticator authenticator;
    private IJsonUtil jsonUtil;
    private ISubscriptionManager subscriptionManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected Account localNode;
    
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
                InitAppUtil.getConf().getKeystoreDir());
        this.localNode = new Account(InitAppUtil.getConf().getNodeName(),
                InitAppUtil.getConf().getNodeAddress(),
                InitAppUtil.getConf().getOrgName(),
                InitAppUtil.getConf().getOrgUnit(),
                InitAppUtil.getConf().getLocality(),
                InitAppUtil.getConf().getState(),
                InitAppUtil.getConf().getCountryCode());
    }
    
    /**
     * Reads subscripion string from http request.
     * @param request Http request
     * @return Subscription string
     * @throws IOException 
     */
    private String readSubscriptions(HttpServletRequest request) 
            throws IOException {
        ServletInputStream sis = request.getInputStream();
        StringWriter writer = new StringWriter();
        String subscriptions = "";
        try {
            IOUtils.copy(sis, writer);
            subscriptions = writer.toString();
        } finally {
            writer.close();
            sis.close();
        }
        return subscriptions;
    }
    
    /**
     * Writes subscription string to http response. 
     * @param response Http response
     * @param subscriptions Subscriptions string
     * @throws IOException 
     */
    private void writeSubscriptions(NodeResponse response, String subscriptions) 
            throws IOException {
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        try {
            response.setNodeName(localNode.getName());
            writer.print(subscriptions);
        } finally {
            writer.close();
        }    
    }
    
    /**
     * Stores subscriptions requested by peer. 
     * @param req Node request
     * @param requestedSubscription Requested subscriptions
     * @return Updated peer subscriptions
     */
    private List<Subscription> storePeerSubscriptions(NodeRequest req,
                List<Subscription> requestedSubscription) {
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        for (Subscription requested : requestedSubscription) {
            Subscription current = subscriptionManager.load(Subscription.UPLOAD,
                    requested.getUser(), requested.getDataSource());
            if (current != null) {
                String[] messageArgs = {current.getDataSource(), 
                        localNode.getName(), current.getUser()};
                if (requested.isActive()) {
                    current.setActive(requested.isActive());
                    current.setSyncronized(true);
                    try {
                        subscriptionManager.update(current);
                        subscriptions.add(current);
                        log.warn(messages.getMessage(
                                GS_SUBSCRIPTION_SUCCESS_KEY, messageArgs));
                    } catch (Exception e) {
                        log.error(messages.getMessage(
                                GS_SUBSCRIPTION_FAILURE_KEY, messageArgs));
                    }
                } else {
                    current.setActive(requested.isActive());
                    current.setSyncronized(true);
                    try {
                        subscriptionManager.delete(current.getId());
                        subscriptions.add(current);
                        log.warn(messages.getMessage(
                                GS_SUBSCRIPTION_SUCCESS_KEY, messageArgs)); 
                    } catch (Exception e) {
                        log.error(messages.getMessage(
                                GS_SUBSCRIPTION_FAILURE_KEY, messageArgs));
                    }
                }
            } else {
                if (requested.isActive()) {
                    current = new Subscription(System.currentTimeMillis(), 
                            Subscription.UPLOAD, localNode.getName(), 
                            requested.getUser(), requested.getDataSource(), 
                            requested.isActive(), true);
                    String[] messageArgs = {current.getDataSource(),
                        localNode.getName(), current.getUser()};
                    try {
                        subscriptionManager.store(current);
                        subscriptions.add(current);
                        log.warn(messages.getMessage(
                                GS_SUBSCRIPTION_SUCCESS_KEY, messageArgs));
                    } catch (Exception e) {
                        log.error(messages.getMessage(
                                GS_SUBSCRIPTION_FAILURE_KEY, messageArgs));
                    }
                }
            }
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
            if (authenticator.authenticate(req.getMessage(), req.getSignature(),
                    req.getNodeName())) {
                String jsonRequested = readSubscriptions(request);
                List<Subscription> requestedSubscription = jsonUtil
                        .jsonToSubscriptions(jsonRequested); 
                List<Subscription> subscriptions = storePeerSubscriptions(req, 
                        requestedSubscription);
                String jsonSubscribed = jsonUtil.subscriptionsToJson(
                        subscriptions);
                if (subscriptions.size() == requestedSubscription.size()) {
                    writeSubscriptions(res, jsonSubscribed);
                    res.setStatus(HttpServletResponse.SC_OK);
                } else if (subscriptions.size() > 0) {
                    writeSubscriptions(res, jsonSubscribed);
                    res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                } else {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND,
                        messages.getMessage(GS_GENERIC_SUBSCRIPTION_ERROR));
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                    messages.getMessage(GS_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (KeyczarException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
                    messages.getMessage(GS_MESSAGE_VERIFIER_ERROR_KEY));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(GS_INTERNAL_SERVER_ERROR_KEY));
        }
    }
    
    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }

    /**
     * @param jsonUtil the jsonUtil to set
     */
    @Autowired
    public void setJsonUtil(IJsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;

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
