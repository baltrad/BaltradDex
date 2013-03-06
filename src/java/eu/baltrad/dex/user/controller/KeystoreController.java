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

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.user.manager.IKeystoreManager;

import org.apache.log4j.Logger;

import org.springframework.ui.ModelMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Implements keystore management functionality.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
@Controller
@RequestMapping("/manage_keystore.htm")
public class KeystoreController {
    
    /** View name */
    private static final String VIEW_NAME = "manage_keystore"; 
    /** Model keys */
    private static final String KEYS = "keys";
    
    /** Keystore manager */
    private IKeystoreManager keystoreManager;
    /** Configuration manager */
    private IConfigurationManager confManager;
    
    /** Message logger */
    private Logger log;
    
    /**
     * Constructor.
     */
    public KeystoreController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Load form backing object and render form.
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) {
        model.addAttribute(KEYS, keystoreManager.load());
        
        
        
        return VIEW_NAME;
    } 
    
    
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(ModelMap model) {
         
         return VIEW_NAME;
    }

    /**
     * @param keystoreManager the keystoreManager to set
     */
    @Autowired
    public void setKeystoreManager(IKeystoreManager keystoreManager) {
        this.keystoreManager = keystoreManager;
    }

    /**
     * @param confManager the confManager to set
     */
    @Autowired
    public void setConfManager(IConfigurationManager confManager) {
        this.confManager = confManager;
    }
    
}
