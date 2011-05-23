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

package eu.baltrad.dex.auth.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.log.model.*;
import eu.baltrad.dex.util.ApplicationSecurityManager;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Login controller class implementing basic user authentication functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class LoginController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public LoginController() {
        this.logManager = new LogManager();
        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_INFO, 
                "Baltrad Data Exchange System started" ) );
    }
    /**
     * Creates new user object.
     * 
     * @param request Http request
     * @return User object
     * @throws java.lang.Exception
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) throws Exception {
        return new User();
    }
    /**
     * Method executed upon form submission.
     *
     * @param request Http request
     * @param response Http response
     * @param command Command object
     * @param errors Form bind errors
     * @return ModelAndView object
     * @throws java.lang.Exception
     */
    @Override
    public ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
                                    Object command, BindException errors ) throws Exception {
        User formUser = ( User )command;
        // Look for user in the database
        User dbUser = userManager.getUserByName( formUser.getName() );
        if( ApplicationSecurityManager.authenticateFormUser( formUser, dbUser ) ) {
            // Set user variable for this session
            ApplicationSecurityManager.setUser( request, dbUser );
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_INFO,
                "User " + dbUser.getName() + " logged on" ) );
        }
        return new ModelAndView( getSuccessView() );
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
