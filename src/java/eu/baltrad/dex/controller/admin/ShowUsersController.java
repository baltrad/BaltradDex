/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.user.UserManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.model.user.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * Creates list of users registered in the system.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ShowUsersController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String MODEL_KEY = "users";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private UserManager userManager;
    private ApplicationSecurityManager applicationSecurityManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Gets list of all registered users except for currently signed user.
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return List of all users available in the system
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
         List users = userManager.getAllUsers();
         User signedUser = ( User )applicationSecurityManager.getUser( request );
         for( int i = 0; i < users.size(); i++ ) {
             User user = ( User )users.get( i );
             if( applicationSecurityManager.authenticateSessionUser( signedUser, user ) ) {
                 users.remove( i );
             }
         }
         return new ModelAndView( getSuccessView(), MODEL_KEY, users );
     }
    /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Method sets reference to success view name string.
     *
     * @param successView Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
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
