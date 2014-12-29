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

package eu.baltrad.dex.config.controller;

import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.config.validator.NodeConfigurationValidator;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

/**
 * Save or modify system configuration.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.6
 */
@Controller
@RequestMapping("/node_settings.htm")
public class NodeConfigurationController {

    private static final String FORM_VIEW = "node_settings";
    private static final String CONF_KEY = "config";
    
    private static final String[] NODE_TYPES = {"Primary", "Backup"};
    
    private static final String SAVE_CONF_OK_MSG_KEY = "saveconf.success";
    private static final String SAVE_CONF_ERROR_MSG_KEY = "saveconf.failure"; 
    
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";

    private ConfigurationManager configurationManager;
    private IUserManager userManager;
    private NodeConfigurationValidator validator;
    private MessageResourceUtil messages;
    private Logger log;

    /**
     * Constructor.
     */
    public NodeConfigurationController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Load form backing object and render form.
     * @param model Model map 
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String setupForm(ModelMap model) {
        model.addAttribute(CONF_KEY, configurationManager.getAppConf());
        return FORM_VIEW;
    }
    
    /**
     * Save configuration.
     * @param conf Configuration object
     * @param result Binding result
     * @param model Model map
     * @return View name
     */
    @Transactional(rollbackFor=Exception.class)
    @RequestMapping(method = RequestMethod.POST)
    protected String processSubmit(
            @ModelAttribute("config") AppConfiguration conf, 
                BindingResult result, ModelMap model) {
        validator.validate(conf, result);
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            if (!configurationManager.getAppConf().equals(conf)) {
                // save local node account
                int accountId = userManager
                        .load(configurationManager.getAppConf()
                        .getNodeName()).getId();
                User user = new User(accountId, conf.getNodeName(), Role.NODE, 
                        null, conf.getOrgName(), conf.getOrgUnit(), 
                        conf.getLocality(), conf.getState(), 
                        conf.getCountryCode(), conf.getNodeAddress());
                userManager.update(user);
                
                String nodeName = 
                        configurationManager.getAppConf().getNodeName();
                // rename key folders if node name changed
                if (!conf.getNodeName().equals(nodeName)) {
                    File srcPriv = new File(configurationManager.getAppConf()
                        .getKeystoreDir() + File.separator + nodeName + 
                            ".priv");
                    File destPriv = new File(configurationManager.getAppConf()
                         .getKeystoreDir() +  File.separator + 
                            conf.getNodeName()+ ".priv");
                    srcPriv.renameTo(destPriv);
                    File srcPub = new File(configurationManager.getAppConf()
                        .getKeystoreDir() + File.separator + nodeName + 
                            ".pub");
                    File destPub = new File(configurationManager.getAppConf()
                         .getKeystoreDir() +  File.separator + 
                            conf.getNodeName()+ ".pub");
                    srcPub.renameTo(destPub);
                }
                // save configuration
                configurationManager.saveAppConf(conf);
            }
            String msg = messages.getMessage(SAVE_CONF_OK_MSG_KEY);
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            String msg = messages.getMessage(SAVE_CONF_ERROR_MSG_KEY, 
                            new Object[] {e.getMessage()});
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return FORM_VIEW;
    }
    
    /**
     * Get node types.
     * @return List of node types
     */
    @ModelAttribute("node_types")
    public List<String> getNodeTypes() {
        return Arrays.asList(NODE_TYPES);
    }
    
    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(
            ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
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
    public void setValidator(NodeConfigurationValidator validator) {
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

