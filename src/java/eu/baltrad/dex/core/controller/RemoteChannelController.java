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

package eu.baltrad.dex.core.controller;

import eu.baltrad.dex.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.frame.model.BaltradFrame;
import eu.baltrad.dex.channel.model.Channel;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.util.InitAppUtil;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class implements remote channel list controller. 
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class RemoteChannelController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String REMOTE_CHANNELS_KEY = "channels";
    private static final String SELECTED_CHANNELS_KEY = "selected_channels";
    private static final String SUBSCRIBED_CHANNELS_KEY = "subscribed_channels";
    private static final String SENDER_NODE_NAME_KEY = "sender_node_name";
    // view names
    private static final String SHOW_CHANNELS_VIEW = "showRemoteChannels";
    private static final String SHOW_SELECTED_CHANNELS_VIEW = "showSelectedRemoteChannels";
    private static final String SHOW_CHANNEL_SELECTION_STATUS_VIEW =
            "showRemoteChannelSelectionStatus";
//---------------------------------------------------------------------------------------- Variables
    private FrameDispatcherController frameDispatcherController;
    private SubscriptionManager subscriptionManager;
    private LogManager logManager;
    // remote channels object list
    private List remoteChannels;
    // selected channel list
    private List selectedChannels;
    // list of subscribed channels
    private List subscribedChannels;
    // remote node name
    private String senderNodeName;
    // sender node address
    private String senderNodeAddress;
    // user name
    private String userName;
    // user's password
    private String password;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Creates list of remote channels available for a given node.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView object holding list of data channels available at a given node
     */
    public ModelAndView showRemoteChannels( HttpServletRequest request,
            HttpServletResponse response ) {
        // set remote channel list
        setRemoteChannels( frameDispatcherController.getChannelListing() );
        // set sender node name
        setSenderNodeName( frameDispatcherController.getRemoteNodeName() );
        // set sender node address
        setSenderNodeAddress( frameDispatcherController.getRemoteNodeAddress() );
        // reset remote channel list, node name and address stored in FrameDispatcherController
        frameDispatcherController.resetChannelListing();
        frameDispatcherController.resetRemoteNodeName();
        frameDispatcherController.resetRemoteNodeAddress();
        // set sender node name
        request.setAttribute( SENDER_NODE_NAME_KEY, getSenderNodeName() );
        return new ModelAndView( SHOW_CHANNELS_VIEW, REMOTE_CHANNELS_KEY, getRemoteChannels() );
    }
    /**
     * Creates list of data channels selected by the user
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView object holding list of data channels selected by the user
     */
    public ModelAndView showSelectedRemoteChannels( HttpServletRequest request,
            HttpServletResponse response ) {
        // get selected channels based on the checkbox values
        String[] selChannelNames = request.getParameterValues( SELECTED_CHANNELS_KEY );
        // check if selected channel list is not empty
        if( selChannelNames != null ) {
            selectedChannels = new ArrayList();
            for( int i = 0; i < selChannelNames.length; i++ ) {
                for( int j = 0; j < getRemoteChannels().size(); j++ ) {
                    Channel ch = ( Channel )getRemoteChannels().get( j );
                    if( ch.getChannelName().equals( selChannelNames[ i ] ) ) {
                        selectedChannels.add( ch );
                    }
                }
            }
        }
        // set sender node name
        request.setAttribute( SENDER_NODE_NAME_KEY, getSenderNodeName() );
        return new ModelAndView( SHOW_SELECTED_CHANNELS_VIEW, SELECTED_CHANNELS_KEY,
                selectedChannels );
    }
    /**
     * Submits channel subscription request by posting subscription list on remote node.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return
     */
    public ModelAndView showRemoteChannelSelectionStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        // prepare subscription request
        try {
            File tempFile = InitAppUtil.createTempFile(
                    new File( InitAppUtil.getLocalTempDir() ) );
            InitAppUtil.writeObjectToStream( getSelectedChannels(), tempFile );
            // prepare frame
            BaltradFrameHandler bfHandler = new BaltradFrameHandler( getSenderNodeAddress() );
            String hdrStr = bfHandler.createObjectHdr( BaltradFrameHandler.BF_MIME_MULTIPART,
                InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                frameDispatcherController.getLocalUserName(),
                BaltradFrameHandler.BF_MSG_CHANNEL_SUBSCRIPTION_REQUEST,
                tempFile.getAbsolutePath() );
            BaltradFrame baltradFrame = new BaltradFrame( hdrStr, tempFile.getAbsolutePath() );
            // handle the frame
            frameDispatcherController.setBfHandler( bfHandler );
            frameDispatcherController.doPost( request, response, baltradFrame );
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );

            // check subscription operation status
            setSubscribedChannels( frameDispatcherController.getConfirmedSubscriptions() );
            // reset confirmed subscriptions list stored in FrameDispatcherController
            frameDispatcherController.resetConfirmedSubscriptions();
            // once subscription is confirmed, add requested channels to local subscription list
            for( int i = 0; i < getSubscribedChannels().size(); i++ ) {
                Channel channel = (  Channel )getSubscribedChannels().get( i );
                if( subscriptionManager.getSubscription( channel.getChannelName(),
                        Subscription.LOCAL_SUBSCRIPTION ) != null ) {
                    logManager.addEntry( new Date(), LogManager.MSG_WRN,
                        "You have already subscribed to " + channel.getChannelName() );
                } else {
                    // add local subscription
                    Subscription subs = new Subscription(
                        frameDispatcherController.getLocalUserName(),
                        channel.getChannelName(), getSenderNodeAddress(), getSenderNodeName(),
                        Subscription.LOCAL_SUBSCRIPTION, true, true );
                    subscriptionManager.addSubscription( subs );
                }
            }
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while adding remote " +
                    "channels to subscription list: " + e.getMessage() );
        }
        return new ModelAndView( SHOW_CHANNEL_SELECTION_STATUS_VIEW, SUBSCRIBED_CHANNELS_KEY,
                getSubscribedChannels() );
    }
    /**
     * Gets the list of channels available for a given remote node.
     *
     * @return List of channels available for a given remote node
     */
    public List getRemoteChannels() { return remoteChannels; }
    /**
     * Sets the list of channels available for a given remote node.
     *
     * @param remoteChannels List of channels available for a given remote node
     */
    public void setRemoteChannels( List remoteChannels ) {
        this.remoteChannels = remoteChannels;
    }
    /**
     * Gets the list of channels selected by the user.
     *
     * @return List of channels selected by the user
     */
    public List getSelectedChannels() { return selectedChannels; }
    /**
     * Sets the list of channels selected by the user..
     *
     * @param selectedChannels List of channels selected by the user
     */
    public void setSelectedChannels( List selectedChannels ) {
        this.selectedChannels = selectedChannels;
    }
    /**
     * Resets the list of selected channels
     */
    public void resetSelectedChannels() { this.selectedChannels = null; }
    /**
     * Gets the list of subscribed channels.
     *
     * @return List of subscribed channels
     */
    public List getSubscribedChannels() { return subscribedChannels; }
    /**
     * Sets the list of subscribed channels.
     *
     * @param subscribedChannels List of subscribed channels
     */
    public void setSubscribedChannels( List subscribedChannels ) {
        this.subscribedChannels = subscribedChannels;
    }
    /**
     * Gets sender node name.
     *
     * @return Sender node name
     */
    public String getSenderNodeName() { return senderNodeName; }
    /**
     * Set sender node name.
     *
     * @param senderNodeName Sender node name
     */
    public void setSenderNodeName( String senderNodeName ) { this.senderNodeName = senderNodeName; }
    /**
     * Gets sender node address.
     *
     * @return Sender node address
     */
    public String getSenderNodeAddress() { return senderNodeAddress; }
    /**
     * Sets sender node address.
     *
     * @param senderNodeAddress Sender node address
     */
    public void setSenderNodeAddress( String senderNodeAddress ) {
        this.senderNodeAddress = senderNodeAddress;
    }
    /**
     * Gets user name.
     *
     * @return User name
     */
    public String getUserName() { return userName; }
    /**
     * Sets user name.
     *
     * @param userName User name
     */
    public void setUserName( String userName ) { this.userName = userName; }
    /**
     * Gets user's password.
     *
     * @return User's password
     */
    public String getPassword() { return password; }
    /**
     * Sets user's password.
     *
     * @param password User's password
     */
    public void setPassword( String password ) { this.password = password; }
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