/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.ChannelManager;
import pl.imgw.baltrad.dex.model.SubscriptionManager;
import pl.imgw.baltrad.dex.model.User;
import pl.imgw.baltrad.dex.util.ApplicationSecurityManager;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.List;

/**
 * Data channel list controller class implementing data channel handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class ChannelController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    public static final String MAP_KEY = "channels";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private SubscriptionManager subscriptionManager;
    private ApplicationSecurityManager applicationSecurityManager;
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
                                                        HttpServletResponse response )
                                                        throws ServletException, IOException {
        // Get signed user
        User user = ( User )getApplicationSecurityManager().getUser( request );
        // Get user subscriptions
        List userSubscriptions = getSubscriptionManager().getUserSubscriptions( user.getId() );
        // Get data channel list and check channels selected by the user
        List channelsList = channelManager.getChannels( userSubscriptions );
        return new ModelAndView( getSuccessView(), MAP_KEY, channelsList );
    }
    /**
     * Method returns reference to data channel manager object.
     *
     * @return Reference to data channel manager object
     */
    public ChannelManager getChannelManager() { return channelManager; }
    /**
     * Method sets reference to data channel manager object.
     *
     * @param Reference to data channel manager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
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
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
    /**
     * Method gets reference to SubscriptionManager class object.
     *
     * @return Reference to SubscriptionManager class object
     */
    public SubscriptionManager getSubscriptionManager() { return subscriptionManager; }
    /**
     * Method sets reference to SubscriptionManager class object.
     *
     * @param subscriptionManager Reference to SubscriptionManager class object
     */
    public void setSubscriptionManager( SubscriptionManager subscriptionManager ) {
        this.subscriptionManager = subscriptionManager;
    }
    /**
     * Method gets reference to ApplicationSecurityManager class object.
     * 
     * @return Reference to ApplicationSecurityManager class object
     */
    public ApplicationSecurityManager getApplicationSecurityManager() {
        return applicationSecurityManager;
    }
    /**
     * Method sets reference to ApplicationSecurityManager class object.
     *
     * @param applicationSecurityManager Reference to ApplicationSecurityManager class object
     */
    public void setApplicationSecurityManager(
                                        ApplicationSecurityManager applicationSecurityManager ) {
        this.applicationSecurityManager = applicationSecurityManager;
    }
}
//--------------------------------------------------------------------------------------------------