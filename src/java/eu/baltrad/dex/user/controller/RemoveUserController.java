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

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

import java.util.List;
import java.util.ArrayList;

/**
 * Multi-action controller handling user account removal functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class RemoveUserController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_USERS_KEY = "users";
    private static final String SELECTED_USERS_KEY = "selected_users";
    private static final String REMOVED_USERS_KEY = "removed_users";
    private static final String HIBERNATE_ERRORS_KEY = "hibernate_errors";
    // view names
    private static final String SHOW_USERS_VIEW = "showUsers";
    private static final String SELECTED_USERS_VIEW = "showSelectedUsers";
    private static final String REMOVED_USERS_VIEW = "showRemovedUsers";
//---------------------------------------------------------------------------------------- Variables
    // User manager
    private UserManager userManager;
    // Name resolver
    private PropertiesMethodNameResolver nameResolver;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Gets list of all registered users except for currently signed user.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Model and view containing list of all user accounts registered in the system
     */
    public ModelAndView showUsers( HttpServletRequest request, HttpServletResponse response ) {
        List users = userManager.getUsers();
        User signedUser = ( User )ApplicationSecurityManager.getUser( request );
        for( int i = 0; i < users.size(); i++ ) {
            User user = ( User )users.get( i );
            if( ApplicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
                users.remove( i );
            }
        }
        return new ModelAndView( SHOW_USERS_VIEW, SHOW_USERS_KEY, users );
    }
    /**
     * Gets list of user accounts selected for removal.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Model and view containing list of user accounts selected for removal
     */
    public ModelAndView showSelectedUsers( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = null;
        String[] userIds = request.getParameterValues( SELECTED_USERS_KEY );
        if( userIds != null ) {
            List< User > users = new ArrayList< User >();
            for( int i = 0; i < userIds.length; i++ ) {
                users.add( userManager.getUserByID( Integer.parseInt( userIds[ i ] ) ) );
            }
            modelAndView = new ModelAndView( SELECTED_USERS_VIEW, SELECTED_USERS_KEY, users );
        } else {
            List users = userManager.getUsers();
            User signedUser = ( User )ApplicationSecurityManager.getUser( request );
            for( int i = 0; i < users.size(); i++ ) {
                User user = ( User )users.get( i );
                if( ApplicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
                    users.remove( i );
                }
            }
            modelAndView = new ModelAndView( SHOW_USERS_VIEW, SHOW_USERS_KEY, users );
        }
        return modelAndView;
    }
    /**
     * Displays information about user account removal status and errors if occured.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Model and view containing data access exception errors if occured
     */
    public ModelAndView showRemovedUsers( HttpServletRequest request,
            HttpServletResponse response ) {
        String[] userIds = request.getParameterValues( REMOVED_USERS_KEY );
        List< String > errorMsgs = new ArrayList< String >();
        for( int i = 0; i < userIds.length; i++ ) {
            try {
                userManager.removeUser( Integer.parseInt( userIds[ i ] ) );
            } catch( HibernateException e ) {
                errorMsgs.add( "Data access exception while removing user account " +
                        "(User ID: " + userIds[ i ] + ")" );
            }
        }
        return new ModelAndView( REMOVED_USERS_VIEW, HIBERNATE_ERRORS_KEY, errorMsgs );
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
