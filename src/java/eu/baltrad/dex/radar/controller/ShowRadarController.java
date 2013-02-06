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

package eu.baltrad.dex.radar.controller;

import eu.baltrad.dex.radar.manager.impl.RadarManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Show radar controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class ShowRadarController {

    private static final String SUCCESS_VIEW = "show_radar";
    private static final String MODEL_KEY = "radars";
    
    private RadarManager radarManager;
    
    /**
     * Load radars.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/show_radar.htm")
    public String showRadars(ModelMap model) {
        model.addAttribute(MODEL_KEY, radarManager.load());
        return SUCCESS_VIEW;
    }
    
    /**
     * @param radarManager 
     */
    @Autowired
    public void setRadarManager(RadarManager radarManager) {
        this.radarManager = radarManager;
    }
    
}
