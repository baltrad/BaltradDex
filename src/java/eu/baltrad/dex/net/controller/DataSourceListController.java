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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.protocol.ResponseParserException;
import eu.baltrad.dex.net.util.UrlValidatorUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.CompressDataUtil;
import eu.baltrad.dex.util.WebValidator;

/**
 * Controls access to data sources available at the peer node for subscription.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class DataSourceListController {
    
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
    private ProtocolManager protocolManager = null;
    
    private IHttpClientUtil httpClient;
    private ModelMessageHelper messageHelper;
    private Logger log;
    
    private final static Logger logger = LogManager.getLogger(DataSourceListController.class);
    
    protected Map<String, DataSource> peerDataSources;
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
              messageHelper.setErrorMessage(model, DS_INVALID_NODE_URL_KEY);
            } else {
                try {
                    RequestFactory requestFactory = protocolManager.getFactory(urlInput);
                    HttpUriRequest req = requestFactory.createPostKeyRequest(localNode, createLocalPubKeyZip());
                    HttpResponse res = httpClient.post(req);
                    ResponseParser parser = protocolManager.createParser(res);
                    if (parser.getStatusCode() == HttpServletResponse.SC_OK) {
                      messageHelper.setSuccessMessage(model, DS_SEND_KEY_SERVER_MSG_KEY);
                    } else if (parser.getStatusCode() == HttpServletResponse.SC_CONFLICT) {
                      messageHelper.setErrorMessage(model, DS_SEND_KEY_EXISTS_MSG_KEY);
                    } else if (parser.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
                      messageHelper.setErrorMessage(model,DS_SEND_KEY_UNATHORIZED_MSG_KEY);                      
                    } else {
                      messageHelper.setErrorDetailsMessage(model, 
                          DS_SEND_KEY_SERVER_ERROR_KEY, 
                          parser.getReasonPhrase());
                    }
                } catch (Exception e) {
                  messageHelper.setErrorDetailsMessage(model, DS_SEND_KEY_CONTROLLER_ERROR_KEY, e.getMessage());
                  logger.error("Failure when sending key", e);
                }
            }
            viewName = DS_CONNECT_VIEW;
        }
        
        // connect to node
        if (connect != null) {
          viewName = nodeConnected_connect(model, nodeSelect, urlInput);
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
                String[] selectedDataSources,
                @RequestParam(value="peer_name", required=true) String peerName) {
        String viewName = null;
        if (selectedDataSources != null) {
          logger.debug("Got selectedDataSources " + selectedDataSources.length);
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
          logger.debug("No selected data sources, adding peerDataSources: " + peerDataSources.size());
          Set<DataSource> peerSourcesToAdd = new HashSet<DataSource>();
          for (String k : peerDataSources.keySet()) {
            peerSourcesToAdd.add(peerDataSources.get(k));
          }
          model.addAttribute(DATA_SOURCES_KEY, peerSourcesToAdd);
          viewName = DS_CONNECTED_VIEW;
        }
        model.addAttribute(PEER_NAME_KEY, peerName);
        return viewName;
    }
    
    /**
     * Handles when connect is pressed
     * @param model the model
     * @param nodeSelect the 
     * @param urlInput
     * @return
     */
    protected String nodeConnected_connect(Model model, String nodeSelect, String urlInput) {
      String viewName = DS_CONNECT_VIEW;

      //Validate node's URL address 
      String urlSelect = null;
      if (WebValidator.validate(nodeSelect)) {
        User user = userManager.load(nodeSelect);
        urlSelect = user.getNodeAddress();
      }
      String url = urlValidator.validate(urlInput) ? urlInput : urlSelect;
      if (!urlValidator.validate(url)) {
        messageHelper.setErrorMessage(model, DS_INVALID_NODE_URL_KEY);
        return DS_CONNECT_VIEW;
      }
      
      // Post request if URL was successfully validated 
      RequestFactory requestFactory = protocolManager.getFactory(url);
      HttpUriRequest req = requestFactory.createDataSourceListingRequest(localNode);

      try {
        authenticator.addCredentials(req, localNode.getName());
        HttpResponse res = httpClient.post(req);
        ResponseParser parser = protocolManager.createParser(res);

        logger.debug("Got a response message of version " + parser.getProtocolVersion());
        logger.debug("Answering node supports version " + parser.getConfiguredProtocolVersion());
        
        if (parser.getStatusCode() == HttpServletResponse.SC_OK) {
          model.addAttribute(PEER_NAME_KEY, parser.getNodeName());
          Set<DataSource> dataSources = parser.getDataSources();
          model.addAttribute(DATA_SOURCES_KEY, dataSources);
          for (DataSource ds : dataSources) {
            logger.debug("Adding DataSource: " + ds.getName() + " to peerDataSources");
            peerDataSources.put(ds.getName(), ds);
          }
          if (parser.getProtocolVersion().equals("2.0")) {
            messageHelper.setTextErrorMessage(model, "Peer node does not support protocol version > 2.0. Local node might not be able to prevent ghost subscriptions.");
          }
          viewName = DS_CONNECTED_VIEW;
        } else if (parser.getStatusCode() == HttpServletResponse.SC_CREATED) {
          // user account established on server, create local account
          model.addAttribute(PEER_NAME_KEY, parser.getNodeName());
          User peer = parser.getUserAccount();
          if (userManager.load(peer.getName()) == null) {
            peer.setRole(Role.PEER);
            userManager.store(peer);
            log.warn("New peer account created: " + peer.getName());
          }
          viewName = DS_CONNECTED_VIEW;
        } else if (parser.getStatusCode() == HttpServletResponse.SC_NOT_FOUND) {
          messageHelper.setErrorDetailsMessage(model, DS_KEY_NOT_APPROVED, parser.getReasonPhrase());
        } else if (parser.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
          messageHelper.setErrorDetailsMessage(model, DS_CONNECTION_UNAUTHORIZED, parser.getReasonPhrase());
        } else {
          messageHelper.setErrorDetailsMessage(model, DS_SERVER_ERROR_KEY, parser.getReasonPhrase());
        }
      } catch (KeyczarException e){
        messageHelper.setErrorDetailsMessage(model, DS_MESSAGE_SIGNER_ERROR_KEY, e.getMessage());
      } catch (ResponseParserException e) {
        messageHelper.setErrorDetailsMessage(model, DS_INTERNAL_CONTROLLER_ERROR_KEY, e.getMessage(), new Object[] {url});
      } catch (IOException e) {
        messageHelper.setErrorDetailsMessage(model, DS_HTTP_CONN_ERROR_KEY, e.getMessage());
      } catch (Exception e) {
        messageHelper.setErrorDetailsMessage(model, DS_GENERIC_CONN_ERROR_KEY, e.getMessage(), new Object[] {url}); 
      }
      return viewName;
    }
    
    /**
     * Packs the local public key
     * @return the public key 
     */
    protected byte[] createLocalPubKeyZip() {
      return new CompressDataUtil(
          confManager.getAppConf().getKeystoreDir() 
          + File.separator + localNode.getName() + ".pub").zip();
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

    @Autowired
    public void setModelMessageHelper(ModelMessageHelper messageHelper) {
      this.messageHelper = messageHelper;
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
 
    /**
     * @param protocolManager the protocol manager to use
     */
    @Autowired
    public void setProtocolManager(ProtocolManager protocolManager) {
      this.protocolManager = protocolManager;
    }    
}
