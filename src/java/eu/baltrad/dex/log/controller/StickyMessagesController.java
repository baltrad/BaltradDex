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
import eu.baltrad.dex.log.model.ILogEntry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

/**
 * Implements functionality allowing to browse sticky messages.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7
 * @since 1.7
 */
@Controller
@RequestMapping("/messages_sticky.htm")
public class StickyMessagesController {
    
    private ILogManager logManager;
    
    /**
     * Form setup.
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(ModelMap model) {
        model.addAttribute("messages", logManager.load(ILogEntry.LEVEL_STICKY));
        return "messages_sticky";
    }
    
    /**
     * Process form submission.
     * @param session HTTP session
     * @param request HTTP servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(HttpSession session, HttpServletRequest request,
                                ModelMap model) {
        int id = Integer.parseInt(request.getParameter("message_id"));
        logManager.delete(id);
        model.addAttribute("messages", logManager.load(ILogEntry.LEVEL_STICKY));
        return "messages_sticky";
    }

    /**
     * @param logManager the logManager to set
     */
    @Autowired
    public void setLogManager(ILogManager logManager) {
        this.logManager = logManager;
    }
    
}
