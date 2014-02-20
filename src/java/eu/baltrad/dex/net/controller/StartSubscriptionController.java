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
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.IFilterManager;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Set;
import java.util.HashSet;

import java.io.IOException;

/**
 * Controls data source subscription process.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
@Controller
public class StartSubscriptionController {
    
    /** Initial view */
    private static final String SUBSCRIBE_VIEW = "subscription_start_status";
    /** Selected data sources model key */
    private static final String DS_SELECTED = "selected_data_sources";
    /** Message signer error key */
    private static final String PS_MESSAGE_SIGNER_ERROR_KEY = 
            "postsubscription.controller.message_signer_error";
    /** Subscription connection error key */
    private final static String PS_HTTP_CONN_ERROR_KEY = 
            "postsubscription.controller.http_connection_error";
    /** Peer account not found error key */
    private final static String PS_PEER_NOT_FOUND_ERROR_KEY =
            "postsubscription.controller.peer_not_found_error";
    /** Generic connection error */
    private static final String PS_GENERIC_CONN_ERROR_KEY = 
            "postsubscription.controller.generic_connection_error"; 
    /** Internal controller error key */
    private static final String PS_INTERNAL_CONTROLLER_ERROR_KEY = 
            "postsubscription.controller.internal_controller_error";
    /** Subscription server - error message */
    private static final String PS_SERVER_ERROR_KEY = 
            "postsubscription.controller.subscription_server_error";
    /** Subscription server - success message */
    private static final String PS_SERVER_SUCCESS_KEY = 
            "postsubscription.controller.subscription_server_success";
    /** Subscription server - partial subscription message */
    private static final String PS_SERVER_PARTIAL_SUBSCRIPTION = 
            "postsubscription.controller.subscription_server_partial";
    /** Peer node name key */
    private static final String PEER_NAME_KEY = "peer_name";
    
    private IConfigurationManager confManager;
    private Authenticator authenticator;
    private IHttpClientUtil httpClient;
    
    private IDataSourceManager dataSourceManager;
    private ISubscriptionManager subscriptionManager;
    private IFilterManager filterManager;
    private IUserManager userManager;
    private ModelMessageHelper messageHelper;
    //private MessageResourceUtil messages;
    private Logger log;
    
    private Logger logger = LogManager.getLogger(StartSubscriptionController.class);
    
    private ProtocolManager protocolManager = null;
    protected User localNode;
    
    
    /**
     * Default constructor.
     */
    public StartSubscriptionController() {
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
    }
    
    /**
     * Stores local subscriptions.
     * @param res Http response
     * @param dataSourceString Data sources Json string
     * @param peerName Peer node name
     * @return True if subscriptions are successfully saved 
     */
    @Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
    protected void storeLocalSubscriptions(String nodeName, Set<DataSource> dataSources, String peerName) 
        throws InternalControllerException {
      try {
        for (DataSource ds : dataSources) {
          // save peer data sources
          DataSource dataSource = dataSourceManager.load(ds.getName(), DataSource.PEER);
          User user = userManager.load(peerName);

          if (dataSource == null) {
            int id = dataSourceManager.store(ds);
            // save peer data source user (operator)
            dataSourceManager.storeUser(id, user.getId());
            
            // save peer data source filter
            createDataSourceFilter(id, ds);
          } else {
            ds.setId(dataSource.getId());
            dataSourceManager.update(ds);
          }
          // save subscriptions
          Subscription requested = createSubscriptionObject(Subscription.LOCAL, nodeName, ds.getName(), true, true);
          Subscription existing = subscriptionManager.load(Subscription.LOCAL, peerName, ds.getName());
          if (existing == null) {
            subscriptionManager.store(requested);
          } else {
            requested.setId(existing.getId());
            subscriptionManager.update(requested);
          }
        }
      } catch (Exception e) {
        logger.debug("Failed to read server response", e);
        throw new InternalControllerException("Failed to read server response");
      }
    }
    
    /**
     * Creates a data source filter if possible
     * @param id the id to store the filter with
     * @param ds the data source
     * @return true if filter was created otherwise false
     */
    protected boolean createDataSourceFilter(int id, DataSource ds) {
      String dsName = ds.getSource();
      String foName = ds.getFileObject();
      if (dsName != null && foName != null) {
        IFilter filter = dataSourceManager.createFilter(dsName, foName);
        filterManager.store(filter);
        dataSourceManager.storeFilter(id, filter.getId());
        return true;
      } else {
        log.info("Source and file object was missing in data source, could not create filter");
      }
      return false;
    }
    
    /**
     * @return the created subscription object
     */
    protected Subscription createSubscriptionObject(String type, String nodeName, String dsName, boolean active, boolean sync) {
      return new Subscription(System.currentTimeMillis(), type, nodeName, dsName, active, sync);
    }
    
    /**userManager
     * Sends subscription request to the server.
     * @param model Model
     * @param peerName Peer node name
     * @param selectedDataSources Data sources selected for subscription
     * @return View name
     */
    @RequestMapping("/subscription_start_status.htm")
    public String startSubscription(HttpServletRequest request, Model model,
            @RequestParam(value="peer_name", required=true) String peerName) {
        initConfiguration();
        @SuppressWarnings("unchecked")
        Set<DataSource> selectedPeerDataSources = (HashSet<DataSource>)request.getSession().getAttribute(DS_SELECTED);
        request.getSession().removeAttribute(DS_SELECTED);
        logger.debug("Loading user '" + peerName + "'");
        User node = userManager.load(peerName);
        
        if (node == null) {
          messageHelper.setErrorMessage(model, PS_PEER_NOT_FOUND_ERROR_KEY, new Object[]{peerName});
        } else {
          RequestFactory requestFactory = protocolManager.getFactory(node.getNodeAddress());
          HttpUriRequest req = requestFactory.createStartSubscriptionRequest(localNode, selectedPeerDataSources);
          try {
            authenticator.addCredentials(req, localNode.getName());
            HttpResponse res = httpClient.post(req);
            ResponseParser parser = protocolManager.createParser(res);
            if (parser.getStatusCode() == HttpServletResponse.SC_OK) {
              messageHelper.setSuccessMessage(model, PS_SERVER_SUCCESS_KEY, new Object[] {peerName});
              storeLocalSubscriptions(parser.getNodeName(), parser.getDataSources(), peerName);
            } else if (parser.getStatusCode() == HttpServletResponse.SC_PARTIAL_CONTENT) {
              messageHelper.setErrorMessage(model, PS_SERVER_PARTIAL_SUBSCRIPTION, new Object[] {peerName});
              storeLocalSubscriptions(parser.getNodeName(), parser.getDataSources(), peerName);
            } else {
              messageHelper.setErrorDetailsMessage(model, PS_SERVER_ERROR_KEY, parser.getReasonPhrase(), new Object[] {peerName});
            }
          } catch (KeyczarException e) { 
            messageHelper.setErrorDetailsMessage(model, PS_MESSAGE_SIGNER_ERROR_KEY, e.getMessage());
          } catch (InternalControllerException e) {
            messageHelper.setErrorDetailsMessage(model, PS_INTERNAL_CONTROLLER_ERROR_KEY, e.getMessage(), new Object[]{peerName});
          } catch (IOException e) {
            messageHelper.setErrorDetailsMessage(model, PS_HTTP_CONN_ERROR_KEY, e.getMessage(), new Object[]{peerName});
          } catch (Exception e) {
            messageHelper.setErrorDetailsMessage(model, PS_GENERIC_CONN_ERROR_KEY, e.getMessage(), new Object[]{peerName});
          }
          model.addAttribute(PEER_NAME_KEY, peerName);               
        }

      return SUBSCRIBE_VIEW;
    }
    
    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(IConfigurationManager confManager) {
        this.confManager = confManager;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }
    
    /**
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(ISubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }
    
    /**
     * @param filterManager the IFilterManager to set
     */
    public void setFilterManager(IFilterManager filterManager) {
        this.filterManager = filterManager;
    }
    
    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * @param httpClient the httpClient to set
     */
    public void setHttpClient(IHttpClientUtil httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * @param log the log to set
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * @param requestManager the request manager to set
     */
    @Autowired
    public void setProtocolManager(ProtocolManager protocolManager) {
      this.protocolManager = protocolManager;
    }

    /**
     * @param messageHelper the model message helper
     */
    @Autowired
    public void setMessageHelper(ModelMessageHelper messageHelper) {
      this.messageHelper = messageHelper;
    }
}