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

import eu.baltrad.frame.model.*;
import static eu.baltrad.frame.model.Protocol.*;
import eu.baltrad.dex.util.InitAppUtil;
import static eu.baltrad.dex.util.InitAppUtil.validate;
import static eu.baltrad.dex.util.InitAppUtil.deleteFile;
import eu.baltrad.dex.subscription.model.Subscription;
import eu.baltrad.dex.subscription.model.SubscriptionManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.util.WebValidator;
import eu.baltrad.dex.net.model.NodeConnectionManager;
import eu.baltrad.dex.net.model.NodeConnection;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.io.File;

/**
 * Implements remote data source list controller.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class RemoteDataSourceController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    private static final String CONNECT_TO_NODE_VIEW = "connectToNode";
    private static final String CONNECTIONS_KEY = "connections";
    
    private static final String SEL_ADDR_PARAM = "selectAddress";
    private static final String ENTER_ADDR_PARAM = "enterAddress";
    
    private static final String SEL_ADDR_ERROR_KEY = "selectAddressError";
    private static final String SEL_ADDR_ERROR_MSG = "Select connection from the list";
    
    private static final String ENTER_ADDR_ERROR_KEY = "enterAddressError";
    private static final String ENTER_ADDR_ERROR_MSG = "Enter new connection address";
    
    private static final String ERROR_MSG_KEY = "errorMsg";
    private static final String CONN_UNAUTHORIZED_MSG = "You have successfully connected to the " +
        " remote node, but it seems that your certificate awaits authentication or has already"
            + " been submitted. Ask remote node's administrator to add your certificate to the " +
            "trusted keystore or try to select existing connection from the list.";
    private static final String DS_LIST_EMPTY_MSG = "Data source list is empty. Ask remote node's" +
            " administrator to make data sources available for you.";
    private static final String DS_LIST_UNAUTHORIZED_MSG = "Failed to authenticate incoming " +
            "data source listing. Contact remote node's administrator if this problem persists.";
     private static final String DS_CONFIRM_EMPTY_MSG = "Remote node failed to confirm " +
             "your subscriptions request.";
    private static final String DS_CONFIRM_UNAUTHORIZED_MSG = "Failed to authenticate data source" +
            " subscription confirmation. Contact remote node's administrator if this problem " +
            "persists.";
    private static final String SUB_REQUEST_UNAUTHORIZED_MSG = "Remote node failed to " +
            "authenticate your subscription request";
    private static final String DS_SELECTION_EMPTY_MSG = "No data sources have been selected. " +
             "Click OK to go back to node connection page.";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Failed to access remote node due to " +
        "internal server error. In case the problem persists, contact remote node's administrator.";
    private static final String CONN_FAILURE_MSG = "Failed to connect to the remote node. " +
        " Check node address or contact remote node administrator";
    private static final String SUB_REQUEST_OK = "Subscription request was successfully completed";
    private static final String REMOTE_NODE_NAME_KEY = "remoteNodeName";    
    private static final String DATA_SOURCES_KEY = "remoteDataSources";
    private static final String SELECTED_DATA_SOURCES_KEY = "selectedDataSources";
    /** Remote data sources view */
    private static final String DATA_SOURCES_VIEW = "dsConnect";
    private static final String SELECTED_DATA_SOURCES_VIEW = "dsToConnect";
    private static final String SUBSCRIBED_DATA_SOURCES_VIEW = "dsSubscribed";
    /** Remove data source message key */
    private static final String MSG_KEY = "message";
    /** Remove data source error key */
    private static final String ERROR_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    private FrameDispatcherController dispatcher;
    private SubscriptionManager subscriptionManager;
    private NodeConnectionManager connMgr;
    private Logger log;
    private static Logger logger = LogManager.getLogger(RemoteDataSourceController.class); // log4j appender style...
    
    // remote data source object list
    private List<DataSource> remoteDataSources;
    // selected data source list
    private List<DataSource> selectedDataSources;
    // remote node name
    private String remoteNodeName;
    /** Remote node address */
    private String remoteNodeAddress;
    /** Web address validator */
    private WebValidator webValidator;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public RemoteDataSourceController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * 
     * @param request
     * @param response
     * @return 
     */
    public ModelAndView connectToNode(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView(CONNECT_TO_NODE_VIEW, CONNECTIONS_KEY, connMgr.get());
    }
    /**
     * Creates list of remote data source available for a given node.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView 
     */
    public ModelAndView dsConnect(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String nodeName = request.getParameter(SEL_ADDR_PARAM);
        String connAddress = request.getParameter(ENTER_ADDR_PARAM);
        NodeConnection nodeConn = null;
        try {
            logger.debug("dsConnect: nodeName="+nodeName+", address="+connAddress);
            if (!validate(nodeName) && !webValidator.validateUrl(connAddress)) {
              logger.debug("dsConnect: not valid: nodeName="+nodeName+", address="+connAddress);
                // Address was not specified
                modelAndView.addObject(SEL_ADDR_ERROR_KEY, SEL_ADDR_ERROR_MSG);
                modelAndView.addObject(ENTER_ADDR_ERROR_KEY, ENTER_ADDR_ERROR_MSG);
                modelAndView.addObject(CONNECTIONS_KEY, connMgr.get());
                modelAndView.setViewName(CONNECT_TO_NODE_VIEW);
            } else {
                if (validate(nodeName) && webValidator.validateUrl(connAddress)) {
                    // use new address
                  logger.debug("dsConnect: Using address="+connAddress+", but name is valid as well: " + nodeName);
                    nodeConn = new NodeConnection(connAddress);
                }
                if (validate(nodeName) && !webValidator.validateUrl(connAddress)) {
                    // use connection selected from the list
                  logger.debug("dsConnect: Using name="+nodeName);
                    nodeConn = connMgr.get(nodeName);
                }
                if (!validate(nodeName) && webValidator.validateUrl(connAddress)) {
                    // use new address
                  logger.debug("dsConnect: Using address="+connAddress);

                    nodeConn = new NodeConnection(connAddress);
                }
                HttpResponse res = postDSListRequest(nodeConn);
                // Get remote node name from response header
                setRemoteNodeName(getHeader(res, HDR_NODE_NAME));
                setRemoteNodeAddress(nodeConn.getNodeAddress());
                int code = res.getStatusLine().getStatusCode();
                
                if (code == HttpServletResponse.SC_UNAUTHORIZED) {
                    modelAndView.addObject(ERROR_MSG_KEY, CONN_UNAUTHORIZED_MSG); 
                }
                if (code == HttpServletResponse.SC_OK) {
                  logger.debug("dsConnect: Response ok. Reading frame");
                    SerialFrame serialFrame = readFrameFromStream(res);
                    if (authenticate(InitAppUtil.getConf().getKeystoreDir(), serialFrame.getNodeName(), 
                            serialFrame.getSignature(), serialFrame.getTimestamp())) {                        
                      logger.debug("dsConnect: Authenticated. Validating data sources");

                      List<DataSource> dsList = (List<DataSource>) serialFrame.getItemList();
                        if (validate(dsList)) {
                            modelAndView.addObject(DATA_SOURCES_KEY, dsList);
                            // make the list available to other methods
                            setRemoteDataSources(dsList);
                        } else {
                            modelAndView.addObject(ERROR_MSG_KEY, DS_LIST_EMPTY_MSG);
                        }
                    } else {
                      logger.debug("dsConnect: Not authorized");

                      modelAndView.addObject(ERROR_MSG_KEY, DS_LIST_UNAUTHORIZED_MSG);
                    }
                }
                modelAndView.addObject(REMOTE_NODE_NAME_KEY, getRemoteNodeName()); 
                modelAndView.setViewName(DATA_SOURCES_VIEW);

                logger.debug("dsConnect: Remote name: " + getRemoteNodeName());

                if (connMgr.get(getRemoteNodeName()) == null) {
                  logger.debug("dsConnect: Saving or updating node connection with name: " 
                          + getRemoteNodeName());
                    try {
                        nodeConn.setNodeName(getRemoteNodeName());
                        connMgr.saveOrUpdate(nodeConn);
                    } catch(Exception e) {
                      logger.debug("Failed to save node connection", e);
                        log.error("Failed to save node connection", e);
                    }
                } else {
                  logger.debug("dsConnect: No connection with name: " + getRemoteNodeName());
                }
            }
            
        } catch (Exception e) {
            modelAndView.addObject(ERROR_MSG_KEY, CONN_FAILURE_MSG); 
            modelAndView.setViewName(DATA_SOURCES_VIEW);
            log.error(CONN_FAILURE_MSG);
        }        
        return modelAndView;
    }
    /**
     * Creates list of data sources selected by the user
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView object holding list of data sources selected by the user
     */
    public ModelAndView dsToConnect(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        String[] selDataSourceNames = request.getParameterValues(SELECTED_DATA_SOURCES_KEY);
        if (selDataSourceNames != null) {
            selectedDataSources = new ArrayList<DataSource>();
            for (int i = 0; i < selDataSourceNames.length; i++) {
                for (int j = 0; j < getRemoteDataSources().size(); j++) {
                    if (getRemoteDataSources().get(j).getName().equals(selDataSourceNames[i])) {
                        selectedDataSources.add(getRemoteDataSources().get(j));
                    }
                }
            }
            modelAndView.addObject(SELECTED_DATA_SOURCES_KEY, getSelectedDataSources()); 
        } else {
            modelAndView.addObject(ERROR_MSG_KEY, DS_SELECTION_EMPTY_MSG);
        }
        modelAndView.addObject(REMOTE_NODE_NAME_KEY, getRemoteNodeName());
        modelAndView.setViewName(SELECTED_DATA_SOURCES_VIEW);
        return modelAndView;
    }
    /**
     * Submits data source subscription request by posting subscription list on remote node.
     *
     * @param request HTTP servlet request
     * @param response HTTP servlet response
     * @return ModelAndView
     */
    public ModelAndView dsSubscribed(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        File dsList = writeObjectToFile(getSelectedDataSources(), 
                InitAppUtil.getWorkDir());
        HttpResponse res = postSubsciptionRequest(getRemoteNodeAddress(), dsList);
        int code = res.getStatusLine().getStatusCode();
        if (code == HttpServletResponse.SC_UNAUTHORIZED) {
            modelAndView.addObject(ERROR_KEY, SUB_REQUEST_UNAUTHORIZED_MSG); 
        }
        if (code == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            modelAndView.addObject(ERROR_KEY, INTERNAL_SERVER_ERROR_MSG); 
        }
        if (code == HttpServletResponse.SC_OK) {
            SerialFrame serialFrame = readFrameFromStream(res);
            if (authenticate(InitAppUtil.getConf().getKeystoreDir(), 
                    serialFrame.getNodeName(), serialFrame.getSignature(), 
                    serialFrame.getTimestamp())) {
                List<DataSource> dsConfirm = (List<DataSource>) serialFrame.getItemList();
                if (validate(dsConfirm)) {
                    try {
                        for (int i = 0; i < dsConfirm.size(); i++) {
                            DataSource dataSource = dsConfirm.get(i);
                            if (subscriptionManager.get(dataSource.getName(), 
                                    Subscription.SUBSCRIPTION_DOWNLOAD) != null) {
                                log.warn("You are already subscribing " + dataSource.getName());
                            } else {
                                Subscription sub = new Subscription(System.currentTimeMillis(),
                                    InitAppUtil.getConf().getNodeName(), dataSource.getName(), 
                                    getRemoteNodeName(), Subscription.SUBSCRIPTION_DOWNLOAD, true, 
                                    true, getRemoteNodeAddress());
                                subscriptionManager.save(sub);
                                log.info("New data source subscribed: " + getRemoteNodeName() +
                                        "/" + dataSource.getName());
                            }
                        }
                        modelAndView.addObject(MSG_KEY, SUB_REQUEST_OK);
                    } catch (Exception e) {
                        log.error("Failed to save subscription information", e);
                    }
                } else {
                    modelAndView.addObject(ERROR_KEY, DS_CONFIRM_EMPTY_MSG);
                }
            } else {
                modelAndView.addObject(ERROR_KEY, DS_CONFIRM_UNAUTHORIZED_MSG);
            }   
        }   
        modelAndView.addObject(REMOTE_NODE_NAME_KEY, getRemoteNodeName());
        modelAndView.setViewName(SUBSCRIBED_DATA_SOURCES_VIEW);
        // Delete temporary files
        deleteFile(dsList);
        return modelAndView;
    }
    /**
     * Post certificate using a defined node connection. 
     * 
     * @param nodeConn Node connection
     * @return Http response
     *
    private HttpResponse postCertRequest(NodeConnection nodeConn) {
        HttpResponse response = null;
        long timestamp = System.currentTimeMillis();
        String signature = getSignatureString(
                InitAppUtil.getConf().getKeystoreDir(),
                ServletContextUtil.getServletContextPath() + InitAppUtil.KS_FILE_PATH, 
                InitAppUtil.getConf().getCertAlias(), timestamp);
        Frame frame = Frame.postCertRequest(nodeConn.getNodeAddress(), 
                InitAppUtil.getConf().getNodeAddress(), InitAppUtil.getConf().getNodeName(), 
                timestamp, signature);
        Handler handler = new Handler(InitAppUtil.getConf().getConnTimeout(), 
                InitAppUtil.getConf().getSoTimeout());
        response = handler.post(frame);
        return response;
    }*/
    /**
     * Post data source listing request using a defined node connection.
     * 
     * @param nodeConn Node connection
     * @return HTTP response
     */
    private HttpResponse postDSListRequest(NodeConnection nodeConn) {
        HttpResponse response = null;
        long timestamp = System.currentTimeMillis();
        String signature = getSignatureString(
                InitAppUtil.getConf().getKeystoreDir(), InitAppUtil.getConf().getCertAlias(), 
                timestamp);
        Frame frame = Frame.postDSListRequest(nodeConn.getNodeAddress(), 
                InitAppUtil.getConf().getNodeAddress(), InitAppUtil.getConf().getNodeName(), 
                timestamp, signature);
        Handler handler = InitAppUtil.getHandler();
        response = handler.post(frame);
        return response;
    }
    /**
     * Post data source subscription request.
     * 
     * @param remoteNodeAddress
     * @param payloadFile
     * @return 
     */
    private HttpResponse postSubsciptionRequest(String remoteNodeAddress, File payloadFile) {
        HttpResponse response = null;
        long timestamp = System.currentTimeMillis();
        String signature = getSignatureString(
                InitAppUtil.getConf().getKeystoreDir(),
                InitAppUtil.getConf().getCertAlias(), timestamp);
        Frame frame = Frame.postSubscriptionRequest(remoteNodeAddress, 
                InitAppUtil.getConf().getNodeAddress(), InitAppUtil.getConf().getNodeName(), 
                timestamp, signature, payloadFile);
        Handler handler = InitAppUtil.getHandler();
        response = handler.post(frame);
        deleteFile(payloadFile);
        return response;
    }
    /**
     * Gets reference to NodeConnectionManager object.
     *
     * @return Reference to NodeConnectionManager object
     */
    public NodeConnectionManager getConnMgr() { return connMgr; }
    /**
     * Sets reference to NodeConnectionManager object.
     *
     * @param nodeManager Reference to NodeConnectionManager object
     */
    public void setConnMgr(NodeConnectionManager connMgr) { this.connMgr = connMgr; }
    /**
     * Gets the list of data sources available for a given remote node.
     *
     * @return List of data sources available for a given remote node
     */
    public List<DataSource> getRemoteDataSources() { return remoteDataSources; }
    /**
     * Sets the list of data sources available for a given remote node.
     *
     * @param remoteDataSources List of data sources available for a given remote node
     */
    public void setRemoteDataSources(List<DataSource> remoteDataSources) {
        this.remoteDataSources = remoteDataSources;
    }
    /**
     * Gets the list of data sources selected by the user.
     *
     * @return List of data sources selected by the user
     */
    public List<DataSource> getSelectedDataSources() { return selectedDataSources; }
    /**
     * Sets the list of data sources selected by the user.
     *
     * @param selectedDataSources List of data sources selected by the user
     */
    public void setSelectedDataSources(List<DataSource> selectedDataSources) {
        this.selectedDataSources = selectedDataSources;
    }
    /**
     * Gets remote node name.
     *
     * @return Remote node name
     */
    public String getRemoteNodeName() { return remoteNodeName; }
    /**
     * Set sender node name.
     *
     * @param senderNodeName Sender node name
     */
    public void setRemoteNodeName(String remoteNodeName) { this.remoteNodeName = remoteNodeName; }
    /**
     * Get remote node address.
     *
     * @return Remote node address
     */
    public String getRemoteNodeAddress() { return remoteNodeAddress; }
    /**
     * Set remote node address.
     *
     * @param remoteNodeAddress Remote node address to set
     */
    public void setRemoteNodeAddress(String remoteNodeAddress) {
        this.remoteNodeAddress = remoteNodeAddress;
    }
    /**
     * Gets reference to FrameDispatcherController object.
     *
     * @return Reference to FrameDispatcherController object
     */
    public FrameDispatcherController getDispatcher() { return dispatcher; }
    /**
     * Sets reference to FrameDispatcherController object.
     *
     * @param frameDispatcherController Reference to FrameDispatcherController object
     */
    public void setDispatcher(FrameDispatcherController dispatcher) {
        this.dispatcher = dispatcher;
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
     * @return the webValidator
     */
    public WebValidator getWebValidator() {
        return webValidator;
    }

    /**
     * @param webValidator the webValidator to set
     */
    public void setWebValidator(WebValidator webValidator) {
        this.webValidator = webValidator;
    }
}
//--------------------------------------------------------------------------------------------------