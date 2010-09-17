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

import eu.baltrad.dex.frame.model.BaltradFrame;
import eu.baltrad.dex.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.channel.model.ChannelManager;
import eu.baltrad.dex.channel.model.Channel;
import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.register.model.DeliveryRegisterEntry;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;
import eu.baltrad.dex.util.FileCatalogConnector;

import eu.baltrad.fc.FileCatalog;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

import java.io.InputStream;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class implements frame dispatching controller. This controller handles all incoming and outgoing
 * BaltradFrames according to the single point of entry rule.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class FrameDispatcherController extends HttpServlet implements Controller {
//---------------------------------------------------------------------------------------- Variables
    private BaltradFrameHandler bfHandler;
    private UserManager userManager;
    private ChannelManager channelManager;
    private SubscriptionManager subscriptionManager;
    private LogManager logManager;
    private DeliveryRegisterManager deliveryRegisterManager;
    // Reference to file catalog object
    private FileCatalog fileCatalog;
    // remote channel listing
    private List channelListing;
    // confirmed subscription list
    private List confirmedSubscriptions;
    // synchronized subscription
    private Subscription synkronizedSubscription;
    // remote node name
    private String remoteNodeName;
    // remote node address
    private String remoteNodeAddress;
    // remote user name
    private String localUserName;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Required by the Spring's Controller interface, wraps actual doGet method used to
     * handle incoming and outgoing frames.
     * 
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView object
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) {
        doGet( request, response );
        return new ModelAndView();
    }
    /**
     * Handles all frame traffic, implements single point of entry concept. Handles incoming
     * and outgoing frames and distinguishes between particular frame types.
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response ) {
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator( request );
            FileItemStream hdrItem = iterator.next();
             if( hdrItem.getFieldName().equals( BaltradFrame.XML_PART ) ) {
                InputStream hdrStream = hdrItem.openStream();
                bfHandler = new BaltradFrameHandler();
                // Handle form field / message XML header
                if( hdrItem.isFormField() ) {
                    // Get header string
                    String header = Streams.asString( hdrStream );

                    // process message frame

                    if( bfHandler.getContentType( header ).equals(
                            BaltradFrameHandler.MSG) ) {

                        // channel listing request - has to be authenticated

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_LIST_RQST ) ) {
                            if( authenticateFrame( header ) ) {
                                logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                    "Channel listing request received from user " +
                                     getUserName( bfHandler.getUserName( header ) ) + " ("
                                     + bfHandler.getSenderNodeName( header ) + ")" );
                                
                                // process channel listing request

                                // create a list of channels that user is allowed to subscribe

                                ///!!! to be implemented


                                List channels = channelManager.getChannels();





                                // write list to temporary file
                                File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                                // write object to stream
                                InitAppUtil.writeObjectToStream( channels, tempFile );
                                // set the return address
                                bfHandler.setUrl( bfHandler.getSenderNodeAddress( header ) );
                                // prepare the return message header
                                String retHeader = bfHandler.createObjectHdr(
                                        BaltradFrameHandler.MIME_MULTIPART,
                                        InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                                        BaltradFrameHandler.CHNL_LIST,
                                        tempFile.getAbsolutePath() );
                                BaltradFrame baltradFrame =
                                        new BaltradFrame( retHeader, tempFile.getAbsolutePath() );
                                // process the frame
                                bfHandler.handleBF( baltradFrame );
                                // delete temporary file
                                InitAppUtil.deleteFile( tempFile );
                            } else {
                                logManager.addEntry( new Date(), LogManager.MSG_WRN,
                                    "Incoming frame could not be authenticated" );
                            }
                        } else {
                            // regular message frame - display message
                            logManager.addEntry( new Date(), bfHandler.getMessageClass( header ),
                                bfHandler.getMessageText( header ) );
                        }
                    }

                    // process object frame

                    if( bfHandler.getContentType( header ).equals(
                            BaltradFrameHandler.OBJECT ) ) {

                        // incoming channel listing

                        if( bfHandler.getMessageText( header ).equals(
                            BaltradFrameHandler.CHNL_LIST ) ) {
                            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                "Channel listing received from " + bfHandler.getSenderNodeName(
                                header ) );

                            // process incoming channel listing

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write list to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve channel list and save it to class field
                            List remoteChannels = ( List )InitAppUtil.readObjectFromStream( tempFile );
                            // set channel listing
                            setChannelListing( remoteChannels );
                            // set remote node name
                            setRemoteNodeName( bfHandler.getSenderNodeName( header ) );
                            // set remote node address
                            setRemoteNodeAddress( bfHandler.getSenderNodeAddress( header ) );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }

                        // incoming channel subscription request

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_SBN_RQST ) ) {
                            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                "Channel subscription request received from " +
                                bfHandler.getSenderNodeName( header ) );

                            // process incoming subscription request

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription request to temporary file
                            // write list to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription list
                            List subscribedChannels = ( List )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            // identify user by name
                            User user = userManager.getUserByName( bfHandler.getUserName( header ) );
                            // return channel list sent back to user to confirm
                            // subscription completion
                            List confirmedChannels = new ArrayList();
                            if( subscribedChannels != null ) {
                                for( int i = 0; i < subscribedChannels.size(); i++ ) {
                                    Channel requestedChannel =
                                            ( Channel )subscribedChannels.get( i );
                                    Channel localChannel = channelManager.getChannel( 
                                            requestedChannel.getChannelName() );
                                    Subscription subs = null;
                                    // make sure user hasn't already subscribed the channels
                                    if( subscriptionManager.getSubscription( user.getName(),
                                            localChannel.getChannelName(),
                                            Subscription.REMOTE_SUBSCRIPTION ) != null ) {
                                        logManager.addEntry( new Date(), LogManager.MSG_WRN,
                                            "User " + user.getName() + " has already subscribed to "
                                            + localChannel.getChannelName() );
                                    } else {
                                        // add subscription
                                        subs = new Subscription( user.getName(),
                                                localChannel.getChannelName(),
                                                Subscription.REMOTE_SUBSCRIPTION );
                                        subscriptionManager.addSubscription( subs );
                                        confirmedChannels.add( requestedChannel );
                                    }
                                }
                            }
                            // send subscription confirmation

                            // write list to temporary file
                            tempFile = InitAppUtil.createTempFile(
                                    new File( InitAppUtil.getLocalTempDir() ) );
                            // write object to the stream
                            InitAppUtil.writeObjectToStream( confirmedChannels, tempFile );
                            // set the return address
                            bfHandler.setUrl( bfHandler.getSenderNodeAddress( header ) );
                            // prepare the return message header
                            String retHeader = bfHandler.createObjectHdr(
                                    BaltradFrameHandler.MIME_MULTIPART,
                                    InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                                    BaltradFrameHandler.CHNL_SBN_CFN,
                                    tempFile.getAbsolutePath() );
                            BaltradFrame baltradFrame =
                                    new BaltradFrame( retHeader, tempFile.getAbsolutePath() );
                            // process the frame
                            bfHandler.handleBF( baltradFrame );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }

                        // incoming channel subscription confirmation

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_SBN_CFN ) ) {
                            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                "Channel subscription confirmation received from " +
                                bfHandler.getSenderNodeName( header ) );

                            // process incoming subscription confirmation

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write list of confirmed subscriptions to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve list of confirmed channels
                            List confirmedChannels = ( List )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            setConfirmedSubscriptions( confirmedChannels );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }

                        // incoming channel subscription change request

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.SBN_CHNG_RQST ) ) {
                            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                "Channel subscription change request received from " +
                                bfHandler.getSenderNodeName( header ) );

                            // process incoming subscription change request

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription change request to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            // create new subscription object
                            Subscription s = new Subscription( subs.getUserName(),
                                    subs.getChannelName(), Subscription.REMOTE_SUBSCRIPTION );
                            // subscription object serves as confirmation
                            Subscription confirmedSub = null;
                            if( subs.getSelected() ) {
                                // add remote subscription
                                subscriptionManager.addSubscription( s );
                                confirmedSub = s;
                                logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                    "User " + s.getUserName() + " subscribed to "
                                    + s.getChannelName() );
                            } else {
                                // remove remote subscription
                                subscriptionManager.removeSubscription( s.getUserName(),
                                        s.getChannelName(), s.getType() );
                                confirmedSub = s;
                                logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                    "User " + s.getUserName() + " cancelled subscription to "
                                    + s.getChannelName() );
                            }
                            
                            // send subscription change confirmation

                            // write subscription object to temporary file
                            tempFile = InitAppUtil.createTempFile(
                                    new File( InitAppUtil.getLocalTempDir() ) );
                            // set the return address
                            bfHandler.setUrl( bfHandler.getSenderNodeAddress( header ) );
                             
                            String retHeader = null;
                            if( confirmedSub != null ) {
                                retHeader = bfHandler.createObjectHdr(
                                    BaltradFrameHandler.MIME_MULTIPART,
                                    InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                                    BaltradFrameHandler.SBN_CHNG_OK,
                                    tempFile.getAbsolutePath() );
                                // write object to the stream
                                InitAppUtil.writeObjectToStream( confirmedSub, tempFile );
                            } else {
                                retHeader = bfHandler.createMsgHdr(
                                    BaltradFrameHandler.MIME_MULTIPART,
                                    InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                                    BaltradFrameHandler.SBN_CHNG_FAIL,
                                    tempFile.getAbsolutePath() );
                                // write object to the stream
                                InitAppUtil.writeObjectToStream( subs, tempFile );
                            }
                            BaltradFrame baltradFrame =
                                    new BaltradFrame( retHeader, tempFile.getAbsolutePath() );
                            // process the frame
                            bfHandler.handleBF( baltradFrame );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }

                        // incoming channel subscription change confirmation - success

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.SBN_CHNG_OK ) ) {
                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription change request to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            String selected = subs.getSelected() ? "Subscribed" : "Unsubscribed";
                            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                "Remote node " + bfHandler.getSenderNodeName( header )
                                + " changed your subscription status for " + subs.getChannelName()
                                + " to: " + selected );
                        }

                        // incoming channel subscription change confirmation - failure

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.SBN_CHNG_FAIL ) ) {
                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription change request to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            String selected = subs.getSelected() ? "Subscribed" : "Unsubscribed";
                            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                "Remote node " + bfHandler.getSenderNodeName( header )  +
                                " failed to change your subscription status for "
                                + subs.getChannelName() + " to: " + selected );
                        }

                        // channel synchronization request

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_SYNC_RQST ) ) {
                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription object to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object from stream
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            // check if the subscribed channel is available
                            try {
                                Channel channel = channelManager.getChannel( subs.getChannelName() );
                                if( channel == null ) {
                                    subs.setSynkronized( false );
                                }
                            } catch( NullPointerException e ) {
                                System.err.println( "Failed to synchronize subscribed channel: "
                                        + subs.getChannelName() );
                            }

                            // send subscription back to requesting node

                            // write list to temporary file
                            tempFile = InitAppUtil.createTempFile(
                                    new File( InitAppUtil.getLocalTempDir() ) );
                            // write object to the stream
                            InitAppUtil.writeObjectToStream( subs, tempFile );
                            // set the return address
                            bfHandler.setUrl( bfHandler.getSenderNodeAddress( header ) );
                            // prepare the return message header
                            String retHeader = bfHandler.createObjectHdr(
                                    BaltradFrameHandler.MIME_MULTIPART,
                                    InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                                    BaltradFrameHandler.CHNL_SYNC_RSPNS,
                                    tempFile.getAbsolutePath() );
                            BaltradFrame baltradFrame =
                                    new BaltradFrame( retHeader, tempFile.getAbsolutePath() );
                            // process the frame
                            bfHandler.handleBF( baltradFrame );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }
                        // channel synchronization response

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_SYNC_RSPNS ) ) {
                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription object to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getLocalTempDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object from stream
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            setSynkronizedSubscription( subs );
                        }
                    }

                    // incoming data frame

                    if( bfHandler.getContentType( header ).equals(
                            BaltradFrameHandler.FILE ) ) {
                        logManager.addEntry( new Date(), LogManager.MSG_INFO,
                            "New data frame received from " + bfHandler.getSenderNodeName( header )
                            + ": " + bfHandler.getFileName( header ) );
                        // write data to the temporary file
                        File tempFile = InitAppUtil.createTempFile( 
                                new File( InitAppUtil.getLocalTempDir() ) );
                        FileItemStream fileItem = iterator.next();
                        InputStream fileStream = fileItem.openStream();
                        InitAppUtil.saveFile( fileStream, tempFile );
                        // save data in the catalogue
                        if( fileCatalog == null ) {
                            fileCatalog = FileCatalogConnector.connect();
                        }
                        eu.baltrad.fc.oh5.File cFile = fileCatalog.catalog(
                                tempFile.getAbsolutePath() );
                        InitAppUtil.deleteFile( tempFile.getAbsolutePath() );

                        // send data to subscribers
                        
                        List< Subscription > remoteSubs = 
                                subscriptionManager.getSubscriptionsByType(
                                Subscription.REMOTE_SUBSCRIPTION );
                        if( remoteSubs != null ) {
                            // iterate through subscriptions
                            for( int i = 0; i < remoteSubs.size(); i++ ) {
                                // check if channel name of the incoming frame equals channel name
                                // in subscription object
                                if( remoteSubs.get( i ).getChannelName().equals( 
                                        bfHandler.getChannel( header ) ) ) {
                                    // get local user
                                    User user = userManager.getUserByName(
                                            remoteSubs.get( i ).getUserName() );
                                    // check data status in data delivery register
                                    DeliveryRegisterEntry dre = deliveryRegisterManager.getEntry(
                                            user.getId(), cFile.name() );
                                    if( dre == null ) {
                                        // create data frame
                                        bfHandler.setUrl( user.getNodeAddress() );
                                        String retHeader = bfHandler.createDataHdr(
                                            BaltradFrameHandler.MIME_MULTIPART,
                                            InitAppUtil.getNodeName(),
                                            remoteSubs.get( i ).getChannelName(), cFile.name() );
                                        BaltradFrame baltradFrame = new BaltradFrame( retHeader,
                                                cFile.path() );
                                        // process the frame
                                        bfHandler.handleBF( baltradFrame );
                                        // add entry to the data delivery register
                                        dre = new DeliveryRegisterEntry( user.getId(),
                                                cFile.name() );
                                        deliveryRegisterManager.addEntry( dre );
                                        logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                            "Sending data from " + 
                                            remoteSubs.get( i ).getChannelName() + " to user "
                                            + user.getName() + ": "
                                            + bfHandler.getFileName( retHeader) );
                                    }
                                }
                            }
                        }
                    }
                }
             }
        } catch( FileUploadException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Frame dispatcher error: " + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Frame dispatcher error: " + e.getMessage() );
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Frame dispatcher error: " + e.getMessage() );
        }
    }
    /**
     * Wraps BaltradFrameHandler's frame handling functionality.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @param baltradFrame Reference to BaltradFrame object
     */
    public void doPost( HttpServletRequest request, HttpServletResponse response, 
            BaltradFrame baltradFrame ) {
        bfHandler.handleBF( baltradFrame );
    }
    /**
     * Autheticates incoming frame by verifying user name and password encoded in frame header
     *
     * @param header Frame header
     * @return True once a frame is successfully authenticated, false otherwise
     */
    public boolean authenticateFrame( String header ) {
        String userNameHash = bfHandler.getUserName( header );
        String passwd = bfHandler.getPassword( header );
        User user = userManager.getUserByNameHash( userNameHash );
        if( user != null && user.getNameHash().equals( userNameHash )
                && user.getPassword().equals( passwd ) ) return true;
        else
            return false;
    }
    /**
     * Gets user name matching a given user name hash
     * 
     * @param userNameHash User name hash
     * @return User name
     */
    public String getUserName( String userNameHash ) {
        User user = userManager.getUserByNameHash( userNameHash );
        return user.getName();
    }
    /**
     * Gets the list of channels available for a given remote node.
     *
     * @return List of channels available for a given remote node
     */
    public List getChannelListing() { return channelListing; }
    /**
     * Sets the list of channels available for a given remote node.
     *
     * @param channelListing List of channels available for a given remote node
     */
    public void setChannelListing( List channelListing ) { this.channelListing = channelListing; }
    /**
     * Resets the list of remote channels.
     */
    public void resetChannelListing() { this.channelListing = null; }
    /**
     * Gets list of subscriptions confirmed by remote node.
     *
     * @return List of subscriptions confirmed by remote node
     */
    public List getConfirmedSubscriptions() { return confirmedSubscriptions; }
    /**
     * Sets list of subscriptions confirmed by remote node.
     *
     * @param confirmedSubscriptions List of subscriptions confirmed by remote node
     */
    public void setConfirmedSubscriptions( List confirmedSubscriptions ) {
        this.confirmedSubscriptions = confirmedSubscriptions;
    }
    /**
     * Resets the list of confirmed subscriptions.
     */
    public void resetConfirmedSubscriptions() { this.confirmedSubscriptions = null; }
    /**
     * Gets remote node name.
     *
     * @return Remote node name
     */
    public String getRemoteNodeName() { return remoteNodeName; }
    /**
     * Set remote node name.
     *
     * @param remoteNodeName Remote node name
     */
    public void setRemoteNodeName( String remoteNodeName ) { this.remoteNodeName = remoteNodeName; }
    /**
     * Resets remote node name.
     */
    public void resetRemoteNodeName() { this.remoteNodeName = null; }
    /**
     * Gets remote node address.
     *
     * @return Remote node address
     */
    public String getRemoteNodeAddress() { return remoteNodeAddress; }
    /**
     * Sets remote node address.
     *
     * @param remoteNodeAddress Remote node address
     */
    public void setRemoteNodeAddress( String remoteNodeAddress ) {
        this.remoteNodeAddress = remoteNodeAddress;
    }
    /**
     * Resets remote node address.
     */
    public void resetRemoteNodeAddress() { this.remoteNodeAddress = null; }
    /**
     * Gets local user name.
     *
     * @return Local user name
     */
    public String getLocalUserName() { return localUserName; }
    /**
     * Sets local user name.
     *
     * @param localUserName Local user name
     */
    public void setLocalUserName( String localUserName ) {
        this.localUserName = localUserName;
    }
    /**
     * Resets local user name.
     */
    public void resetLocalUserName() { this.localUserName = null; }
    /**
     * Gets synchronized subscription.
     *
     * @return Synchronized subscription
     */
    public Subscription getSynkronizedSubscription() { return synkronizedSubscription; }
    /**
     * Sets synchronized subscription.
     *
     * @param synkronizedSubscription Synchronized subscription
     */
    public void setSynkronizedSubscription( Subscription synkronizedSubscription ) {
        this.synkronizedSubscription = synkronizedSubscription;
    }
    /**
     * Resets synchronized subscription object.
     */
    public void resetSynkronizedSubscription() { this.synkronizedSubscription = null; }
    /**
     * Gets reference to BaltradFrameHandler object.
     *
     * @return Reference to BaltradFrameHandler object
     */
    public BaltradFrameHandler getBfHandler() { return bfHandler; }
    /**
     * Sets reference to BaltradFrameHandler object.
     *
     * @param bfHandler Reference to BaltradFrameHandler object
     */
    public void setBfHandler( BaltradFrameHandler bfHandler ) { this.bfHandler = bfHandler; }
    /**
     * Method gets reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) { this.userManager = userManager; }
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
    /**
     * Method gets reference to DeliveryRegisterManager class instance.
     *
     * @return Reference to DeliveryRegisterManager class instance
     */
    public DeliveryRegisterManager getDeliveryRegisterManager() { return deliveryRegisterManager; }
    /**
     * Method sets reference to DeliveryRegisterManager class instance.
     *
     * @param deliveryRegisterManager Reference to DeliveryRegisterManager class instance
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
    }
}
//--------------------------------------------------------------------------------------------------
