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

import eu.baltrad.dex.radar.model.RadarManager;
import eu.baltrad.dex.radar.model.Radar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * Edit radar controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.0.0
 */
public class EditRadarController implements Controller {

    private static final String MODEL_KEY = "radars";

    private String successView;
    private RadarManager radarManager;

     public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException {
         List<Radar> radars = radarManager.load();
         return new ModelAndView(getSuccessView(), MODEL_KEY, radars);
     }
     /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Method sets reference to success view name string.
     *
     * @param successView Reference to success view name string
     */
    public void setSuccessView( String successView ) { 
        this.successView = successView; 
    }
    /**
     * Method returns reference to radar manager object.
     *
     * @return Reference to radar manager object
     */
    public RadarManager getRadarManager() { return radarManager; }
    /**
     * Method sets reference to radar manager object.
     *
     * @param Reference to radar manager object
     */
    public void setRadarManager( RadarManager radarManager ) {
        this.radarManager = radarManager;
    }
}

