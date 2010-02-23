/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.User;
import pl.imgw.baltrad.dex.model.UserManager;
import pl.imgw.baltrad.dex.model.LogManager;
import pl.imgw.baltrad.dex.util.ApplicationSecurityManager;

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
 * @author szewczenko
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
        logManager.addLogEntry( new Date(), logManager.MSG_INFO,
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
            logManager.addLogEntry( new Date(), logManager.MSG_INFO, "User "
                                                                + dbUser.getName() + " signed in" );
        } else {
            logManager.addLogEntry( new Date(), logManager.MSG_WRN,
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
     * @return the userManager
     */
    public UserManager getUserManager() {
        return userManager;
    }
    /**
     * @param userManager the userManager to set
     */
    public void setUserManager( UserManager userManager ) {
        this.userManager = userManager;
    }
}
//--------------------------------------------------------------------------------------------------
