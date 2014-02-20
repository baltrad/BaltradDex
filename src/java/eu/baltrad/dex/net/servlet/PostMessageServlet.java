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

import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.dex.config.manager.IConfigurationManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;

import org.keyczar.exceptions.KeyczarException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Receives and handles post message requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
@SuppressWarnings("serial")
@Controller
public class PostMessageServlet extends HttpServlet {
    private static final String PM_MESSAGE_VERIFIER_ERROR_KEY = 
            "postmessage.server.message_verifier_error"; 
    private static final String PM_UNAUTHORIZED_REQUEST_KEY =
            "postmessage.server.unauthorized_request";
    private static final String PM_INTERNAL_SERVER_ERROR_KEY = 
            "postmessage.server.internal_server_error";
    
    private Logger log;
    private IConfigurationManager confManager;
    private Authenticator authenticator;
    private MessageResourceUtil messages;
    private IBltMessageManager bltMessageManager;
    
    private ProtocolManager protocolManager = null;
    private final static Logger logger = LogManager.getLogger(PostMessageServlet.class); 
       
    /**
     * Constructor.
     */
    public PostMessageServlet() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Initializes servlet with current configuration.
     */
    protected void initConfiguration() {
        this.setAuthenticator(new KeyczarAuthenticator(
                 confManager.getAppConf().getKeystoreDir()));
    }
    
    /**
     * Implements Controller interface.
     * @param request Http servlet request
     * @param response Http servlet response
     * @return Model and view
     */
    @RequestMapping("/post_message.htm")
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
          log.info("New message received from " + requestParser.getNodeName());
          IBltXmlMessage msg = requestParser.getBltXmlMessage();
          bltMessageManager.manage(msg);
          requestParser.getWriter(response).statusResponse(HttpServletResponse.SC_OK);
        } else {
          requestParser.getWriter(response).messageResponse(
              messages.getMessage(PM_UNAUTHORIZED_REQUEST_KEY),
              HttpServletResponse.SC_UNAUTHORIZED);
        }
      } catch (KeyczarException e) {
        requestParser.getWriter(response).messageResponse(
            messages.getMessage(PM_MESSAGE_VERIFIER_ERROR_KEY),
            HttpServletResponse.SC_UNAUTHORIZED);
      } catch (Exception e) {
        requestParser.getWriter(response).messageResponse(
            messages.getMessage(PM_INTERNAL_SERVER_ERROR_KEY),
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }

//        NodeRequest req = new NodeRequest(request);
//        NodeResponse res = new NodeResponse(response);
//        logger.debug("Request arrived using protocol version '" + req.getProtocolVersion() + "'");
//        
//        try {
//            if (authenticator.authenticate(req.getMessage(), 
//                    req.getSignature(), req.getNodeName())) {
//                log.info("New message received from " + req.getNodeName());
//                String message = readMessage(req);
//                IBltXmlMessage msg = xmlMessageParser.parse(message);
//                bltMessageManager.manage(msg);
//                res.setStatus(HttpServletResponse.SC_OK);
//            } else {
//                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
//                    messages.getMessage(PM_UNAUTHORIZED_REQUEST_KEY));
//            }
//        } catch (KeyczarException e) {   
//            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
//                    messages.getMessage(PM_MESSAGE_VERIFIER_ERROR_KEY));
//        } catch (Exception e) {
//            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//                    messages.getMessage(PM_INTERNAL_SERVER_ERROR_KEY));
//        }    
    }

    //    @Override
//    public void doPost(HttpServletRequest request, HttpServletResponse response) 
//    {
//        NodeRequest req = new NodeRequest(request);
//        NodeResponse res = new NodeResponse(response);
//        logger.debug("Request arrived using protocol version '" + req.getProtocolVersion() + "'");
//        
//        try {
//            if (authenticator.authenticate(req.getMessage(), 
//                    req.getSignature(), req.getNodeName())) {
//                log.info("New message received from " + req.getNodeName());
//                String message = readMessage(req);
//                IBltXmlMessage msg = xmlMessageParser.parse(message);
//                bltMessageManager.manage(msg);
//                res.setStatus(HttpServletResponse.SC_OK);
//            } else {
//                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
//                    messages.getMessage(PM_UNAUTHORIZED_REQUEST_KEY));
//            }
//        } catch (KeyczarException e) {   
//            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
//                    messages.getMessage(PM_MESSAGE_VERIFIER_ERROR_KEY));
//        } catch (Exception e) {
//            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
//                    messages.getMessage(PM_INTERNAL_SERVER_ERROR_KEY));
//        }    
//    }
    
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
     * @param bltMessageManager the bltMessageManager to set
     */
    @Autowired
    public void setBltMessageManager(IBltMessageManager bltMessageManager) {
        this.bltMessageManager = bltMessageManager;
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
