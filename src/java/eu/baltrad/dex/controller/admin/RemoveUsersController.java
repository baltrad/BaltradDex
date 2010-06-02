/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.user.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.hibernate.HibernateException;

/**
 * Removes selected user accounts.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class RemoveUsersController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String REQUEST_PARAM_KEY = "submitted_users";
    private static final String MODEL_KEY = "hibernate_errors";
//---------------------------------------------------------------------------------------- Variables
    private String successView;
    private UserManager userManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Removes selected user accounts.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Empty model and view object
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        String[] userIds = request.getParameterValues( REQUEST_PARAM_KEY );
        List< String > errorMsgs = new ArrayList< String >();
        for( int i = 0; i < userIds.length; i++ ) {
            try {
                userManager.removeUser( Integer.parseInt( userIds[ i ] ) );
            } catch( HibernateException e ) {
                errorMsgs.add( "Data access exception while removing user account " +
                        "(User ID: " + userIds[ i ] + ")" );
            }
        }
        return new ModelAndView( getSuccessView(), MODEL_KEY, errorMsgs );
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
}
//--------------------------------------------------------------------------------------------------
