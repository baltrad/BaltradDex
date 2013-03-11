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
import eu.baltrad.dex.user.model.Key;

import org.springframework.ui.ModelMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Implements keystore management functionality.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
@Controller
@RequestMapping("/manage_keystore.htm")
public class KeystoreController {
    
    /** View names */
    private static final String FORM_VIEW = "manage_keystore";
    
    /** Model keys */
    private static final String KEYS = "keys";
    private static final String DELETE_KEY_ID = "delete_key_id";
    private static final String LOCAL_NODE_NAME = "local_node_name";
    
    /** Directory to store incoming keys */
    private static final String INCOMING_KEY_DIR = ".incoming";
    
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
     * @return Key list
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) {
        model.addAttribute(KEYS, keystoreManager.load());
        model.addAttribute(LOCAL_NODE_NAME, 
                confManager.getAppConf().getNodeName());
        return FORM_VIEW;
    } 
    
    /**
     * Grant or revoke access for selected key or delete key permanently.
     * @param model Model map 
     * @param grant Grant access parameter 
     * @param revoke Revoke access parameter
     * @param delete Delete key parameter
     * @param confirmDelete Confirm key deletion parameter
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(ModelMap model,
            @RequestParam(value="grant", required=false) String grant,
            @RequestParam(value="revoke", required=false) String revoke,
            @RequestParam(value="delete", required=false) String delete,
            @RequestParam(value="confirm_delete", required=false) 
                    String confirmDelete) {
        
        try {
            if (grant != null) {
                int id = Integer.parseInt(grant);
                Key key = keystoreManager.load(id);
                key.setAuthorized(true);
                int update = keystoreManager.update(key);
                if(update == 1 && grant(key.getName())) {
                    log.warn("Access granted for key " + key.getName());
                } else {
                    log.error("Failed to grant access for key " + 
                            key.getName());
                }
            }
            if (revoke != null) {
                int id = Integer.parseInt(revoke);
                Key key = keystoreManager.load(id);
                key.setAuthorized(false);
                int update = keystoreManager.update(key);
                if (update == 1 && revoke(key.getName())) {
                    log.warn("Access revoked for key " + key.getName());
                } else {
                    log.error("Failed to revoke access for key " + 
                            key.getName());
                }
            }
            if (delete != null) {
                model.addAttribute(DELETE_KEY_ID, delete);   
            }
            if (confirmDelete != null) {
                int id = Integer.parseInt(confirmDelete);
                Key key = keystoreManager.load(id);
                int del = keystoreManager.delete(id);
                delete(key.getName());
                if (del == 1) {
                    log.warn("Permanently deleted key " + key.getName());
                } else {
                    log.error("Failed to delete key " + key.getName());
                }
            }
            model.addAttribute(KEYS, keystoreManager.load());
            model.addAttribute(LOCAL_NODE_NAME, 
                confManager.getAppConf().getNodeName());
            return FORM_VIEW;
        } catch (Exception e) {
            model.addAttribute(KEYS, keystoreManager.load());
            model.addAttribute(LOCAL_NODE_NAME, 
                confManager.getAppConf().getNodeName());
            return FORM_VIEW;
        }
    }
    
    /**
     * Move key directory in order to grant access. 
     * @param keyName Key name
     * @return True in case directory was successfully moved  
     */
    private boolean grant(String keyName) {
        File src = new File(confManager.getAppConf().getKeystoreDir() +
                File.separator + INCOMING_KEY_DIR + File.separator + keyName + 
                ".pub");
        File dest = new File(confManager.getAppConf().getKeystoreDir() +
                File.separator + keyName + ".pub");
        return src.renameTo(dest);
    }
    
    /**
     * Move key directory in order to revoke access.
     * @param keyName Key name
     * @return True in case directory was successfully moved  
     */
    private boolean revoke(String keyName) {
        File src = new File(confManager.getAppConf().getKeystoreDir() +
                File.separator + keyName + ".pub");
        File dest = new File(confManager.getAppConf().getKeystoreDir() +
                File.separator + INCOMING_KEY_DIR + File.separator + keyName +
                ".pub");
        return src.renameTo(dest);
    }
    
    /**
     * Delete key with a given name
     * @param keyName Key name
     * @return True in case the key was successfully deleted
     */
    private void delete(String keyName) throws Exception {
        File src = new File(confManager.getAppConf().getKeystoreDir() +
                File.separator + INCOMING_KEY_DIR + File.separator + keyName + 
                ".pub");
        File dest = new File(confManager.getAppConf().getKeystoreDir() +
                File.separator + keyName + ".pub");
        if (src.exists()) {
            FileUtils.deleteDirectory(src);
        }
        if (dest.exists()) {
            FileUtils.deleteDirectory(dest);
        }
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
