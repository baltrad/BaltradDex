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
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.core.model.CertManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.core.model.Cert;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Multi-action controller handling user account removal functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class RemoveUserController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String REMOVE_ACCOUNT_KEY = "users";
    private static final String ACCOUNT_TO_REMOVE_KEY = "selected_users";
    private static final String REMOVED_USERS_KEY = "removed_users";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";

    // view names
    private static final String REMOVE_ACCOUNT_VIEW = "removeAccount";
    private static final String ACCOUNT_TO_REMOVE_VIEW = "accountToRemove";
    private static final String REMOVED_ACCOUNT_VIEW = "removeAccountStatus";
//---------------------------------------------------------------------------------------- Variables
    // User manager
    private UserManager userManager;
    /** Certificate manager */
    private CertManager certManager;
    // Logger object
    private Logger log;
    /** List of user accounts to be removed */
    private List<User> removeUsers;
    
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public RemoveUserController() {
        removeUsers = new ArrayList<User>();
        log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets list of all registered users except for currently signed user.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Model and view containing list of all user accounts registered in the system
     */
    public ModelAndView removeAccount( HttpServletRequest request, HttpServletResponse response ) {
        List<User> allUsers = userManager.get();
        removeUsers.clear();
        for (int i = 0; i < allUsers.size(); i++) {
            if (!allUsers.get(i).getRoleName().equals(User.ROLE_ADMIN)) {
                removeUsers.add(allUsers.get(i));
            }
        }
        /*User signedUser = ( User )ApplicationSecurityManager.getUser( request );
        for( int i = 0; i < users.size(); i++ ) {
            User user = ( User )users.get( i );
            if( ApplicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
                users.remove( i );
            }
        }*/
        return new ModelAndView( REMOVE_ACCOUNT_VIEW, REMOVE_ACCOUNT_KEY, removeUsers );
    }
    /**
     * Gets list of user accounts selected for removal.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Model and view containing list of user accounts selected for removal
     */
    public ModelAndView accountToRemove( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = null;
        String[] userIds = request.getParameterValues( ACCOUNT_TO_REMOVE_KEY );
        if( userIds != null ) {
            List< User > users = new ArrayList< User >();
            for( int i = 0; i < userIds.length; i++ ) {
                users.add( userManager.get( Integer.parseInt( userIds[ i ] ) ) );
            }
            modelAndView = new ModelAndView( ACCOUNT_TO_REMOVE_VIEW, ACCOUNT_TO_REMOVE_KEY, users );
        } else {
            
            /*List users = userManager.get();
            User signedUser = ( User )ApplicationSecurityManager.getUser( request );
            for( int i = 0; i < users.size(); i++ ) {
                User user = ( User )users.get( i );
                if( ApplicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
                    users.remove( i );
                }
            }*/
            modelAndView = new ModelAndView(REMOVE_ACCOUNT_VIEW, REMOVE_ACCOUNT_KEY, removeUsers);
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
    public ModelAndView removeAccountStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        String[] userIds = request.getParameterValues( REMOVED_USERS_KEY );
        String userName = "";
        for( int i = 0; i < userIds.length; i++ ) {
            try {
                User user = userManager.get( Integer.parseInt( userIds[ i ] ) );
                userName = user.getName();
                userManager.deleteUser( Integer.parseInt( userIds[ i ] ) );
                /* Remove user certificate if user is peer
                if (user.getRoleName().equals(User.ROLE_PEER)) {
                    String ksFileName = ServletContextUtil.getServletContextPath() + 
                        InitAppUtil.KS_FILE_PATH;
                    String ksPasswd = InitAppUtil.getConf().getKeystorePass();
                    certManager.deleteFromKS(ksFileName, ksPasswd, user.getName());
                    Cert cert = certManager.get(user.getName());
                    cert.setTrusted(false);
                    certManager.saveOrUpdate(cert);
                }*/
                log.warn( "User account removed from the system: " + userName );
            } catch( Exception e ) {
                String msg = "Failed to remove user account";
                request.getSession().removeAttribute(OK_MSG_KEY);
                request.getSession().setAttribute(ERROR_MSG_KEY, msg);
                log.error(msg, e);
            }
        }
        String msg = "Selected user account successfully removed.";
        return new ModelAndView(REMOVED_ACCOUNT_VIEW, OK_MSG_KEY, msg);
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
     * Certificate manager getter.
     * 
     * @return Reference to certificate manager
     */
    public CertManager getCertManager() { return certManager; }
    /**
     * Certificate manager setter.
     * 
     * @param certManager Reference to certificate manager to set
     */
    public void setCertManager(CertManager certManager) { this.certManager = certManager; }
}
//--------------------------------------------------------------------------------------------------
