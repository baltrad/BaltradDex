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

import eu.baltrad.dex.net.model.Subscription;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.net.model.SubscriptionManager;
import eu.baltrad.dex.log.model.MessageLogger;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
    
    private static final String UPLOAD_IDS = "uploadIds";
    private static final String DOWNLOAD_IDS = "downloadIds";
    
    private static final String SELECTED_SUBSCRIPTIONS_KEY = "selectedSubscriptions";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    // view names
    private static final String REMOVE_SUBSCRIPTIONS_VIEW = "remove_download";
    private static final String SELECT_REMOVE_SUBSCRIPTION_VIEW = "remove_selected_download";
    private static final String SUBSCRIPTION_REMOVAL_STATUS_VIEW = "remove_download_status";
    private static final String REDIRECT_VIEW = "remove_download.htm";
    private static final String SHOW_PEERS_SUBSCRIPTIONS_VIEW = "remove_upload";
    private static final String SHOW_SELECTED_PEERS_SUBSCRIPTIONS_VIEW = "remove_selected_upload";
    private static final String SHOW_REMOVED_PEERS_SUBSCRIPTIONS_VIEW = "remove_upload_status";
//---------------------------------------------------------------------------------------- Variables
    private SubscriptionManager subscriptionManager;
    private Logger log;
    // subscription change request
    private List<Subscription> changedSubscriptions;
    // removed subscriptions
    private List<Subscription> removedSubscriptions;
    // removed peers subscriptions
    private List<Subscription> removedPeersSubscriptions;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SubscriptionController() {
        log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    
    /**
     * Shows list of all subscribed channels with check-boxes allowing user to select channels
     * for removal.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of all available channels
     */
    public ModelAndView remove_download( HttpServletRequest request,
            HttpServletResponse response ) {
        List subscriptions = subscriptionManager.load(
                Subscription.SUBSCRIPTION_DOWNLOAD);
        return new ModelAndView( REMOVE_SUBSCRIPTIONS_VIEW, SHOW_SUBSCRIPTIONS_KEY, subscriptions );
    }
    /**
     * Shows subscribed channels selected for removal.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView holding list of channels selected for removal
     */
    public ModelAndView remove_selected_download( HttpServletRequest request,
            HttpServletResponse response ) {
        // get the list of channels selected for subscription by the user
        String[] downloadIds = request.getParameterValues(DOWNLOAD_IDS);
        ModelAndView modelAndView = null;
        if( downloadIds == null ) {
            try {
                response.sendRedirect( REDIRECT_VIEW );
            } catch( IOException e ) {
                log.error( "Error while redirecting to " + REDIRECT_VIEW, e );
            }
        } else {
            // determines whether user has selected an active subscription
            boolean isActive = false;
            for( int i = 0; i < downloadIds.length; i++ ) {
                Subscription subs = subscriptionManager.load(
                        Integer.parseInt(downloadIds[i]));
                
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
                for( int i = 0; i < downloadIds.length; i++ ) {
                    Subscription subs = subscriptionManager.load(
                            Integer.parseInt(downloadIds[i]));
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
    public ModelAndView remove_download_status( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            for( int i = 0; i < getRemovedSubscriptions().size(); i++ ) {
                subscriptionManager.delete(getRemovedSubscriptions().get(i));
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
    public ModelAndView remove_upload( HttpServletRequest request,
            HttpServletResponse response ) {
        List subscriptions = subscriptionManager.load(
                Subscription.SUBSCRIPTION_UPLOAD);
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
    public ModelAndView remove_selected_upload( HttpServletRequest request,
            HttpServletResponse response ) {
        String[] uploadIds = request.getParameterValues(UPLOAD_IDS);
        ModelAndView modelAndView = null;
        if( uploadIds == null ) {
            try {
                response.sendRedirect( SHOW_PEERS_SUBSCRIPTIONS_VIEW + ".htm" );
            } catch( IOException e ) {
                log.error( "Error while redirecting to " + SHOW_PEERS_SUBSCRIPTIONS_VIEW, e );
            }
        } else {
            List< Subscription > currentSubs = new ArrayList< Subscription >();
            // create subscription list based on chosen channels
            for( int i = 0; i < uploadIds.length; i++ ) {
                Subscription subs = subscriptionManager.load(
                        Integer.parseInt(uploadIds[i]));
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
    public ModelAndView remove_upload_status( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            for( int i = 0; i < getRemovedPeersSubscriptions().size(); i++ ) {
                subscriptionManager
                        .delete(getRemovedPeersSubscriptions().get(i));
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
     * Gets a list of changed subscriptions.
     *
     * @return List of changed subscriptions
     */
    public List<Subscription> getChangedSubscriptions() { return changedSubscriptions; }
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
    public List<Subscription> getRemovedSubscriptions() { return removedSubscriptions; }
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
