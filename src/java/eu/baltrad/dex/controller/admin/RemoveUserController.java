/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.model.user.UserManager;
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
 * Multi action controller handling user account removal functionality.
 *
 * @author szewczenko
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
    // Application security manager
    private ApplicationSecurityManager applicationSecurityManager;
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
        List users = userManager.getAllUsers();
        User signedUser = ( User )applicationSecurityManager.getUser( request );
        for( int i = 0; i < users.size(); i++ ) {
            User user = ( User )users.get( i );
            if( applicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
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
            List users = userManager.getAllUsers();
            User signedUser = ( User )applicationSecurityManager.getUser( request );
            for( int i = 0; i < users.size(); i++ ) {
                User user = ( User )users.get( i );
                if( applicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
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
    /**
     * Method returns reference to ApplicationSecurityManager object.
     *
     * @return applicationSecurityManager Reference to ApplicationSecurityManager object
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
}
//--------------------------------------------------------------------------------------------------
