/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.admin;

import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.model.user.UserManager;
import eu.baltrad.dex.model.log.LogManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Sign in controller class implementing basic user authentication functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class AdminUserController implements Controller {

//---------------------------------------------------------------------------------------- Constants
    private static final String ADMIN_USER_NAME = "admin";
//---------------------------------------------------------------------------------------- Variables
    private ApplicationSecurityManager applicationSecurityManager;
    private UserManager userManager;
    private LogManager logManager = new LogManager();
    private String successView;
    private String redirectView;
//------------------------------------------------------------------------------------------ Methods

    /**
     * Method is used to control access to administrative functionalities.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
                            HttpServletResponse response ) throws ServletException, IOException {

        User sessionUser = ( User )applicationSecurityManager.getUser( request );
        User dbUser = userManager.getUserByName( ADMIN_USER_NAME );
        if( !sessionUser.getName().equals( ADMIN_USER_NAME ) ||
                    !applicationSecurityManager.authenticateSessionUser( sessionUser, dbUser ) ) {
            response.sendRedirect( getRedirectView() );
        }
        return new ModelAndView( getSuccessView() );
    }

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
    /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() {
        return successView;
    }
    /**
     * Method sets reference to success view name string.
     *
     * @param successView Reference to success view name string
     */
    public void setSuccessView( String successView ) {
        this.successView = successView;
    }
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
    /**
     * Method gets reference to user manager class object.
     *
     * @return Reference to user manager class object
     */
    public UserManager getUserManager() {
        return userManager;
    }
    /**
     * Method sets reference to user manager class object.
     *
     * @param userManager Reference to user manager class object
     */
    public void setUserManager( UserManager userManager ) {
        this.userManager = userManager;
    }
    /**
     * Method gets redirect view name.
     *
     * @return Redirect view name
     */
    public String getRedirectView() { return redirectView; }
    /**
     * Method sets redirect view name.
     *
     * @param redirectView Redirect view name
     */
    public void setRedirectView( String redirectView ) { this.redirectView = redirectView; }
}
//--------------------------------------------------------------------------------------------------
