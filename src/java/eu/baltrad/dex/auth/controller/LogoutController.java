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

package eu.baltrad.dex.auth.controller;

import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.user.model.User;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logout controller class implementing basic user authentication functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class LogoutController implements Controller {
//---------------------------------------------------------------------------------------- Variables
    private LogManager logManager = new LogManager();
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request. Removes user attribute from session.
     * 
     * @param request Http request
     * @param response Http response
     * @return ModelAndView object
     * @throws java.lang.Exception
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = ( User )ApplicationSecurityManager.getUser( request );
        ApplicationSecurityManager.removeUser( request );
        logManager.addEntry( System.currentTimeMillis(), LogManager.MSG_INFO, "User " + user.getName()
                + " logged out" );
        return new ModelAndView( getSuccessView() );
    }
    /**
     * Gets reference to success view name.
     *
     * @return Reference to success view name
     */
    public String getSuccessView() { return successView; }
    /**
     * Sets reference to success view name.
     *
     * @param successView Reference to success view name
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------