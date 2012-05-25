/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.net.model.*;
import eu.baltrad.dex.log.model.MessageLogger;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Controls access to data sources available at the peer node for subscription.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class DataSourceListController implements MessageSetter {
    
    /** Initial view - connect to data source*/
    private static final String DS_CONNECT_VIEW = "dsconnect";
    /** Successful connection view */
    private static final String DS_CONNECTED_VIEW = "dsconnected";
    /** Presents list of data sources selected for subscription */
    private static final String DS_SELECTED_VIEW = "dsselected";
    
    /** List of available connections */
    private final static String CONNECTIONS_KEY = "connections";
    /** Data sources object used to render the view */
    private static final String DATA_SOURCES_KEY = "data_sources";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    
    /** Invalid URL address message key */
    private static final String DS_INVALID_NODE_URL_KEY = 
            "datasource.controller.invalid_node_url";
    /** Internal controller error message key */
    private static final String DS_INTERNAL_CONTROLLER_ERROR_KEY = 
            "datasource.controller.internal_controller_error";
    /** Data source server error key */
    private static final String DS_INTERNAL_SERVER_ERROR_KEY = 
            "datasource.controller.internal_server_error";
    /** Data source connection error key */
    private final static String DS_HTTP_CONN_ERROR_KEY = 
            "datasource.controller.http_connection_error";        
    /** Data source general connection error */
    private static final String DS_GENERIC_CONN_ERROR_KEY = 
            "datasource.controller.generic_connection_error"; 
    
    private INodeConnectionManager nodeConnectionManager; 
    private Authenticator authenticator;
    private UrlValidatorUtil urlValidator;
    private RequestFactory requestFactory;
    private IHttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private Logger log;
    private String nodeName;
    private String nodeAddress;
    
    /** These variables are shared between methods */
    private String peerNodeName;
    private String peerNodeAddress;
    private Set<DataSource> peerDataSources;
    
    /**
     * Constructor.
     * @param nodeName Local node name
     * @param nodeAddress Local node address
     */
    public DataSourceListController(String nodeName, String nodeAddress) {
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
    }
    
    /**
     * Default constructor.
     */
    public DataSourceListController() {
        this.authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir(),
                InitAppUtil.getConf().getNodeName());
        this.httpClient = new HttpClientUtil(
                InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
        this.nodeName = InitAppUtil.getConf().getNodeName();
        this.nodeAddress = InitAppUtil.getConf().getNodeAddress();
        this.log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
    }
    
    /**
     * Sets message.
     * @param model Model
     * @param messageKey Message key 
     * @param message Message 
     */
    public void setMessage(Model model, String messageKey, String message) {
        model.addAttribute(messageKey, message);
    }
    
    /**
     * Sets detailed message.
     * @param model Model
     * @param messageKey Message key
     * @param detailsKey Details key
     * @param message Message
     * @param details Detailed message
     */
    public void setMessage(Model model, String messageKey, String detailsKey,
            String message, String details) 
    {
        model.addAttribute(messageKey, message);
        model.addAttribute(detailsKey, details);
    }
    
    /**
     * Reads data sources from http response.
     * @param response Http response
     * @return Data source string
     * @throws IOException 
     */
    private Set<DataSource> readDataSources(HttpResponse response) 
            throws InternalControllerException {
        try {
            InputStream is = null;
            try {
                is = response.getEntity().getContent();
                return jsonUtil.jsonToDataSources(IOUtils.toString(is));
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new InternalControllerException(e.getMessage());
        } catch (RuntimeException e) {
            throw new InternalControllerException(e.getMessage());
        }
    }
    
    /**
     * Renders initial connect view.
     * @param model Model
     * @return View name
     */
    @RequestMapping("/dsconnect.htm")
    public String dsConnect(Model model) { 
        model.addAttribute(CONNECTIONS_KEY, nodeConnectionManager.get());
        return DS_CONNECT_VIEW;
    }
    
    /**
     * Sends data source listing request to the server.
     * @param model Model
     * @param nodeSelect Node name selected from the drop-down list
     * @param urlInput URL typed in the text box
     * @return View name
     */
    @RequestMapping("/dsconnected.htm")
    public String dsConnected(Model model, 
            @RequestParam(value="node_select", required=false) String nodeSelect,
            @RequestParam(value="url_input", required=false) String urlInput) 
    {
        String viewName = null;
        // Validate node's URL address 
        String urlSelect = null;
        if (InitAppUtil.validate(nodeSelect)) {
            NodeConnection conn = nodeConnectionManager.get(nodeSelect);
            urlSelect = conn.getNodeAddress();
        }
        String url = urlValidator.validate(urlInput) ? urlInput : urlSelect;
        if (!urlValidator.validate(url)) {
            setMessage(model, ERROR_MSG_KEY,
                       messages.getMessage(DS_INVALID_NODE_URL_KEY));
            viewName = DS_CONNECT_VIEW;
        } else {
            // Post request if URL was successfully validated 
            requestFactory = new DefaultRequestFactory(URI.create(url));
            HttpUriRequest req = requestFactory
                .createGetDataSourceListingRequest(nodeName, nodeAddress);
            authenticator.addCredentials(req);
            try {
                HttpResponse res = httpClient.post(req);
                // Server reponse is OK, process data source list 
                if (res.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_OK) {
                    // Make peer node name available for other methods
                    peerNodeName = res.getFirstHeader("Node-Name").getValue();
                    peerNodeAddress = 
                            res.getFirstHeader("Node-Address").getValue();
                    model.addAttribute(PEER_NAME_KEY, peerNodeName);
                    // Add connection to the list
                    if (nodeConnectionManager.get(peerNodeName) == null) {
                        NodeConnection conn = new NodeConnection(peerNodeName, 
                                peerNodeAddress);
                        nodeConnectionManager.saveOrUpdate(conn);
                    }
                    // Make data sources available for other methods
                    peerDataSources = readDataSources(res);
                    viewName = DS_CONNECTED_VIEW;
                    model.addAttribute(DATA_SOURCES_KEY, peerDataSources);
                // Server failed to respond, retrieve error message      
                } else {
                    viewName = DS_CONNECT_VIEW;
                    String errorMsg = messages.getMessage(
                        DS_INTERNAL_SERVER_ERROR_KEY);
                    String errorDetails = res.getStatusLine().getReasonPhrase(); 
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                            errorMsg, errorDetails);
                    log.error(errorMsg + ": " + errorDetails);
                }
            } catch (InternalControllerException e) {
                 viewName = DS_CONNECT_VIEW;
                 String errorMsg = messages.getMessage(
                     DS_INTERNAL_CONTROLLER_ERROR_KEY, 
                     new String[] {peerNodeName});
                 String errorDetails = e.getMessage();
                 setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, errorDetails);
                 log.error(errorMsg + ": " + errorDetails);
            } catch (IOException e) {
                viewName = DS_CONNECT_VIEW;
                String errorMsg = messages.getMessage(DS_HTTP_CONN_ERROR_KEY,
                        new String[] {url});
                String errorDetails = e.getMessage();
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, errorDetails);
                log.error(errorMsg + ": " + errorDetails);
            } catch (Exception e) {
                viewName = DS_CONNECT_VIEW;
                String errorMsg = messages.getMessage(DS_GENERIC_CONN_ERROR_KEY,
                        new String[] {url});
                String errorDetails = e.getMessage();
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, errorDetails);
                log.error(errorMsg + ": " + errorDetails);
            }
        }
        model.addAttribute(CONNECTIONS_KEY, nodeConnectionManager.get());
        return viewName;
    }
    
    /**
     * Allows to select data sources at a peer node.  
     * @param model Model 
     * @param selectedDataSources Data sources selected for subscription 
     * @return View name
     */
    @RequestMapping("/dsselected.htm")
    public String dsSelected(Model model, 
                @RequestParam(value="selected_data_sources", required=false) 
                String[] selectedDataSources) {
        String viewName = null;
        if (selectedDataSources != null) {
            Set<DataSource> selectedPeerDataSources = new HashSet<DataSource>();
            for (int i = 0; i < selectedDataSources.length; i++) {
                String[] parms = selectedDataSources[i].split("_");
                selectedPeerDataSources.add(new DataSource(
                        Integer.parseInt(parms[0]), parms[1], parms[2]));
            }
            model.addAttribute(DATA_SOURCES_KEY, selectedPeerDataSources);
            viewName = DS_SELECTED_VIEW;
        } else {
            model.addAttribute(DATA_SOURCES_KEY, peerDataSources);
            viewName = DS_CONNECTED_VIEW;
        }
        model.addAttribute(PEER_NAME_KEY, peerNodeName);
        return viewName;
    }
    
     /**
     * @param nodeConnectionManager the nodeConnectionManager to set
     */
    @Autowired
    public void setNodeConnectionManager(
            INodeConnectionManager nodeConnectionManager) {
        this.nodeConnectionManager = nodeConnectionManager;
    }

    /**
     * @param urlValidator the urlValidator to set
     */
    @Autowired
    public void setUrlValidator(UrlValidatorUtil urlValidator) {
        this.urlValidator = urlValidator;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
    /**
     * @param jsonUtil the jsonUtil to set
     */
    @Autowired
    public void setJsonUtil(IJsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * @param httpClient the httpClient to set
     */
    public void setHttpClient(IHttpClientUtil httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }
    
}
