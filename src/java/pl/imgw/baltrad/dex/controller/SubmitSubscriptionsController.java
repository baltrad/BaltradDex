/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.DataChannelManager;
import pl.imgw.baltrad.dex.model.SubscriptionManager;
import pl.imgw.baltrad.dex.model.User;
import pl.imgw.baltrad.dex.model.Subscription;
import pl.imgw.baltrad.dex.util.ApplicationSecurityManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Submit subscriptions controller implements subscription modification functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class SubmitSubscriptionsController implements Controller {

//---------------------------------------------------------------------------------------- Constants
    private static final String REQUEST_PARAM_KEY = "selectedChannels";
    private static final String RESPONSE_PARAM_KEY = "submittedSubscriptions";
//---------------------------------------------------------------------------------------- Variables
    private DataChannelManager dataChannelManager;
    private SubscriptionManager subscriptionManager;
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

        String[] selectedSubscriptions = request.getParameterValues( REQUEST_PARAM_KEY );
        // Get user from request
        User user = ( User )applicationSecurityManager.getUser( request );
        // Remove all active subscriptions
        subscriptionManager.cancelUserSubscriptions( user.getId() );

        List submittedSubscriptions = new ArrayList();
        
        if( selectedSubscriptions != null && selectedSubscriptions.length > 0 ) {
            for( int i = 0; i < selectedSubscriptions.length; i++ ) {
                String dataChannelName = selectedSubscriptions[ i ].substring( 0, 1 ).toLowerCase()
                   + selectedSubscriptions[ i ].substring( 1, selectedSubscriptions[ i ].length() );
                submittedSubscriptions.add( dataChannelManager.getDataChannel( dataChannelName ) );
                // Add subscription to subscription table
                Subscription subscription = new Subscription( user.getId(),
                        dataChannelManager.getDataChannel( dataChannelName ).getDataChannelID() );
                subscriptionManager.registerSubscription( subscription );
            }
        }
        return new ModelAndView( getSuccessView(), RESPONSE_PARAM_KEY, submittedSubscriptions );
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
    public DataChannelManager getDataChannelManager() {
        return dataChannelManager;
    }

    /**
     * Method sets reference to data channel manager object.
     *
     * @param dataChannelManager Reference to data channel manager object
     */
    public void setDataChannelManager( DataChannelManager dataChannelManager ) {
        this.dataChannelManager = dataChannelManager;
    }

    /**
     * Method returns reference to SubscriptionManager object.
     *
     * @return Reference to SubscriptionManager object
     */
    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

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
    
}

//--------------------------------------------------------------------------------------------------
