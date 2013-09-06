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

import eu.baltrad.dex.log.manager.ILogManager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Implements functionality allowing to display sticky message counter.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7
 * @since 1.7
 */
@Controller
public class StickyMessagesCounter {
    
    private ILogManager logManager;
    
    /**
     * Display sticky message counter.
     * @param request HTTP servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/messages_sticky_counter.htm")
    public String stickyMessages(HttpServletRequest request, ModelMap model) {
        long messageCount = logManager.count(ILogManager.SQL_SELECT_STICKY);
        HttpSession session = request.getSession();
        session.setAttribute("message_count", messageCount);
        return "messages_sticky_counter"; 
    }

    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(ILogManager logManager) {
        this.logManager = logManager;
    }
    
}
