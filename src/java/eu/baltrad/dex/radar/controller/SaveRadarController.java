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

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

/**
 * Save radar controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.1.6
 */
public class SaveRadarController extends SimpleFormController {

    public static final String RADAR_ID = "id";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";

    private RadarManager radarManager;
    private Logger log;

    /**
     * Constructor.
     */
    public SaveRadarController() {
        this.log = Logger.getLogger("DEX");
    }
    /**
     * Create for backing object.
     * @param request HttpServletRequest
     * @return Radar object
     */
    @Override
    protected Object formBackingObject(HttpServletRequest request) 
            throws Exception {
        Radar radar = null;
        if( request.getParameter(RADAR_ID) != null
                && request.getParameter(RADAR_ID).trim().length() > 0 ) {
            radar = getRadarManager().load(Integer.parseInt(
                    request.getParameter(RADAR_ID) ) );
        } else {
            radar = new Radar();
        }
        return radar;
    }
    /**
     * Saves Channel object.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, 
        HttpServletResponse response, Object command, BindException errors) 
            throws Exception {
        Radar radar = (Radar) command;
        try {
            String msg = "";
            if (radar.getId() > 0) {
                getRadarManager().update(radar);
                msg = "Radar station " + radar.getName() + 
                        " successfully updated";
            } else {
                getRadarManager().storeNoId(radar);
                msg = "New radar station " + radar.getName() + 
                        " successfully saved";
            }
            request.setAttribute(OK_MSG_KEY, msg);
            log.warn(msg);
        } catch (Exception e) {
            request.removeAttribute(OK_MSG_KEY);
            String msg = "Failed to save radar station " + radar.getName();
            request.setAttribute(ERROR_MSG_KEY, msg);
            log.error(msg, e);
        }
        return new ModelAndView(getSuccessView());
    }
    /**
     * Method sets reference to radar manager object.
     *
     * @param Reference to radar manager object
     */
    public void setRadarManager(RadarManager radarManager) {
        this.radarManager = radarManager;
    }

    /**
     * @return the radarManager
     */
    public RadarManager getRadarManager() {
        return radarManager;
    }
}
