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

import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.net.request.factory.RequestFactory;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.MessageSetter;
import eu.baltrad.dex.net.model.impl.Node;
import eu.baltrad.dex.net.util.UrlValidatorUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.manager.INodeManager;
import eu.baltrad.dex.user.manager.IAccountManager;

import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
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
    private static final String DS_CONNECT_VIEW = "connect_to_node";
    /** Successful connection view */
    private static final String DS_CONNECTED_VIEW = "node_connected";
    /** Presents list of data sources selected for subscription */
    private static final String DS_SELECTED_VIEW = "selected_datasource";
    
    /** List of available connections */
    private final static String NODES_KEY = "nodes";
    /** Data sources object used to render the view */
    private static final String DATA_SOURCES_KEY = "data_sources";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    
    /** Message signer error key */
    private static final String DS_MESSAGE_SIGNER_ERROR_KEY = 
            "datasource.controller.message_signer_error";
    /** Invalid URL address message key */
    private static final String DS_INVALID_NODE_URL_KEY = 
            "datasource.controller.invalid_node_url";
    /** Internal controller error message key */
    private static final String DS_INTERNAL_CONTROLLER_ERROR_KEY = 
            "datasource.controller.internal_controller_error";
    /** Data source server error key */
    private static final String DS_SERVER_ERROR_KEY = 
            "datasource.controller.server_error";
    /** Data source connection error key */
    private final static String DS_HTTP_CONN_ERROR_KEY = 
            "datasource.controller.http_connection_error";        
    /** Data source general connection error */
    private static final String DS_GENERIC_CONN_ERROR_KEY = 
            "datasource.controller.generic_connection_error"; 
    
    private IAccountManager accountManager;
    private INodeManager nodeManager; 
    private Authenticator authenticator;
    private UrlValidatorUtil urlValidator;
    private RequestFactory requestFactory;
    private IHttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected Account localNode;
    
    private String peerNodeName;
    private Set<DataSource> peerDataSources;
    
    /**
     * Default constructor.
     */
    public DataSourceListController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Initializes controller with current configuration
     */
    protected void initConfiguration() {
        this.authenticator = new KeyczarAuthenticator(
                InitAppUtil.getConf().getKeystoreDir());
        this.httpClient = new HttpClientUtil(
                InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
        this.localNode = new Account(InitAppUtil.getConf().getNodeName(),
                InitAppUtil.getConf().getNodeAddress(),
                InitAppUtil.getConf().getOrgName(),
                InitAppUtil.getConf().getOrgUnit(),
                InitAppUtil.getConf().getLocality(),
                InitAppUtil.getConf().getState(),
                InitAppUtil.getConf().getCountryCode());
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
     * Read http response body
     * @param response Http response
     * @return Response body
     * @throws InternalControllerException 
     */
    protected String readResponse(HttpResponse response) 
            throws InternalControllerException {
        try {
            InputStream is = null;
            try {
                is = response.getEntity().getContent();
                return IOUtils.toString(is);
            } finally {
                is.close();
            }
        } catch (IOException e) {            
            throw new InternalControllerException("Failed to read server "
                    + "response");
        } catch (RuntimeException e) {
            throw new InternalControllerException("Failed to read server "
                    + "response");
        }
    }
    
    /**
     * Get peer node's list.
     * @return Peer node's list
     */
    private List<Node> getPeerNodes() {
        List<Node> peers = new ArrayList<Node>();
        for (Node node : nodeManager.load()) {
            if (!node.getAddress().equals(Node.LOCAL_NODE_ADDRESS)) {
                peers.add(node);
            }
        }
        return peers;
    }
    
    /**
     * Renders initial connect view.
     * @param model Model
     * @return View name
     */
    @RequestMapping("/connect_to_node.htm")
    public String connect2Node(Model model) {
        model.addAttribute(NODES_KEY, getPeerNodes());
        return DS_CONNECT_VIEW;
    }
    
    /**
     * Sends data source listing request to the server.
     * @param model Model
     * @param nodeSelect Node name selected from the drop-down list
     * @param urlInput URL typed in the text box
     * @return View name
     */
    @RequestMapping("/node_connected.htm")
    public String nodeConnected(Model model, 
            @RequestParam(value="node_select", required=false) String nodeSelect,
            @RequestParam(value="url_input", required=false) String urlInput) 
    {
        initConfiguration();
        String viewName = null;
        // Validate node's URL address 
        String urlSelect = null;
        if (InitAppUtil.validate(nodeSelect)) {
            Node node = nodeManager.load(nodeSelect);
            urlSelect = node.getAddress();
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
                .createDataSourceListingRequest(localNode);
            try {
                authenticator.addCredentials(req, localNode.getName());
                HttpResponse res = httpClient.post(req);
                if (res.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_OK) {
                    // alright, read data sources
                    peerNodeName = res.getFirstHeader("Node-Name").getValue();
                    model.addAttribute(PEER_NAME_KEY, peerNodeName);
                    String json = readResponse(res);
                    peerDataSources = jsonUtil.jsonToDataSources(json);
                    viewName = DS_CONNECTED_VIEW;
                    model.addAttribute(DATA_SOURCES_KEY, peerDataSources);
                 
                } else if (res.getStatusLine().getStatusCode() ==
                        HttpServletResponse.SC_CREATED) {
                    // user account established on server, create local account
                    peerNodeName = res.getFirstHeader("Node-Name").getValue();
                    model.addAttribute(PEER_NAME_KEY, peerNodeName);
                    String json = readResponse(res);
                    Account peer = jsonUtil.jsonToUserAccount(json);
                    if (accountManager.load(peer.getName()) == null) {
                        peer.setRoleName(Role.PEER);
                        try {
                            accountManager.store(peer);
                            log.warn("New peer account created: " + 
                                    peer.getName());
                        } catch (Exception e) {
                            throw e;
                        }   
                    }       
                    viewName = DS_CONNECTED_VIEW;        
                    
                } else {
                    // server error     
                    viewName = DS_CONNECT_VIEW;
                    String errorMsg = messages.getMessage(
                        DS_SERVER_ERROR_KEY);
                    String errorDetails = res.getStatusLine().getReasonPhrase(); 
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                            errorMsg, errorDetails);
                    log.error(errorMsg + ": " + errorDetails);
                }
            } catch (KeyczarException e){ 
                viewName = DS_CONNECT_VIEW;
                String errorMsg = messages.getMessage(
                        DS_MESSAGE_SIGNER_ERROR_KEY); 
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            } catch (InternalControllerException e) {
                 viewName = DS_CONNECT_VIEW;
                 String errorMsg = messages.getMessage(
                     DS_INTERNAL_CONTROLLER_ERROR_KEY, 
                     new String[] {peerNodeName});
                 setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, e.getMessage());
                 log.error(errorMsg + ": " + e.getMessage());
            } catch (IOException e) {
                viewName = DS_CONNECT_VIEW;
                String errorMsg = messages.getMessage(DS_HTTP_CONN_ERROR_KEY,
                        new String[] {url});
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            } catch (Exception e) {
                viewName = DS_CONNECT_VIEW;
                String errorMsg = messages.getMessage(DS_GENERIC_CONN_ERROR_KEY,
                        new String[] {url});
                setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                        errorMsg, e.getMessage());
                log.error(errorMsg + ": " + e.getMessage());
            }
        }
        model.addAttribute(NODES_KEY, getPeerNodes());
        return viewName;
    }
    
    /**
     * Allows to select data sources at a peer node.  
     * @param model Model 
     * @param selectedDataSources Data sources selected for subscription 
     * @return View name
     */
    @RequestMapping("/selected_datasource.htm")
    public String selectedDataSources(Model model, 
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
     * @param accountManager the accountManager to set
     */
    @Autowired
    public void setAccountManager(IAccountManager accountManager) {
        this.accountManager = accountManager;
    }
    
    /**
     * @param nodeConnectionManager the nodeConnectionManager to set
     */
    @Autowired
    public void setNodeManager(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
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
