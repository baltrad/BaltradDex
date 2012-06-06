/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.radar.controller;

import eu.baltrad.dex.radar.model.RadarManager;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

/**
 * Multi action controller handling data channel removal functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class RemoveRadarController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_CHANNELS_KEY = "channels";
    private static final String SELECTED_CHANNELS_KEY = "selected_channels";
    private static final String REMOVED_CHANNELS_KEY = "removed_channels";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    // view names
    private static final String SHOW_CHANNELS_VIEW = "remove_radar";
    private static final String SELECTED_CHANNELS_VIEW = "radar_to_remove";
    private static final String REMOVED_CHANNELS_VIEW = "remove_radar_status";
//---------------------------------------------------------------------------------------- Variables
    // Radar manager
    private RadarManager radarManager;
    // Message logger
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /*
     * Constructor.
     */
    public RemoveRadarController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
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
        List channels = radarManager.getChannels();
        return new ModelAndView( SHOW_CHANNELS_VIEW, SHOW_CHANNELS_KEY, channels );
    }
    /**
     * Shows channels selected for removal.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of channels selected for removal
     */
    public ModelAndView radar_to_remove( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = null;
        String[] channelIds = request.getParameterValues( SELECTED_CHANNELS_KEY );
        if( channelIds != null ) {
            List< Radar > channels = new ArrayList< Radar >();
            for( int i = 0; i < channelIds.length; i++ ) {
                channels.add( radarManager.getChannel( Integer.parseInt( channelIds[ i ] ) ) );
            }
            modelAndView = new ModelAndView( SELECTED_CHANNELS_VIEW, SHOW_CHANNELS_KEY, channels );
        } else {
            List channels = radarManager.getChannels();
            modelAndView = new ModelAndView( SHOW_CHANNELS_VIEW, SHOW_CHANNELS_KEY, channels );
        }
        return modelAndView;
    }
    /**
     * Displays information about channel removal status and errors if occured.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing data access exception errors if occured.
     */
    public ModelAndView remove_radar_status( HttpServletRequest request,
            HttpServletResponse response ) {
        String[] channelIds = request.getParameterValues( REMOVED_CHANNELS_KEY );
        String channelName = "";
        try {
            for( int i = 0; i < channelIds.length; i++ ) {
                Radar channel = radarManager.getChannel( Integer.parseInt( channelIds[ i ] ) );
                channelName = channel.getChannelName();
                radarManager.deleteChannel( Integer.parseInt( channelIds[ i ] ) );
                log.warn( "Local radar station successfully removed: " + channelName );
            }
            request.getSession().setAttribute( OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.removeradar.removesuccess" ) );
        } catch( Exception e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.removeradar.removefail" ) );
            log.warn( "Failed to remove local radar station", e );
        }
        return new ModelAndView( REMOVED_CHANNELS_VIEW );
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
//--------------------------------------------------------------------------------------------------
