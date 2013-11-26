/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.net.request.factory.RequestFactory;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.MessageSetter;
import eu.baltrad.dex.net.util.UrlValidatorUtil;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.WebValidator;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.util.CompressDataUtil;

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
import java.io.File;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;

/**
 * Controls access to data sources available at the peer node for subscription.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class DataSourceListController implements MessageSetter {
    
    /** Initial view - connect to data source*/
    private static final String DS_CONNECT_VIEW = "node_connect";
    /** Successful connection view */
    private static final String DS_CONNECTED_VIEW = "node_connected";
    /** Presents list of data sources selected for subscription */
    private static final String DS_SELECTED_VIEW = "node_datasources";
    
    /** List of available connections */
    private final static String NODES_KEY = "nodes";
    /** Data sources object used to render the view */
    private static final String DATA_SOURCES_KEY = "data_sources";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    
    /** Send key controller error */
    private static final String DS_SEND_KEY_CONTROLLER_ERROR_KEY = 
            "datasource.controller.send_key_controller_error";
    /** Send key server error */
    private static final String DS_SEND_KEY_SERVER_ERROR_KEY = 
            "datasource.controller.send_key_server_error";
    /** Send key server OK message */
    private static final String DS_SEND_KEY_SERVER_MSG_KEY = 
            "datasource.controller.send_key_server_msg";
    /** Key already exists on server */
    private static final String DS_SEND_KEY_EXISTS_MSG_KEY =
            "datasource.controller.send_key_exists";
    /** Key could not be verified by server */
    private static final String DS_SEND_KEY_UNATHORIZED_MSG_KEY =
            "datasource.controller.send_key_unauthorized";
    
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
    /** Key not approved message */ 
    private final static String DS_KEY_NOT_APPROVED = 
            "datasource.controller.key_not_approved";
    /** Unauthorized connection message */
    private final static String DS_CONNECTION_UNAUTHORIZED = 
            "datasource.controller.connection_unauthorized";
    /** Data source general connection error */
    private static final String DS_GENERIC_CONN_ERROR_KEY = 
            "datasource.controller.generic_connection_error"; 
    
    private IConfigurationManager confManager;
    private IUserManager userManager;
    private Authenticator authenticator;
    private UrlValidatorUtil urlValidator;
    private RequestFactory requestFactory;
    private IHttpClientUtil httpClient;
    private IJsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private Logger log;
    
    private Map<String, DataSource> peerDataSources;
    private String peerNodeName;
    
    protected User localNode;
    
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
                confManager.getAppConf().getKeystoreDir());
        this.httpClient = new HttpClientUtil(
                Integer.parseInt(confManager.getAppConf().getConnTimeout()), 
                Integer.parseInt(confManager.getAppConf().getSoTimeout()));
        this.localNode = new User(confManager.getAppConf().getNodeName(),
                Role.NODE, null, confManager.getAppConf().getOrgName(),
                confManager.getAppConf().getOrgUnit(),
                confManager.getAppConf().getLocality(),
                confManager.getAppConf().getState(),
                confManager.getAppConf().getCountryCode(),
                confManager.getAppConf().getNodeAddress());
        this.peerDataSources = new HashMap<String, DataSource>();
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
                return IOUtils.toString(is, "UTF-8");
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
     * Renders initial connect view.
     * @param model Model
     * @return View name
     */
    @RequestMapping("/node_connect.htm")
    public String nodeConnect(Model model) {
        model.addAttribute(NODES_KEY, userManager.loadPeerNames());
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
            @RequestParam(value="url_input", required=false) String urlInput,
            @RequestParam(value="connect", required=false) String connect,
            @RequestParam(value="send_key", required=false) String sendKey) 
    {
        initConfiguration();
        String viewName = null;
        // send key
        if (sendKey != null) {
            if (!urlValidator.validate(urlInput)) {
                setMessage(model, ERROR_MSG_KEY,
                           messages.getMessage(DS_INVALID_NODE_URL_KEY));       
            } else {
                try {
                    CompressDataUtil cdu = new CompressDataUtil(
                            confManager.getAppConf().getKeystoreDir() 
                            + File.separator + localNode.getName() + ".pub");
                    requestFactory = new DefaultRequestFactory(
                            URI.create(urlInput));
                    HttpUriRequest req = requestFactory.createPostKeyRequest(
                            localNode, cdu.zip());
                    HttpResponse res = httpClient.post(req);
                    if (res.getStatusLine().getStatusCode() 
                            == HttpServletResponse.SC_OK) {
                        String okMsg = messages
                                .getMessage(DS_SEND_KEY_SERVER_MSG_KEY);
                        setMessage(model, SUCCESS_MSG_KEY, okMsg);
                    } else if (res.getStatusLine().getStatusCode() 
                            == HttpServletResponse.SC_CONFLICT) {
                        String errorMsg = messages
                                .getMessage(DS_SEND_KEY_EXISTS_MSG_KEY);
                        setMessage(model, ERROR_MSG_KEY, errorMsg);
                    } else if (res.getStatusLine().getStatusCode() 
                            == HttpServletResponse.SC_UNAUTHORIZED) {
                        String errorMsg = messages
                                .getMessage(DS_SEND_KEY_UNATHORIZED_MSG_KEY);
                        setMessage(model, ERROR_MSG_KEY, errorMsg);
                    } else {
                        String errorMsg = messages.getMessage(
                            DS_SEND_KEY_SERVER_ERROR_KEY);
                        String errorDetails = res.getStatusLine()
                                .getReasonPhrase();
                        setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                                errorMsg, errorDetails);
                        log.error(errorMsg + ": " + errorDetails);
                    }
                } catch (Exception e) {
                    String errorMsg = messages.getMessage(
                            DS_SEND_KEY_CONTROLLER_ERROR_KEY);
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                            errorMsg, e.getMessage());
                    log.error(errorMsg + ": " + e.getMessage());
                }
            }
            viewName = DS_CONNECT_VIEW;
        }
        
        // connect to node
        if (connect != null) {
            // Validate node's URL address 
            String urlSelect = null;
            if (WebValidator.validate(nodeSelect)) {
                User user = userManager.load(nodeSelect);
                urlSelect = user.getNodeAddress();
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
                        peerNodeName = res.getFirstHeader("Node-Name")
                                .getValue();
                        model.addAttribute(PEER_NAME_KEY, peerNodeName);
                        String json = readResponse(res);
                        Set<DataSource> dataSources = jsonUtil.jsonToDataSources(json);
                        for (DataSource ds : dataSources) {
                            peerDataSources.put(ds.getName(), ds);
                        }
                        viewName = DS_CONNECTED_VIEW;
                        model.addAttribute(DATA_SOURCES_KEY, dataSources);
                    } else if (res.getStatusLine().getStatusCode() ==
                            HttpServletResponse.SC_CREATED) {
                        // user account established on server, create local 
                        // account
                        peerNodeName = res.getFirstHeader("Node-Name")
                                .getValue();
                        model.addAttribute(PEER_NAME_KEY, peerNodeName);
                        String json = readResponse(res);
                        User peer = jsonUtil.jsonToUserAccount(json);
                        if (userManager.load(peer.getName()) == null) {
                            peer.setRole(Role.PEER);
                            try {
                                userManager.store(peer);
                                log.warn("New peer account created: " + 
                                        peer.getName());
                            } catch (Exception e) {
                                throw e;
                            }   
                        }
                        viewName = DS_CONNECTED_VIEW;
                    } else if (res.getStatusLine().getStatusCode() == 
                            HttpServletResponse.SC_NOT_FOUND) {
                        viewName = DS_CONNECT_VIEW;
                        String errorMsg = messages.getMessage(
                            DS_KEY_NOT_APPROVED);
                        String errorDetails = res.getStatusLine()
                                .getReasonPhrase(); 
                        setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                                errorMsg, errorDetails);
                        log.error(errorMsg + ": " + errorDetails);
                    } else if (res.getStatusLine().getStatusCode() ==
                            HttpServletResponse.SC_UNAUTHORIZED) {
                        viewName = DS_CONNECT_VIEW;
                        String errorMsg = messages.getMessage(
                            DS_CONNECTION_UNAUTHORIZED);
                        String errorDetails = res.getStatusLine()
                                .getReasonPhrase();
                        setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                                errorMsg, errorDetails);
                        log.error(errorMsg + ": " + errorDetails);
                    } else {
                        viewName = DS_CONNECT_VIEW;
                        String errorMsg = messages.getMessage(
                            DS_SERVER_ERROR_KEY);
                        String errorDetails = res.getStatusLine()
                                .getReasonPhrase(); 
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
                    String errorMsg = messages.getMessage(
                            DS_HTTP_CONN_ERROR_KEY, new String[] {url});
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                            errorMsg, e.getMessage());
                    log.error(errorMsg + ": " + e.getMessage());
                } catch (Exception e) {
                    viewName = DS_CONNECT_VIEW;
                    String errorMsg = messages.getMessage(
                            DS_GENERIC_CONN_ERROR_KEY, new String[] {url});
                    setMessage(model, ERROR_MSG_KEY, ERROR_DETAILS_KEY,
                            errorMsg, e.getMessage());
                    log.error(errorMsg + ": " + e.getMessage());
                }
            }
        }
        model.addAttribute(NODES_KEY, userManager.loadPeerNames());
        return viewName;
    }
    
    /**
     * Allows to select data sources at a peer node.  
     * @param model Model 
     * @param selectedDataSources Data sources selected for subscription 
     * @return View name
     */
    @RequestMapping("/node_datasources.htm")
    public String selectedDataSources(HttpServletRequest request, Model model, 
                @RequestParam(value="selected_data_sources", required=false) 
                String[] selectedDataSources) {
        String viewName = null;
        if (selectedDataSources != null) {
            Set<DataSource> selectedPeerDataSources = new HashSet<DataSource>();
            for (int i = 0; i < selectedDataSources.length; i++) {
                DataSource ds = peerDataSources.get(selectedDataSources[i]);
                ds.setType(DataSource.PEER);
                selectedPeerDataSources.add(ds);
            }
            // pass session attribute to the next controller
            request.getSession().setAttribute("selected_data_sources", 
                    selectedPeerDataSources);
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
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(IConfigurationManager confManager) {
        this.confManager = confManager;
    }
    
    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
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
