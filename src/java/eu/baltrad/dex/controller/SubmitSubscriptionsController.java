/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller;

import eu.baltrad.dex.model.ChannelManager;
import eu.baltrad.dex.model.AuxSubscriptionManager;
import eu.baltrad.dex.model.User;
import eu.baltrad.dex.model.AuxSubscription;
import eu.baltrad.dex.model.Channel;
import eu.baltrad.dex.util.ApplicationSecurityManager;

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
    private static final String REQUEST_PARAM_KEY = "selected_channels";
    private static final String RESPONSE_PARAM_KEY = "submitted_subscriptions";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private AuxSubscriptionManager auxSubscriptionManager;
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

        String[] selectedChannels = request.getParameterValues( REQUEST_PARAM_KEY );
        User user = ( User )applicationSecurityManager.getUser( request );
        
        List submittedSubscriptions = new ArrayList();
        if( selectedChannels != null && selectedChannels.length > 0 ) {
            for( int i = 0; i < selectedChannels.length; i++ ) {
                // Get selected data channels by name
                String channelName = selectedChannels[ i ].substring( 0, 1 ).toLowerCase()
                   + selectedChannels[ i ].substring( 1, selectedChannels[ i ].length() );
                submittedSubscriptions.add( channelManager.getChannel( channelName ) );
                
                // Add selected subscriptions to auxiliary subscriptions table
                Channel dataChannel = channelManager.getChannel( channelName );
                AuxSubscription auxSubscription = new AuxSubscription( user.getId(), 
                                                                            dataChannel.getId() );
                auxSubscriptionManager.registerSubscription( auxSubscription );
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
    public ChannelManager getChannelManager() { return channelManager; }
    /**
     * Method sets reference to data channel manager object.
     *
     * @param channelManager Reference to data channel manager object
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
     * @param successView Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
    /**
     * Method gets reference to auxiliary subscription manager object.
     *
     * @return Auxiliary subscription manager object
     */
    public AuxSubscriptionManager getAuxSubscriptionManager() { return auxSubscriptionManager; }
    /**
     * Method sets reference to auxiliary subscription manager object.
     *
     * @param auxSubscriptionManager Reference to auxiliary subscription manager object
     */
    public void setAuxSubscriptionManager( AuxSubscriptionManager auxSubscriptionManager ) {
        this.auxSubscriptionManager = auxSubscriptionManager;
    }
}
//--------------------------------------------------------------------------------------------------
