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

package eu.baltrad.dex.channel.controller;

import eu.baltrad.dex.channel.model.ChannelManager;
import eu.baltrad.dex.channel.model.Channel;
import eu.baltrad.dex.log.model.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Multi action controller handling data channel removal functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class RemoveChannelController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_CHANNELS_KEY = "channels";
    private static final String SELECTED_CHANNELS_KEY = "selected_channels";
    private static final String REMOVED_CHANNELS_KEY = "removed_channels";
    private static final String OK_MSG_KEY = "ok_message";
    private static final String ERROR_MSG_KEY = "error_message";
    // view names
    private static final String SHOW_CHANNELS_VIEW = "showLocalChannels";
    private static final String SELECTED_CHANNELS_VIEW = "showSelectedLocalChannels";
    private static final String REMOVED_CHANNELS_VIEW = "showRemovedLocalChannels";
//---------------------------------------------------------------------------------------- Variables
    // Channel manager
    private ChannelManager channelManager;
    // Name resolver
    private PropertiesMethodNameResolver nameResolver;
    // Log manager
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Shows all available channels.
     *
     * @param request Http request
     * @param response Http response
     * @return Models and view containing list of all available channels
     */
    public ModelAndView showLocalChannels( HttpServletRequest request,
            HttpServletResponse response ) {
        List channels = channelManager.getChannels();
        return new ModelAndView( SHOW_CHANNELS_VIEW, SHOW_CHANNELS_KEY, channels );
    }
    /**
     * Shows channels selected for removal.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of channels selected for removal
     */
    public ModelAndView showSelectedLocalChannels( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = null;
        String[] channelIds = request.getParameterValues( SELECTED_CHANNELS_KEY );
        if( channelIds != null ) {
            List< Channel > channels = new ArrayList< Channel >();
            for( int i = 0; i < channelIds.length; i++ ) {
                channels.add( channelManager.getChannel( Integer.parseInt( channelIds[ i ] ) ) );
            }
            modelAndView = new ModelAndView( SELECTED_CHANNELS_VIEW, SHOW_CHANNELS_KEY, channels );
        } else {
            List channels = channelManager.getChannels();
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
    public ModelAndView showRemovedLocalChannels( HttpServletRequest request,
            HttpServletResponse response ) {
        String[] channelIds = request.getParameterValues( REMOVED_CHANNELS_KEY );
        String channelName = "";
        for( int i = 0; i < channelIds.length; i++ ) {
            try {
                Channel channel = channelManager.getChannel( Integer.parseInt( channelIds[ i ] ) );
                channelName = channel.getChannelName();
                channelManager.deleteChannel( Integer.parseInt( channelIds[ i ] ) );
                request.getSession().setAttribute( OK_MSG_KEY,
                        getMessageSourceAccessor().getMessage(
                        "message.removeradar.removesuccess" ) );
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN, 
                        "Local radar station " + channelName + " removed from the system" ) );
            } catch( SQLException e ) {
                request.getSession().removeAttribute( OK_MSG_KEY );
                request.getSession().setAttribute( ERROR_MSG_KEY,
                        getMessageSourceAccessor().getMessage(
                        "message.removeradar.removefail" ) );
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN, 
                        "Failed to remove local " + "radar station " + channelName ) );
            } catch( Exception e ) {
                request.getSession().removeAttribute( OK_MSG_KEY );
                request.getSession().setAttribute( ERROR_MSG_KEY,
                        getMessageSourceAccessor().getMessage(
                        "message.removeradar.removefail" ) );
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN, 
                        "Failed to remove local " + "radar station " + channelName ) );
            }
        }
        return new ModelAndView( REMOVED_CHANNELS_VIEW );
    }
    /**
     * Gets reference to name resolver object.
     *
     * @return the multiactionMethodNameResolver Name resolver class
     */
    public PropertiesMethodNameResolver getNameResolver() { return nameResolver; }
    /**
     * Sets reference to name resolver object.
     *
     * @param multiactionMethodNameResolver 
     */
    public void setNameResolver( PropertiesMethodNameResolver nameResolver ) {
        this.nameResolver = nameResolver;
    }
    /*
     * Method returns reference to data channel manager object.
     *
     * @return Reference to data channel manager object
     */
    public ChannelManager getChannelManager() { return channelManager; }
    /**
     * Method sets reference to data channel manager object.
     *
     * @param Reference to data channel manager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
    }
     /**
     * Method gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Method sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------
