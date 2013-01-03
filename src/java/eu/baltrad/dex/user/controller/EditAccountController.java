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

import eu.baltrad.dex.user.manager.impl.AccountManager;
import eu.baltrad.dex.user.model.Account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Collections;

/**
 * Creates list of editable user accounts.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0
 */
@Controller
public class EditAccountController {

    private static final String SUCCESS_VIEW = "edit_user_account";
    private static final String ACCOUNTS_KEY = "accounts";
    
    private AccountManager accountManager;

    /**
     * Creates list of user accounts.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/edit_user_account.htm")
    public String handleRequest(ModelMap model) {
         List<Account> accounts = accountManager.load();
         Collections.sort(accounts);
         model.addAttribute(ACCOUNTS_KEY, accounts);
         return SUCCESS_VIEW;
    }
    
    /**
     * @param accountManager 
     */
    @Autowired
    public void setAccountManager(AccountManager accountManager) { 
        this.accountManager = accountManager; 
    }
}

