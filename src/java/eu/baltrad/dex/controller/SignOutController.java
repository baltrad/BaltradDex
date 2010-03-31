/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller;

import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.model.LogManager;
import eu.baltrad.dex.model.User;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;

/**
 * Sign out controller class implementing basic user authentication functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SignOutController implements Controller {

//---------------------------------------------------------------------------------------- Variables
    private ApplicationSecurityManager applicationSecurityManager;
    private LogManager logManager = new LogManager();
    private String successView;

//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request. Removes user attribute from session.
     * 
     * @param request Http request
     * @param response Http response
     * @return ModelAndView object
     * @throws java.lang.Exception
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response)
                                                                                throws Exception {
        User user = ( User )applicationSecurityManager.getUser( request );
        applicationSecurityManager.removeUser( request );
        logManager.addLogEntry( new Date(), logManager.MSG_INFO, "User " + user.getName()
                                                                                + " signed out" );
        return new ModelAndView( getSuccessView() );
    }

    /**
     * @return the applicationSecurityManager
     */
    public ApplicationSecurityManager getApplicationSecurityManager() {
        return applicationSecurityManager;
    }

    /**
     * @param applicationSecurityManager the applicationSecurityManager to set
     */
    public void setApplicationSecurityManager( 
                                        ApplicationSecurityManager applicationSecurityManager ) {
        this.applicationSecurityManager = applicationSecurityManager;
    }

    /**
     * @return the successView
     */
    public String getSuccessView() {
        return successView;
    }

    /**
     * @param successView the successView to set
     */
    public void setSuccessView(String successView) {
        this.successView = successView;
    }

}
//--------------------------------------------------------------------------------------------------