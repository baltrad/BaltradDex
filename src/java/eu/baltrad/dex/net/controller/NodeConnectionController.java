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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.model.NodeConnectionManager;
import eu.baltrad.dex.net.model.NodeConnection;
import eu.baltrad.dex.log.model.MessageLogger;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

/**
 * Multi action controller handling node connection managing.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class NodeConnectionController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    // model keys
    private static final String SHOW_CONN_MODEL = "node_connections";
    private static final String SHOW_SEL_CONN_MODEL = "selected_node_connections";
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    // view names
    private static final String SHOW_CONN_VIEW = "removeNodeConnections";
    private static final String SHOW_SEL_CONN_VIEW = "nodeConnectionsToRemove";
    private static final String SHOW_REM_CONN_VIEW = "nodeConnectionsRemovalStatus";
//---------------------------------------------------------------------------------------- Variables
    private NodeConnectionManager nodeConnectionManager;
    private Logger log;
    // selected node connections
    private List< NodeConnection > selectedConns;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public NodeConnectionController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Shows all registered node connections.
     *
     * @param request Http request
     * @param response Http response
     * @return ModelAndView object containing list of all registered node connections
     */
    public ModelAndView removeNodeConnections( HttpServletRequest request,
            HttpServletResponse response ) {
        List nodeConnections = nodeConnectionManager.get();
        return new ModelAndView( SHOW_CONN_VIEW, SHOW_CONN_MODEL, nodeConnections );
    }
    /**
     * Shows node connections selected for removal.
     *
     * @param request Http request
     * @param response Http response
     * @return ModelAndView object containing list of node connections selected for removal
     */
    public ModelAndView nodeConnectionsToRemove( HttpServletRequest request,
            HttpServletResponse response ) {
        ModelAndView modelAndView = null;
        String[] connectionIds = request.getParameterValues( SHOW_SEL_CONN_MODEL );
        if( connectionIds != null ) {
            List< NodeConnection > selConns = new ArrayList< NodeConnection >();
            for( int i = 0; i < connectionIds.length; i++ ) {
                selConns.add( nodeConnectionManager.get(Integer.parseInt(connectionIds[i])));
            }
            // set list of node connections selected for removal
            setSelectedConns( selConns );
            modelAndView = new ModelAndView( SHOW_SEL_CONN_VIEW, SHOW_SEL_CONN_MODEL, selConns );
        } else {
            List connections = nodeConnectionManager.get();
            modelAndView = new ModelAndView( SHOW_CONN_VIEW, SHOW_CONN_MODEL, connections );
        }
        return modelAndView;
    }
    /**
     * Removes node connections selected by the user.
     *
     * @param request Http request
     * @param response Http response
     * @return ModelAndView object containing error messages.
     */
    public ModelAndView nodeConnectionsRemovalStatus( HttpServletRequest request,
            HttpServletResponse response ) {
        try {
            for( int i = 0; i < getSelectedConns().size(); i++ ) {
                nodeConnectionManager.delete(getSelectedConns().get( i ).getId());
            }
            String msg = "Selected node connections successfully removed";
            request.getSession().setAttribute( OK_MSG_KEY, msg );
            log.warn( msg );
        } catch( Exception e ) {
            String msg = "Failed to remove selected node connections";
            request.getSession().setAttribute( ERROR_MSG_KEY, msg );
            log.error( msg, e );
        }
        return new ModelAndView( SHOW_REM_CONN_VIEW );
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
     * Gets list of node connections selected for removal.
     *
     * @return List of node connections selected for removal
     */
    public List<NodeConnection> getSelectedConns() { return selectedConns; }
    /**
     * Sets list of node connections selected for removal.
     *
     * @param selectedConns List of node connections selected for removal
     */
    public void setSelectedConns( List<NodeConnection> selectedConns ) {
        this.selectedConns = selectedConns;
    }
}
//--------------------------------------------------------------------------------------------------