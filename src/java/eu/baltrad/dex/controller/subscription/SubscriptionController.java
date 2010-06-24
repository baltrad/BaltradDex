/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.controller.subscription;

import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.model.channel.Channel;
import eu.baltrad.dex.model.subscription.Subscription;
import eu.baltrad.dex.model.channel.ChannelManager;
import eu.baltrad.dex.model.subscription.SubscriptionManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.model.log.LogManager;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Multi-action controller for data channel subscription functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_SUBSCRIPTIONS_KEY = "subscriptions";
    private static final String SELECTED_SUBSCRIPTIONS_KEY = "selected_subscriptions";
    private static final String SUBMITTED_SUBSCRIPTIONS_KEY = "submitted_subscriptions";
    private static final String SUBSCRIPTION_STATUS_KEY = "subscription_status";
    // view names
    private static final String SHOW_SUBSCRIPTIONS_VIEW = "showSubscriptions";
    private static final String SUBMITTED_SUBSCRIPTIONS_VIEW = "showSelectedSubscriptions";
    private static final String SUBSCRIPTION_STATUS_VIEW = "showSubscriptionStatus";
//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private SubscriptionManager subscriptionManager;
    private ApplicationSecurityManager applicationSecurityManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Shows list of data channels available for subscription.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of all channels available for subscription
     */
    public ModelAndView showSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        User user = ( User )applicationSecurityManager.getUser( request );
        List userSubscriptions = subscriptionManager.getUserSubscriptions( user.getId() );
        List subscriptions = channelManager.getChannels( userSubscriptions );
        return new ModelAndView( SHOW_SUBSCRIPTIONS_VIEW, SHOW_SUBSCRIPTIONS_KEY, subscriptions );
    }
    /**
     * Shows list of data channels selected for subscription.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of data channels selected for subscription
     */
    public ModelAndView showSelectedSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        // List of channels selected for subscription
        String[] selChannels = request.getParameterValues( SELECTED_SUBSCRIPTIONS_KEY );
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
        return new ModelAndView( SUBMITTED_SUBSCRIPTIONS_VIEW, SUBMITTED_SUBSCRIPTIONS_KEY, chSubmit );
    }
    /**
     * Shows list of subscribed data channels.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of subscribed data channels
     */
    public ModelAndView showSubscriptionStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        // List of channels selected for subscription
        String[] subChannels = request.getParameterValues( SUBMITTED_SUBSCRIPTIONS_KEY );
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
                logManager.addEntry( new Date(), LogManager.MSG_INFO, "User " + user.getName() +
                                        " subscribed to " + ch.getName() );
            }
        }
        return new ModelAndView( SUBSCRIPTION_STATUS_VIEW, SUBSCRIPTION_STATUS_KEY, userSubs );
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
