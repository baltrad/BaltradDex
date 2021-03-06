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
import eu.baltrad.dex.user.manager.impl.UserManager;
import eu.baltrad.dex.auth.manager.SecurityManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.IOException;

/**
 * Select and remove user account functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.0
 */
@Controller
public class RemoveAccountController {
    
    private static final String REMOVE_USER_ACCOUNT_VIEW = 
            "user_remove";
    private static final String REMOVE_SELECTED_USER_ACCOUNT_VIEW = 
            "user_remove_selected";
    private static final String REMOVE_USER_ACCOUNT_STATUS_VIEW = 
            "user_remove_status";
    
    private static final String ACCOUNTS_KEY = "accounts";
    private static final String REMOVE_ACCOUNT_OK_MSG_KEY = 
            "removeaccount.success";
    private static final String REMOVE_ACCOUNT_COMPLETED_OK_MSG_KEY = 
            "removeaccount.completed_success";
    private static final String REMOVE_ACCOUNT_COMPLETED_ERROR_MSG_KEY = 
            "removeaccount.completed_failure";
    
    private static final String OK_MSG_KEY = "user_remove_success";
    private static final String ERROR_MSG_KEY = "user_remove_error";
    
    private UserManager accountManager;
    private PlatformTransactionManager transactionManager;
    private MessageResourceUtil messages;
    private Logger log;

    /**
     * Constructor.
     */
    public RemoveAccountController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Lists all registered user accounts except for currently signed user.
     * @param request Http request
     * @param model Model map 
     * @return View name
     */
    @RequestMapping("/user_remove.htm")
    public String removeUserAccount(HttpServletRequest request, ModelMap model) 
    {
        List<User> all = accountManager.load();
        List<User> users = new ArrayList<User>();
        for (User user : all) {
            if (!user.getName().equals(SecurityManager.getSessionUser(
                    request.getSession()).getName()) 
                    && !user.getRole().equals(Role.NODE)) {
                users.add(user);
            }
        }
        Collections.sort(users);
        model.addAttribute(ACCOUNTS_KEY, users);
        return REMOVE_USER_ACCOUNT_VIEW;
    }
    
    /**
     * Lists user accounts selected for removal.
     * @param request Http request
     * @param response Http response
     * @param model Model map
     * @return View name
     * @throws IOException 
     */
    @RequestMapping("/user_remove_selected.htm")
    public String removeSelectedUserAccount(HttpServletRequest request, 
            HttpServletResponse response, ModelMap model) throws IOException {
        String[] userIds = request.getParameterValues(ACCOUNTS_KEY);
        if (userIds != null) {
            List<User> users = new ArrayList<User>();
            for (int i = 0; i < userIds.length; i++) {
                users.add(accountManager.load(Integer.parseInt(userIds[i])));
            }
            Collections.sort(users);
            model.addAttribute(ACCOUNTS_KEY, users);
        } else {
            response.sendRedirect("user_remove.htm");
        }
        return REMOVE_SELECTED_USER_ACCOUNT_VIEW;
    }
    
    /**
     * Shows user account removal status.
     * @param request Http request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/user_remove_status.htm")
    public String removeUserAccountStatus(HttpServletRequest request, 
                    ModelMap model) {
        String[] userIds = request.getParameterValues(ACCOUNTS_KEY);
        // begin transaction
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            for (int i = 0; i < userIds.length; i++) {
                User user = accountManager.load(
                        Integer.parseInt(userIds[i]));
                accountManager.delete(Integer.parseInt(userIds[i]));
                String msg = messages.getMessage(REMOVE_ACCOUNT_OK_MSG_KEY, 
                                new Object[] {user.getName()});
                log.warn(msg);
            }    
            transactionManager.commit(status);
            request.getSession().setAttribute(OK_MSG_KEY, 
                messages.getMessage(REMOVE_ACCOUNT_COMPLETED_OK_MSG_KEY));
        } catch (Exception e) {   
            transactionManager.rollback(status);
            request.getSession().setAttribute(ERROR_MSG_KEY, 
                messages.getMessage(REMOVE_ACCOUNT_COMPLETED_ERROR_MSG_KEY));
        }
        return REMOVE_USER_ACCOUNT_STATUS_VIEW;
    }
    
    /**
     * @param userManager Reference to user manager object
     */
    @Autowired
    public void setAccountManager(UserManager accountManager) { 
        this.accountManager = accountManager; 
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }

    /**
     * @param transactionManager the transactionManager to set
     */
    @Autowired
    public void setTransactionManager(
            PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
}

