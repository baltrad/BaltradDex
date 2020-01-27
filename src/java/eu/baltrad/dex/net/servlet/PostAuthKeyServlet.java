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

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

import eu.baltrad.beast.exchange.ExchangeMessage;
import eu.baltrad.beast.exchange.IExchangeManager;
import eu.baltrad.beast.security.Authorization;
import eu.baltrad.beast.security.AuthorizationRequest;
import eu.baltrad.beast.security.IAuthorizationRequestManager;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.beast.security.mail.IAdminMailer;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.log.StickyLevel;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * Receives and handles post key requests new style
 * @author anders
 */
@Controller
public class PostAuthKeyServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  private static final String PK_KEY_RECEIVED = "postkey.server.key_received";

  /**
   * The configuration manager
   */
  private IConfigurationManager confManager;
  
  /**
   * Message helper
   */
  private MessageResourceUtil messages;
  
  /**
   * DEX logger
   */
  private Logger log;

  /**
   * Exchange manager taking care of the actual authorization request handling
   */
  private IExchangeManager exchangeManager = null;

  /**
   * Security manager keeping track on security
   */
  private ISecurityManager securityManager = null;

  /**
   * Request manager for getting access to authorizations
   */
  private IAuthorizationRequestManager authorizationRequestManager = null;

  /**
   * Admin mailer when receiving a request
   */
  private IAdminMailer adminMailer = null;

  /**
   * The debug logger
   */
  private final static Logger logger = LogManager.getLogger(PostAuthKeyServlet.class);

  /**
   * Default constructor.
   */
  public PostAuthKeyServlet() {
    this.log = Logger.getLogger("DEX");
  }

  /**
   * @param request  servlet requests
   * @param response servlet responses
   * @return the model and view
   */
  @RequestMapping("/exchangeManager.htm")
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    logger.info("/exchangeManager.htm ContentType: " + request.getContentType());
    if (request.getContentType().startsWith("application/json")) {
      try {
        ExchangeMessage message = exchangeManager.parse(request.getInputStream());
        logger.info("Message = " + message);
        Date now = new Date();
        if (message != null && message.isAuthorizationRequest()) {
          logger.info("AuthorizationRequest:  " + message.getAuthorizationRequest().getNodeName() + " from "
              + request.getRemoteAddr());
          message.getAuthorizationRequest().setRemoteHost(request.getRemoteAddr());
          List<AuthorizationRequest> requests = authorizationRequestManager.findByRemoteHost(request.getRemoteAddr());
          for (AuthorizationRequest r : requests) {
            logger.info(
                "Request exist with timeoffset = " + Math.abs(r.getReceivedAt().getTime() - now.getTime()) + " ms");
            if (Math.abs(r.getReceivedAt().getTime() - now.getTime()) < 10000) {
              logger.error("Possible DOS attack? Received request from same address within 10 seconds");
              return null;
            }
          }
        }

        ExchangeMessage handledMessage = exchangeManager.receive(message);
        if (handledMessage != null && handledMessage.isAuthorizationRequest()) {
          log.log(StickyLevel.STICKY, messages.getMessage(PK_KEY_RECEIVED, new String[] {handledMessage.getAuthorizationRequest().getNodeName()}));

          if (adminMailer != null) {
            String mailTo = confManager.getAppConf().getAdminEmail();
            String linkURL = confManager.getAppConf().getNodeAddress();
            Authorization local = securityManager.getLocal();
            AuthorizationRequest remote = handledMessage.getAuthorizationRequest();
            if (local != null) {
              mailTo = local.getNodeEmail();
              linkURL = local.getNodeAddress();

            }
            try {
              adminMailer.sendKeyApprovalRequest(mailTo,
                  "Key approval request received from " + remote.getNodeName() + " with IP " + remote.getRemoteHost(),
                  linkURL + "/BaltradDex/authorization_request.htm?uuid=" + remote.getRequestUUID(), remote.getMessage(),
                  remote);
            } catch (Error e) {
              logger.error("Failure when trying to send mail", e);
            }
          }
        }
      } catch (Exception e) {
        logger.error("Failure", e);
      }
    }

    return new ModelAndView();
  }

  /**
   * @return the admin mailer
   */
  public IAdminMailer getAdminMailer() {
    return adminMailer;
  }

  /**
   * @param adminMailer the admin mailer
   */
  @Autowired
  public void setAdminMailer(IAdminMailer adminMailer) {
    logger.info("Setting admin mailer");
    this.adminMailer = adminMailer;
  }

  /**
   * @param confManager the configuration manager
   */
  @Autowired
  public void setConfManager(IConfigurationManager confManager) {
    this.confManager = confManager;
  }

  /**
   * @param exchangeManager the exchange manager
   */
  @Autowired
  public void setExchangeManager(IExchangeManager exchangeManager) {
    this.exchangeManager = exchangeManager;
  }

  /**
   * @param securityManager the security manager
   */
  @Autowired
  public void setSecurityManager(ISecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  /**
   * @param authorizationRequestManager the request manager
   */
  @Autowired
  public void setAuthorizationRequestManager(IAuthorizationRequestManager authorizationRequestManager) {
    this.authorizationRequestManager = authorizationRequestManager;
  }
  
  /**
   * @param messages the message handler
   */
  @Autowired
  public void setMessageResourceUtil(MessageResourceUtil messages) {
    this.messages = messages;
  }
}
