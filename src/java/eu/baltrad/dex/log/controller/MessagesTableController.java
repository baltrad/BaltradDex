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

package eu.baltrad.dex.log.controller;

import eu.baltrad.dex.log.manager.impl.LogManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

/**
 * Class implements system message controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class MessagesTableController {
    
    private static final String MESSAGES_KEY = "messages";
    private static final int ENTRIES_PER_PAGE = 20;
    
    private LogManager logManager;
    
    /**
     * Loads log table.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/messages_table.htm")
    public String showLogTable(ModelMap model) {
        model.addAttribute(MESSAGES_KEY, logManager.load(ENTRIES_PER_PAGE));
        return "messages_table";
    }

    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(LogManager logManager) {
        this.logManager = logManager;
    }
    
}

