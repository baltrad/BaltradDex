/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.user.controller;

import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.log.util.MessageLogger;
import eu.baltrad.dex.util.MessageDigestUtil;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Save or modify user account.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class SaveUserController extends SimpleFormController {

    public static final String USER_ID = "userId";
    public static final String ROLES = "roles";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";

    private UserManager userManager;
    private Logger log;

    /**
     * Constructor.
     */
    public SaveUserController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Fetches User object with a given USER_ID passed as request parameter,
     * or creates new User instance in case USER_ID is not set in request.
     *
     * @param request HttpServletRequest
     * @return User class object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        User user = null;
        if( request.getParameter( USER_ID ) != null
                && request.getParameter( USER_ID ).trim().length() > 0 ) {
            user = userManager
                    .load(Integer.parseInt(request.getParameter(USER_ID)));
        } else {
            user = new User();
        }
        return user;
    }
    /**
     * Returns HashMap holding list of all user roles defined in the system.
     *
     * @param request HttpServletRequest
     * @return HashMap object holding role names
     * @throws Exception
     */
    @Override
    protected HashMap referenceData( HttpServletRequest request ) 
            throws Exception {
        HashMap model = new HashMap();
        List<Role> roles = userManager.loadRoles();
        List<Role> allButPeer = new ArrayList<Role>();
        for (Role role : roles) {
            if (!role.getRole().equals("peer")) {
                allButPeer.add(role);
            }
        }
        model.put(ROLES, allButPeer);
        model.remove(User.ROLE_PEER);
        return model;
    }
    /**
     * Saves User object.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, 
        HttpServletResponse response, Object command, BindException errors) {
        User user = ( User )command;
        user.setPassword(MessageDigestUtil.createHash("MD5", 16, 
                user.getPassword()));
        try {
            String msg = "";
            if (user.getId() > 0) {
                userManager.update(user);
                msg = "User account successfully updated: " + user.getName();
            } else {
                userManager.storeNoId(user);
                msg = "User account successfully stored: " + user.getName();
            }
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to save user account";
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
    public void setUserManager(UserManager userManager) { 
        this.userManager = userManager; 
    }
}
