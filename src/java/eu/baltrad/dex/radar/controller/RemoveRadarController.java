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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

/**
 * Controller implementing radar removal functionality.
 *
 * @author szewczenko
 * @version 1.2.1
 * @since 1.0.0
 */
public class RemoveRadarController extends MultiActionController {

    // model keys
    private static final String SHOW_RADARS_KEY = "radars";
    private static final String SELECTED_RADARS_KEY = "selected_radars";
    private static final String REMOVED_RADARS_KEY = "removed_radars";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    // view names
    private static final String SHOW_RADARS_VIEW = "remove_radar";
    private static final String SELECTED_RADARS_VIEW = "radar_to_remove";
    private static final String REMOVED_RADARS_VIEW = "remove_radar_status";

    // Radar manager
    private RadarManager radarManager;
    // Message logger
    private Logger log;

    /*
     * Constructor.
     */
    public RemoveRadarController() {
        this.log = Logger.getLogger("DEX");
    }
    /**
     * Shows all available channels.
     *
     * @param request Http request
     * @param response Http response
     * @return Models and view containing list of all available channels
     */
    public ModelAndView remove_radar( HttpServletRequest request,
            HttpServletResponse response ) {
        List channels = radarManager.load();
        return new ModelAndView(SHOW_RADARS_VIEW, SHOW_RADARS_KEY, channels);
    }
    /**
     * Shows channels selected for removal.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of channels selected for removal
     */
    public ModelAndView radar_to_remove(HttpServletRequest request,
            HttpServletResponse response) {
        ModelAndView modelAndView = null;
        String[] radarIds = request.getParameterValues(SELECTED_RADARS_KEY);
        if (radarIds != null) {
            List<Radar> radars = new ArrayList<Radar>();
            for (int i = 0; i < radarIds.length; i++) {
                radars.add( radarManager.load(Integer.parseInt(radarIds[i])));
            }
            modelAndView = new ModelAndView(SELECTED_RADARS_VIEW, 
                    SHOW_RADARS_KEY, radars);
        } else {
            List radars = radarManager.load();
            modelAndView = new ModelAndView(SHOW_RADARS_VIEW, SHOW_RADARS_KEY, 
                    radars);
        }
        return modelAndView;
    }
    /**
     * Displays information about radar removal status and errors if occured.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing data access exception errors if occured.
     */
    public ModelAndView remove_radar_status(HttpServletRequest request,
            HttpServletResponse response) {
        String[] radarIds = request.getParameterValues(REMOVED_RADARS_KEY);
        String radarName = "";
        try {
            for (int i = 0; i < radarIds.length; i++) {
                Radar radar = radarManager.load(Integer.parseInt(radarIds[i]));
                radarName = radar.getName();
                if (radarManager.delete(Integer.parseInt(radarIds[i])) > 0) {
                    log.warn("Radar station " + radarName + 
                        " successfully removed");
                } else {
                    log.error("Failed to remove radar station: " + radarName);
                }
            }
            request.getSession().setAttribute(OK_MSG_KEY, 
                    getMessageSourceAccessor().getMessage(
                        "message.removeradar.removesuccess" ) );
        } catch(Exception e) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, 
                    getMessageSourceAccessor().getMessage(
                        "message.removeradar.removefail" ) );
            log.warn("Failed to remove radar station", e);
        }
        return new ModelAndView( REMOVED_RADARS_VIEW );
    }
    /*
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

