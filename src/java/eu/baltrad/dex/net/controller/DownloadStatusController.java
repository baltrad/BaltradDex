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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.manager.impl.SubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.manager.IUserManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

/**
 * Creates list of data sources subscribed by local user.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0
 */
@Controller
public class DownloadStatusController {

    /** View name */
    private static final String DATA_DOWNLOAD_VIEW = "data_download";
    
     /** Peers model key */
    private static final String OPERATORS = "operators";
    /** Subscriptions model key */
    private static final String SUBSCRIPTIONS = "subscriptions";
    
    private IUserManager userManager;
    private SubscriptionManager subscriptionManager;
    
    /**
     * Get node operators and subscribed data sources.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/data_download.htm")
    public String dataDownload(ModelMap model) {
        model.addAttribute(OPERATORS, userManager.loadOperators());
        model.addAttribute(SUBSCRIPTIONS, 
                subscriptionManager.load(Subscription.LOCAL));
        return DATA_DOWNLOAD_VIEW;
    }    

    /**
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(SubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
    
}

