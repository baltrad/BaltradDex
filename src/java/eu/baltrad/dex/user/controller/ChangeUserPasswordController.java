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

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.validator.PasswordValidator;
import eu.baltrad.dex.auth.manager.SecurityManager;
import eu.baltrad.dex.util.MessageDigestUtil;
import eu.baltrad.dex.util.MessageResourceUtil;
import java.util.List;

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
import org.springframework.validation.FieldError;

/**
 * Enables password change functionality for non-admin users.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
@Controller
@RequestMapping("/user_settings.htm")
@SessionAttributes("user_account")
public class ChangeUserPasswordController {
    
    private static final String FORM_VIEW = "user_settings";
    
    private static final String USER_ACCOUNT_MODEL_KEY = "user_account";
    private static final String CHANGE_PASSWORD_ERROR_MSG_KEY = 
            "changepassword.failure";
    private static final String CHANGE_PASSWORD_OK_MSG_KEY = 
            "changepassword.success";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    
    private IUserManager userManager;
    private PasswordValidator validator;
    private MessageResourceUtil messages;
    private Logger log;
    
    /**
     * Constructor.
     */
    public ChangeUserPasswordController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Set up form.
     * @param model Model map
     * @param request Http servlet request
     * @return Form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model, HttpServletRequest request) {
        User user = userManager.load(SecurityManager
                .getSessionUser(request.getSession()).getId()); 
        model.addAttribute(USER_ACCOUNT_MODEL_KEY, user);
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
    public String processSubmit(HttpServletRequest request, ModelMap model, 
            @ModelAttribute("user_account") User user, BindingResult result) {
        validator.validate(request, user, result);
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            userManager.updatePassword(user.getId(), 
                    MessageDigestUtil.createHash(
                        "MD5", 16, user.getPassword()));
            String msg = messages.getMessage(CHANGE_PASSWORD_OK_MSG_KEY, 
                            new Object[] {user.getName()});
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);        
        } catch (Exception e) {
            String msg = messages.getMessage(CHANGE_PASSWORD_ERROR_MSG_KEY, 
                            new Object[] {user.getName()});
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);   
        }
        return FORM_VIEW;
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
    public void setValidator(PasswordValidator validator) {
        this.validator = validator;
    }
    
    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}
