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

package eu.baltrad.dex.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.baltrad.dex.auth.manager.SecurityManager;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.util.MessageResourceUtil;

/**
 * Login controller class implementing basic user authentication functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class LoginController {

    private static final String INIT_ERROR = "login.controller.node_init_error";
    private static final String LOGIN_ERROR = "login.controller.login_error";
    private static final String LOGOUT_MSG = "login.controller.logout_message";
    
    private ConfigurationManager confManager;
    private MessageResourceUtil messages;
    private Logger log;
    /**
     * Default constructor.
     */
    public LoginController() {
        this.log = Logger.getLogger("DEX");
        log.info("BALTRAD system started");
    }
    
    /**
     * Renders login page.
     * @param model Model
     * @return Login page name
     */
    @RequestMapping("/login.htm")
    public String login(Model model, HttpServletRequest request, HttpSession session) {
      //logger.info("Entering /login.htm");
      return "login";
    }    
    
    /**
     * In case of errors, redirects to login page and sets login error 
     * attribute.
     * @param model Model
     * @return Login page name
     */
    @RequestMapping("/login_failed.htm")
    public String loginFailed(Model model) {
        model.addAttribute("login_error", messages.getMessage(LOGIN_ERROR));
        return "login";
    }
    
    /**
     * Resets session user, sets logout attribute and renders login page.
     * @param model Model 
     * @param session Http session
     * @return Login page name
     */
    @RequestMapping("/logout.htm")
    public String logout(Model model, HttpSession session) {
        SecurityManager.resetSessionUser(session);
        SecurityManager.resetSessionRole(session);
        model.addAttribute("logout_msg", messages.getMessage(LOGOUT_MSG));
        return "login";
    }
    
    /**
     * Load node name as model attribute. 
     * @return Node name
     */
    @ModelAttribute("init_error")
    public String getInitStatus() {
        if (confManager.getAppConf() == null) {
            return messages.getMessage(INIT_ERROR);
        }
        return null;
    }
    
    /**
     * Load node name as model attribute. 
     * @return Node name
     */
    @ModelAttribute("node_name")
    public String getNodeName() {
        return confManager.getAppConf().getNodeName();
    }
    
    /**
     * Load admin email as model attribute. 
     * @return Admin email
     */
    @ModelAttribute("admin_email")
    public String getAdminEmail() {
        return confManager.getAppConf().getAdminEmail();
    }

    /**
     * @param confManager the confManager to set
     */
    @Autowired
    public void setConfManager(ConfigurationManager confManager) {
        this.confManager = confManager;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

