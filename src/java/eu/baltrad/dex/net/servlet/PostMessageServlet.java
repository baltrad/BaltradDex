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

import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.net.model.NodeRequest;
import eu.baltrad.dex.net.model.NodeResponse;
import eu.baltrad.dex.net.util.Authenticator;
import eu.baltrad.dex.net.util.KeyczarAuthenticator;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.beast.parser.IXmlMessageParser;

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

import java.io.StringWriter;
import java.io.IOException;


/**
 * Receives and handles post message requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
@Controller
public class PostMessageServlet extends HttpServlet {
    
    private static final String PM_UNAUTHORIZED_REQUEST_KEY =
            "postmessage.server.unauthorized_request";
    private static final String PM_INTERNAL_SERVER_ERROR_KEY = 
            "postmessage.server.internal_server_error"; 
    
    private Logger log;
    private Authenticator authenticator;
    private MessageResourceUtil messages;
    
    private IXmlMessageParser xmlMessageParser;
    private IBltMessageManager bltMessageManager;
    
    protected String nodeName;
    protected String nodeAddress;
    
    /**
     * Constructor.
     */
    public PostMessageServlet() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Initializes servlet with current configuration.
     */
    protected void initConfiguration() {
        this.setAuthenticator(new KeyczarAuthenticator(
                 InitAppUtil.getConf().getKeystoreDir()));
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
    }
    
    /**
     * Reads message string from http request.
     * @param request Http request
     * @return Message string
     * @throws IOException 
     */
    private String readMessage(HttpServletRequest request) 
            throws IOException {
        ServletInputStream sis = request.getInputStream();
        StringWriter writer = new StringWriter();
        String message = "";
        try {
            IOUtils.copy(sis, writer);
            message = writer.toString();
        } finally {
            writer.close();
            sis.close();
        }
        return message;
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
                log.info("New message received from " + req.getNodeName());
                String message = readMessage(req);
                IBltXmlMessage msg = xmlMessageParser.parse(message);
                bltMessageManager.manage(msg);
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED, 
                    messages.getMessage(PM_UNAUTHORIZED_REQUEST_KEY));
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messages.getMessage(PM_INTERNAL_SERVER_ERROR_KEY));
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
     * @param xmlMessageParser the xmlMessageParser to set
     */
    @Autowired
    public void setXmlMessageParser(IXmlMessageParser xmlMessageParser) {
        this.xmlMessageParser = xmlMessageParser;
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
    
}
