/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller;

import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.model.ChannelManager;
import eu.baltrad.dex.model.SubscriptionManager;
import eu.baltrad.dex.model.LogManager;
import eu.baltrad.dex.model.Subscription;
import eu.baltrad.dex.model.Channel;
import eu.baltrad.dex.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Subscription status controller implements subscription status information functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionStatusController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    //private static final String SUBMIT_OPTION = "submit_button";
    private static final String REQUEST_PARAM_KEY = "submitted_channels";
    private static final String RESPONSE_PARAM_KEY = "user_subscriptions";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private SubscriptionManager subscriptionManager;
    private LogManager logManager;
    private ApplicationSecurityManager applicationSecurityManager;
    private String successView;
//------------------------------------------------------------------------------------------ Methods
     /**
     * Method handles http request. Returns ModelAndView object containing list of
     * subscribed data channels.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
            HttpServletResponse response ) throws ServletException, IOException {

        // List of channels selected for subscription
        String[] subChannels = request.getParameterValues( REQUEST_PARAM_KEY );
        User user = ( User )applicationSecurityManager.getUser( request );
        List userSubs = new ArrayList();
        // Cancel user subscriptions
        subscriptionManager.cancelUserSubscriptions( user.getId() );
        // Check if submitted channels list is not null
        if( subChannels != null ) {
            for( int i = 0; i < subChannels.length; i++ ) {
                Channel ch = channelManager.getChannel( subChannels[ i ] );
                userSubs.add( ch );
                Subscription s = new Subscription( user.getId(), ch.getId() );
                subscriptionManager.registerSubscription( s );
                logManager.addLogEntry( new Date(), LogManager.MSG_INFO, "User " + user.getName() +
                                        " subscribed to " + ch.getName() );
            }
        }
        return new ModelAndView( getSuccessView(), RESPONSE_PARAM_KEY, userSubs );
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