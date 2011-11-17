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
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.bltdata.controller.BltDataProcessorController;
import eu.baltrad.dex.core.util.FramePublisherManager;
import eu.baltrad.dex.core.util.FramePublisher;
import eu.baltrad.dex.core.util.HandleFrameTask;
import eu.baltrad.dex.core.util.IncomingFileNamer;
import eu.baltrad.dex.registry.model.*;
import eu.baltrad.dex.bltdata.model.BltFileManager;

import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileEntry;
import eu.baltrad.fc.DuplicateEntry;
import eu.baltrad.fc.FileCatalogError;
import eu.baltrad.fc.Oh5MetadataMatcher;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.mo.BltDataMessage;
import eu.baltrad.beast.db.IFilter;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
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
    private InitAppUtil init;
    private Logger log;
    private BltDataProcessorController bltDataProcessorController;
    // Reference to file catalog object
    private FileCatalog fileCatalog;
    // Beast message manager
    private IBltMessageManager bltMessageManager;
    // remote data source listing
    private List dataSourceListing;
    // confirmed subscription list
    private List confirmedSubscriptions;
    // synchronized subscription
    private Subscription synkronizedSubscription;
    /** Remote node's address */
    private String remNodeAddress;
    // remote node name
    private String remNodeName;
    // remote user name
    private String locUsrName;
    // Frame publisher manager
    private FramePublisherManager framePublisherManager;
    // Frame publisher object
    private FramePublisher framePublisher;
    /** References delivery register manager object @see DeliveryRegisterManager */
    private DeliveryRegisterManager deliveryRegisterManager;
    /** Reference to BltFileManager */
    private BltFileManager bltFileManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public FrameDispatcherController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        this.init = new InitAppUtil();
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
            FileItemStream itemStream = null;

            String frameHeader = null;
            File frameFile = null;

            if (iterator.hasNext()) {
                itemStream = iterator.next();
                if (itemStream.getFieldName().equals(BaltradFrame.XML_PART)) {
                    InputStream hdrStream = itemStream.openStream();
                    // Handle form field / message XML header
                    if (itemStream.isFormField()) {
                        // Get header string
                        frameHeader = Streams.asString(hdrStream);
                    }
                }
            }
            
            if (iterator.hasNext()) {
                itemStream = iterator.next();
                InputStream fileStream = itemStream.openStream();
                // write to a temporary file
                frameFile = InitAppUtil.createTempFile(new File(init.getWorkDirPath()));
                InitAppUtil.saveFile(fileStream, frameFile);
            }

            if (frameHeader != null) {
                processIncomingFrame(frameHeader, frameFile, response);
            } else {
                log.error("Invalid baltrad frame (no header parsed from request)");
                response.setStatus(BaltradFrameHandler.HTTP_STATUS_CODE_500);
            }
        } catch (Exception e) {
            log.error("Frame dispatcher error", e);
            response.setStatus(BaltradFrameHandler.HTTP_STATUS_CODE_500);
        } finally {
            // clean work directory from temporary files
            InitAppUtil.cleanUpTempFiles( init.getWorkDirPath() );
        }
    }

    protected void processIncomingFrame(String header, File file, HttpServletResponse response) throws Exception {
        String contentType = BaltradFrameHandler.getContentType(header);
        log.debug("Received frame with contentType: " + contentType);
        
        String userName = BaltradFrameHandler.getUserName(header);
        User user = null;
        if (userName == null) {
            // Not all frames set user information. This is for backwards compatibility.
            // XXX: this should be fixed ASAP!
            log.warn("Allowing frame with no user data to pass");
        } else {
            // XXX: we are in a messy state where some frames contain user by name
            // and some by hash
            user = userManager.getUserByNameHash(userName);
            if (user == null)
                user = userManager.getUserByName(userName);
            if (user == null) {
                log.error("received frame from unknown user: " + userName);
                response.setStatus(response.SC_FORBIDDEN);
                return;
            }

            // XXX: some frames contain a user, but no password, skip authn for these
            String passwd = BaltradFrameHandler.getPassword( header );
            if (passwd == null) {
                log.warn("skipping authentication for frame with no password from user: " + userName);
            } else if (!authenticateFrame(user, header)) {
                log.error("failed to authenticate frame from user: " + userName);
                response.setStatus(response.SC_FORBIDDEN);
                return;
            }

            if (!authorizeUser(user)) {
                log.error("unauthorized frame from user: " + userName);
                response.setStatus(response.SC_FORBIDDEN);
                return;
            }
        }

        if (contentType.equals(BaltradFrameHandler.MSG)) {
            processIncomingMessageFrame(user, header, file, response);
        } else if (contentType.equals(BaltradFrameHandler.OBJECT)) {
            processIncomingObjectFrame(user, header, file, response);
        } else if (contentType.equals(BaltradFrameHandler.FILE)) {
            response.setStatus(processIncomingDataFrame(user, header, file));
        } else {
            log.warn("unhandled baltrad frame type: " + contentType);
            response.setStatus(BaltradFrameHandler.HTTP_STATUS_CODE_500);
        }
    }

    protected void processIncomingMessageFrame(User user, String header, File file, HttpServletResponse response) throws Exception {
        String messageText = BaltradFrameHandler.getMessageText(header);

        // channel listing request - has to be authenticated
        if (messageText.equals(BaltradFrameHandler.CHNL_LIST_RQST)) {
            log.info( "Data source listing request received from " +
                "user " + getUserName( BaltradFrameHandler.getUserName( header ) ) + " ("
                 + BaltradFrameHandler.getSenderNodeName( header ) + ")" );
            
            // process channel listing request

            // create a list of data sources that user is allowed to subscribe
            List<Integer> dataSourceIds = dataSourceManager.getDataSourceIds( 
                    user.getId() );

            // data sources allowed to be used by a given user
            List<DataSource> dataSources = new ArrayList<DataSource>();
            for( int i = 0; i < dataSourceIds.size(); i++ ) {
                dataSources.add( dataSourceManager.getDataSource(
                        dataSourceIds.get( i ) ) );
            }
            // write list to temporary file
            File tempFile = InitAppUtil.createTempFile(
                    new File( init.getWorkDirPath() ) );
            // write object to stream
            InitAppUtil.writeObjectToStream( dataSources, tempFile );

            // set the return address
            String remoteNodeAddress = BaltradFrameHandler.getSenderNodeAddress(header);

            // prepare the return message header
            String retHeader = BaltradFrameHandler.createObjectHdr(
                    BaltradFrameHandler.MIME_MULTIPART,
                    init.getConfiguration().getNodeAddress(),
                    init.getConfiguration().getNodeName(),
                    BaltradFrameHandler.CHNL_LIST,
                    tempFile.getAbsolutePath() );
            //
            BaltradFrame baltradFrame = new BaltradFrame( retHeader, tempFile );
            // process the frame
            bfHandler.handleBF( remoteNodeAddress, baltradFrame );
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );
        } else {
            // regular message frame - display message
            log.info( BaltradFrameHandler.getMessageText( header ) );
        }
    }

    protected void processIncomingObjectFrame(User user, String header, File file, HttpServletResponse response) throws Exception {
        String messageText = BaltradFrameHandler.getMessageText(header);

        // incoming channel listing
        if (messageText.equals(BaltradFrameHandler.CHNL_LIST)) {
            log.info( "Data source listing received from " +
                BaltradFrameHandler.getSenderNodeName( header ) );

            // process incoming data source listing

            List remoteDataSources = ( List )InitAppUtil.readObjectFromStream(file);
            // set channel listing
            setDataSourceListing( remoteDataSources );
            // Set remote node address
            setRemNodeAddress( BaltradFrameHandler.getSenderNodeAddress( header ) );
            // Set remote node name
            setRemNodeName( BaltradFrameHandler.getSenderNodeName( header ) );
        // incoming channel subscription request
        } else if (messageText.equals(BaltradFrameHandler.CHNL_SBN_RQST)) {
            log.info( "Data source subscription request received from " +
                BaltradFrameHandler.getSenderNodeName( header ) );

            // retrieve subscription list
            List subscribedDataSources = ( List )InitAppUtil.readObjectFromStream(file);

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
                        log.warn( "User " + user.getName() + " has already " +
                            "subscribed " + localDataSource.getName() );
                    } else {
                        // add subscription
                        subs = new Subscription( System.currentTimeMillis(),
                            user.getName(), localDataSource.getName(),
                            init.getConfiguration().getNodeName(),
                            Subscription.REMOTE_SUBSCRIPTION, false, false,
                            init.getConfiguration().getScheme(),
                            init.getConfiguration().getHostAddress(),
                            init.getConfiguration().getPort(),
                            init.getConfiguration().getAppCtx(),
                            init.getConfiguration().getEntryAddress() );
                        subscriptionManager.saveSubscription( subs );
                        confirmedDataSources.add( requestedDataSource );
                    }
                }
            }
            // send subscription confirmation

            // write list to temporary file
            File tempFile = InitAppUtil.createTempFile(
                    new File( init.getWorkDirPath() ) );
            // write object to the stream
            InitAppUtil.writeObjectToStream( confirmedDataSources, tempFile );
            // set the return address
            String remoteNodeAddress = BaltradFrameHandler.getSenderNodeAddress(header);
            // prepare the return message header
            String retHeader = BaltradFrameHandler.createObjectHdr(
                    BaltradFrameHandler.MIME_MULTIPART,
                    init.getConfiguration().getNodeAddress(),
                    init.getConfiguration().getNodeName(),
                    BaltradFrameHandler.CHNL_SBN_CFN,
                    tempFile.getAbsolutePath() );
            //
            BaltradFrame baltradFrame = new BaltradFrame(retHeader, tempFile);
            // process the frame
            bfHandler.handleBF( remoteNodeAddress, baltradFrame );
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );

        // incoming channel subscription confirmation
        } else if (messageText.equals(BaltradFrameHandler.CHNL_SBN_CFN)) {
            log.info( "Data source subscription confirmation received " +
                " from " + BaltradFrameHandler.getSenderNodeName( header ) );

            // process incoming subscription confirmation

            // retrieve list of confirmed channels
            List confirmedDataSources = ( List )InitAppUtil.readObjectFromStream(file);
            setConfirmedSubscriptions( confirmedDataSources );

        // incoming channel subscription change request
        } else if (messageText.equals(BaltradFrameHandler.SBN_CHNG_RQST)) {
            log.info( "Data source subscription change request received from " +
                    BaltradFrameHandler.getSenderNodeName( header ) );

            // process incoming subscription change request

            // retrieve subscription object
            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(file);

            // create new subscription object
            Subscription s = new Subscription( System.currentTimeMillis(),
                    subs.getUserName(), subs.getDataSourceName(),
                    subs.getOperatorName(), Subscription.REMOTE_SUBSCRIPTION,
                    false, false, subs.getScheme(), subs.getHostAddress(),
                    subs.getPort(), subs.getAppCtx(), subs.getEntryAddress() );
            // subscription object serves as confirmation
            Subscription confirmedSub = null;
            if( subs.getActive() ) {
                if( subscriptionManager.getSubscription( s.getUserName(), 
                    s.getDataSourceName(), Subscription.REMOTE_SUBSCRIPTION ) == null) {
                    // subscription doesn't exist in the database - add as new
                    // subscription
                    subscriptionManager.saveSubscription( s );
                    confirmedSub = s;
                    confirmedSub.setActive( true );
                    log.info( "User " + s.getUserName() + " subscribed " +
                            s.getDataSourceName() );
                } else {
                    // subscription exists in the database - modify subscription
                    subscriptionManager.updateSubscription( s.getDataSourceName(),
                            Subscription.REMOTE_SUBSCRIPTION, true );
                    confirmedSub = s;
                    confirmedSub.setActive( true );
                    log.info( "User " + s.getUserName() + " subscribed " +
                            s.getDataSourceName() );
                }
            } else {
                // remove remote subscription
                subscriptionManager.deleteSubscription( s.getUserName(),
                        s.getDataSourceName(), s.getType() );
                confirmedSub = s;
                log.info( "User " + s.getUserName() + " cancelled subscription of "
                        + s.getDataSourceName() );
            }

            // send subscription change confirmation

            // write subscription object to temporary file
            File tempFile = InitAppUtil.createTempFile( 
                    new File( init.getWorkDirPath() ) );
            // set the return address
            String remoteNodeAddress = BaltradFrameHandler.getSenderNodeAddress(header);

            String returnMessage = null;
            if (confirmedSub != null) {
                returnMessage = BaltradFrameHandler.SBN_CHNG_OK;
            } else {
                returnMessage = BaltradFrameHandler.SBN_CHNG_FAIL;
            }

            String retHeader = BaltradFrameHandler.createObjectHdr(
                    BaltradFrameHandler.MIME_MULTIPART,
                    init.getConfiguration().getNodeAddress(),
                    init.getConfiguration().getNodeName(),
                    returnMessage,
                    tempFile.getAbsolutePath()
            );
            // write object to the stream
            InitAppUtil.writeObjectToStream( confirmedSub, tempFile );

            BaltradFrame baltradFrame = new BaltradFrame(retHeader, tempFile);
            // process the frame
            bfHandler.handleBF( remoteNodeAddress, baltradFrame );
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );

        // incoming channel subscription change confirmation - success
        } else if (messageText.equals(BaltradFrameHandler.SBN_CHNG_OK)) {
            // retrieve subscription object
            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(file);
            String selected = subs.getActive() ? "Subscribed" : "Unsubscribed";
            log.info( "Remote node " + BaltradFrameHandler.getSenderNodeName(
                    header ) + " changed your subscription status of " +
                    subs.getDataSourceName() + " to: " + selected );

        // incoming channel subscription change confirmation - failure
        } else if (messageText.equals(BaltradFrameHandler.SBN_CHNG_FAIL)) {
            // retrieve subscription object
            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(file);
            // delete temporary file
            String selected = subs.getActive() ? "Subscribed" : "Unsubscribed";
            log.error( "Remote node " + BaltradFrameHandler.getSenderNodeName(
                    header )  + " failed to change your subscription status of "
                + subs.getDataSourceName() + " to: " + selected );

        // channel synchronization request
        } else if (messageText.equals(BaltradFrameHandler.CHNL_SYNC_RQST)) {
            // retrieve subscription object from stream
            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(file);
            // check if the subscribed channel is available
            try {
                DataSource dataSource = dataSourceManager.getDataSource(
                        subs.getDataSourceName() );
                if( dataSource == null ) {
                    subs.setSynkronized( false );
                }
            } catch( NullPointerException e ) {
                log.error( "Failed to synchronize subscribed data source: "
                        + subs.getDataSourceName(), e );
            }

            // send subscription back to requesting node

            // write list to temporary file
            File tempFile = InitAppUtil.createTempFile(
                    new File( init.getWorkDirPath() ) );
            // write object to the stream
            InitAppUtil.writeObjectToStream( subs, tempFile );
            // set the return address
            String remoteNodeAddress = BaltradFrameHandler.getSenderNodeAddress(header);
            // prepare the return message header
            String retHeader = BaltradFrameHandler.createObjectHdr(
                    BaltradFrameHandler.MIME_MULTIPART,
                    init.getConfiguration().getNodeAddress(),
                    init.getConfiguration().getNodeName(),
                    BaltradFrameHandler.CHNL_SYNC_RSPNS,
                    tempFile.getAbsolutePath() );
            BaltradFrame baltradFrame = new BaltradFrame(retHeader, tempFile );
            // process the frame
            bfHandler.handleBF( remoteNodeAddress, baltradFrame );
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );

        // channel synchronization response
        } else if (messageText.equals(BaltradFrameHandler.CHNL_SYNC_RSPNS)) {
            // retrieve subscription object from stream
            Subscription subs = ( Subscription )InitAppUtil.readObjectFromStream(file);
            setSynkronizedSubscription( subs );
        } else {
            log.error("unhandled object message: " + messageText);
        }
    }

    /**
     *
     * @param header
     * @param fileItem
     * @return
     * @throws IOException
     */
    protected int processIncomingDataFrame(User user, String header, File file)
            throws IOException {
        String frameSource = BaltradFrameHandler.getSenderNodeName( header ) + "/" +
                BaltradFrameHandler.getChannel( header );
        log.info("New data frame received from " + frameSource);
        FileEntry fileEntry = null;
        try {
            fileEntry = fileCatalog.store( file.getAbsolutePath() );
        } catch (DuplicateEntry e) {
            log.error("Duplicate entry error", e );
            // exception while storing file in FileCatalog - set error code
            return BaltradFrameHandler.HTTP_STATUS_CODE_500;
        } catch (FileCatalogError e) {
            log.error("File catalog error", e );
            // exception while storing file in FileCatalog - set error code
            return BaltradFrameHandler.HTTP_STATUS_CODE_500;
        }
        IncomingFileNamer namer = new IncomingFileNamer();
        String friendlyName = namer.name(fileEntry);

        log.info(friendlyName + " stored with UUID " + fileEntry.uuid());

        // file successfully stored in File Catalog
        // send message to Beast Framework
        BltDataMessage message = new BltDataMessage();
        message.setFileEntry(fileEntry.clone());
        bltMessageManager.manage( message );
        
        Oh5MetadataMatcher metadataMatcher = new Oh5MetadataMatcher();

        // iterate through subscriptions list to send data to subscribers
        List<Subscription> subs =
            subscriptionManager.getSubscriptions(Subscription.REMOTE_SUBSCRIPTION);
        for (Subscription sub : subs) {
            // check if file entry matches the subscribed data source's filter
            String dataSourceName = sub.getDataSourceName();
            IFilter filter = bltFileManager.getFilter( dataSourceName );
            boolean matches = metadataMatcher.match(fileEntry.metadata(), filter.getExpression());
            // make sure that user exists locally
            User receivingUser = userManager.getUserByName(sub.getUserName());
            DeliveryRegisterEntry dre =
                deliveryRegisterManager.getEntry(receivingUser.getId(), fileEntry.uuid());

            if (matches && dre == null) {
                // create frame delivery task
                HandleFrameTask task = new HandleFrameTask(getDeliveryRegisterManager(),
                                                           log, receivingUser, dataSourceName,
                                                           fileEntry, file);
                // add task to publisher manager
                framePublisherManager.getFramePublisher(receivingUser.getName()).addTask(task);
            }
        }
        return BaltradFrameHandler.HTTP_STATUS_CODE_200;
    }

    /**
     * Post a baltrad frame to a remote node
     *
     * @param remoteNodeAddress address this frame should be posted to
     * @param baltradFrame Reference to BaltradFrame object
     */
    public void doPost(String remoteNodeAddress, BaltradFrame baltradFrame) {
        bfHandler.handleBF(remoteNodeAddress, baltradFrame);
    }

    /**
     * Check if the frame is indeed from the user
     *
     * @param user the user to check against
     * @param header Frame header
     * @return true if the frame is from the user
     *
     * check if the password contained in the frame is correct
     */
    public boolean authenticateFrame( User user, String header ) {
        String passwd = BaltradFrameHandler.getPassword( header );
        return user.getPassword().equals(passwd);
    }
    
    /**
     * check if the user is allowed to send messages to this node
     *
     * @param user the user to authorize
     * @return true if the user is authorized
     *
     * Only users belonging to groups ADMIN and PEER are authorized to exchange data.
     */
    public boolean authorizeUser(User user) {
        String userRole = user.getRoleName();
        if (userRole.equals(User.ROLE_ADMIN) || userRole.equals(User.ROLE_PEER)) {
            return true;
        }
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
    public String getRemNodeName() { return remNodeName; }
    /**
     * Set remote node name.
     *
     * @param remoteNodeName Remote node name
     */
    public void setRemNodeName( String remNodeName ) { this.remNodeName = remNodeName; }
    /**
     * Resets remote node name.
     */
    public void resetRemNodeName() { this.remNodeName = ""; }
    /**
     * Get remote node's address.
     *
     * @return Remote node'saddress
     */
    public String getRemNodeAddress() { return remNodeAddress; }
    /**
     * Set remote node's address.
     *
     * @param  Remote node's address to set
     */
    public void setRemNodeAddress( String remNodeAddress ) { this.remNodeAddress = remNodeAddress; }
    /**
     * Reset remote node's address.
     */
    public void resetRemNodeAddress() { this.remNodeAddress = ""; }
    /**
     * Gets local user name.
     *
     * @return Local user name
     */
    public String getLocUsrName() { return locUsrName; }
    /**
     * Sets local user name.
     *
     * @param locUsrName Local user name
     */
    public void setLocUsrName( String locUsrName ) { this.locUsrName = locUsrName; }
    /**
     * Resets local user name.
     */
    public void resetLocUsrName() { this.locUsrName = ""; }
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
    /**
     * Method returns reference to file manager object.
     *
     * @return Reference to file manager object
     */
    public BltFileManager getBltFileManager() { return bltFileManager; }
    /**
     * Method sets reference to file manager object.
     *
     * @param Reference to file manager object
     */
    public void setBltFileManager( BltFileManager bltFileManager ) {
        this.bltFileManager = bltFileManager;
    }

    public FileCatalog getFileCatalog() { return fileCatalog; }

    public void setFileCatalog(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
    }
}
//--------------------------------------------------------------------------------------------------
