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

import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.manager.IRoleManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.validator.AccountValidator;
import eu.baltrad.dex.util.MessageDigestUtil;
import eu.baltrad.dex.util.WebValidator;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.ui.ModelMap;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 * Allows to configure new user account or to modify an existing one.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.1.6
 */
@Controller
@RequestMapping("/save_user_account.htm")
@SessionAttributes("user_account")
public class SaveAccountController  {
    
    private static final String FORM_VIEW = "save_user_account";
    private static final String SUCCESS_VIEW = "save_user_account_status";
    private static final String USER_ACCOUNT_MODEL_KEY = "user_account";
    private static final String SAVE_ACCOUNT_ERROR_MSG_KEY = 
            "saveaccount.failure";
    private static final String SAVE_ACCOUNT_OK_MSG_KEY = 
            "saveaccount.success";
    private static final String ERROR_MSG_KEY = "error";
    private static final String OK_MSG_KEY = "message";
    
    private IUserManager userManager;
    private IRoleManager roleManager;
    private AccountValidator validator;
    private MessageResourceUtil messages;
    private Logger log;

    /**
     * Constructor.
     */
    public SaveAccountController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Load form backing object.
     * @param userId User id
     * @param model Model map
     * @return Form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam(value="user_id", required=false) 
            String userId, ModelMap model) {
        User user;
        if (WebValidator.validate(userId)) {
            user = userManager.load(Integer.parseInt(userId));
        } else {
            user = new User();
        } 
        model.addAttribute(USER_ACCOUNT_MODEL_KEY, user);
        return FORM_VIEW;
    }
    
    /**
     * Save user account.
     * @param account User account
     * @param request HTTP servlet request
     * @param result Form binding result
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(HttpServletRequest request, ModelMap model, 
            @ModelAttribute("user_account") User user, BindingResult result) {
        validator.validate(request, user, result);
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        user.setPassword(MessageDigestUtil
                .createHash("MD5", user.getPassword()));
        try {
            if (user.getId() > 0) {
                userManager.update(user);
            } else {
                userManager.store(user);
            }
            String msg = messages.getMessage(SAVE_ACCOUNT_OK_MSG_KEY, 
                            new Object[] {user.getName()});
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            String msg = messages.getMessage(SAVE_ACCOUNT_ERROR_MSG_KEY, 
                            new Object[] {user.getName()});
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return SUCCESS_VIEW;
    }
    
    /**
     * Creates list of roles.
     * @return List of roles
     */
    @ModelAttribute("roles")
    public List<String> getRoles() {
        List<Role> roles = roleManager.load();
        List<String> roleNames = new ArrayList<String>();
        for (Role role : roles) {
            if (!role.getName().equals(Role.PEER) 
                    && !role.getName().equals(Role.NODE)) {
                roleNames.add(role.getName());
            }
        }
        return roleNames;
    }
    
    /**
     * @param userManager 
     */
    @Autowired
    public void setUserManager(IUserManager userManager) { 
        this.userManager = userManager; 
    }

    /**
     * @param roleManager the roleManager to set
     */
    @Autowired
    public void setRoleManager(IRoleManager roleManager) {
        this.roleManager = roleManager;
    }

    /**
     * @param validator the validator to set
     */
    @Autowired
    public void setValidator(AccountValidator validator) {
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
