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

package eu.baltrad.dex.auth.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.auth.util.SecurityManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * Login controller class implementing basic user authentication functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.0
 */

@Controller
public class LoginController {

    private UserManager userManager;
    private Logger log;
    
    /**
     * Default constructor.
     */
    public LoginController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
        log.info("BALTRAD system started");
    }
    
    /**
     * Renders login page.
     * @param model Model
     * @return Login page name
     */
    @RequestMapping("/login.htm")
    public String login(Model model) {
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
        model.addAttribute("login_error", "true");
        return "login";
    }
    
    /**
     * Sets session user and renders welcome page.
     * @param model Model 
     * @param principal Principal
     * @param session Http session
     * @return Welcome page name
     */
    @RequestMapping("/home.htm")
    public String welcome(Model model, Principal principal, HttpSession session) 
    {
        User user = userManager.load(principal.getName());
        SecurityManager.setSessionUser(session, user);
        log.info("User " + user.getName() + " logged on");
        return "home";
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
        model.addAttribute("logout_message", "true");
        return "login";
    }
    
    /**
     * Sets reference to user manager object.
     * @param userManager User manager object.
     */
    @Autowired
    public void setUserManager(UserManager userManager ) { 
        this.userManager = userManager; 
    }
    
}
//--------------------------------------------------------------------------------------------------
