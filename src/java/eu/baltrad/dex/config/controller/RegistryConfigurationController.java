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

package eu.baltrad.dex.config.controller;

import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.config.validator.RegistryConfigurationValidator;
import eu.baltrad.dex.registry.manager.impl.RegistryManager;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.log4j.Logger;

/**
 * Configure delivery registry.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.7.3
 */
@Controller
@RequestMapping("/configure_registry.htm")
@SessionAttributes("config")
public class RegistryConfigurationController {

    // View name
    private static final String FORM_VIEW = "configure_registry";
    
    // Model keys
    private static final String CONF_KEY = "config";
    private static final String SAVE_REGISTRY_CONF_OK = 
            "saveregistryconf.completed_success";
    private static final String SAVE_REGISTRY_CONF_ERROR = 
            "saveregistryconf.completed_failure";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";

    private ConfigurationManager configManager;
    private RegistryManager registryManager;
    private RegistryConfigurationValidator validator;
    private MessageResourceUtil messages;
    private Logger log;
    
    /**
     * Constructor.
     */
    public RegistryConfigurationController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Load form backing object and render form.
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String setupForm(ModelMap model) {
        LogConfiguration conf = configManager.loadRegConf();
        if (conf == null) {
            conf = new LogConfiguration();
        }
        model.addAttribute(CONF_KEY, conf);
        return FORM_VIEW;
    }
    
    /**
     * Save configuration.
     * @param conf Configuration object
     * @param result Binding result
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    protected String processSubmit(
            @ModelAttribute("config") LogConfiguration conf, 
                BindingResult result, ModelMap model) {
        
        validator.validate(conf, result);
        if (result.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            if (conf.getTrimByNumber()) {
                registryManager.setTrimmer(conf.getRecordLimit());
                configManager.saveRegConf(conf);
            } else {
                registryManager.removeTrimmer(
                        RegistryManager.TRIM_REG_BY_NUMBER_TG);
            }
            if (conf.getTrimByAge()) {
                registryManager.setTrimmer(conf.getMaxAgeDays(), 
                        conf.getMaxAgeHours(), conf.getMaxAgeMinutes());
                configManager.saveRegConf(conf);
            } else {
                registryManager.removeTrimmer(
                        RegistryManager.TRIM_REG_BY_AGE_TG);
            }
            String msg = messages.getMessage(SAVE_REGISTRY_CONF_OK);
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            String msg = messages.getMessage(SAVE_REGISTRY_CONF_ERROR, 
                            new Object[] {e.getMessage()});
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return FORM_VIEW;
    }
    
    /**
     * @param configManager the configManager to set
     */
    @Autowired
    public void setConfigManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * @param registryManager the registryManager to set
     */
    public void setRegistryManager(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }
    
    /**
     * @param validator the validator to set
     */
    @Autowired
    public void setValidator(RegistryConfigurationValidator validator) {
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

