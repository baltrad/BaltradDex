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

import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.NodeAddress;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Implements remote data source list controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class RemoteDataSourceController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    /** Remote data source key */
    private static final String DATA_SOURCES_KEY = "remoteDataSources";
    private static final String SELECTED_DATA_SOURCES_KEY = "selectedDataSources";
    private static final String SUBSCRIBED_DATA_SOURCES_KEY = "subscribedDataSources";
    private static final String SENDER_NODE_NAME_KEY = "sender_node_name";
    /** Remote data sources view */
    private static final String DATA_SOURCES_VIEW = "dsConnect";
    private static final String SELECTED_DATA_SOURCES_VIEW = "dsToConnect";
    private static final String SUBSCRIBED_DATA_SOURCES_VIEW = "dsSubscribed";
    /** Remove data source message key */
    private static final String DS_SUBSCRIBE_MSG_KEY = "message";
    /** Remove data source error key */
    private static final String DS_SUBSCRIBE_ERROR_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    private FrameDispatcherController frameDispatcherController;
    private SubscriptionManager subscriptionManager;
    private Logger log;
    private InitAppUtil init;
    // remote data source object list
    private List remoteDataSources;
    // selected data source list
    private List selectedDataSources;
    // subscribed data sources
    private List subscribedDataSources;
    // remote node name
    private String senderNodeName;
     /** Sender communication scheme */
    private String senderScheme;
    /** Sender host address */
    private String senderHostAddress;
    /** Sender port number */
    private int senderPort;
    /** Sender application context */
    private String senderAppCtx;
    /** Sender entry point address */
    private String senderEntryAddress;
    // user name
    private String userName;
    // user's password
    private String password;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public RemoteDataSourceController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        this.init = InitAppUtil.getInstance();
    }
     /**
     * Creates list of remote data source available for a given node.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView object holding list of data sources available at a given node
     */
    public ModelAndView dsConnect( HttpServletRequest request,
            HttpServletResponse response ) {
        // set remote data source list
        setRemoteDataSources( frameDispatcherController.getDataSourceListing() );
        // set sender node name
        setSenderNodeName( frameDispatcherController.getRemNodeName() );
        // set sender node address
        setSenderScheme( frameDispatcherController.getRemScheme() );
        setSenderHostAddress( frameDispatcherController.getRemHostAddress() );
        setSenderPort( frameDispatcherController.getRemPort() );
        setSenderAppCtx( frameDispatcherController.getRemAppCtx() );
        setSenderEntryAddress( frameDispatcherController.getRemEntryAddress() );
        // reset remote data source list, node name and address stored in FrameDispatcherController
        frameDispatcherController.resetDataSourceListing();
        frameDispatcherController.resetRemNodeName();
        frameDispatcherController.resetRemScheme();
        frameDispatcherController.resetRemHostAddress();
        frameDispatcherController.resetRemPort();
        frameDispatcherController.resetRemAppCtx();
        frameDispatcherController.resetRemEntryAddress();
        // set sender node name
        request.setAttribute( SENDER_NODE_NAME_KEY, getSenderNodeName() );
        return new ModelAndView( DATA_SOURCES_VIEW, DATA_SOURCES_KEY, getRemoteDataSources() );
    }
    /**
     * Creates list of data sources selected by the user
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView object holding list of data sources selected by the user
     */
    public ModelAndView dsToConnect( HttpServletRequest request,
            HttpServletResponse response ) {
        // get selected sources based on the checkbox values
        String[] selDataSourceNames = request.getParameterValues( SELECTED_DATA_SOURCES_KEY );
        // check if selected channel list is not empty
        if( selDataSourceNames != null ) {
            selectedDataSources = new ArrayList();
            for( int i = 0; i < selDataSourceNames.length; i++ ) {
                for( int j = 0; j < getRemoteDataSources().size(); j++ ) {
                    DataSource ds = ( DataSource )getRemoteDataSources().get( j );
                    if( ds.getName().equals( selDataSourceNames[ i ] ) ) {
                        selectedDataSources.add( ds );
                    }
                }
            }
        }
        // set sender node name
        request.setAttribute( SENDER_NODE_NAME_KEY, getSenderNodeName() );
        return new ModelAndView( SELECTED_DATA_SOURCES_VIEW, SELECTED_DATA_SOURCES_KEY,
                selectedDataSources );
    }
    /**
     * Submits data source subscription request by posting subscription list on remote node.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView
     */
    public ModelAndView dsSubscribed( HttpServletRequest request, HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            File tempFile = InitAppUtil.createTempFile( new File( init.getWorkDirPath() ) );
            InitAppUtil.writeObjectToStream( getSelectedDataSources(), tempFile );
            // prepare frame
            BaltradFrameHandler bfHandler = new BaltradFrameHandler( getSenderScheme(),
                    getSenderHostAddress(), getSenderPort(), getSenderAppCtx(),
                    getSenderEntryAddress(), init.getConfiguration().getSoTimeout(),
                    init.getConfiguration().getConnTimeout() );
            //
            String hdrStr = BaltradFrameHandler.createObjectHdr( BaltradFrameHandler.MIME_MULTIPART,
                init.getConfiguration().getScheme(), init.getConfiguration().getHostAddress(),
                init.getConfiguration().getPort(), init.getConfiguration().getAppCtx(), 
                init.getConfiguration().getEntryAddress(), init.getConfiguration().getNodeName(),
                frameDispatcherController.getLocUsrName(), BaltradFrameHandler.CHNL_SBN_RQST,
                tempFile.getAbsolutePath() );
            //
            BaltradFrame baltradFrame = new BaltradFrame( NodeAddress.ADDR_SEPARATOR +
                    getSenderAppCtx() + NodeAddress.ADDR_SEPARATOR + getSenderEntryAddress(),
                    hdrStr, tempFile );
            // handle the frame
            frameDispatcherController.setBfHandler( bfHandler );
            frameDispatcherController.doPost( request, response, baltradFrame );
            // delete temporary file
            InitAppUtil.deleteFile( tempFile );
            // check subscription operation status
            setSubscribedDataSources( frameDispatcherController.getConfirmedSubscriptions() );
            // reset confirmed subscriptions list stored in FrameDispatcherController
            frameDispatcherController.resetConfirmedSubscriptions();
            // once subscription is confirmed, add requested data source to local subscription list
            for( int i = 0; i < getSubscribedDataSources().size(); i++ ) {
                DataSource dataSource = ( DataSource )getSubscribedDataSources().get( i );
                if( subscriptionManager.getSubscription( dataSource.getName(),
                        Subscription.LOCAL_SUBSCRIPTION ) != null ) {
                    log.warn( "You have already subscribed to " + dataSource.getName() );
                } else {
                    // add local subscription
                    Subscription subs = new Subscription( System.currentTimeMillis(),
                        frameDispatcherController.getLocUsrName(), dataSource.getName(),
                        getSenderNodeName(), Subscription.LOCAL_SUBSCRIPTION, true, true,
                        getSenderScheme(), getSenderHostAddress(), getSenderPort(),
                        getSenderAppCtx(), getSenderEntryAddress() );
                    subscriptionManager.saveSubscription( subs );
                }
            }
            String msg = "Subscription request was successfully completed";
            modelAndView.addObject( DS_SUBSCRIBE_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to complete subscription request";
            modelAndView.addObject( DS_SUBSCRIBE_ERROR_KEY, msg );
            log.error( msg, e );
        }
        modelAndView.setViewName( SUBSCRIBED_DATA_SOURCES_VIEW );
        return modelAndView;
    }
    /**
     * Gets the list of data sources available for a given remote node.
     *
     * @return List of data sources available for a given remote node
     */
    public List getRemoteDataSources() { return remoteDataSources; }
    /**
     * Sets the list of data sources available for a given remote node.
     *
     * @param remoteDataSources List of data sources available for a given remote node
     */
    public void setRemoteDataSources( List remoteDataSources ) {
        this.remoteDataSources = remoteDataSources;
    }
    /**
     * Gets the list of data sources selected by the user.
     *
     * @return List of data sources selected by the user
     */
    public List getSelectedDataSources() { return selectedDataSources; }
    /**
     * Sets the list of data sources selected by the user.
     *
     * @param selectedDataSources List of data sources selected by the user
     */
    public void setSelectedDataSources( List selectedDataSources ) {
        this.selectedDataSources = selectedDataSources;
    }
    /**
     * Resets the list of selected data sources
     */
    public void resetSelectedDataSources() { this.selectedDataSources = null; }
    /**
     * Gets the list of subscribed data sources.
     *
     * @return List of subscribed data sources
     */
    public List getSubscribedDataSources() { return subscribedDataSources; }
    /**
     * Sets the list of subscribed data sources.
     *
     * @param subscribedDataSources List of subscribed data sources
     */
    public void setSubscribedDataSources( List subscribedDataSources ) {
        this.subscribedDataSources = subscribedDataSources;
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
     * Get sender's communication scheme.
     *
     * @return senderScheme Sender's communication scheme
     */
    public String getSenderScheme() { return senderScheme; }
    /**
     * Set sender's communication scheme
     *
     * @param senderScheme Sender's communication scheme to set
     */
    public void setSenderScheme( String senderScheme ) { this.senderScheme = senderScheme; }
    /**
     * Get sender's host address.
     *
     * @return senderHostAddress Sender's host address
     */
    public String getSenderHostAddress() { return senderHostAddress; }
    /**
     * Set sender's host address.
     *
     * @param senderHostAddress Sender's host address to set
     */
    public void setSenderHostAddress( String senderHostAddress ) {
        this.senderHostAddress = senderHostAddress;
    }
    /**
     * Get sender's port number.
     *
     * @return senderPort Sender's port number
     */
    public int getSenderPort() { return senderPort; }
    /**
     * Set sender's port number.
     *
     * @param senderPort Remote node's port number to set
     */
    public void setSenderPort(int senderPort) { this.senderPort = senderPort; }
    /**
     * Get sender's application context.
     *
     * @return senderAppCtx Sender's application context
     */
    public String getSenderAppCtx() { return senderAppCtx; }
    /**
     * Set sender's application context.
     *
     * @param senderAppCtx Sender's application context to set
     */
    public void setSenderAppCtx( String senderAppCtx ) { this.senderAppCtx = senderAppCtx; }
    /**
     * Get sender's entry point address.
     *
     * @return senderEntryAddress Sender's entry point address
     */
    public String getSenderEntryAddress() { return senderEntryAddress; }
    /**
     * Set sender's entry point address.
     *
     * @param senderEntryAddress Sender's entry point address to set.
     */
    public void setSenderEntryAddress( String senderEntryAddress ) {
        this.senderEntryAddress = senderEntryAddress;
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
}
//--------------------------------------------------------------------------------------------------