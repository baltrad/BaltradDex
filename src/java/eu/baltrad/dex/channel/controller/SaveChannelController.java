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
import eu.baltrad.dex.channel.model.ChannelPermission;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.log.model.LogManager;
import java.util.ArrayList;

import org.hibernate.HibernateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import java.util.Date;
import java.util.List;
import java.util.HashMap;

/**
 * Controller class registers new channel in the system or modifies existing data channel.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class SaveChannelController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    public static final String CHANNEL_ID = "id";
    public static final String USERS = "users";
    private static final String OK_MSG_KEY = "ok_message";
    private static final String ERROR_MSG_KEY = "error_message";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private UserManager userManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches Channel object with a given CHANNEL_ID passed as request parameter,
     * or creates new Channel instance in case CHANNEL_ID is not set in request.
     *
     * @param request HttpServletRequest
     * @return Channel class object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        Channel channel = null;
        if( request.getParameter( CHANNEL_ID ) != null
                && request.getParameter( CHANNEL_ID ).trim().length() > 0 ) {
            channel = channelManager.getChannel( Integer.parseInt(
                    request.getParameter( CHANNEL_ID ) ) );
        } else {
            channel = new Channel();
        }
        return channel;
    }
    /**
     * Returns HashMap holding list of registered users.
     *
     * @param request HttpServletRequest
     * @return HashMap holding list of users
     * @throws Exception
     */
    @Override
    protected HashMap referenceData( HttpServletRequest request ) throws Exception {
        HashMap model = new HashMap();
        if( request.getParameter( CHANNEL_ID ) != null
                && !request.getParameter( CHANNEL_ID ).isEmpty() ) {
            List< ChannelPermission > channelPermissions = channelManager.getPermissionByChannel(
                Integer.parseInt( request.getParameter( CHANNEL_ID ) ) );
            List< User > users = new ArrayList< User >();
            for( int i = 0; i < channelPermissions.size(); i++ ) {
                User user = userManager.getUserByID( channelPermissions.get( i ).getUserId() );
                users.add( user );
            }
            model.put( USERS, users );
            // set session attribute in order to define channel permissions
            request.getSession().setAttribute( CHANNEL_ID, request.getParameter( CHANNEL_ID ) );
        }
        return model;
    }
    /**
     * Saves Channel object
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
        Channel channel = ( Channel )command;
        try {
            channelManager.addChannel( channel );
            request.getSession().setAttribute( OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                "message.addradar.savesuccess" ) );
            logManager.addEntry( new Date(), LogManager.MSG_WRN, "Saved local radar station " +
                channel.getChannelName() + "." );
        } catch( HibernateException e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                "message.addradar.savefail" ) );
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Saved local radar station " +
                channel.getChannelName() + "." );
        }
        return new ModelAndView( getSuccessView() );
    }
    /**
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
     * Method gets reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) { this.userManager = userManager; }
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