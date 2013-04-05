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
import eu.baltrad.dex.user.manager.IKeystoreManager;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.net.response.impl.NodeResponse;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.user.model.Key;
import eu.baltrad.dex.util.CompressDataUtil;
import eu.baltrad.dex.util.MessageDigestUtil;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletInputStream;

import java.io.File;

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
    
    private IConfigurationManager confManager;
    private IKeystoreManager keystoreManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    /**
     * Default constructor.
     */
    public PostKeyServlet() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Stores compressed key in the keystore directory.
     * @param request HTTP servlet request
     * @param nodeName Node name
     * @throws RuntimeException  
     */
    protected void storeKey(HttpServletRequest request, String nodeName) 
            throws RuntimeException {
        CompressDataUtil cdu = new CompressDataUtil();
        try {
            ServletInputStream sis = request.getInputStream();
            try {
                String incomingPath = confManager.getAppConf().getKeystoreDir() 
                        + File.separator + INCOMING_KEY_DIR; 
                File incomingDir = new File(incomingPath); 
                if (!incomingDir.exists()) {
                    incomingDir.mkdir();
                }
                cdu.unzip(incomingPath + File.separator + nodeName + ".pub", sis);
                File keyDir = new File(incomingPath + File.separator + nodeName 
                        + ".pub");
                String checksum = MessageDigestUtil.createHash("MD5", 
                        MessageDigestUtil.getBytes(keyDir));
                Key key = new Key(nodeName, checksum, false);
                keystoreManager.store(key);
            } finally {
                sis.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract key from " +
                    "input stream", e);
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    {
        NodeRequest req = new NodeRequest(request);
        NodeResponse res = new NodeResponse(response);
        
        
        log.debug("PostKeyServlet: keystore dir: " + confManager.getAppConf().getKeystoreDir() + 
                File.separator + INCOMING_KEY_DIR);
        log.debug("PostKeyServlet: key received from " + req.getNodeName());
        
        try {
            if (keystoreManager.load(req.getNodeName()) == null) {
                log.info("Public key received from " + req.getNodeName());
                storeKey(request, req.getNodeName());
                res.setStatus(HttpServletResponse.SC_OK);
                
                log.debug("PostKeyServlet: key stored OK");
                
                
            } else {
                res.setStatus(HttpServletResponse.SC_CONFLICT);
                
                log.debug("PostKeyServlet: key already exists ");
                
            }
        } catch (Exception e) {
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
     * @param keystoreManager the keystoreManager to set
     */
    @Autowired
    public void setKeystoreManager(IKeystoreManager keystoreManager) {
        this.keystoreManager = keystoreManager;
    }
    
    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}
