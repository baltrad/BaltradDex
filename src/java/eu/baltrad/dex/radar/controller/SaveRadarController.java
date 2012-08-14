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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

/**
 * Controller class registers new channel in the system or modifies existing data channel.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class SaveRadarController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    public static final String CHANNEL_ID = "channelId";
    public static final String USERS = "users";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    private RadarManager radarManager;
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SaveRadarController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Fetches Channel object with a given CHANNEL_ID passed as request parameter,
     * or creates new Channel instance in case CHANNEL_ID is not set in request.
     *
     * @param request HttpServletRequest
     * @return Channel class object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) throws Exception {
        Radar channel = null;
        if( request.getParameter( CHANNEL_ID ) != null
                && request.getParameter( CHANNEL_ID ).trim().length() > 0 ) {
            channel = radarManager.getRadar( Integer.parseInt(
                    request.getParameter( CHANNEL_ID ) ) );
        } else {
            channel = new Radar();
        }
        return channel;
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
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) throws Exception {
        Radar channel = ( Radar )command;
        try {
            radarManager.saveOrUpdate( channel );
            String msg = "Local radar station successfully saved: " + channel.getRadarName();
            request.setAttribute( OK_MSG_KEY, msg  );
            log.warn( msg );
        } catch( Exception e ) {
            request.removeAttribute( OK_MSG_KEY );
            String msg = "Failed to save local radar station " + channel.getRadarName();
            request.setAttribute( ERROR_MSG_KEY, msg  );
            log.error( msg, e );
        }
        return new ModelAndView( getSuccessView() );
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
//--------------------------------------------------------------------------------------------------