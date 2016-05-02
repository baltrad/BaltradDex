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

import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * Receives and handles data source listing requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@SuppressWarnings("serial")
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
    private IUserManager userManager;
    private IDataSourceManager dataSourceManager;
    private MessageResourceUtil messages;
    private Logger log;
    private final static Logger logger = LogManager.getLogger(DataSourceListServlet.class);
    
    private ProtocolManager protocolManager = null;
    
    protected User localNode;
    
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
        this.localNode = new User(confManager.getAppConf().getNodeName(),
                Role.NODE, null, confManager.getAppConf().getOrgName(),
                confManager.getAppConf().getOrgUnit(),
                confManager.getAppConf().getLocality(),
                confManager.getAppConf().getState(),
                confManager.getAppConf().getCountryCode(),
                confManager.getAppConf().getNodeAddress());   
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
        logger.debug("doPost: Authenticating request");
        if (parser.isAuthenticated(authenticator)) {
          logger.debug("doPost: Request authenticated");
          User peer = parser.getUserAccount();
          User loadedPeer = userManager.load(peer.getName());
          if (loadedPeer == null) {
            logger.debug("doPost: " + peer.getName() + " not found, creating account");
            peer.setRole(Role.PEER);
            userManager.store(peer);
            log.warn("New peer account created: " + peer.getName());
            parser.getWriter(response).userAccountResponse(localNode.getName(), localNode, HttpServletResponse.SC_CREATED);
          } else {
            logger.debug("doPost: " + peer.getName() + " already exists, returning available sources");
            List<DataSource> dataSources = dataSourceManager.load(loadedPeer.getId(), DataSource.LOCAL);
            parser.getWriter(response).dataSourcesResponse(localNode.getName(), dataSources, HttpServletResponse.SC_OK);
          }
        } else {
          logger.debug("doPost: Could not authenticate request");
          String msg = messages.getMessage(DS_UNAUTHORIZED_REQUEST_KEY);
          parser.getWriter(response).messageResponse(msg, HttpServletResponse.SC_UNAUTHORIZED);
        }
      } catch (KeyczarException e) {
        String msg = messages.getMessage(DS_MESSAGE_VERIFIER_ERROR_KEY);
        logger.error("doPost", e);
        parser.getWriter(response).messageResponse(msg, HttpServletResponse.SC_UNAUTHORIZED);
      } catch (Exception e) {
        String msg = messages.getMessage(DS_INTERNAL_SERVER_ERROR_KEY, new Object[]{e.getMessage()});
        logger.error("doPost", e);
        parser.getWriter(response).messageResponse(msg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

    /**
     * @param protocolManager the protocol manager to use
     */
    @Autowired
    public void setProtocolManager(ProtocolManager protocolManager) {
      this.protocolManager = protocolManager;
    }
}
