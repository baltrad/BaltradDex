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

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.net.response.impl.NodeResponse;
import eu.baltrad.dex.user.manager.IAccountManager;
import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.util.MessageResourceUtil;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;

import org.apache.log4j.Logger;

import org.apache.commons.io.IOUtils;

import org.keyczar.exceptions.KeyczarException;

import java.io.PrintWriter;
import java.io.StringWriter;
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
    /** Message verifier error key */
    private static final String DS_MESSAGE_VERIFIER_ERROR_KEY = 
            "datasource.server.message_verifier_error";
    /** Internal server error key */
    private static final String DS_INTERNAL_SERVER_ERROR_KEY = 
            "datasource.server.internal_server_error";
    
    private IConfigurationManager confManager;
    private Authenticator authenticator;
    private IAccountManager accountManager;
    private IDataSourceManager dataSourceManager;
    private IJsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected Account localNode;
    
    /**
     * Default constructor.
     */
    public DataSourceListServlet() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Initializes servlet with current configuration.
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                confManager.getAppConf().getKeystoreDir());
        this.localNode = new Account(confManager.getAppConf().getNodeName(),
                confManager.getAppConf().getNodeAddress(),
                confManager.getAppConf().getOrgName(),
                confManager.getAppConf().getOrgUnit(),
                confManager.getAppConf().getLocality(),
                confManager.getAppConf().getState(),
                confManager.getAppConf().getCountryCode());   
    }
    
    /**
     * Read http request body.
     * @param request Http request
     * @return JSON string
     * @throws IOException 
     */
    private String readRequest(HttpServletRequest request) 
            throws IOException {
        ServletInputStream sis = request.getInputStream();
        StringWriter writer = new StringWriter();
        String json = "";
        try {
            IOUtils.copy(sis, writer);
            json = writer.toString();
        } finally {
            writer.close();
            sis.close();
        }
        return json;
    }
    
    /**
     * Write to http response output stream.
     * @param response Http response 
     * @param body Response body
     * @param status Status code
     * @throws IOException 
     */
    private void writeResponse(NodeResponse response, String body, int status) 
            throws IOException {
        PrintWriter writer = new PrintWriter(response.getOutputStream());
        try {
            writer.print(body);
            response.setStatus(status);
            response.setNodeName(localNode.getName());
        } finally {
            writer.close();
        }
    }
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/datasource_listing.htm")
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
                // TODO User account will be created when 
                // keys are exchanged
                String json = readRequest(request);
                Account peer = jsonUtil.jsonToUserAccount(json);
                // account not found
                if (accountManager.load(peer.getName()) == null) {
                    peer.setRoleName(Role.PEER);
                    try {
                        accountManager.store(peer);
                        log.warn("New peer account created: " + peer.getName());
                    } catch (Exception e) {
                        throw e;
                    }
                    writeResponse(res, jsonUtil.userAccountToJson(peer),
                            HttpServletResponse.SC_CREATED);
                } else {
                    // account exists
                    Account account = accountManager.load(peer.getName());
                    List<DataSource> userDataSources = dataSourceManager
                            .loadByUser(account.getId());

                    writeResponse(res, jsonUtil.dataSourcesToJson(
                            new HashSet<DataSource>(userDataSources)),
                            HttpServletResponse.SC_OK);
                }
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                        messages.getMessage(DS_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (KeyczarException e) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED,
                    messages.getMessage(DS_MESSAGE_VERIFIER_ERROR_KEY));
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(DS_INTERNAL_SERVER_ERROR_KEY,
                        new String[] {e.getMessage()}));
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
     * @param accountManager the accountManager to set
     */
    @Autowired
    public void setAccountManager(IAccountManager accountManager) {
        this.accountManager = accountManager;
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
