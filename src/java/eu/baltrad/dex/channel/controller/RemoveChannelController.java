/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

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
    private static final String HIBERNATE_ERRORS_KEY = "hibernate_errors";
    // view names
    private static final String SHOW_CHANNELS_VIEW = "showChannels";
    private static final String SELECTED_CHANNELS_VIEW = "showSelectedChannels";
    private static final String REMOVED_CHANNELS_VIEW = "showRemovedChannels";
//---------------------------------------------------------------------------------------- Variables
    // Channel manager
    private ChannelManager channelManager;
    // Name resolver
    private PropertiesMethodNameResolver nameResolver;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Shows all available channels.
     *
     * @param request Http request
     * @param response Http response
     * @return Models and view containing list of all available channels
     */
    public ModelAndView showChannels( HttpServletRequest request, HttpServletResponse response ) {
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
    public ModelAndView showSelectedChannels( HttpServletRequest request,
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
    public ModelAndView showRemovedChannels( HttpServletRequest request,
            HttpServletResponse response ) {
        String[] channelIds = request.getParameterValues( REMOVED_CHANNELS_KEY );
        List< String > errorMsgs = new ArrayList< String >();
        for( int i = 0; i < channelIds.length; i++ ) {
            try {
                channelManager.removeChannel( Integer.parseInt( channelIds[ i ] ) );
            } catch( HibernateException e ) {
                errorMsgs.add( "Data access exception while removing data channel " +
                        "(Channel ID: " + channelIds[ i ] + ")" );
            }
        }
        return new ModelAndView( REMOVED_CHANNELS_VIEW, HIBERNATE_ERRORS_KEY, errorMsgs );
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
}
//--------------------------------------------------------------------------------------------------
