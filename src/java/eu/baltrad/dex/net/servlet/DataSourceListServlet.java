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

import eu.baltrad.dex.net.model.NodeRequest;
import eu.baltrad.dex.net.model.NodeResponse;
import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.IUserManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.IDataSourceManager;
import eu.baltrad.dex.log.util.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.List;
import java.util.HashSet;

/**
 * Receives and handles data source listing requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class DataSourceListServlet extends HttpServlet {
    /** Unauthorized request error key */
    private static final String DS_UNAUTHORIZED_REQUEST_KEY = 
            "datasource.server.unauthorized_request";
    /** Internal server error key */
    private static final String DS_INTERNAL_SERVER_ERROR_KEY = 
            "datasource.server.internal_server_error";
    
    private Authenticator authenticator;
    private IUserManager userManager;
    private IDataSourceManager dataSourceManager;
    private IJsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected String nodeName;
    protected String nodeAddress;
    
    /**
     * Default constructor.
     */
    public DataSourceListServlet() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Initializes servlet with current configuration.
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir());
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
    }
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/get_datasource_listing.htm")
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        initConfiguration();
        HttpSession session = request.getSession(true);
        doGet(request, response);
        return new ModelAndView();
    }
    
    /**
     * Actual mathod processing requests.
     * @param request Http servlet request
     * @param response Http servlet response 
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    {
        NodeRequest req = new NodeRequest(request);
        NodeResponse res = new NodeResponse(response);
        try {
            if (authenticator.authenticate(req.getMessage(), req.getSignature(),
                    req.getNodeName())) {
                // TODO User account will be created when 
                // keys are exchanged
                User user = userManager.load(req.getNodeName());
                if (user == null) {
                    user = new User(req.getNodeName(), 
                        User.ROLE_PEER, req.getNodeAddress());
                    if (user.getId() > 0) {
                        userManager.update(user);
                        log.warn("Peer account updated: " + user.getName());
                    } else {
                        userManager.storeNoId(user);
                        log.warn("New peer account created: " + user.getName());
                    }
                }
                // Get data sources available for the user
                List<DataSource> userDataSources = dataSourceManager
                        .loadByUser(user.getId());
                PrintWriter writer = new PrintWriter(res.getOutputStream());
                try {
                    writer.print(jsonUtil.dataSourcesToJson(
                            new HashSet<DataSource>(userDataSources))); 
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setNodeName(nodeName);
                    res.setNodeAddress(nodeAddress);
                } finally {
                    writer.close();
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                        messages.getMessage(DS_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(DS_INTERNAL_SERVER_ERROR_KEY,
                        new String[] {e.getMessage()}));
        }
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
    
    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * @param jsonUtil the jsonUtil to set
     */
    @Autowired
    public void setJsonUtil(IJsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
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
