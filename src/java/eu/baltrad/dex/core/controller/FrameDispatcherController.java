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

package eu.baltrad.dex.core.controller;

import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.log.model.*;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.dex.bltdata.controller.BltDataProcessorController;
import eu.baltrad.dex.core.util.FramePublisherManager;
import eu.baltrad.dex.core.util.FramePublisher;
import eu.baltrad.dex.core.util.HandleFrameTask;
import eu.baltrad.dex.register.model.DeliveryRegisterManager;

import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileEntry;
import eu.baltrad.fc.DuplicateEntry;
import eu.baltrad.fc.FileCatalogError;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.mo.BltDataMessage;

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

/**
 * Class implements frame dispatching controller. This controller handles all incoming and outgoing
 * BaltradFrames according to the single point of entry rule.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class FrameDispatcherController extends HttpServlet implements Controller {
//---------------------------------------------------------------------------------------- Constants
    // path to HDF5 dataset used to generate image thumb
    private static final String H5_THUMB_DATASET_PATH = "/dataset1/data1/data";
    // path to HDF5 metadata group used to generate image thumb
    private static final String H5_THUMB_GROUP_PATH = "/dataset1/where";
    // image thumb size
    private static final int THUMB_IMAGE_SIZE = 64;
    // range rings distance
    private static final short THUMB_RANGE_RINGS_DISTANCE = 0;
    // range mask line stroke
    private static final float THUMB_RANGE_MASK_STROKE = 6.0f;
    // range rings color string
    private static final String THUMB_RANGE_RINGS_COLOR = "#FFFFFF";
    // range mask color string
    private static final String THUMB_RANGE_MASK_COLOR = "#FFFFFF";
    // image file extension
    private static final String IMAGE_FILE_EXT = ".png";
//---------------------------------------------------------------------------------------- Variables
    private BaltradFrameHandler bfHandler;
    private UserManager userManager;
    private DataSourceManager dataSourceManager;
    private SubscriptionManager subscriptionManager;
    private LogManager logManager;
    private BltDataProcessorController bltDataProcessorController;
    // Reference to file catalog object
    private FileCatalog fc;
    // Beast message manager
    private IBltMessageManager bltMessageManager;
    // remote data source listing
    private List dataSourceListing;
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
    // Frame publisher manager
    private FramePublisherManager framePublisherManager;
    // Frame publisher object
    private FramePublisher framePublisher;
    /** References delivery register manager object @see DeliveryRegisterManager */
    private DeliveryRegisterManager deliveryRegisterManager;
    /** Reference to FileCatalogConnector */
    private FileCatalogConnector fileCatalogConnector;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to FileCatalogConnector instance.
     */
    public FrameDispatcherController() {
        this.fileCatalogConnector = FileCatalogConnector.getInstance();
        this.fc = fileCatalogConnector.getFileCatalog();
    }
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
                            // user ID set upon authentication
                            int userId;
                            if( ( userId = authenticateFrame( header ) ) != 0 ) {
                                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, 
                                    LogEntry.LEVEL_INFO, "Channel listing request received from " +
                                    "user " + getUserName( bfHandler.getUserName( header ) ) + " ("
                                     + bfHandler.getSenderNodeName( header ) + ")" ) );
                                
                                // process channel listing request

                                // create a list of data sources that user is allowed to subscribe
                                List<Integer> dataSourceIds = dataSourceManager.getDataSourceIds( 
                                        userId );

                                // data sources allowed to be used by a given user
                                List<DataSource> dataSources = new ArrayList<DataSource>();
                                for( int i = 0; i < dataSourceIds.size(); i++ ) {
                                    dataSources.add( dataSourceManager.getDataSource(
                                            dataSourceIds.get( i ) ) );
                                }

                                // write list to temporary file
                                File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                                // write object to stream
                                InitAppUtil.writeObjectToStream( dataSources, tempFile );

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
                                bfHandler.handleBF( baltradFrame, InitAppUtil.getConnTimeout(),
                                        InitAppUtil.getSoTimeout() );
                                // delete temporary file
                                InitAppUtil.deleteFile( tempFile );
                            } else {
                                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                    LogEntry.LEVEL_WARN, 
                                    "Incoming frame could not be authenticated" ) );
                            }
                        } else {
                            // regular message frame - display message
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                    bfHandler.getMessageClass( header ),
                                    bfHandler.getMessageText( header ) ) );
                        }
                    }

                    // process object frame

                    if( bfHandler.getContentType( header ).equals(
                            BaltradFrameHandler.OBJECT ) ) {

                        // incoming channel listing

                        if( bfHandler.getMessageText( header ).equals(
                            BaltradFrameHandler.CHNL_LIST ) ) {
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_INFO, "Channel listing received from " +
                                bfHandler.getSenderNodeName( header ) ) );

                            // process incoming data source listing

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write list to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve channel list and save it to class field
                            List remoteDataSources = ( List )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // set channel listing
                            setDataSourceListing( remoteDataSources );
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

                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_INFO, "Channel subscription request received from " +
                                bfHandler.getSenderNodeName( header ) ) );

                            // process incoming subscription request

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription request to temporary file
                            // write list to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription list
                            List subscribedDataSources = ( List )InitAppUtil.readObjectFromStream(
                                    tempFile );

                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            // identify user by name
                            User user = userManager.getUserByName( bfHandler.getUserName( header ) );
                            // return data source list sent back to user to confirm
                            // subscription completion
                            List confirmedDataSources = new ArrayList();
                            if( subscribedDataSources != null ) {
                                for( int i = 0; i < subscribedDataSources.size(); i++ ) {
                                    DataSource requestedDataSource =
                                            ( DataSource )subscribedDataSources.get( i );
                                    DataSource localDataSource = dataSourceManager.getDataSource(
                                            requestedDataSource.getName() );
                                    Subscription subs = null;
                                    // make sure user hasn't already subscribed the channels
                                    if( subscriptionManager.getSubscription( user.getName(),
                                            localDataSource.getName(),
                                            Subscription.REMOTE_SUBSCRIPTION ) != null ) {
                                        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                            LogEntry.LEVEL_WARN, "User " + user.getName() +
                                            " has already subscribed to " 
                                            + localDataSource.getName() ) );
                                    } else {
                                        // add subscription
                                        subs = new Subscription( System.currentTimeMillis(),
                                            user.getName(), localDataSource.getName(),
                                            InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                                            Subscription.REMOTE_SUBSCRIPTION, false, false );
                                        subscriptionManager.saveSubscription( subs );
                                        confirmedDataSources.add( requestedDataSource );
                                    }
                                }
                            }
                            // send subscription confirmation

                            // write list to temporary file
                            tempFile = InitAppUtil.createTempFile(
                                    new File( InitAppUtil.getWorkDir() ) );
                            // write object to the stream
                            InitAppUtil.writeObjectToStream( confirmedDataSources, tempFile );
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
                            bfHandler.handleBF( baltradFrame, InitAppUtil.getConnTimeout(),
                                        InitAppUtil.getSoTimeout() );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }
                        // incoming channel subscription confirmation

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_SBN_CFN ) ) {
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_INFO, "Channel subscription confirmation received " +
                                " from " + bfHandler.getSenderNodeName( header ) ) );

                            // process incoming subscription confirmation

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write list of confirmed subscriptions to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve list of confirmed channels
                            List confirmedDataSources = ( List )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            setConfirmedSubscriptions( confirmedDataSources );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                        }

                        // incoming channel subscription change request

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.SBN_CHNG_RQST ) ) {
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_INFO, "Channel subscription change request " +
                                "received from " + bfHandler.getSenderNodeName( header ) ) );

                            // process incoming subscription change request

                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription change request to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );

                            // create new subscription object
                            Subscription s = new Subscription( System.currentTimeMillis(),
                                    subs.getUserName(), subs.getDataSourceName(), subs.getNodeAddress(),
                                    subs.getOperatorName(), Subscription.REMOTE_SUBSCRIPTION,
                                    false, false );
                            // subscription object serves as confirmation
                            Subscription confirmedSub = null;
                            if( subs.getActive() ) {
                                if( subscriptionManager.getSubscription( s.getUserName(), 
                                    s.getDataSourceName(), Subscription.REMOTE_SUBSCRIPTION ) == null) {
                                    // subscription doesn't exist in the database - add as new
                                    // subscription
                                    subscriptionManager.saveSubscription( s );
                                    confirmedSub = s;
                                    //@
                                    confirmedSub.setActive( true );
                                    logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                        LogEntry.LEVEL_INFO, "User " + s.getUserName() +
                                        " subscribed to " + s.getDataSourceName() ) );

                                } else {
                                    // subscription exists in the database - modify subscription
                                    subscriptionManager.updateSubscription( s.getDataSourceName(),
                                            Subscription.REMOTE_SUBSCRIPTION, true );
                                    confirmedSub = s;
                                    confirmedSub.setActive( true );
                                    logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                        LogEntry.LEVEL_INFO, "User " + s.getUserName() +
                                        " subscribed to " + s.getDataSourceName() ) );
                                }
                                /*/ add remote subscription
                                subscriptionManager.addSubscription( s );
                                confirmedSub = s;
                                //@
                                confirmedSub.setSelected( true );
                                */
                            } else {
                                // remove remote subscription
                                subscriptionManager.deleteSubscription( s.getUserName(),
                                        s.getDataSourceName(), s.getType() );
                                confirmedSub = s;
                                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                    LogEntry.LEVEL_INFO, "User " + s.getUserName() +
                                    " cancelled subscription to " + s.getDataSourceName() ) );
                            }

                            // send subscription change confirmation

                            // write subscription object to temporary file
                            tempFile = InitAppUtil.createTempFile(
                                    new File( InitAppUtil.getWorkDir() ) );
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
                            bfHandler.handleBF( baltradFrame, InitAppUtil.getConnTimeout(),
                                        InitAppUtil.getSoTimeout() );
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
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            String selected = subs.getActive() ? "Subscribed" : "Unsubscribed";
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_INFO, "Remote node " +
                                bfHandler.getSenderNodeName( header )
                                + " changed your subscription status for " + 
                                subs.getDataSourceName() + " to: " + selected ) );
                        }

                        // incoming channel subscription change confirmation - failure

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.SBN_CHNG_FAIL ) ) {
                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription change request to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            String selected = subs.getActive() ? "Subscribed" : "Unsubscribed";
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_ERROR, "Remote node " +
                                bfHandler.getSenderNodeName( header )  +
                                " failed to change your subscription status for "
                                + subs.getDataSourceName() + " to: " + selected ) );
                        }

                        // channel synchronization request

                        if( bfHandler.getMessageText( header ).equals(
                                BaltradFrameHandler.CHNL_SYNC_RQST ) ) {
                            FileItemStream fileItem = iterator.next();
                            InputStream fileStream = fileItem.openStream();
                            // write subscription object to temporary file
                            File tempFile = InitAppUtil.createTempFile(
                                        new File( InitAppUtil.getWorkDir() ) );
                            InitAppUtil.saveFile( fileStream, tempFile );
                            // retrieve subscription object from stream
                            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(
                                    tempFile );
                            // delete temporary file
                            InitAppUtil.deleteFile( tempFile );
                            // check if the subscribed channel is available
                            try {
                                DataSource dataSource = dataSourceManager.getDataSource(
                                        subs.getDataSourceName() );
                                if( dataSource == null ) {
                                    subs.setSynkronized( false );
                                }
                            } catch( NullPointerException e ) {
                                System.err.println( "Failed to synchronize subscribed channel: "
                                        + subs.getDataSourceName() );
                            }

                            // send subscription back to requesting node

                            // write list to temporary file
                            tempFile = InitAppUtil.createTempFile(
                                    new File( InitAppUtil.getWorkDir() ) );
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
                            bfHandler.handleBF( baltradFrame, InitAppUtil.getConnTimeout(),
                                        InitAppUtil.getSoTimeout() );
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
                                        new File( InitAppUtil.getWorkDir() ) );
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
                        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                            LogEntry.LEVEL_INFO, "New data frame received from " +
                            bfHandler.getSenderNodeName( header ) + " / " +
                            bfHandler.getChannel( header ) ) );
                        // write data to swap file
                        File swapFile = InitAppUtil.createTempFile(
                                new File( InitAppUtil.getWorkDir() ) );
                        FileItemStream fileItem = iterator.next();
                        InputStream fileStream = fileItem.openStream();
                        InitAppUtil.saveFile( fileStream, swapFile );
                        
                        try {
                            FileEntry fileEntry = fc.store( swapFile.getAbsolutePath() );



                            
                            




                            if( fileEntry == null ) {
                                // failed to store file in File Catalog - set error code
                                response.setStatus( BaltradFrameHandler.HTTP_STATUS_CODE_500 );
                            } else {
                                // file successfully stored in File Catalog
                                response.setStatus( BaltradFrameHandler.HTTP_STATUS_CODE_200 );
                                // send message to Beast Framework
                                BltDataMessage message = new BltDataMessage();
                                message.setFileEntry( fileEntry );
                                bltMessageManager.manage( message );
                                
                                // iterate through subscriptions list to send data to subscribers
                                List<Subscription> subs =
                                        subscriptionManager.getSubscriptions(
                                        Subscription.REMOTE_SUBSCRIPTION );
                                for( int i = 0; i < subs.size(); i++ ) {


                                    // check if file entry matches the subscribed data source
                                    


                                    if( subs.get( i ).getDataSourceName().equals( 
                                            bfHandler.getChannel( header ) ) ) {

                                        // make sure that user exists locally
                                        User user = userManager.getUserByName(
                                                                      subs.get( i ).getUserName() );
                                        String dataSourceName = subs.get( i ).getDataSourceName();
                                        // look up file item in data register - if file item doesn't 
                                        // exist, create frame delivery task
                                        if( getDeliveryRegisterManager().getEntry( user.getId(),
                                                fileEntry.uuid() ) == null ) {

                                            // create frame delivery task
                                            HandleFrameTask task = new HandleFrameTask(
                                                getDeliveryRegisterManager(), getLogManager(),
                                                user, dataSourceName, fileEntry, swapFile );

                                            // add task to publisher manager
                                            framePublisherManager.getFramePublisher(
                                                    user.getName() ).addTask( task );
                                        }
                                    }
                                }
                            }
                        } catch( DuplicateEntry e ) {
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_ERROR, "Duplicate entry error: " + e.getMessage() ) );
                            // exception while storing file in FileCatalog - set error code
                            response.setStatus( BaltradFrameHandler.HTTP_STATUS_CODE_500 );
                        } catch( FileCatalogError e ) {
                            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX,
                                LogEntry.LEVEL_ERROR, "File catalog error: " + e.getMessage() ) );
                            // exception while storing file in FileCatalog - set error code
                            response.setStatus( BaltradFrameHandler.HTTP_STATUS_CODE_500 );
                        }
                    }
                }
            }
            // clean work directory from temporary files
            InitAppUtil.cleanUpTempFiles( InitAppUtil.getWorkDir() );

        } catch( FileUploadException e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR, 
                    "Frame dispatcher error: " + e.getMessage() ) );
            e.printStackTrace();
        } catch( IOException e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR, 
                    "Frame dispatcher error: " + e.getMessage() ) );
            e.printStackTrace();
        } catch( Exception e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR, 
                    "Frame dispatcher error: " + e.getMessage() ) );
            e.printStackTrace();
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
        bfHandler.handleBF( baltradFrame, InitAppUtil.getConnTimeout(), InitAppUtil.getSoTimeout() );
    }
    /**
     * Autheticates incoming frame by verifying user name and password encoded in frame header.
     * WARNING: Only users belonging to groups ADMIN and PEER are authorized to exchange data.
     *
     * @param header Frame header
     * @return User ID once a frame is successfully authenticated, 0 otherwise
     */
    public int authenticateFrame( String header ) {
        String userNameHash = bfHandler.getUserName( header );
        String passwd = bfHandler.getPassword( header );
        User user = userManager.getUserByNameHash( userNameHash );
        if( user != null && user.getNameHash().equals( userNameHash )
            && user.getPassword().equals( passwd ) && ( user.getRoleName().equals( User.ROLE_ADMIN )
            || user.getRoleName().equals( User.ROLE_PEER ) ) ) return user.getId();
        else
            return 0;
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
     * Gets the list of data sources available for a given remote node.
     *
     * @return List of data sources available for a given remote node
     */
    public List getDataSourceListing() { return dataSourceListing; }
    /**
     * Sets the list of data sources available for a given remote node.
     *
     * @param dataSourceListing List of data sources available for a given remote node
     */
    public void setDataSourceListing( List dataSourceListing ) {
        this.dataSourceListing = dataSourceListing;
    }
    /**
     * Resets the list of remote data sources
     */
    public void resetDataSourceListing() { this.dataSourceListing = null; }
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
     * Gets reference to BltDataProcessorController object.
     *
     * @return Reference to BltDataProcessorController object
     */
    public BltDataProcessorController getBltDataProcessorController() {
        return bltDataProcessorController;
    }
    /**
     * Sets reference to BltDataProcessorController object.
     *
     * @param bltDataProcessorController Reference to BltDataProcessorController object
     */
    public void setBltDataProcessorController(
            BltDataProcessorController bltDataProcessorController ) {
        this.bltDataProcessorController = bltDataProcessorController;
    }
    /**
     * Gets reference to FramePublisherManager object.
     *
     * @return Reference to FramePublisherManager object
     */
    public FramePublisherManager getFramePublisherManager() {
        return framePublisherManager;
    }
    /**
     * Sets reference to FramePublisherManager object.
     *
     * @param framePublisherManager Reference to FramePublisherManager object to set
     */
    public void setFramePublisherManager( FramePublisherManager framePublisherManager ) {
        this.framePublisherManager = framePublisherManager;
    }
    /**
     * Gets reference to FramePublisher object
     *
     * @return the framePublisher
     */
    public FramePublisher getFramePublisher() {
        return framePublisher;
    }
    /**
     * Sets reference to FramePublisher object
     *
     * @param framePublisher the framePublisher to set
     */
    public void setFramePublisher(FramePublisher framePublisher) {
        this.framePublisher = framePublisher;
    }
    /**
     * Gets reference to Beast's IBltMessageManager object.
     *
     * @return Reference to Beast's IBltMessageManager object
     */
    public IBltMessageManager getBltMessageManager() { return bltMessageManager; }
    /**
     * Sets reference to Beast's IBltMessageManager object.
     *
     * @param bltMessageManager Reference to Beast's IBltMessageManager object
     */
    public void setBltMessageManager( IBltMessageManager bltMessageManager ) {
        this.bltMessageManager = bltMessageManager;
    }
    /**
     * Gets reference to delivery register manager object.
     *
     * @return Reference to delivery register manager object
     * @see DeliveryRegisterManager
     */
    public DeliveryRegisterManager getDeliveryRegisterManager() {
        return deliveryRegisterManager;
    }
    /**
     * Sets reference to delivery register manager object.
     *
     * @param deliveryRegisterManager Reference to delivery register manager to set
     * @see DeliveryRegisterManager
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
    }
}
//--------------------------------------------------------------------------------------------------
