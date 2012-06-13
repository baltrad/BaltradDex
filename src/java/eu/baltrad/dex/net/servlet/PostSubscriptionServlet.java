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

import eu.baltrad.dex.net.model.NodeResponse;
import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.net.model.ISubscriptionManager;

import org.springframework.web.servlet.mvc.Controller;
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

import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

/**
 * Receives and handles post subscription requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class PostSubscriptionServlet extends HttpServlet implements Controller {
 
    private static final String PS_UNAUTHORIZED_REQUEST_KEY =
            "postsubscription.server.unauthorized_request";
    private static final String PS_INTERNAL_SERVER_ERROR_KEY = 
            "postsubscription.server.internal_server_error";
    private static final String PS_SUBSCRIPTION_SUCCESS_KEY = 
            "postsubscription.server.subscription_success";
    private static final String PS_SUBSCRIPTION_FAILURE_KEY = 
            "postsubscription.server.subscription_failure";
    private static final String PS_GENERIC_SUBSCRIPTION_ERROR = 
            "postsubscription.server.generic_subscription_error";
    
    private Authenticator authenticator;
    private IJsonUtil jsonUtil;
    private ISubscriptionManager subscriptionManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    private String nodeName;
    private String nodeAddress;
    
    /**
     * Default constructor.
     */
    public PostSubscriptionServlet() {
        /**this.authenticator = new KeyczarAuthenticator(
            InitAppUtil.getConf().getKeystoreDir(),
            InitAppUtil.getConf().getNodeName());
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();*/
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Constructor.
     * @param nodeName Node name
     * @param nodeAddress Node address 
     */
    public PostSubscriptionServlet(String nodeName, String nodeAddress) {
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
    }
    
    /**
     * Reads data source string from http request.
     * @param request Http request
     * @return Data source string
     * @throws IOException 
     */
    private String readDataSources(HttpServletRequest request) 
            throws IOException {
        ServletInputStream sis = request.getInputStream();
        StringWriter writer = new StringWriter();
        String dataSources = "";
        try {
            IOUtils.copy(sis, writer);
            dataSources = writer.toString();
        } finally {
            writer.close();
            sis.close();
        }
        return dataSources;
    }
    
    /**
     * Writes data source string to http response. 
     * @param response Http response
     * @param dataSources Data source string
     * @throws IOException 
     */
    private void writeDataSources(NodeResponse response, String dataSources) 
            throws IOException {
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        try {
            response.setNodeName(nodeName);
            response.setNodeAddress(nodeAddress);
            writer.print(dataSources);
        } finally {
            writer.close();
        }    
    }
    
    /**
     * Stores subscriptions requested by peer.
     * @param request Http request
     * @param requestedDataSources Requested data sources
     * @return Subscribed data sources
     */
    private Set<DataSource> storePeerSubscriptions(HttpServletRequest request,
            Set<DataSource> requestedDataSources) {
        Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
        for (DataSource ds : requestedDataSources) {
            // Save or update depending on whether subscription exists
            Subscription requested = new Subscription(
                System.currentTimeMillis(), 
                authenticator.getNodeName(request),
                ds.getName(), nodeName, 
                Subscription.SUBSCRIPTION_UPLOAD, 
                false, false, nodeAddress);
            Subscription existing = 
                subscriptionManager.load(
                    authenticator.getNodeName(request), ds.getName(), 
                    Subscription.SUBSCRIPTION_UPLOAD);
            String[] messageArgs = {ds.getName(), nodeName, 
                    authenticator.getNodeName(request)};
            if (existing == null) {
                if (subscriptionManager.storeNoId(requested) == 1) {
                    subscribedDataSources.add(new DataSource(
                            ds.getName(), ds.getDescription()));
                    log.warn(messages.getMessage(
                            PS_SUBSCRIPTION_SUCCESS_KEY, messageArgs));
                } else {
                    log.error(messages.getMessage(
                            PS_SUBSCRIPTION_FAILURE_KEY, messageArgs));
                }
            } else {
                requested.setId(existing.getId());
                if (subscriptionManager.update(requested) == 1) {
                    subscribedDataSources.add(new DataSource(
                            ds.getName(), ds.getDescription()));
                    log.warn(messages.getMessage(
                            PS_SUBSCRIPTION_SUCCESS_KEY, messageArgs));
                } else {
                    log.error(messages.getMessage(
                            PS_SUBSCRIPTION_FAILURE_KEY, messageArgs));
                }
            }            
        }
        return subscribedDataSources;
    }
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
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
        NodeResponse res = new NodeResponse(response);
        try {
            authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir(),
                InitAppUtil.getConf().getNodeName(),
                    request.getHeader("Node-Name"));
            nodeName = InitAppUtil.getConf().getNodeName();
            nodeAddress = InitAppUtil.getConf().getNodeAddress();
            
            if (authenticator.authenticate(authenticator.getMessage(
                    request), authenticator.getSignature(request))) {
                String jsonRequested = readDataSources(request);
                Set<DataSource> requestedDataSources = jsonUtil
                            .jsonToDataSources(jsonRequested);
                Set<DataSource> subscribedDataSources = 
                        storePeerSubscriptions(request, requestedDataSources);
                String jsonSubscribed = jsonUtil.dataSourcesToJson(
                        subscribedDataSources);
                if (subscribedDataSources.equals(requestedDataSources)) {
                    writeDataSources(res, jsonSubscribed);
                    res.setStatus(HttpServletResponse.SC_OK);
                } else if (subscribedDataSources.size() > 0) {
                    writeDataSources(res, jsonSubscribed);
                    res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                } else {
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND,
                        messages.getMessage(PS_GENERIC_SUBSCRIPTION_ERROR));
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                    messages.getMessage(PS_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(PS_INTERNAL_SERVER_ERROR_KEY));
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