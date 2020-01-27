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

import eu.baltrad.beast.security.Authorization;
import eu.baltrad.beast.security.AuthorizationRequest;
import eu.baltrad.beast.security.IAuthorizationRequestManager;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.beast.security.mail.IAdminMailer;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.keystore.manager.IKeystoreManager;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.request.INodeRequest;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.net.response.impl.NodeResponse;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.keystore.model.Key;
import eu.baltrad.dex.log.StickyLevel;
import eu.baltrad.dex.util.CompressDataUtil;
import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Receives and handles post key requests.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
@Controller
public class PostKeyServlet extends HttpServlet {
    
    private static final String INCOMING_KEY_DIR = ".incoming";
    private static final String PK_INTERNAL_SERVER_ERROR_KEY = 
            "postkey.server.internal_server_error";
    private static final String PK_KEY_RECEIVED = "postkey.server.key_received";
    
    private IConfigurationManager confManager;

    private MessageResourceUtil messages;
    
    private ISecurityManager securityManager = null;

    private IAuthorizationRequestManager authorizationRequestManager = null;
    
    private IAdminMailer adminMailer = null;

    private Logger log;
    
    private final static Logger logger = LogManager.getLogger(PostKeyServlet.class); 
    
    /**
     * Default constructor.
     */
    public PostKeyServlet() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Gets the bytes from the stream
     */
    protected byte[] getBytesFromStream(InputStream is) {
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(is, baos);
        return baos.toByteArray();
      } catch (Exception e) {
        logger.error("Failed to extract key content: " + e.getMessage(), e);
        throw new RuntimeException("Failed to extract key content: " + e.getMessage(), e);
      }
    }
    
    
    /**
     * Handles incoming HTTP request.
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return Model and view 
     */
    @RequestMapping("/post_key.htm")
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        doPost(request, response);
        return new ModelAndView();
    }
    
    /**
     * Process HTTP request and send response. 
     * @param request HTTP servlet request
     * @param response HTTP servlet response 
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        NodeRequest req = new NodeRequest(request);
        NodeResponse res = new NodeResponse(response);
        logger.debug("Request arrived using protocol version '" + req.getProtocolVersion() + "'");

        try {
          AuthorizationRequest remote = new AuthorizationRequest();
          Authorization local = securityManager.getLocal();
          String mailTo = confManager.getAppConf().getAdminEmail();
          String linkURL = confManager.getAppConf().getNodeAddress();
          
          remote.setChecksum((String)request.getAttribute("Content-MD5"));
          remote.setMessage("Old style key request. Note that node address and email has been autogenerated. Verify with requestor first, then adjust and approve.");
          remote.setNodeAddress("http://" + request.getRemoteAddr() + ":8080");
          remote.setNodeName(req.getNodeName());
          remote.setNodeEmail(req.getNodeName());
          remote.setOutgoing(false);
          remote.setRemoteHost(request.getRemoteHost());
          remote.setRequestUUID(UUID.randomUUID().toString());
          remote.setPublicKey(getBytesFromStream(request.getInputStream()));
          remote.setReceivedAt(new Date());
          remote.setAutorequest(false);
          authorizationRequestManager.add(remote);
          
          log.log(StickyLevel.STICKY, messages.getMessage(PK_KEY_RECEIVED, new String[] {req.getNodeName()}));
          
          if (local != null) {
            mailTo = local.getNodeEmail();
            linkURL = local.getNodeAddress();
            
          }
          adminMailer.sendKeyApprovalRequest(mailTo, 
              "Key approval request received from " + remote.getNodeName() + " with IP " + remote.getRemoteHost(), 
              linkURL + "/BaltradDex/authorization_request.htm?uuid="+remote.getRequestUUID(), 
              remote.getMessage(), 
              remote);
        } catch (Exception e) {
            logger.error("doPost failed: " + e.getMessage(), e);
            log.error(messages.getMessage(PK_INTERNAL_SERVER_ERROR_KEY));
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    messages.getMessage(PK_INTERNAL_SERVER_ERROR_KEY));
        }
    }        

    /**
     * @param confManager the confManager to set
     */
    @Autowired
    public void setConfManager(IConfigurationManager confManager) {
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
     * @param securityManager the securityManager to set
     */
    @Autowired
    public void setSecurityManager(ISecurityManager securityManager) {
      this.securityManager = securityManager;
    }
    
    /**
     * @param authorizationRequestManager the authorizationRequestManager to set
     */
    @Autowired
    public void setAuthorizationRequestManager(IAuthorizationRequestManager authorizationRequestManager) {
      this.authorizationRequestManager = authorizationRequestManager;
    }
    
    /**
     * @param adminMailer the adminMailer to set
     */
    @Autowired
    public void setAdminMailer(IAdminMailer adminMailer) {
      this.adminMailer = adminMailer;
    }
    
}
