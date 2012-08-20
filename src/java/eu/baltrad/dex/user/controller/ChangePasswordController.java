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

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Password;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.log.model.MessageLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import org.apache.log4j.Logger;

/**
 * Controller class allows to modify password associated with a given account / user ID.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.1
 * @since 0.7.1
 */
public class ChangePasswordController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    /* User ID key */
    public static final String USER_ID = "userId";
    /* Success message key */
    private static final String OK_MSG_KEY = "message";
    /* Error message key */
    private static final String ERROR_MSG_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    /* Reference to user manager object */
    private UserManager userManager;
    /* Message logger */
    private Logger log;
    /* Stores user object */
    private User user;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public ChangePasswordController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Constructs password objects matching a given user identified by ID.
     *
     * @param request HttpServletRequest
     * @return Password class object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        user = userManager.get( Integer.parseInt( request.getParameter( USER_ID ) ) );
        Password passwd = new Password( user.getName() );
        return passwd;
    }
    /**
     * Saves user's password..
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {
        Password passwd = ( Password )command;
        try {
            user.setPassword( passwd.getNewPasswd() );
            userManager.saveOrUpdate( user );
            String msg = "Password successfully changed for user " + user.getName();
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to change password for user " + user.getName();
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( msg, e );
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
