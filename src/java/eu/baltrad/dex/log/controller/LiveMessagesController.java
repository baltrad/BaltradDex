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

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Implements functionality allowing to display and auto-refresh 
 * system messages.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.6.1
 * @since 0.1.6
 */
@Controller
public class LiveMessagesController {
    
    /**
     * Display auto-updated message set.
     * @param request HTTP servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/messages_live.htm")
    public String liveMessages(HttpServletRequest request, ModelMap model) {
        String parm = request.getParameter("refresh");
        if (parm == null) {
            model.addAttribute("auto_update", "on");
        } else {
            if (parm.equals("on")) {
                model.addAttribute("auto_update", "on");
            } else {
                model.addAttribute("auto_update", "off");
            }
        }
        return "messages_live";
    }
    
}
