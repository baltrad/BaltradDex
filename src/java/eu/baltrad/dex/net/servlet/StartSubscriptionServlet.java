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
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.net.response.impl.NodeResponse;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.keyczar.exceptions.KeyczarException;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;

import org.apache.log4j.Logger;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;

/**
 * Receives and handles subscription requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
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
    private IJsonUtil jsonUtil;
    private ISubscriptionManager subscriptionManager;
    private MessageResourceUtil messages;
    private Logger log;
    
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
            IOUtils.copy(sis, writer, "UTF-8");
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
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
        try {
            response.setNodeName(localNode.getName());
            writer.print(dataSources);
        } finally {
            writer.close();
        }    
    }
    
    /**
     * Stores subscriptions requested by peer.
     * @param req Node request
     * @param requestedDataSources Requested data sources
     * @return Subscribed data sources
     */
    @Transactional(propagation=Propagation.REQUIRED, 
            rollbackFor=Exception.class)
    private Set<DataSource> storePeerSubscriptions(NodeRequest req,
            Set<DataSource> requestedDataSources) {
        Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
        try {
            for (DataSource ds : requestedDataSources) {
                // Save or update depending on whether subscription exists
                Subscription requested = new Subscription(
                        System.currentTimeMillis(), Subscription.PEER, 
                        req.getNodeName(), ds.getName(), true, true);
                Subscription existing = subscriptionManager.load(
                        Subscription.PEER, req.getNodeName(), ds.getName());
                String[] msgArgs = {ds.getName(), localNode.getName(), 
                        req.getNodeName()};
                if (existing == null) {
                    subscriptionManager.store(requested);
                    subscribedDataSources.add(new DataSource(
                            ds.getName(), ds.getType(), ds.getDescription()));
                    log.warn(messages.getMessage(PS_SUBSCRIPTION_SUCCESS_KEY, 
                            msgArgs));
                } else {
                    requested.setId(existing.getId());
                    subscriptionManager.update(requested);
                    subscribedDataSources.add(new DataSource(
                            ds.getName(), ds.getType(), ds.getDescription()));
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
            if (authenticator.authenticate(req.getMessage(), 
                    req.getSignature(), req.getNodeName())) {
                String jsonRequested = readDataSources(request);
                
                //System.out.println("StartSubscriptionServlet::doPost(): jsonRequested - " + jsonRequested);
                
                
                Set<DataSource> requestedDataSources = jsonUtil
                            .jsonToDataSources(jsonRequested);
                
                
                
                
                Set<DataSource> subscribedDataSources = 
                        storePeerSubscriptions(req, requestedDataSources);
                
                
                /*if (subscribedDataSources != null) {
                    System.out.println("StartSubscriptionServlet::doPost(): subscribedDataSources size - " +
                        subscribedDataSources.size());
                } else {
                    System.out.println("StartSubscriptionServlet::doPost(): subscribedDataSources is null");
                }*/
                
                
                
                String jsonSubscribed = jsonUtil.dataSourcesToJson(
                        subscribedDataSources);
                
                //System.out.println("StartSubscriptionServlet::doPost(): jsonSubcribed - " + jsonSubscribed);
                
                
                if (subscribedDataSources.equals(requestedDataSources)) {
                    writeDataSources(res, jsonSubscribed);
                    res.setStatus(HttpServletResponse.SC_OK);
                    
                    //System.out.println("StartSubscriptionServlet::doPost(): SC_OK");
                    
                } else if (subscribedDataSources.size() > 0) {
                    writeDataSources(res, jsonSubscribed);
                    res.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    
                    //System.out.println("StartSubscriptionServlet::doPost(): SC_PARTIAL_CONTENT");
                    
                } else {
                    
                    //System.out.println("StartSubscriptionServlet::doPost(): SC_NOT_FOUND");
                    
                    res.setStatus(HttpServletResponse.SC_NOT_FOUND,
                        messages.getMessage(PS_GENERIC_SUBSCRIPTION_ERROR));
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                    messages.getMessage(PS_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (KeyczarException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
                    messages.getMessage(PS_MESSAGE_VERIFIER_ERROR_KEY));
            
            //System.out.println("StartSubscriptionServlet::doPost(): KeyczarException - " + e.getMessage());
            
        } catch (Exception e) {
            
            //System.out.println("StartSubscriptionServlet::doPost(): Exception - " + e.getMessage());
            
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(PS_INTERNAL_SERVER_ERROR_KEY));
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