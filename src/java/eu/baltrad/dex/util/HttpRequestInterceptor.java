/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class implementing http request interceptor functionality. Most of http request is redirected
 * to HttpRequestInterceptor object to provide user authentication control.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class HttpRequestInterceptor extends HandlerInterceptorAdapter {

    // Sign in page
    private String signInPage;

    // Application security manager utility
    private ApplicationSecurityManager applicationSecurityManager;

    /**
     * Method handles http request.
     *
     * @param request Http request
     * @param response Http response
     * @param handler Handler
     * @return True if user object is not null, false otherwise
     * @throws java.lang.Exception
     */
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
                                                                Object handler ) throws Exception {
        
        if( applicationSecurityManager.getUser( request ) == null ) {
            response.sendRedirect( signInPage );
            return false;
        } else {
            return true;
        }
    }
   
    /**
     * Method returns reference to sign in page.
     *
     * @return Reference to sign in page.
     */
    public String getSignInPage() {
        return signInPage;
    }

    /**
     * Method sets reference to sign in page.
     *
     * @param signInPage Reference to sign in page.
     */
    public void setSignInPage( String signInPage ) {
        this.signInPage = signInPage;
    }

    /**
     * Method returns reference to application security manager object.
     *
     * @return Reference to application security manager object
     */
    public ApplicationSecurityManager getApplicationSecurityManager() {
        return applicationSecurityManager;
    }
    
    /**
     * Method sets reference to application security manager object.
     *
     * @param applicationSecurityManager Reference to application security manager object.
     */
    public void setApplicationSecurityManager( 
                                ApplicationSecurityManager applicationSecurityManager ) {
        this.applicationSecurityManager = applicationSecurityManager;
    }

}
