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
package eu.baltrad.dex.util;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;

import javax.servlet.http.HttpServletRequest;

/**
 * Class implementing session handling functionality and user authentication control.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class ApplicationSecurityManager {
//---------------------------------------------------------------------------------------- Constants
    // Session user attribute
    private static final String USER = "user";
    // Redirect views
    public static final String SIGNIN_PAGE_REDIRECT = "login.htm";
    public static final String WELCOME_PAGE_REDIRECT = "home.htm";
//---------------------------------------------------------------------------------------- Variables
    private static UserManager userManager = new UserManager();
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method authenticates user based on credentials provided in the login form.
     * Credentials are compared with corresponding user record in the database.
     * WARNING: Only users belonging to group ADMIN, OPERATOR and USER are allowed to log in
     * to local node.
     *
     * @param formUser Current user signing in
     * @param dbUser User in the database
     * @return True if users are the same, false otherwise
     */
    public static boolean authenticateFormUser( User formUser, User dbUser ) {
        if( formUser == null || dbUser == null ) {
            return false;
        } else {
            String formUserName = formUser.getName().trim();
            String formUserPassword = /*MessageDigestUtil.createHash(*/ formUser.getPassword().trim() /*)*/;
            String dbUserName = dbUser.getName().trim();
            String dbUserPassword = dbUser.getPassword().trim();
            if( formUserName.equals( dbUserName ) && formUserPassword.equals( dbUserPassword ) && (
                    dbUser.getRoleName().equals( User.ROLE_ADMIN ) ||
                    dbUser.getRoleName().equals( User.ROLE_OPERATOR ) ||
                    dbUser.getRoleName().equals( User.ROLE_USER ) ) ) {
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Method authenticates user based on session data. Credentials retrieved from session data
     * are compared with corresponding user record in the database. Method is used to control
     * user privileges in order to restrict access to administrative functionalities.
     *
     * @param sessionUser Currently signed user
     * @param dbUser User in the database
     * @return True if users are the same, false otherwise
     */
    public static boolean authenticateSessionUser( User sessionUser, User dbUser ) {
        if( sessionUser == null || dbUser == null ) {
            return false;
        } else {
            String sessionUserName = sessionUser.getName().trim();
            String sessionUserPassword = sessionUser.getPassword().trim();

            String dbUserName = dbUser.getName().trim();
            String dbUserPassword = dbUser.getPassword().trim();

            if( sessionUserName.equals( dbUserName ) && sessionUserPassword.equals(
                    dbUserPassword ) ) {
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Method gets user session attribute.
     *
     * @param request Http request
     * @return User attribute
     */
    public static Object getUser( HttpServletRequest request ) {
        return request.getSession( true ).getAttribute( USER );
    }
    /**
     * Method sets user session attribute.
     *
     * @param request HTTP request
     * @param user User object
     */
    public static void setUser( HttpServletRequest request, Object user ) {
        request.getSession( true ).setAttribute( USER, user );
    }
    /**
     * Method takes session user as parameter, seeks for the matching user in the database
     * and returns associated user role name.
     *
     * @param request HTTP request method
     * @return User role name
     */
    public static String getUserRole( HttpServletRequest request ) {
        User sessionUser = ( User )request.getSession( true ).getAttribute( USER );
        User dbUser = userManager.getByName( sessionUser.getName() );
        return dbUser.getRoleName();
    }
    /**
     * Method removes user session attribute.
     *
     * @param request Http request
     */
    public static void removeUser( HttpServletRequest request ) {
        request.getSession( true ).removeAttribute( USER );
    }
}
//--------------------------------------------------------------------------------------------------
