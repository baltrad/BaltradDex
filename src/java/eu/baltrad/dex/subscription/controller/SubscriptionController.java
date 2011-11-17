/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.core.controller.FrameDispatcherController;
import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Multi-action controller for data channel subscription functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class SubscriptionController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_SUBSCRIPTIONS_KEY = "subscriptions";
    private static final String SELECTED_DATA_SOURCES_KEY = "selectedDataSources";

    private static final String SELECTED_SUBSCRIPTIONS_KEY = "selectedSubscriptions";
    private static final String REQUEST_STATUS_KEY = "request_status";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    // view names
    private static final String SHOW_SUBSCRIPTIONS_VIEW = "showSubscriptions";
    private static final String SELECTED_SUBSCRIPTIONS_VIEW = "showSelectedSubscriptions";
    private static final String SUBSCRIPTION_STATUS_VIEW = "showSubscriptionStatus";
    private static final String REMOVE_SUBSCRIPTIONS_VIEW = "removeDownloadSubscriptions";
    private static final String SELECT_REMOVE_SUBSCRIPTION_VIEW = "downloadSubscriptionsToRemove";
    private static final String SUBSCRIPTION_REMOVAL_STATUS_VIEW = "downloadSubscriptionsRemovalStatus";
    private static final String REDIRECT_VIEW = "removeDownloadSubscriptions.htm";
    private static final String SHOW_PEERS_SUBSCRIPTIONS_VIEW = "removeUploadSubscriptions";
    private static final String SHOW_SELECTED_PEERS_SUBSCRIPTIONS_VIEW =
                                                                "uploadSubscriptionsToRemove";
    private static final String SHOW_REMOVED_PEERS_SUBSCRIPTIONS_VIEW =
                                                                "uploadSubscriptionsRemovalStatus";
//---------------------------------------------------------------------------------------- Variables
    //private ChannelManager channelManager;
    private DataSourceManager dataSourceManager;
    private SubscriptionManager subscriptionManager;
    private FrameDispatcherController frameDispatcherController;
    private Logger log;
    // subscription change request
    private List< Subscription > changedSubscriptions;
    // removed subscriptions
    private List< Subscription > removedSubscriptions;
    // removed peers subscriptions
    private List< Subscription > removedPeersSubscriptions;
    /** Reference to InitAppUtil */
    private InitAppUtil init;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SubscriptionController() {
        log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        init = new InitAppUtil();
    }
    /**
     * Shows list of all subscriptions.
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view containing list of all subscriptions
     */
    public ModelAndView showSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        List< Subscription > subscriptions = subscriptionManager.getSubscriptions(
                Subscription.LOCAL_SUBSCRIPTION );

        // synchronize local and remote subscriptions
        for( int i = 0; i < subscriptions.size(); i++ ) {
            File tempFile = InitAppUtil.createTempFile( new File( init.getWorkDirPath() ) );
            InitAppUtil.writeObjectToStream( subscriptions.get( i ), tempFile );

            BaltradFrameHandler bfHandler = new BaltradFrameHandler(
                init.getConfiguration().getSoTimeout(),
                init.getConfiguration().getConnTimeout()
            );

            String hdrStr = BaltradFrameHandler.createObjectHdr(
                BaltradFrameHandler.MIME_MULTIPART,
                init.getConfiguration().getNodeAddress(),
                init.getConfiguration().getNodeName(),
                BaltradFrameHandler.CHNL_SYNC_RQST,
                tempFile.getAbsolutePath()
            );

            try {
                BaltradFrame baltradFrame = new BaltradFrame(hdrStr, tempFile);
                // handle the frame
                frameDispatcherController.setBfHandler( bfHandler );
                String remoteNodeAddress = subscriptions.get(i).getNodeAddress();
                frameDispatcherController.doPost( remoteNodeAddress, baltradFrame );
            } catch( Exception e ) {
                log.error( "Failed to collect subscription information", e );
            }
            if( frameDispatcherController.getSynkronizedSubscription() != null ) {
                // check if current subscription matches sunchronized subscription
                if( subscriptionManager.compareSubscriptions( subscriptions.get( i ),
                        frameDispatcherController.getSynkronizedSubscription() ) ) {
                    subscriptions.get( i ).setSynkronized(
                        frameDispatcherController.getSynkronizedSubscription().getSynkronized() );
                    frameDispatcherController.resetSynkronizedSubscription();
                }

            }
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );
        }
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
        // get the list of data sources selected for subscription by the user
        String[] selDataSources = request.getParameterValues( SELECTED_DATA_SOURCES_KEY );
        // get the list of all available channels
        List< Subscription > currentSubs = subscriptionManager.getSubscriptions(
                Subscription.LOCAL_SUBSCRIPTION );
        List< Subscription > selectedSubs = subscriptionManager.getSubscriptions(
                Subscription.LOCAL_SUBSCRIPTION );
        // make sure the list of selected data sources is not null
        if( selDataSources != null ) {
            // create subscription list based on chosen data sources
            for( int i = 0; i < selectedSubs.size(); i++ ) {
                selectedSubs.get( i ).setActive( false );
                String dataSourceName = selectedSubs.get( i ).getDataSourceName();
                int j = 0;
                while( j < selDataSources.length ) {
                    if( dataSourceName.equals( selDataSources[ j ] ) ) {
                        selectedSubs.get( i ).setActive( true );
                        break;
                    }
                    j++;
                }
            }
        } else {
            // user submits empty list - cancells all subscriptions
            for( int i = 0; i < selectedSubs.size(); i++ ) {
                selectedSubs.get( i ).setActive( false );
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
        ModelAndView modelAndView = new ModelAndView();
        for( int i = 0; i < getChangedSubscriptions().size(); i++ ) {
            try {
                File tempFile = InitAppUtil.createTempFile( new File( init.getWorkDirPath() ) );
                InitAppUtil.writeObjectToStream( getChangedSubscriptions().get( i ), tempFile );
                // prepare the frame
                BaltradFrameHandler bfHandler = new BaltradFrameHandler(
                        init.getConfiguration().getSoTimeout(),
                        init.getConfiguration().getConnTimeout() );
                //
                String hdrStr = BaltradFrameHandler.createObjectHdr(
                        BaltradFrameHandler.MIME_MULTIPART,
                        init.getConfiguration().getNodeAddress(),
                        init.getConfiguration().getNodeName(),
                        getChangedSubscriptions().get( i ).getUserName(),
                        BaltradFrameHandler.SBN_CHNG_RQST,
                        tempFile.getAbsolutePath() );
                //
                BaltradFrame baltradFrame = new BaltradFrame( hdrStr, tempFile );
                // handle the frame
                frameDispatcherController.setBfHandler( bfHandler );
                String remoteNodeAddress = getChangedSubscriptions().get( i ).getNodeAddress();
                frameDispatcherController.doPost( remoteNodeAddress, baltradFrame );

                // update local subscription status

                subscriptionManager.updateSubscription(
                        getChangedSubscriptions().get( i ).getDataSourceName(),
                        Subscription.LOCAL_SUBSCRIPTION,
                        getChangedSubscriptions().get( i ).getActive() );
                String msg = "Subscription request successfully completed";
                modelAndView.addObject( OK_MSG_KEY, msg );
            } catch( Exception e ) {
                log.error( "Error while processing subscription change request for " +
                    getChangedSubscriptions().get( i ).getOperatorName() + ", channel " +
                    getChangedSubscriptions().get( i ).getDataSourceName(), e );
                String msg = "Failed to complete subscription request";
                modelAndView.addObject( ERROR_MSG_KEY, msg );
            }
        }
        modelAndView.setViewName( SUBSCRIPTION_STATUS_VIEW );
        return modelAndView;
    }
    /**
     * Shows list of all subscribed channels with check-boxes allowing user to select channels
     * for removal.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of all available channels
     */
    public ModelAndView removeDownloadSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        List subscriptions = subscriptionManager.getSubscriptions(
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
    public ModelAndView downloadSubscriptionsToRemove( HttpServletRequest request,
            HttpServletResponse response ) {
        // get the list of channels selected for subscription by the user
        String[] selDataSources = request.getParameterValues( SELECTED_DATA_SOURCES_KEY );
        ModelAndView modelAndView = null;
        if( selDataSources == null ) {
            try {
                response.sendRedirect( REDIRECT_VIEW );
            } catch( IOException e ) {
                log.error( "Error while redirecting to " + REDIRECT_VIEW, e );
            }
        } else {
            // determines whether user has selected an active subscription
            boolean isActive = false;
            for( int i = 0; i < selDataSources.length; i++ ) {
                Subscription subs = subscriptionManager.getSubscription( selDataSources[ i ],
                        Subscription.LOCAL_SUBSCRIPTION );
                if( subs.getActive() ) {
                    isActive = true;
                }
            }
            // user is not allowed to remove active subscription
            if( isActive ) {
                try {
                    request.getSession().setAttribute( ERROR_MSG_KEY,
                        getMessageSourceAccessor().getMessage(
                        "error.removesubscription.active" ) );
                    response.sendRedirect( REDIRECT_VIEW );
                } catch( IOException e ) {
                    log.error( "Error while redirecting to " + REDIRECT_VIEW, e );
                }
            } else {
                List< Subscription > currentSubs = new ArrayList< Subscription >();
                // create subscription list based on chosen channels
                for( int i = 0; i < selDataSources.length; i++ ) {
                    Subscription subs = subscriptionManager.getSubscription( selDataSources[ i ],
                            Subscription.LOCAL_SUBSCRIPTION );
                    if( subs != null ) {
                        currentSubs.add( subs );
                    }
                }
                // write the list to class variable
                setRemovedSubscriptions( currentSubs );
                modelAndView =  new ModelAndView( SELECT_REMOVE_SUBSCRIPTION_VIEW,
                        SELECTED_SUBSCRIPTIONS_KEY, currentSubs );
            }
        }
        return modelAndView;
    }
    /**
     * Removes selected subscriptions.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of removed subscriptions
     */
    public ModelAndView downloadSubscriptionsRemovalStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            for( int i = 0; i < getRemovedSubscriptions().size(); i++ ) {
                subscriptionManager.deleteSubscription(
                    getRemovedSubscriptions().get( i ).getDataSourceName(),
                    Subscription.LOCAL_SUBSCRIPTION );
            }
            String msg = "Selected subscriptions successfully removed";
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to remove selected subscriptions";
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( msg, e );
        }
        modelAndView.setViewName( SUBSCRIPTION_REMOVAL_STATUS_VIEW );
        return modelAndView;
    }
    /**
     * Creates list of subscriptions made by peers.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list peers subscriptions
     */
    public ModelAndView removeUploadSubscriptions( HttpServletRequest request,
            HttpServletResponse response ) {
        List subscriptions = subscriptionManager.getSubscriptions(
                Subscription.REMOTE_SUBSCRIPTION );
        return new ModelAndView( SHOW_PEERS_SUBSCRIPTIONS_VIEW, SHOW_SUBSCRIPTIONS_KEY,
                subscriptions );
    }
    /**
     * Creates list of peers subscriptions selected for removal.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of peers subscriptions selected for removal
     */
    public ModelAndView uploadSubscriptionsToRemove( HttpServletRequest request,
            HttpServletResponse response ) {
        // get the list of channels selected for subscription by the user
        //String[] selChannels = request.getParameterValues( SELECTED_CHANNELS_KEY );
        String[] selDataSources = request.getParameterValues( SELECTED_DATA_SOURCES_KEY );
        ModelAndView modelAndView = null;
        if( selDataSources == null ) {
            try {
                response.sendRedirect( SHOW_PEERS_SUBSCRIPTIONS_VIEW + ".htm" );
            } catch( IOException e ) {
                log.error( "Error while redirecting to " + SHOW_PEERS_SUBSCRIPTIONS_VIEW, e );
            }
        } else {
            List< Subscription > currentSubs = new ArrayList< Subscription >();
            // create subscription list based on chosen channels
            for( int i = 0; i < selDataSources.length; i++ ) {
                Subscription subs = subscriptionManager.getSubscription( selDataSources[ i ],
                        Subscription.REMOTE_SUBSCRIPTION );
                if( subs != null ) {
                    currentSubs.add( subs );
                }
            }
            // write the list to class variable
            setRemovedPeersSubscriptions( currentSubs );
            modelAndView =  new ModelAndView( SHOW_SELECTED_PEERS_SUBSCRIPTIONS_VIEW,
                    SELECTED_SUBSCRIPTIONS_KEY, currentSubs );
        }
        return modelAndView;
    }
    /**
     * Removes selected subscriptions made by peers.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView
     */
    public ModelAndView uploadSubscriptionsRemovalStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            for( int i = 0; i < getRemovedPeersSubscriptions().size(); i++ ) {
                subscriptionManager.deleteSubscription(
                    getRemovedPeersSubscriptions().get( i ).getDataSourceName(),
                    Subscription.REMOTE_SUBSCRIPTION );
            }
            String msg = "Selected peer subscriptions successfully removed";
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to remove selected peer subscriptions";
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( msg, e );
        }
        modelAndView.setViewName( SHOW_REMOVED_PEERS_SUBSCRIPTIONS_VIEW );
        return modelAndView;
    }
    /**
     * Gets reference to data source manager object.
     *
     * @return Reference to data source manager object
     */
    public DataSourceManager getDataSourceManager() { return dataSourceManager; }
    /**
     * Sets reference to data source manager object.
     *
     * @param dataSourceManager Reference to data source manager object
     */
    public void setDataSourceManager( DataSourceManager dataSourceManager ) {
        this.dataSourceManager = dataSourceManager;
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
    /**
     * Gets a list of removed peers' subscriptions.
     *
     * @return List of removed peers' subscriptions
     */
    public List< Subscription > getRemovedPeersSubscriptions() { return removedPeersSubscriptions; }
    /**
     * Sets a list of removed peers' subscriptions.
     *
     * @param removedPeersSubscriptions List of removed peers' subscriptions
     */
    public void setRemovedPeersSubscriptions( List< Subscription > removedPeersSubscriptions ) {
        this.removedPeersSubscriptions = removedPeersSubscriptions;
    }
}
//--------------------------------------------------------------------------------------------------
