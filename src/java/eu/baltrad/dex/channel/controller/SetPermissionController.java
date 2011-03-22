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

import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.channel.model.ChannelManager;
import eu.baltrad.dex.channel.model.ChannelPermission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import java.util.HashMap;
import java.util.List;

/**
 * Controller class sets permission for a given data channel.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class SetPermissionController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    private static final String CHANNEL_ID = "channelId";
    private static final String USERS = "users";
    private static final String SELECTED_USERS_KEY = "selected_users";
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
    private ChannelManager channelManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Returns HashMap holding a list of all users. If user is allowed to use a given data channel
     *
     * @param request HttpServletRequest
     * @return HashMap holding list of all users
     * @throws Exception
     */
    @Override
    protected HashMap referenceData( HttpServletRequest request ) throws Exception {
        List<User> users = userManager.getUsers();
        List<ChannelPermission> channelPermissions = channelManager.getPermissionByChannel(
                Integer.parseInt( ( String )request.getSession().getAttribute( CHANNEL_ID ) ) );
        for( int i = 0; i < users.size(); i++ ) {
            int j = 0;
            while( j < channelPermissions.size() ) {
                if( users.get( i ).getId() == channelPermissions.get( j ).getUserId() ) {
                    users.get( i ).setChecked( true );
                    break;
                } else {
                    users.get( i ).setChecked( false );
                }
                j++;    
            }
        }
        HashMap model = new HashMap();
        model.put( USERS, users );
        return model;
    }
    /**
     * Submits selected permission set.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command class
     * @param errors Errors
     * @return ModelAndView object
     * @throws Exception
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) throws Exception {
        int channelId = Integer.parseInt( ( String )request.getSession().getAttribute( CHANNEL_ID ) );
        String[] selUsers = request.getParameterValues( SELECTED_USERS_KEY );
        List< User > users = userManager.getUsers();
        int selSize = 0;
        if( selUsers != null ) {
            selSize = selUsers.length;
        }
        for( int i = 0; i < users.size(); i++ ) {
            User user = users.get( i );
            int j = 0;
            boolean selected = false;
            while( j < selSize ) {
                int userId = Integer.parseInt( selUsers[ j ] );
                if( userId == user.getId() ) {
                    selected = true;
                }
                j++;
            }
            if( selected ) {
                ChannelPermission channelPermission = channelManager.getPermission(
                    channelId, user.getId() );
                if( channelPermission == null ) {
                    channelManager.savePermission( new ChannelPermission( channelId,
                            user.getId() ) );
                }
            } else {
                ChannelPermission channelPermission = channelManager.getPermission(
                    channelId, user.getId() );
                if( channelPermission != null ) {
                    channelManager.deletePermission( channelId, user.getId() );
                }
            }
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

}
//--------------------------------------------------------------------------------------------------
