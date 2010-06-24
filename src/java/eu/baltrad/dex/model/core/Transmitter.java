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

package eu.baltrad.dex.model.core;

import eu.baltrad.dex.model.register.DeliveryRegisterManager;
import eu.baltrad.dex.model.register.DeliveryRegisterEntry;
import eu.baltrad.dex.model.data.DataManager;
import eu.baltrad.dex.model.data.Data;
import eu.baltrad.dex.model.user.UserManager;
import eu.baltrad.dex.model.user.User;
import eu.baltrad.dex.model.channel.ChannelManager;
import eu.baltrad.dex.model.channel.Channel;
import eu.baltrad.dex.model.frame.BaltradFrameHandler;
import eu.baltrad.dex.model.frame.BaltradFrame;
import eu.baltrad.dex.model.log.LogManager;
import eu.baltrad.dex.model.subscription.Subscription;
import eu.baltrad.dex.model.subscription.SubscriptionManager;
import eu.baltrad.fc.FileCatalog;
import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.dex.util.InitAppUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.util.Date;
import java.util.List;

/**
 * Class implementing data transmitter module functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class Transmitter extends HttpServlet implements Controller {
//---------------------------------------------------------------------------------------- Variables
    // Reference to log manager object
    private LogManager logManager;
    // Reference to file catalog object
    private FileCatalog fileCatalog;
    // Reference to file catalog connector object
    private FileCatalogConnector fileCatalogConnector;
    // Reference to subscription manager object
    private SubscriptionManager subscriptionManager;
    // Reference to channel manager object
    private ChannelManager channelManager;
    // Reference to user manager object
    private UserManager userManager;
    // Reference to data manager object
    private DataManager dataManager;
    // Reference to delivery register manager
    private DeliveryRegisterManager deliveryRegisterManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles HTTP request.
     *
     * @param request Http request
     * @param response Http response
     * @return ModelAndView
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        doGet( request, response );
        return null;
    }
    /**
     * Method overrides HTTP POST method.
     *
     * @param request Http request
     * @param response Http response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator( request );
            FileItemStream hdrItem = iterator.next();
            // Check if incoming frame is BaltradFrame
            if( hdrItem.getFieldName().equals( BaltradFrame.BF_XML_PART ) ) {
                InputStream hdrStream = hdrItem.openStream();
                BaltradFrameHandler bfHandler = new BaltradFrameHandler();
                // Handle form field / message XML header
                if( hdrItem.isFormField() ) {
                    // Get header string
                    String hdrStr = Streams.asString( hdrStream );
                    // Check frame content type
                    // Handle message frame
                    if( bfHandler.getBFContentType( hdrStr ).equals(
                            BaltradFrameHandler.BF_MSG_CONTENT ) ) {
                        logManager.addEntry( new Date(), bfHandler.getBFMessageClass( hdrStr ),
                                bfHandler.getBFMessageText( hdrStr ) );
                    }
                    // Handle data frame / file content
                    if( bfHandler.getBFContentType( hdrStr ).equals(
                            BaltradFrameHandler.BF_FILE_CONTENT ) && iterator.hasNext() ) {
                        // Get file content
                        logManager.addEntry( new Date(), LogManager.MSG_INFO, "New file " +
                                " received from " + bfHandler.getBFSender( hdrStr ) +
                                "\n: " + bfHandler.getBFFileName( hdrStr ) );

                        // Save data to local file system
                        String incomingDir = InitAppUtil.getIncomingDataDir() +
                                File.separator + bfHandler.getBFSender( hdrStr );
                        // Create directory if it not exists
                        InitAppUtil.makeDir( incomingDir );
                        FileItemStream fileItem = iterator.next();
                        InputStream fileStream = fileItem.openStream();
                        String absFilePath = incomingDir + File.separator +
                                bfHandler.getBFFileName( hdrStr );

                        InitAppUtil.saveFile( fileStream, absFilePath );
                        // Add data to the file catalog
                        if( fileCatalog == null ) {
                            fileCatalog = getFileCatalogConnector().connect();
                        }
                        fileCatalog.catalog( absFilePath );
                        // Delete file once it is added to the catalog
                        InitAppUtil.deleteFile( absFilePath );
                        // Send data to subscribers
                        // Get current subscriptions list
                        List subscriptionList = subscriptionManager.getSubscriptionList();
                        // Iterate through subscriptions
                        if( subscriptionList != null ) {
                            for( int i = 0; i < subscriptionList.size(); i++ ) {
                                Subscription subscription =
                                        ( Subscription )subscriptionList.get( i );
                                int userId = subscription.getUserId();
                                int channelId = subscription.getChannelId();
                                User user = ( User )userManager.getUserByID( userId );
                                Channel channel = ( Channel )channelManager.getChannel( channelId );

                                
                                List dataFromChannel = dataManager.getDataFromChannel( fileCatalog,
                                        channel.getName() );


                                //List dataFromChannel = dataManager.getDataFromChannel(
                                //        channel.getName() );
                                


                                // Iterate through product list
                                for( int j = 0; j < dataFromChannel.size(); j++ ) {
                                    Data data = ( Data )dataFromChannel.get( j );
                                    String filePath = data.getPath();
                                    // Check the file in delivery register
                                    DeliveryRegisterEntry deliveryRegisterEntry =
                                         getDeliveryRegisterManager().getEntry( user.getId(),
                                            data.getId() );
                                    // Data was not found in the register
                                    if( deliveryRegisterEntry == null ) {
                                        // Post the data
                                        bfHandler.setUrl( user.getNodeAddress() );
                                        // outgoing frame header


                                        // just for the time being, assumes that user with name
                                        // "admin" exists in the system, this has to be changed later
                                        String outHdrStr = bfHandler.createBFDataHdr(
                                            BaltradFrameHandler.BF_MIME_MULTIPART,
                                            userManager.getUserByName( User.ROLE_ADMIN ).getNodeAddress(),
                                            data.getChannelName(), filePath, data.getId() );



                                        BaltradFrame bf = new BaltradFrame( outHdrStr, filePath );
                                        bfHandler.handleBF( bf );

                                        logManager.addEntry( new Date(), LogManager.MSG_INFO,
                                            "Sending data from " + data.getChannelName() + 
                                            "\n to user " + user.getName() + ": " +
                                            InitAppUtil.getRelFileName( bfHandler.getBFFileName(
                                                outHdrStr ) ) );
                                        // Data was not found in the register
                                        // add data to delivery register
                                        DeliveryRegisterEntry dre = new DeliveryRegisterEntry();
                                        dre.setUserId( user.getId() );
                                        dre.setDataId( data.getId() );
                                        getDeliveryRegisterManager().addEntry( dre );
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                logManager.addEntry( new Date(), LogManager.MSG_ERR, "Unrecognized format " +
                    "of incoming data\n from local file system" );
            }
        } catch( FileUploadException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while processing " +
                    "incoming data \nfrom local file system: \n" + e.getMessage() );
        }
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
     * Method gets reference to SubscriptionManager class object.
     *
     * @return Reference to SubscriptionManager class object
     */
    public SubscriptionManager getSubscriptionManager() { return subscriptionManager; }
    /**
     * Method sets reference to SubscriptionManager class object.
     *
     * @param subscriptionManager Reference to SubscriptionManager class object
     */
    public void setSubscriptionManager( SubscriptionManager subscriptionManager ) {
        this.subscriptionManager = subscriptionManager;
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
     * @param Reference to data channel manager object
     */
    public void setChannelManager( ChannelManager channelManager ) {
        this.channelManager = channelManager;
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
     * Method returns reference to data manager object.
     *
     * @return Reference to data manager object
     */
    public DataManager getDataManager() { return dataManager; }
    /**
     * Method sets reference to data manager object.
     *
     * @param Reference to data manager object
     */
    public void setDataManager( DataManager dataManager ) { this.dataManager = dataManager; }
    /**
     * Method gets reference to delivery register manager object.
     *
     * @return Reference to delivery register manager
     */
    public DeliveryRegisterManager getDeliveryRegisterManager() { return deliveryRegisterManager; }
    /**
     * Method sets reference to delivery register manager
     *
     * @param deliveryRegisterManager Reference to delivery register manager
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
    }

    /**
     * @return the fileCatalogConnector
     */
    public FileCatalogConnector getFileCatalogConnector() {
        return fileCatalogConnector;
    }

    /**
     * @param fileCatalogConnector the fileCatalogConnector to set
     */
    public void setFileCatalogConnector(FileCatalogConnector fileCatalogConnector) {
        this.fileCatalogConnector = fileCatalogConnector;
    }
}
//--------------------------------------------------------------------------------------------------




