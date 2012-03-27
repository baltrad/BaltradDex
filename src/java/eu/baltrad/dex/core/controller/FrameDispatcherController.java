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

import eu.baltrad.frame.model.*;
import static eu.baltrad.frame.model.Protocol.*;
import static eu.baltrad.dex.util.InitAppUtil.validate;
import static eu.baltrad.dex.util.InitAppUtil.deleteFile;
import static eu.baltrad.dex.util.InitAppUtil.cleanUpTempFiles;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.subscription.model.*;
import eu.baltrad.dex.bltdata.controller.BltDataProcessorController;
import eu.baltrad.dex.core.util.FramePublisherManager;
import eu.baltrad.dex.core.util.FramePublisher;
import eu.baltrad.dex.core.util.HandleFrameTask;
import eu.baltrad.dex.core.util.IncomingFileNamer;
import eu.baltrad.dex.registry.model.*;
import eu.baltrad.dex.db.model.BltFileManager;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.db.DuplicateEntry;
import eu.baltrad.bdb.oh5.MetadataMatcher;
import eu.baltrad.bdb.db.DatabaseError;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.beast.message.mo.BltDataMessage;
import eu.baltrad.beast.parser.IXmlMessageParser;
import eu.baltrad.beast.db.IFilter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class implements frame dispatching controller. This controller handles all incoming and outgoing
 * BaltradFrames according to the single point of entry rule.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class FrameDispatcherController extends HttpServlet implements Controller, InitializingBean {
//---------------------------------------------------------------------------------------- Constants
    // path to HDF5 dataset used to generate image thumb
    /*private static final String H5_THUMB_DATASET_PATH = "/dataset1/data1/data";
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
    private static final String IMAGE_FILE_EXT = ".png";*/
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
    private DataSourceManager dataSourceManager;
    private SubscriptionManager subscriptionManager;
    private Logger log;
    
    private static Logger logger = LogManager.getLogger(FrameDispatcherController.class);
    
    private BltDataProcessorController bltDataProcessorController;
    // Reference to file catalog object
    private FileCatalog fileCatalog;
    
    // Beast xml message parser
    private IXmlMessageParser xmlMessageParser;
    // Beast message manager
    private IBltMessageManager bltMessageManager;
    // Frame publisher manager
    private FramePublisherManager framePublisherManager;
    // Frame publisher object
    private FramePublisher framePublisher;
    /** References delivery register manager */
    private DeliveryRegisterManager deliveryRegisterManager;
    /** Reference to BltFileManager */
    private BltFileManager bltFileManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public FrameDispatcherController() {
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
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
     * Handles all types of requests by calling handler methods depending on the request type.
     * 
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     */
    @Override
    public void doGet( HttpServletRequest request, HttpServletResponse response ) {
        // Create new session to avoid comitting response too early 
        HttpSession session = request.getSession(true);
        // Parse incoming baltrad frame
        HashMap parms = parse(request, InitAppUtil.getWorkDir(), InitAppUtil.getWorkDir(), 
                InitAppUtil.getCertsDir());
        // Handle post certificate request
        /*if (Frame.getRequestType(parms).equals(BF_POST_CERT)) {
            handleCertRequest(parms, response);
        }*/
        // Handle data source listing request
        if (Frame.getRequestType(parms).equals(BF_GET_DS_LIST)) {
            handleDSListRequest(parms, response);
        }
        // Handle subscription request
        if (Frame.getRequestType(parms).equals(BF_POST_SUBSCRIPTION_REQUEST)) {
            handleSubscriptionRequest(parms, response);
        }
        // Handle subscription synchronization request
        if (Frame.getRequestType(parms).equals(BF_POST_SUBSCRIPTION_SYNC_REQUEST)) {
            handleSubscriptionSyncRequest(parms, response);
        }
        // Handle subscription update request
        if (Frame.getRequestType(parms).equals(BF_POST_SUBSCRIPTION_UPDATE_REQUEST)) {
            handleSubscriptionUpdateRequest(parms, response);
        }
        // Handle data delivery request
        if (Frame.getRequestType(parms).equals(BF_POST_DATA_DELIVERY_REQUEST)) {
            handleDataDeliveryRequest(parms, response);
        }
         // Handle message delivery request
        if (Frame.getRequestType(parms).equals(BF_POST_MESSAGE_DELIVERY_REQUEST)) {
            handleMessageDeliveryRequest(parms, request);
        }
    }
    /**
     * Handles certificate post request.
     * 
     * @param parms Request parameters
     * @param response HTTP response
     *
    private void handleCertRequest(HashMap parms, HttpServletResponse response) {
        if (authenticate(ServletContextUtil.getServletContextPath() 
                + InitAppUtil.KS_FILE_PATH,
                InitAppUtil.getConf().getKeystoreDir(), Frame.getNodeName(parms), 
                Frame.getSignature(parms), Frame.getTimestamp(parms))) {
            try {
                String msg = "";
                msg = "New certificate received from " + Frame.getNodeName(parms);
                log.info(msg);
                // create user account
                User user = null;
                if ((user = userManager.getByName(Frame.getNodeName(parms))) == null) {
                    // Create user with just a name and address 
                    user = new User(Frame.getNodeName(parms), User.ROLE_PEER,
                            Frame.getLocalUri(parms));
                    userManager.saveOrUpdatePeer(user);
                    log.warn("New peer account created: " + user.getName());
                }
                // Set node name as response header
                addHeader(response, HDR_NODE_NAME, InitAppUtil.getConf().getNodeName());
            } catch(Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("Failed to save certificate configuration", e);
            }
        } else { 
            log.error("Failed to authenticate frame");
            // Set node name as response header
            addHeader(response, HDR_NODE_NAME, InitAppUtil.getConf().getNodeName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    /**
     * Handles data source listing request
     * 
     * @param parms Request parameters
     * @param response HTTP response
     */
    private void handleDSListRequest(HashMap parms, HttpServletResponse response) {
        if (authenticate(InitAppUtil.getConf().getKeystoreDir(), 
                Frame.getNodeName(parms), Frame.getSignature(parms), 
                Frame.getTimestamp(parms))) {
            try {
                log.info(Frame.getNodeName(parms) + " requested data source listing");
                // create user account
                User user = null;
                if ((user = userManager.getByName(Frame.getNodeName(parms))) == null) {
                    // Create user with just a name and address 
                    user = new User(Frame.getNodeName(parms), User.ROLE_PEER,
                            Frame.getLocalUri(parms));
                    userManager.saveOrUpdatePeer(user);
                    log.warn("New peer account created: " + user.getName());
                }
                // Set node name as response header
                addHeader(response, HDR_NODE_NAME, InitAppUtil.getConf().getNodeName());
                response.setStatus(HttpServletResponse.SC_OK);
                // Send data source listing to the client
                List<Integer> dataSourceIds = dataSourceManager.getDataSourceIds(user.getId());
                List<DataSource> dsList = new ArrayList<DataSource>();
                for (int i = 0; i < dataSourceIds.size(); i++) {
                    dsList.add(dataSourceManager.getDataSource(dataSourceIds.get(i)));
                }
                long timestamp = System.currentTimeMillis();
                String signature = getSignatureString(
                    InitAppUtil.getConf().getKeystoreDir(), InitAppUtil.getConf().getCertAlias(), 
                        timestamp);
                SerialFrame serialFrame = SerialFrame.postDSListResponse(user.getNodeAddress(), 
                        InitAppUtil.getConf().getNodeAddress(), InitAppUtil.getConf().getNodeName(), 
                        timestamp, signature, dsList);
                writeFrameToStream(response, serialFrame);
            } catch(Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("Failed to send data source listing", e);
            }
        } else { 
            log.error("Failed to authenticate frame");
            // Set node name as response header
            addHeader(response, HDR_NODE_NAME, InitAppUtil.getConf().getNodeName());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } 
    }
    /**
     * Handles data source subscription request
     * 
     * @param parms Request parameters
     * @param response HTTP response 
     */
    private void handleSubscriptionRequest(HashMap parms, HttpServletResponse response) {
        if (authenticate(InitAppUtil.getConf().getKeystoreDir(), Frame.getNodeName(parms), 
                Frame.getSignature(parms), Frame.getTimestamp(parms))) {
            try {
                log.info(Frame.getNodeName(parms) + " requested data source subscription");
                List<DataSource> subRequest = (List<DataSource>) readObjectFromFile(
                        Frame.getPayloadFile(parms));
                List<DataSource> subConfirm = new ArrayList<DataSource>();
                if (validate(subRequest)) {
                    for (int i = 0; i < subRequest.size(); i++) {
                        DataSource dsRequest = subRequest.get(i);
                        DataSource dsLocal = dataSourceManager.getDataSource(dsRequest.getName());
                        User user = userManager.getByName(Frame.getNodeName(parms));
                        // make sure user hasn't already subscribed the selected data sources
                        if (subscriptionManager.get(user.getName(), dsLocal.getName(),
                                Subscription.SUBSCRIPTION_UPLOAD) != null) {
                            log.warn("User " + user.getName() + " has already subscribed " + 
                                    dsLocal.getName() );
                        } else {
                            // add subscription
                            Subscription sub = new Subscription(System.currentTimeMillis(),
                                user.getName(), dsLocal.getName(), 
                                InitAppUtil.getConf().getNodeName(),
                                Subscription.SUBSCRIPTION_UPLOAD, false, false,
                                InitAppUtil.getConf().getNodeAddress());
                            subscriptionManager.save(sub);
                            subConfirm.add(dsRequest);
                            log.info("User " + user.getName() + " subscribed " + 
                                    dsLocal.getName());
                        }
                    }
                }
                // Send subscription confirmation                
                response.setStatus(HttpServletResponse.SC_OK);
                User user = userManager.getByName(Frame.getNodeName(parms));
                long timestamp = System.currentTimeMillis();
                String signature = getSignatureString(InitAppUtil.getConf().getKeystoreDir(), 
                    InitAppUtil.getConf().getCertAlias(), timestamp); 
                SerialFrame serialFrame = SerialFrame.postSubscriptionResponse(
                        user.getNodeAddress(), InitAppUtil.getConf().getNodeAddress(), 
                        InitAppUtil.getConf().getNodeName(), timestamp, signature, subConfirm);
                writeFrameToStream(response, serialFrame);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("Failed to handle subscription request", e);
            }
        } else { 
            log.error("Failed to authenticate frame");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        // Delete temporary files 
        deleteFile(Frame.getPayloadFile(parms));
    }
    /**
     * Handles subscription synchronization request
     * 
     * @param parms Request parameters
     * @param response HTTP response 
     */
    private void handleSubscriptionSyncRequest(HashMap parms, HttpServletResponse response) {
        if (authenticate(InitAppUtil.getConf().getKeystoreDir(), Frame.getNodeName(parms), 
                Frame.getSignature(parms), Frame.getTimestamp(parms))) {
            try {
                Subscription sub = (Subscription) readObjectFromFile(Frame.getPayloadFile(parms));
                // Check if subscribed data source is available
                try {
                    DataSource dataSource = dataSourceManager.getDataSource(
                            sub.getDataSourceName());
                    if (dataSource == null) {
                        sub.setSynkronized(false);
                    }
                } catch (NullPointerException e) {
                    log.error("Failed to synchronize subscribed data source: "
                            + sub.getDataSourceName(), e);
                }
                // Send response
                response.setStatus(HttpServletResponse.SC_OK);
                User user = userManager.getByName(Frame.getNodeName(parms));
                long timestamp = System.currentTimeMillis();
                String signature = getSignatureString(InitAppUtil.getConf().getKeystoreDir(),    
                    InitAppUtil.getConf().getCertAlias(), timestamp); 
                SerialFrame serialFrame = SerialFrame.postSubscriptionSyncResponse(
                        user.getNodeAddress(), InitAppUtil.getConf().getNodeAddress(), 
                        InitAppUtil.getConf().getNodeName(), timestamp, signature, sub);
                writeFrameToStream(response, serialFrame);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("Failed to handle subscription synchronization request", e);
            }
        } else { 
            log.error("Failed to authenticate frame");
            // Set node name as response header
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        // Delete temporary files 
        deleteFile(Frame.getPayloadFile(parms));
    }
    /**
     * Handles subscription update request.
     * 
     * @param parms Request parameters
     * @param response HTTP response 
     */
    private void handleSubscriptionUpdateRequest(HashMap parms, HttpServletResponse response) {
        if (authenticate(InitAppUtil.getConf().getKeystoreDir(), Frame.getNodeName(parms), 
                Frame.getSignature(parms), Frame.getTimestamp(parms))) {
            try {
                log.info(Frame.getNodeName(parms) + " updated subscription status");
                Subscription sub = (Subscription) readObjectFromFile(Frame.getPayloadFile(parms));
                // Create new subscription object

                Subscription s = new Subscription(System.currentTimeMillis(),
                    sub.getUserName(), sub.getDataSourceName(),
                    sub.getOperatorName(), Subscription.SUBSCRIPTION_UPLOAD,
                    false, false, sub.getNodeAddress());
                // This subscription object serves asa confirrmation
                Subscription confirmedSub = new Subscription();
                if (sub.getActive()) {
                    if (subscriptionManager.get(s.getUserName(), s.getDataSourceName(), 
                            Subscription.SUBSCRIPTION_UPLOAD) == null) {
                        // Subscription doesn't exist in the database - add as new subscription
                        subscriptionManager.save(s);
                        confirmedSub = s;
                        confirmedSub.setActive(true);
                        log.info("User " + s.getUserName() + " subscribed " + s.getDataSourceName());
                    } else {
                        // Subscription exists in the database - update subscription

                        subscriptionManager.update(s.getDataSourceName(), 
                                Subscription.SUBSCRIPTION_UPLOAD, true);
                        confirmedSub = s;
                        confirmedSub.setActive(true);
                        log.info("User " + s.getUserName() + " subscribed " + s.getDataSourceName());
                    }                    
                } else {
                    // Delete remote subscription
                    int ii = subscriptionManager.delete(s.getUserName(), s.getDataSourceName(), s.getType());
                    confirmedSub = s;
                    log.info("User " + s.getUserName() + " cancelled subscription of "
                            + s.getDataSourceName());
                }
                // Send response
                response.setStatus(HttpServletResponse.SC_OK);
                User user = userManager.getByName(Frame.getNodeName(parms));
                long timestamp = System.currentTimeMillis();
                String signature = getSignatureString(InitAppUtil.getConf().getKeystoreDir(),    
                    InitAppUtil.getConf().getCertAlias(), timestamp); 
                SerialFrame serialFrame = SerialFrame.postSubscriptionUpdateResponse(
                        user.getNodeAddress(), InitAppUtil.getConf().getNodeAddress(), 
                        InitAppUtil.getConf().getNodeName(), timestamp, signature, confirmedSub);
                writeFrameToStream(response, serialFrame);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("Failed to handle subscription update request", e);
            }
        } else {
            log.error("Failed to authenticate frame");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
    /**
     * Handles data delivery request.
     * 
     * @param parms Request parameters
     * @param response HTTP response 
     */
    private void handleDataDeliveryRequest(HashMap parms, HttpServletResponse response) {
        if (authenticate(InitAppUtil.getConf().getKeystoreDir(), Frame.getNodeName(parms), 
                Frame.getSignature(parms), Frame.getTimestamp(parms))) {
            FileInputStream fis = null;
            try {
                log.info("New data frame received from " + Frame.getNodeName(parms));
                File payloadFile = Frame.getPayloadFile(parms);
                FileEntry fileEntry = null;
                fis = new FileInputStream(payloadFile.getAbsolutePath());
                fileEntry = fileCatalog.store(fis);
                IncomingFileNamer namer = new IncomingFileNamer();
                String friendlyName = namer.name(fileEntry);
                log.info(friendlyName + " stored with UUID " + fileEntry.getUuid().toString());
                // Send message to the Beast framework
                BltDataMessage message = new BltDataMessage();
                message.setFileEntry(fileEntry);
                bltMessageManager.manage(message);
                MetadataMatcher metadataMatcher = new MetadataMatcher();
                // Iterate through subscriptions list to send data to the users
                List<Subscription> subs = subscriptionManager.get(Subscription.SUBSCRIPTION_UPLOAD);
                for (Subscription sub : subs) {
                    // Check if file entry matches the subscribed data source's filter
                    String dataSourceName = sub.getDataSourceName();
                    IFilter filter = bltFileManager.getFilter(dataSourceName);
                    boolean matches = metadataMatcher.match(fileEntry.getMetadata(), 
                            filter.getExpression());
                    // Make sure that user exists locally
                    User receiver = userManager.getByName(sub.getUserName());
                    DeliveryRegisterEntry dre = deliveryRegisterManager.getEntry(
                            receiver.getId(), fileEntry.getUuid().toString());
                    if (matches && dre == null) {
                        long timestamp = System.currentTimeMillis();
                        String signature = getSignatureString(InitAppUtil.getConf().getKeystoreDir(),    
                            InitAppUtil.getConf().getCertAlias(), timestamp);
                        Frame frame = Frame.postDataDeliveryRequest(receiver.getNodeAddress(), 
                            InitAppUtil.getConf().getNodeAddress(),
                            InitAppUtil.getConf().getNodeName(), timestamp, signature, payloadFile);
                        // Create frame delivery task
                        HandleFrameTask task = new HandleFrameTask(getDeliveryRegisterManager(),
                            log, receiver, fileEntry, frame);
                        // Add task to publisher manager
                        framePublisherManager.getFramePublisher(
                                receiver.getName()).addTask(task);
                    }
                } 
                response.setStatus(HttpServletResponse.SC_OK);
                 // Delete temporary files
                cleanUpTempFiles(InitAppUtil.getWorkDir());
            } catch (DuplicateEntry e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("Duplicate entry error", e);
            } catch (DatabaseError e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("File catalog error", e);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                log.error("File catalog error", e);
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    log.error("Failed to close the strem", e);
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.error("Failed to authenticate frame");
        }
    }
    
    /**
     * Authenticates a message from the parameters
     * @param parms the parameters. Should contain Node name, signature and timestamp
     * @return true if authenticated
     */
    boolean isAuthenticated(HashMap parms) {
      return authenticate(InitAppUtil.getConf().getKeystoreDir(),
                          Frame.getNodeName(parms),
                          Frame.getSignature(parms),
                          Frame.getTimestamp(parms));
    }
    
    /**
     * Returns this nodes name. This method exists just for making testing
     * simpler.
     * @return this nodes name.
     */
    String getConfigNodeName() {
      return InitAppUtil.getConf().getNodeName();
    }
    
    /**
     * Handles message delivery request.
     * 
     * @param parms Request parameters
     * @param response HTTP response 
     */
    void handleMessageDeliveryRequest(HashMap parms, HttpServletRequest request) {
      if (isAuthenticated(parms)) {
        String nodeName = getConfigNodeName();
        String frameNodeName = Frame.getNodeName(parms);
        if (nodeName != null && nodeName.equals(frameNodeName)) {
          String msg = Frame.getMessage(parms);
          logger.debug("Got a message delivery: '"+msg+"'");
          try {
            IBltXmlMessage message = xmlMessageParser.parse(msg);
            bltMessageManager.manage(message);
          } catch (Exception e) {
            // pass
          }
        } else {
          logger.debug("message from unknown node, ignoring");
        }
      }
    }
    
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
     * Dependency injected xml parser for identifying beast messages.
     * @param parser the beast xml message parser
     */
    public void setXmlMessageParser(IXmlMessageParser parser) {
      this.xmlMessageParser = parser;
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
    /**
     * 
     * @return 
     */
    public FileCatalog getFileCatalog() { return fileCatalog; }
    /**
     * 
     * @param fileCatalog 
     */
    public void setFileCatalog(FileCatalog fileCatalog) { this.fileCatalog = fileCatalog; }
    

    /**
     * Called by spring fwk after the object initialization is completed.
     * @throws Exception upon error
     */
    @Override
    public void afterPropertiesSet() throws Exception {
      InitAppUtil.loadAppConf();
    }
}
//--------------------------------------------------------------------------------------------------
