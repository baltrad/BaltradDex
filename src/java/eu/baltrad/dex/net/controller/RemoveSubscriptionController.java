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
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.auth.KeyczarAuthenticator;
import eu.baltrad.dex.net.request.factory.RequestFactory;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.apache.log4j.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

import java.net.URI;

/**
 * Allows to remove subscriptions.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
@Controller
public class RemoveSubscriptionController {

    // View names
    private static final String SUBSCRIBED_PEERS_VIEW = 
            "subscription_remove_downloads_peers";
    private static final String REMOVE_DOWNLOADS_VIEW = 
            "subscription_remove_downloads";
    private static final String REMOVE_SELECTED_DOWNLOADS_VIEW = 
            "subscription_remove_selected_downloads";
    private static final String REMOVE_DOWNLOADS_STATUS_VIEW = 
            "subscription_remove_downloads_status";
    private static final String REMOVE_UPLOADS_VIEW = 
            "subscription_remove_uploads";
    private static final String REMOVE_SELECTED_UPLOADS_VIEW = 
            "subscription_remove_selected_uploads";
    private static final String REMOVE_UPLOADS_STATUS_VIEW = 
            "subscription_remove_uploads_status";
    
    // Model keys
    private static final String DOWNLOADS_PEERS_KEY = "downloads_peers";
    private static final String PEER_NAME_KEY = "peer_name";
    private static final String DOWNLOADS_KEY = "downloads"; 
    private static final String UPLOADS_KEY = "uploads"; 
    private static final String SELECTED_DOWNLOADS_KEY = "selected_downloads"; 
    private static final String SELECTED_UPLOADS_KEY = "selected_uploads"; 
    
    private static final String DOWNLOAD_IDS = "downloadIds";
    private static final String UPLOAD_IDS = "uploadIds";
    
    private static final String OK_MSG_KEY = "subscription_remove_success";
    private static final String ERROR_MSG_KEY = "subscription_remove_error";
    
    private static final String DOWNLOADS_CANCEL_OK_REMOVE_OK = 
            "removesubscription.cancel_ok_remove_ok";
    private static final String DOWNLOADS_CANCEL_FAIL_REMOVE_OK = 
            "removesubscription.cancel_fail_remove_ok";
    private static final String DOWNLOADS_CANCEL_OK_REMOVE_FAIL = 
            "removesubscription.cancel_ok_remove_fail";
    private static final String DOWNLOADS_CANCEL_FAIL_REMOVE_FAIL = 
            "removesubscription.cancel_fail_remove_fail";
    
    private static final String REMOVE_SUBSCRIPTION_SUCCESS_MSG_KEY = 
            "removesubscription.success";
    private static final String COMPLETED_OK_MSG_KEY = 
            "removesubscription.completed_success";
    private static final String COMPLETED_FAILURE_MSG_KEY =
            "removesubscription.completed_failure";

    private Authenticator authenticator;
    private IConfigurationManager confManager;
    private IHttpClientUtil httpClient;
    private IUserManager userManager;
    private ISubscriptionManager subscriptionManager;
    private IDataSourceManager dataSourceManager;
    private PlatformTransactionManager txManager;
    private RequestFactory requestFactory;
    private MessageResourceUtil messages;
    private Logger log;
    
    protected List<Subscription> selectedDownloads;
    protected List<Subscription> selectedActiveDownloads;
    protected List<Subscription> selectedUploads;

    protected String peerName;
    protected User localNode;
    
    /**
     * Constructor.
     */
    public RemoveSubscriptionController() {
        this.log = Logger.getLogger("DEX");
        this.selectedDownloads = new ArrayList<Subscription>();
        this.selectedActiveDownloads = new ArrayList<Subscription>();
        this.selectedUploads = new ArrayList<Subscription>();
    }
    
    /**
     * Initializes controller with current configuration
     */
    protected void initConfiguration() {
        this.setAuthenticator(new KeyczarAuthenticator(
                 confManager.getAppConf().getKeystoreDir()));
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
     * Shows lists of subscribed peer nodes.
     * @param model Model
     * @return Subscribed peers view
     */
    @RequestMapping("/subscription_remove_downloads_peers.htm")
    public String subscribedPeers(Model model) {
        model.addAttribute(DOWNLOADS_PEERS_KEY, userManager.loadOperators());
        return SUBSCRIBED_PEERS_VIEW;
    }
    
    /**
     * Shows list of subscriptions for a given peer node. 
     * @param model Model
     * @param peerName Peer node name
     * @return Subscriptions by peer view
     */
    @RequestMapping("/subscription_remove_downloads.htm")
    public String downloadsByPeer(Model model,
            @RequestParam(value="peer_name", required=false) String peerName) {
        if (peerName != null) {
            this.peerName = peerName;
        }
        List<Subscription> subscriptionByPeer = subscriptionManager.load(
                Subscription.LOCAL, this.peerName);
        model.addAttribute(DOWNLOADS_KEY, subscriptionByPeer);
        model.addAttribute(PEER_NAME_KEY, this.peerName);
        return REMOVE_DOWNLOADS_VIEW;
    }
    
    /**
     * Creates list of downloads selected fro removal.
     * @param request Http servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/subscription_remove_selected_downloads.htm")
    public String removeSelectedDownloads(HttpServletRequest request,
            Model model) {
        model.addAttribute(PEER_NAME_KEY, peerName);
        String[] downloadIds = request.getParameterValues(DOWNLOAD_IDS);
        if (downloadIds == null) {
            model.addAttribute(DOWNLOADS_KEY, 
                subscriptionManager.load(Subscription.LOCAL, peerName));
            return REMOVE_DOWNLOADS_VIEW;
        } else {
            selectedDownloads.clear();
            selectedActiveDownloads.clear();
            for (int i = 0; i < downloadIds.length; i++) {
                Subscription s = subscriptionManager
                        .load(Integer.parseInt(downloadIds[i]));
                selectedDownloads.add(s);
                if (s.isActive()) {
                    selectedActiveDownloads.add(s);
                }
            }
            model.addAttribute(SELECTED_DOWNLOADS_KEY, selectedDownloads);
            return REMOVE_SELECTED_DOWNLOADS_VIEW;
        }
    }
    
    /**
     * Removes selected downloads.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/subscription_remove_downloads_status.htm")
    public String removeDownloadsStatus(Model model) {
        initConfiguration();
        int cancel = cancelDownloads(peerName, localNode, 
                selectedActiveDownloads);
        int remove = removeDownloads(selectedDownloads);
        if (cancel == HttpServletResponse.SC_OK && remove == 0) {
            model.addAttribute(OK_MSG_KEY, 
                    messages.getMessage(DOWNLOADS_CANCEL_OK_REMOVE_OK));
            log.warn(messages.getMessage(DOWNLOADS_CANCEL_OK_REMOVE_OK));
        }
        if (cancel != HttpServletResponse.SC_OK && remove == 0) {
            model.addAttribute(ERROR_MSG_KEY, 
                    messages.getMessage(DOWNLOADS_CANCEL_FAIL_REMOVE_OK));
            log.error(messages.getMessage(DOWNLOADS_CANCEL_FAIL_REMOVE_OK));
        }
        if (cancel == HttpServletResponse.SC_OK && remove == 1) {
            model.addAttribute(ERROR_MSG_KEY, 
                    messages.getMessage(DOWNLOADS_CANCEL_OK_REMOVE_FAIL));
            log.error(messages.getMessage(DOWNLOADS_CANCEL_OK_REMOVE_FAIL));
        }
        if (cancel != HttpServletResponse.SC_OK && remove == 1) {
            model.addAttribute(ERROR_MSG_KEY, 
                    messages.getMessage(DOWNLOADS_CANCEL_FAIL_REMOVE_FAIL));
            log.error(messages.getMessage(DOWNLOADS_CANCEL_FAIL_REMOVE_FAIL));
        }
        return REMOVE_DOWNLOADS_STATUS_VIEW;
    }
    
    /**
     * Cancel subscriptions at the peer node.
     * @param peerName Peer node name
     * @param local Local node user
     * @param downloads Active subscriptions to cancel
     * @return HTTP status code
     */
    private int cancelDownloads(String peerName, User local, 
                                    List<Subscription> downloads) {
        try {
            User peer = userManager.load(peerName);
            requestFactory = new DefaultRequestFactory(
                    URI.create(peer.getNodeAddress()));
            for (Subscription s : downloads) {
                s.setActive(false);
            }
            HttpUriRequest req = requestFactory
                .createUpdateSubscriptionRequest(local, downloads);
            authenticator.addCredentials(req, local.getName());
            HttpResponse res = httpClient.post(req);
            return res.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }
    
    /**
     * Remove subscriptions from local registry.
     * @param downloads Subscriptions to remove
     * @return Operation status code: 0 in case of success, otherwise 1
     */
    private int removeDownloads(List<Subscription> downloads) {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = txManager.getTransaction(def);
        try {    
            for (Subscription s : downloads) {
                String[] msgArgs = {s.getUser(), s.getDataSource()};
                // remove peer data sources
                int dataSourceId = dataSourceManager.load(s.getDataSource(), 
                        DataSource.PEER).getId();
                dataSourceManager.delete(dataSourceId);
                // remove subscriptions
                subscriptionManager.delete(s.getId());
                String msg = messages.getMessage(
                    REMOVE_SUBSCRIPTION_SUCCESS_MSG_KEY, msgArgs);
                log.warn(msg);
            }
            txManager.commit(status);
            return 0;
        } catch (Exception e) {
            txManager.rollback(status);
            return 1;
        }
    }
    
    /**
     * Creates list of all available uploads.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/subscription_remove_uploads.htm")
    public String removeUploads(Model model) {
        model.addAttribute(UPLOADS_KEY, 
                subscriptionManager.load(Subscription.PEER));
        return REMOVE_UPLOADS_VIEW;
    }
    
    /**
     * Creates list of uploads selected fro removal.
     * @param request Http servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/subscription_remove_selected_uploads.htm")
    public String removeSelectedUploads(HttpServletRequest request,
            Model model) {
        String[] uploadIds = request.getParameterValues(UPLOAD_IDS);
        if (uploadIds == null) {
            model.addAttribute(UPLOADS_KEY, 
                subscriptionManager.load(Subscription.PEER));
            return REMOVE_UPLOADS_VIEW;
        } else {
            selectedUploads.clear();
            for (int i = 0; i < uploadIds.length; i++) {
                selectedUploads.add(subscriptionManager
                        .load(Integer.parseInt(uploadIds[i])));
            }
            model.addAttribute(SELECTED_UPLOADS_KEY, selectedUploads);
            return REMOVE_SELECTED_UPLOADS_VIEW;
        }
    }
    
    /**
     * Removes selected uploads.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/subscription_remove_uploads_status.htm")
    public String removeUploadsStatus(Model model) {
        try {
            for (Subscription s : selectedUploads) {
                String[] msgArgs = {s.getDataSource(), s.getUser()};
                subscriptionManager.delete(s.getId());
                String msg = messages.getMessage(
                    REMOVE_SUBSCRIPTION_SUCCESS_MSG_KEY, msgArgs);
                log.warn(msg);
            }
            model.addAttribute(OK_MSG_KEY, 
                    messages.getMessage(COMPLETED_OK_MSG_KEY));
            log.warn(messages.getMessage(COMPLETED_OK_MSG_KEY));
        } catch (Exception e) {
            model.addAttribute(ERROR_MSG_KEY,
                    messages.getMessage(COMPLETED_FAILURE_MSG_KEY));
            log.error(messages.getMessage(COMPLETED_FAILURE_MSG_KEY));
        }
        return REMOVE_UPLOADS_STATUS_VIEW;
    }

    /**
     * @param configurationManager 
     */
    @Autowired
    public void setConfigurationManager(IConfigurationManager confManager) {
        this.confManager = confManager;
    }
    
    /**
     * @param authenticator the authenticator to set
     */
    @Autowired
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
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
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(ISubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }
    
    /**
     * @param txManager the txManager to set
     */
    @Autowired
    public void setTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }
    
    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

