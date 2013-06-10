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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.log.manager.impl.LogManager;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.apache.log4j.Logger;

/**
 * Clear system log.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class RemoveMessagesController {

    // View names
    private static final String CLEAR_MESSAGES_VIEW = "messages_delete";
    private static final String CLEAR_MESSAGES_STATUS_VIEW = 
            "messages_delete_status";
    
    // Model keys
    private static final String NUMBER_OF_ENTRIES_KEY = "number_of_entries";
    private static final String CLEAR_MESSAGES_OK_MSG_KEY = 
            "clearmessages.completed_success";
    private static final String CLEAR_MESSAGES_ERROR_MSG_KEY = 
            "clearmessages.completed_failure";
    private static final String OK_MSG_KEY = "messages_delete_success";
    private static final String ERROR_MSG_KEY = "messages_delete_error";

    private Logger log;
    private LogManager logManager;
    private MessageResourceUtil messages;

    /**
     * Constructor.
     */
    public RemoveMessagesController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Renders clear system log page.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/messages_delete.htm")
    public String clearMessages(ModelMap model) {
        model.addAttribute(NUMBER_OF_ENTRIES_KEY, logManager.count());
        return CLEAR_MESSAGES_VIEW;
    }
    
    /**
     * Removes all messages from system log.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/messages_delete_status.htm")
    public String clearMessagesStatus(ModelMap model) {
        try {
            int delete = logManager.delete();
            String msg = messages.getMessage(CLEAR_MESSAGES_OK_MSG_KEY, 
                    new String[] {Integer.toString(delete)});
            model.addAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            String msg = messages.getMessage(CLEAR_MESSAGES_ERROR_MSG_KEY);
            model.addAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return CLEAR_MESSAGES_STATUS_VIEW;
    }

    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }
    
    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

