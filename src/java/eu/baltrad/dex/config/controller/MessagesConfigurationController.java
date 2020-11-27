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

import java.util.Arrays;
import java.util.List;

import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.config.validator.MessagesConfigurationValidator;
import eu.baltrad.dex.log.manager.impl.LogManager;
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
 * Configure system messages.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.7.3
 */
@Controller
@RequestMapping("/messages_configure.htm")
@SessionAttributes("config")
public class MessagesConfigurationController {

    // View name
    private static final String FORM_VIEW = "messages_configure";
    
    // Model keys
    private static final String CONF_KEY = "config";
    private static final String SAVE_MESSAGES_CONF_OK = 
            "savemsgconf.completed_success";
    private static final String SAVE_MESSAGES_CONF_ERROR = 
            "savemsgconf.completed_failure";
    private static final String OK_MSG_KEY = "messages_conf_success";
    private static final String ERROR_MSG_KEY = "messages_conf_error";

    private ConfigurationManager configManager;
    private LogManager logManager;
    private MessagesConfigurationValidator validator;
    private MessageResourceUtil messages;
    private Logger log;

    /**
     * Constructor.
     */
    public MessagesConfigurationController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Load form backing object and render form.
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String setupForm(ModelMap model) {
        LogConfiguration conf = configManager.getLogConf();
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
            if (!Boolean.parseBoolean(conf.getMsgTrimByNumber())) {
                conf.setMsgTrimByNumber("false");   
            }
            if (!Boolean.parseBoolean(conf.getMsgTrimByAge())) {
                conf.setMsgTrimByAge("false");
            }
            configManager.saveLogConf(conf);
            String msg = messages.getMessage(SAVE_MESSAGES_CONF_OK);
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            String msg = messages.getMessage(SAVE_MESSAGES_CONF_ERROR, 
                            new Object[] {e.getMessage()});
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return FORM_VIEW;
    }

    @ModelAttribute("scroll_ranges")
    public List<String> getNodeTypes() {
        return Arrays.asList(new String[]{"5","7","9","11"});
    }
    
    /**
     * @param configManager the configManager to set
     */
    @Autowired
    public void setConfigManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }
    
    /**
     * @param validator the validator to set
     */
    @Autowired
    public void setValidator(MessagesConfigurationValidator validator) {
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

