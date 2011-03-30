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
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
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
    private static final String OK_MSG_KEY = "ok_message";
    private static final String ERROR_MSG_KEY = "error_message";

    // view names
    private static final String SHOW_USERS_VIEW = "showUsers";
    private static final String SELECTED_USERS_VIEW = "showSelectedUsers";
    private static final String REMOVED_USERS_VIEW = "showRemovedUsers";
//---------------------------------------------------------------------------------------- Variables
    // User manager
    private UserManager userManager;
    // Name resolver
    private PropertiesMethodNameResolver nameResolver;
    // Log manager
    private LogManager logManager;
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
                users.add( userManager.getUserById( Integer.parseInt( userIds[ i ] ) ) );
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
        String userName = "";
        for( int i = 0; i < userIds.length; i++ ) {
            try {
                User user = userManager.getUserById( Integer.parseInt( userIds[ i ] ) );
                userName = user.getName();
                userManager.deleteUser( Integer.parseInt( userIds[ i ] ) );
                request.getSession().setAttribute( OK_MSG_KEY, 
                        getMessageSourceAccessor().getMessage(
                        "message.removeuser.removesuccess" ) );
                logManager.addEntry( System.currentTimeMillis(), LogManager.MSG_WRN,
                        "User account " + userName + " removed from the system" );

            } catch( SQLException e ) {
                request.getSession().removeAttribute( OK_MSG_KEY );
                request.getSession().setAttribute( ERROR_MSG_KEY,
                        getMessageSourceAccessor().getMessage(
                        "message.removeuser.removefail" ) );
                logManager.addEntry( System.currentTimeMillis(), LogManager.MSG_ERR,
                        "Failed to remove user account " + userName + "." );
            } catch( Exception e ) {
                request.getSession().removeAttribute( OK_MSG_KEY );
                request.getSession().setAttribute( ERROR_MSG_KEY,
                        getMessageSourceAccessor().getMessage(
                        "message.removeuser.removefail" ) );
                logManager.addEntry( System.currentTimeMillis(), LogManager.MSG_ERR,
                        "Failed to remove user account " + userName + "." );
            }
        }
        return new ModelAndView( REMOVED_USERS_VIEW );
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
