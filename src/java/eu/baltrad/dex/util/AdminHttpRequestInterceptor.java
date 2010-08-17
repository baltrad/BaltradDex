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

package eu.baltrad.dex.util;

import eu.baltrad.dex.user.model.User;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class implementing http request interceptor functionality. Most of http request is redirected
 * to HttpRequestInterceptor object to provide user authentication control.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class AdminHttpRequestInterceptor extends HandlerInterceptorAdapter {
//---------------------------------------------------------------------------------------- Constants
    private static final String SIGNIN_PAGE_REDIRECT = "signin.htm";
    private static final String WELCOME_PAGE_REDIRECT = "welcome.htm";
//---------------------------------------------------------------------------------------- Variables
    // Application security manager utility
    private ApplicationSecurityManager applicationSecurityManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request.
     *
     * @param request Http request
     * @param response Http response
     * @param handler Handler
     * @return True if user object is not null, false otherwise
     * @throws java.lang.Exception
     */
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
                                                                Object handler ) throws Exception {
        if( applicationSecurityManager.getUser( request ) == null ) {
            response.sendRedirect( SIGNIN_PAGE_REDIRECT );
            return false;
        } else if ( !applicationSecurityManager.getUserRole( request ).equals( User.ROLE_ADMIN ) ) {
            response.sendRedirect( WELCOME_PAGE_REDIRECT );
            return false;
        } else {
            return true;
        }
    }
    /**
     * Method returns reference to application security manager object.
     *
     * @return Reference to application security manager object
     */
    public ApplicationSecurityManager getApplicationSecurityManager() {
        return applicationSecurityManager;
    }
    /**
     * Method sets reference to application security manager object.
     *
     * @param applicationSecurityManager Reference to application security manager object.
     */
    public void setApplicationSecurityManager(
                                ApplicationSecurityManager applicationSecurityManager ) {
        this.applicationSecurityManager = applicationSecurityManager;
    }
}
//--------------------------------------------------------------------------------------------------
