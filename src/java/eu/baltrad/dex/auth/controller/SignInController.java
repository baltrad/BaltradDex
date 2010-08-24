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

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Date;

/**
 * Sign in controller class implementing basic user authentication functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SignInController extends SimpleFormController {

//------------------------------------------------------------------------------------------- Fields
    private ApplicationSecurityManager applicationSecurityManager;
    private UserManager userManager;
    private LogManager logManager = new LogManager();
    private String viewName;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SignInController() {
        logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                                        "Baltrad Data Exchange System started" );
    }

    /**
     * Method returns new user object. 
     * 
     * @param request Http request
     * @return User object
     * @throws java.lang.Exception
     */
    protected Object formBackingObject( HttpServletRequest request ) throws Exception {
        return new User();
    }

    /**
     * Method redirects user to the welcome page once authentication is completed.
     *
     * @param request Http request
     * @param response Http response
     * @param errors Form bind errors
     * @param controlModel
     * @return ModelAndView object
     * @throws java.lang.Exception
     */
    public ModelAndView showForm( HttpServletRequest request, HttpServletResponse response,
                                    BindException errors, Map controlModel ) throws Exception {
        if( applicationSecurityManager.getUser( request ) != null )
            return new ModelAndView( getViewName() );
        return super.showForm( request, response, errors, controlModel );
    }

    /**
     * Method looks up user and password in the database.
     *
     * @param request Http request
     * @param command Command object
     * @param errors Form bind errors
     * @throws java.lang.Exception
     */
    public void onBindAndValidate( HttpServletRequest request, Object command,
                                                        BindException errors) throws Exception {
        if( errors.hasErrors() ) return;
        User formUser = ( User )command;
        User dbUser = ( User )command;
        // Look for user in the database
        dbUser = userManager.getUserByName( formUser.getName() );
        if( applicationSecurityManager.authenticateFormUser( formUser, dbUser ) ) {
            // Set user variable for this session
            applicationSecurityManager.setUser( request, dbUser );
            logManager.addEntry( new Date(), LogManager.MSG_INFO, "User "
                                                                + dbUser.getName() + " signed in" );
        } else {
            logManager.addEntry( new Date(), LogManager.MSG_WRN,
                                                                "User name or password invalid" );
            errors.reject( "error.login.invalid" );
        }
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
    public ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
                                    Object command, BindException errors) throws Exception {
        return new ModelAndView( getSuccessView() );
    }
    /**
     * @return the viewName
     */
    public String getViewName() { return viewName; }

    /**
     * @param viewName the viewName to set
     */
    public void setViewName( String viewName ) { this.viewName = viewName; }
    /**
     * Method gets reference to ApplicationSecurityManager object.
     *
     * @return Reference to ApplicationSecurityManager object
     */
    public ApplicationSecurityManager getApplicationSecurityManager() {
        return applicationSecurityManager;
    }
    /**
     * Method sets reference to ApplicationSecurityManager object.
     *
     * @param applicationSecurityManager Reference to ApplicationSecurityManager object
     */
    public void setApplicationSecurityManager(
                                    ApplicationSecurityManager applicationSecurityManager ) {
        this.applicationSecurityManager = applicationSecurityManager;
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