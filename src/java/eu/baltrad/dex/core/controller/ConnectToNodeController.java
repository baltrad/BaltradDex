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

import eu.baltrad.dex.core.model.NodeConnection;
import eu.baltrad.dex.core.model.NodeConnectionManager;
import eu.baltrad.frame.model.BaltradFrame;
import eu.baltrad.frame.model.BaltradFrameHandler;
import eu.baltrad.dex.log.model.*;
import eu.baltrad.dex.util.MessageDigestUtil;
import eu.baltrad.dex.util.InitAppUtil;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Controller creates node connection object and sends it to the remote node.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class ConnectToNodeController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    private static final String NODE_LIST = "connection_list";
//---------------------------------------------------------------------------------------- Variables
    private FrameDispatcherController frameDispatcherController;
    private NodeConnectionManager nodeConnectionManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Cretes new connection parameters object.
     * 
     * @param request HttpServletRequest
     * @return Connection parameters object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        return new NodeConnection();
    }
    /**
     * Returns HashMap holding list of all Baltrad nodes registered in the system.
     *
     * @param request HttpServletRequest
     * @return HashMap object holding list of registered nodes
     * @throws Exception
     */
    @Override
    protected HashMap referenceData( HttpServletRequest request ) throws Exception {
        HashMap model = new HashMap();
        model.put( NODE_LIST, nodeConnectionManager.getConnections() );
        return model;
    }
    /**
     * Prepares remote data source list request. The request is send to the remote node
     * as BaltradFrame.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {
        NodeConnection formConn = ( NodeConnection )command;
        NodeConnection nodeConn = null;
        if( formConn.getConnectionName() != null ) {
            nodeConn = nodeConnectionManager.getConnection( formConn.getConnectionName() );
        } else {
            nodeConn = formConn;
        }
        nodeConn.setFullAddress( NodeConnection.HTTP_PREFIX + nodeConn.getShortAddress() +
            NodeConnection.PORT_SEPARATOR + nodeConn.getPortNumber() +
            NodeConnection.ADDRESS_SEPARATOR + NodeConnection.APP_CONTEXT +
            NodeConnection.ADDRESS_SEPARATOR + NodeConnection.ENTRY_ADDRESS );
        // prepare BaltradFrame
        BaltradFrameHandler bfHandler = new BaltradFrameHandler( nodeConn.getFullAddress() );

        // prepare data source request frame holding user name, password and node address
        // this frame will be validated and authenticated upon reception

        String hdrStr = bfHandler.createMsgHdr( BaltradFrameHandler.MIME_MULTIPART,
                MessageDigestUtil.createHash( nodeConn.getUserName() ),
                MessageDigestUtil.createHash( nodeConn.getPassword() ),
                InitAppUtil.getNodeAddress(), InitAppUtil.getNodeName(),
                /*BaltradFrameHandler.REQUEST*/BaltradFrameHandler.LEVEL_INFO.toString(),
                BaltradFrameHandler.CHNL_LIST_RQST );
        // set local user name in frame dispatcher
        frameDispatcherController.setLocalUserName( nodeConn.getUserName() );
        BaltradFrame baltradFrame = new BaltradFrame( hdrStr );
        frameDispatcherController.setBfHandler( bfHandler );
        // post remote data source request
        frameDispatcherController.doPost( request, response, baltradFrame );

        // add connection to the database
        if( frameDispatcherController.getDataSourceListing() != null &&
                frameDispatcherController.getRemoteNodeName() != null ) {
            // check if connection exists
            if( nodeConnectionManager.getConnection(
                    frameDispatcherController.getRemoteNodeName() ) == null ) {
                nodeConn.setConnectionName( frameDispatcherController.getRemoteNodeName() );
                try {
                    nodeConnectionManager.saveOrUpdate( nodeConn );
                } catch( SQLException e ) {
                    logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                            "Failed to save node connection: " + e.getMessage() ) );
                } catch( Exception e ) {
                    logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                            "Failed to save node connection: " + e.getMessage() ) );
                }
            }
        }
        return new ModelAndView( getSuccessView() );
    }
    /**
     * Gets reference to NodeConnectionManager object.
     *
     * @return Reference to NodeConnectionManager object
     */
    public NodeConnectionManager getNodeConnectionManager() { return nodeConnectionManager; }
    /**
     * Sets reference to NodeConnectionManager object.
     *
     * @param nodeManager Reference to NodeConnectionManager object
     */
    public void setNodeConnectionManager( NodeConnectionManager nodeConnectionManager ) {
        this.nodeConnectionManager = nodeConnectionManager;
    }
    /**
     * Gets reference to frame dispatcher controller object.
     *
     * @return frameDispatcherController Reference to frame dispatcher controller object
     */
    public FrameDispatcherController getFrameDispatcherController() {
        return frameDispatcherController;
    }
    /**
     * Sets reference to frame dispatcher controller object.
     *
     * @param frameDispatcherController Reference to frame dispatcher controller object
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
}
//--------------------------------------------------------------------------------------------------