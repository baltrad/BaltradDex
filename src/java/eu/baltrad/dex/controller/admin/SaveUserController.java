/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.user.UserManager;
import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.model.log.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import org.hibernate.HibernateException;

import java.util.HashMap;
import java.util.Date;

/**
 * Controller class registers new user in the system or modifies existing user account.
 *
 * @author szewczenko
 */
public class SaveUserController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    public static final String USER_ID = "id";
    public static final String MSG = "message";
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches User object with a given USER_ID passed as request parameter,
     * or creates new User instance in case USER_ID is not set in request.
     *
     * @param request HttpServletRequest
     * @return User class object
     */
    protected Object formBackingObject( HttpServletRequest request ) {
        User user = null;
        if( request.getParameter( USER_ID ) != null
                && request.getParameter( USER_ID ).trim().length() > 0 ) {
            user = userManager.getUserByID( Integer.parseInt( request.getParameter( USER_ID ) ) );
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
    protected HashMap referenceData( HttpServletRequest request ) throws Exception {
        HashMap model = new HashMap();
        model.put( "roles", userManager.getAllRoles() );
        return model;
    }
    /**
     * Saves User object
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {
        User user = ( User )command;
        try {
            userManager.addUser( user );
            request.getSession().setAttribute( MSG, getMessageSourceAccessor().getMessage(
                "message.adduser.savesuccess" ) );
            logManager.addEntry( new Date(), LogManager.MSG_WRN, "User account saved: " +
                user.getName() );
        } catch( HibernateException e ) {
            request.getSession().setAttribute( MSG, getMessageSourceAccessor().getMessage(
                "message.adduser.nameexists" ) );
            errors.reject( "message.adduser.nameexists" );
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
    /**
     * Method gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Method sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------