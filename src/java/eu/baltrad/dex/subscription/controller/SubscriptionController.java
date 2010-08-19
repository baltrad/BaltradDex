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

package eu.baltrad.dex.subscription.controller;

import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.channel.model.ChannelManager;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.core.controller.FrameDispatcherController;
import eu.baltrad.dex.frame.model.BaltradFrame;
import eu.baltrad.dex.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.util.InitAppUtil;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import org.hibernate.HibernateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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
    private static final String SELECTED_CHANNELS_KEY = "selected_channels";
    private static final String SELECTED_SUBSCRIPTIONS_KEY = "selected_subscriptions";
    private static final String REQUEST_STATUS_KEY = "request_status";
    private static final String REMOVED_SUBSCRIPTIONS_KEY = "removed_subscriptions";
    // hibernate errors
    private static final String HIBERNATE_ERRORS_KEY = "hibernate_errors";
    // view names
    private static final String SHOW_SUBSCRIPTIONS_VIEW = "showSubscriptions";
    private static final String SELECTED_SUBSCRIPTIONS_VIEW = "showSelectedSubscriptions";
    private static final String SUBSCRIPTION_STATUS_VIEW = "showSubscriptionStatus";
    private static final String REMOVE_SUBSCRIPTIONS_VIEW = "selectRemoveSubscriptions";
    private static final String SELECT_REMOVE_SUBSCRIPTION_VIEW = "showRemovedSubscriptions";
    private static final String SUBSCRIPTION_REMOVAL_STATUS_VIEW = "showSubscriptionRemovalStatus";

//---------------------------------------------------------------------------------------- Variables
    private ChannelManager channelManager;
    private SubscriptionManager subscriptionManager;
    private FrameDispatcherController frameDispatcherController;
    private ApplicationSecurityManager applicationSecurityManager;
    private LogManager logManager;
    // subscription change request
    private List< Subscription > changedSubscriptions;
    // removed subscriptions
    private List< Subscription > removedSubscriptions;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Shows list of all subscriptions.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of all subscriptions
     */
    public ModelAndView showSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        List subscriptions = subscriptionManager.getSubscriptionsByType(
                Subscription.LOCAL_SUBSCRIPTION );
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
        // get the list of channels selected for subscription by the user
        String[] selChannels = request.getParameterValues( SELECTED_CHANNELS_KEY );
        // get the list of all available channels
        List< Subscription > currentSubs = subscriptionManager.getSubscriptionsByType(
                Subscription.LOCAL_SUBSCRIPTION );
        List< Subscription > selectedSubs = subscriptionManager.getSubscriptionsByType(
                Subscription.LOCAL_SUBSCRIPTION );
        // make sure the list of selected channels is not null
        if( selChannels != null ) {
            // create subscription list based on chosen channels
            for( int i = 0; i < selectedSubs.size(); i++ ) {
                selectedSubs.get( i ).setSelected( false );
                String channelName = selectedSubs.get( i ).getChannelName();
                int j = 0;
                while( j < selChannels.length ) {
                    if( channelName.equals( selChannels[ j ] ) ) {
                        selectedSubs.get( i ).setSelected( true );
                        break;
                    }
                    j++;
                }
            }
        } else {
            // user submits empty list - cancells all subscriptions
            for( int i = 0; i < selectedSubs.size(); i++ ) {
                selectedSubs.get( i ).setSelected( false );
            }
            request.setAttribute( REQUEST_STATUS_KEY, 2 );
        }
        // sort subscription lists
        Collections.sort( currentSubs );
        Collections.sort( selectedSubs );
        // determine subscription status
        if( subscriptionManager.compareSubscriptionLists( currentSubs, selectedSubs ) ) {
            request.setAttribute( REQUEST_STATUS_KEY, 0 );
        } else {
            request.setAttribute( REQUEST_STATUS_KEY, 1 );
        }
        // submit only changes - remove unchanged elements from the list of subscriptions
        List< Subscription > changedSubs = new ArrayList< Subscription >();
        for( int i = 0; i < currentSubs.size(); i++ ) {
            if( !subscriptionManager.compareSubscriptions( currentSubs.get( i ),
                    selectedSubs.get( i ) ) ) {
                changedSubs.add( selectedSubs.get( i ) );
            }
        }
        // write the list to class variable
        setChangedSubscriptions( changedSubs );
        return new ModelAndView( SELECTED_SUBSCRIPTIONS_VIEW, SELECTED_SUBSCRIPTIONS_KEY,
                changedSubs );
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
        // handle subscription change requests
        for( int i = 0; i < getChangedSubscriptions().size(); i++ ) {
            try {
                File tempFile = InitAppUtil.createTempFile( new File( InitAppUtil.getLocalTempDir() ) );
                ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( tempFile ) );
                try {
                    oos.writeObject( getChangedSubscriptions().get( i ) );
                } finally {
                    oos.close();
                }
                // prepare the frame
                BaltradFrameHandler bfHandler = new BaltradFrameHandler(
                        getChangedSubscriptions().get( i ).getNodeAddress() );
                String hdrStr = bfHandler.createObjectHdr( BaltradFrameHandler.BF_MIME_MULTIPART,
                    InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                    getChangedSubscriptions().get( i ).getUserName(),
                    BaltradFrameHandler.BF_MSG_SUBSCRIPTION_CHANGE_REQUEST,
                    tempFile.getAbsolutePath() );
                BaltradFrame baltradFrame = new BaltradFrame( hdrStr, tempFile.getAbsolutePath() );
                // handle the frame
                frameDispatcherController.setBfHandler( bfHandler );
                frameDispatcherController.doPost( request, response, baltradFrame );

                // update local subscription status

                subscriptionManager.updateSubscription(
                        getChangedSubscriptions().get( i ).getChannelName(),
                        Subscription.LOCAL_SUBSCRIPTION,
                        getChangedSubscriptions().get( i ).getSelected() );
            } catch( IOException e ) {
                logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while processing " +
                    "subscription change request for " +
                    getChangedSubscriptions().get( i ).getOperatorName() + ", channel " +
                    getChangedSubscriptions().get( i ).getChannelName() + ": " + e.getMessage() );
            } catch( Exception e ) {
                logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while processing " +
                    "subscription change request for " +
                    getChangedSubscriptions().get( i ).getOperatorName() + ", channel " +
                    getChangedSubscriptions().get( i ).getChannelName() + ": " + e.getMessage() );
            }
        }
        return new ModelAndView( SUBSCRIPTION_STATUS_VIEW );
    }

    /**
     * Shows list of all subscribed channels with check-boxes allowing user to select channels
     * for removal.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of all available channels
     */
    public ModelAndView selectRemoveSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        List subscriptions = subscriptionManager.getSubscriptionsByType(
                Subscription.LOCAL_SUBSCRIPTION );
        return new ModelAndView( REMOVE_SUBSCRIPTIONS_VIEW, SHOW_SUBSCRIPTIONS_KEY, subscriptions );
    }
    /**
     * Shows subscribed channels selected for removal.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of channels selected for removal
     */
    public ModelAndView showRemovedSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        // get the list of channels selected for subscription by the user
        String[] selChannels = request.getParameterValues( SELECTED_CHANNELS_KEY );
        List< Subscription > currentSubs = new ArrayList< Subscription >();
        // make sure the list of selected channels is not null
        if( selChannels != null ) {
            request.setAttribute( REQUEST_STATUS_KEY, 1 );
            // create subscription list based on chosen channels
            for( int i = 0; i < selChannels.length; i++ ) {
                Subscription subs = subscriptionManager.getSubscription( selChannels[ i ],
                        Subscription.LOCAL_SUBSCRIPTION );
                if( subs != null ) {
                    currentSubs.add( subs );
                }
            }
        } else {
            request.setAttribute( REQUEST_STATUS_KEY, 0 );
        }
        // write the list to class variable
        setRemovedSubscriptions( currentSubs );
        return new ModelAndView( SELECT_REMOVE_SUBSCRIPTION_VIEW, SELECTED_SUBSCRIPTIONS_KEY,
                currentSubs );
    }
    /**
     * Removes selected subscriptions.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of removed subscriptions
     */
    public ModelAndView showSubscriptionRemovalStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        List< String > errorMsgs = new ArrayList<String>();
        for( int i = 0; i < getRemovedSubscriptions().size(); i++ ) {
            try {
                subscriptionManager.removeSubscription(
                    getRemovedSubscriptions().get( i ).getChannelName(),
                    Subscription.LOCAL_SUBSCRIPTION );
            } catch( HibernateException e ) {
                errorMsgs.add( "Data access exception while removing subscription " +
                     "(Channel name: " + getRemovedSubscriptions().get( i ).getChannelName() + ")" );
            }
        }
        return new ModelAndView( SUBSCRIPTION_REMOVAL_STATUS_VIEW, HIBERNATE_ERRORS_KEY,
                errorMsgs );
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
     * Gets reference to FrameDispatcherController object.
     *
     * @return Reference to FrameDispatcherController object
     */
    public FrameDispatcherController getFrameDispatcherController() {
        return frameDispatcherController;
    }
    /**
     * Sets reference to FrameDispatcherController object.
     *
     * @param frameDispatcherController Reference to FrameDispatcherController object
     */
    public void setFrameDispatcherController(
            FrameDispatcherController frameDispatcherController ) {
        this.frameDispatcherController = frameDispatcherController;
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
    /**
     * Gets a list of changed subscriptions.
     *
     * @return List of changed subscriptions
     */
    public List< Subscription > getChangedSubscriptions() { return changedSubscriptions; }
    /**
     * Sets a list of changed subscriptions.
     *
     * @param changedSubscriptions List of changed subscriptions
     */
    public void setChangedSubscriptions( List< Subscription > changedSubscriptions ) {
        this.changedSubscriptions = changedSubscriptions;
    }
    /**
     * Gets a list of removed subscriptions.
     *
     * @return List of removed subscriptions
     */
    public List< Subscription > getRemovedSubscriptions() { return removedSubscriptions; }
    /**
     * Sets a list of removed subscriptions.
     *
     * @param removedSubscriptions List of removed subscriptions
     */
    public void setRemovedSubscriptions( List< Subscription > removedSubscriptions ) {
        this.removedSubscriptions = removedSubscriptions;
    }
}
//--------------------------------------------------------------------------------------------------
