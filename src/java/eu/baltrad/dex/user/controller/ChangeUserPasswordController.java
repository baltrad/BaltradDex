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

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.Password;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.IUserManager;
import eu.baltrad.dex.user.util.ChangePasswordValidator;
import eu.baltrad.dex.auth.util.SecurityManager;
import eu.baltrad.dex.util.MessageDigestUtil;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Enables password change functionality for non-admin users.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
@Controller
@RequestMapping("/user_settings.htm")
@SessionAttributes("password")
public class ChangeUserPasswordController {
    
    /** Form view */
    private static final String FORM_VIEW = "user_settings";
    /** Password model key */
    private static final String PASSWD_KEY = "password";
    /* Success message key */
    private static final String OK_MSG_KEY = "message";
    /* Error message key */
    private static final String ERROR_MSG_KEY = "error";
    
    private IUserManager userManager;
    private ChangePasswordValidator validator;
    private Logger log;
    
    /**
     * Constructor.
     */
    public ChangeUserPasswordController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
    
    /**
     * @param validator the validator to set
     */
    @Autowired
    public void setValidator(ChangePasswordValidator validator) {
        this.validator = validator;
    }
    
    /**
     * Set up form.
     * @param model Model map
     * @param request Http servlet request
     * @return Form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model, HttpServletRequest request) {
        Password passwd = new Password(SecurityManager
                .getSessionUser(request.getSession()).getName());
        model.addAttribute(PASSWD_KEY, passwd);
        return FORM_VIEW;
    }
    
    /**
     * Process form submission.
     * @param model Model map
     * @param passwd Password object
     * @param result Binding result
     * @return Form view name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(ModelMap model, @ModelAttribute("password") 
            Password passwd, BindingResult result) {    
        validator.validate(passwd, result);
        if (!result.hasErrors()) {
            User user = userManager.load(passwd.getUserName());
            int update = userManager.updatePassword(user.getId(), 
                MessageDigestUtil.createHash("MD5", 16, 
                    passwd.getNewPasswd()));
            if (update > 0) {
                String msg = "Password successfully changed for user " + 
                                user.getName();
                model.addAttribute(OK_MSG_KEY, msg);
                log.warn(msg);
            } else {
                String msg = "Failed to change password for user " + 
                    user.getName();
                model.remove(OK_MSG_KEY);
                model.addAttribute(ERROR_MSG_KEY, msg);
                log.error(msg);
            }
        }
        model.addAttribute(PASSWD_KEY, passwd);
        return FORM_VIEW;
    }
    
}
