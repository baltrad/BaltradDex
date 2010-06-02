/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller.subscription;

import eu.baltrad.dex.model.channel.ChannelManager;
import eu.baltrad.dex.model.subscription.SubscriptionManager;
import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * Subscriptions controller class implementing data channel subscription functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionsController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String MAP_KEY = "subscriptions";
//---------------------------------------------------------------------------------------- Variables
    private ApplicationSecurityManager applicationSecurityManager;
    private ChannelManager channelManager;
    private SubscriptionManager subscriptionManager;
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request. Returns ModelAndView object containing list of 
     * user subscriptions.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {
        User user = ( User )applicationSecurityManager.getUser( request );
        List userSubscriptions = subscriptionManager.getUserSubscriptions( user.getId() );
        List subscriptions = channelManager.getChannels( userSubscriptions );
        return new ModelAndView( successView, MAP_KEY, subscriptions );
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
     * Method returns reference to data channel manager object.
     *
     * @return Reference to data channel manager object
     */
    public ChannelManager getChannelManager() { return channelManager; }
    /**
     * Method sets reference to data channel manager object.
     *
     * @param dataChannelManager Reference to data channel manager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
    }
    /**
     * Method returns reference to SubscriptionManager object.
     *
     * @return Reference to SubscriptionManager object
     */
    public SubscriptionManager getSubscriptionManager() { return subscriptionManager; }
    /**
     * Method sets reference to SubscriptionManager object.
     *
     * @param subscriptionManager Reference to SubscriptionManager object
     */
    public void setSubscriptionManager( SubscriptionManager subscriptionManager ) {
        this.subscriptionManager = subscriptionManager;
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
}
//--------------------------------------------------------------------------------------------------
