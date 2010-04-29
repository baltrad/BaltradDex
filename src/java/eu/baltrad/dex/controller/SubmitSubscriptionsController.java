/*
 * BaltradDex :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.controller;

import eu.baltrad.dex.model.ChannelManager;
import eu.baltrad.dex.model.SubscriptionManager;
import eu.baltrad.dex.model.User;
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
    private static final String RESPONSE_PARAM_KEY = "submitted_channels";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
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

        // List of channels selected for subscription
        String[] selChannels = request.getParameterValues( REQUEST_PARAM_KEY );
        User user = ( User )applicationSecurityManager.getUser( request );
        // Selected channel IDs
        List sel = channelManager.getChannelIds( selChannels );
        // Subscribed channel IDs
        List subs = subscriptionManager.getChannelIds( user.getId() );
        // Compare lists
        boolean changed = false;
        if( sel.size() != subs.size() ) {
            changed = true;
        } else {
            for( int i = 0; i < sel.size(); i++ ) {
                Object o = sel.get( i );
                if( !subs.contains( o ) ) {
                    changed = true;
                }
            }
        }
        // If status has not changed, the list remains null 
        List chSubmit = null;
        // Status has changed, the list will either be empty or contain some objects
        if( changed ) {
            if( selChannels == null ) {
                // User submits empty list / cancels subscriptions
                chSubmit = new ArrayList();
            } else {
                // User submits new subscriptions list
                chSubmit = new ArrayList();
                for( int i = 0; i < selChannels.length; i++ ) {
                    Channel ch = ( Channel )channelManager.getChannel( selChannels[ i ] );
                    chSubmit.add( ch );
                }
            }
        }
        return new ModelAndView( getSuccessView(), RESPONSE_PARAM_KEY, chSubmit );
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
}
//--------------------------------------------------------------------------------------------------
