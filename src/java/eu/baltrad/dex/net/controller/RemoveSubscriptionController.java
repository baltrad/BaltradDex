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

import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.impl.SubscriptionManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ArrayList;

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
    private static final String REMOVE_DOWNLOADS_VIEW = "remove_downloads";
    private static final String REMOVE_SELECTED_DOWNLOADS_VIEW = 
            "remove_selected_downloads";
    private static final String REMOVE_DOWNLOADS_STATUS_VIEW = 
            "remove_downloads_status";
    private static final String REMOVE_UPLOADS_VIEW = "remove_uploads";
    private static final String REMOVE_SELECTED_UPLOADS_VIEW = 
            "remove_selected_uploads";
    private static final String REMOVE_UPLOADS_STATUS_VIEW = 
            "remove_uploads_status";
    
    // Model keys
    private static final String DOWNLOADS_KEY = "downloads"; 
    private static final String UPLOADS_KEY = "uploads"; 
    private static final String SELECTED_DOWNLOADS_KEY = "selected_downloads"; 
    private static final String SELECTED_UPLOADS_KEY = "selected_uploads"; 
    
    private static final String DOWNLOAD_IDS = "downloadIds";
    private static final String UPLOAD_IDS = "uploadIds";
    
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
    
    private static final String REMOVE_SUBSCRIPTION_SUCCESS_MSG_KEY = 
            "removesubscription.success";
    private static final String COMPLETED_OK_MSG_KEY = 
            "removesubscription.completed_success";
    private static final String COMPLETED_FAILURE_MSG_KEY =
            "removesubscription.completed_failure";

    private SubscriptionManager subscriptionManager;
    private IDataSourceManager dataSourceManager;
    private PlatformTransactionManager txManager;
    private MessageResourceUtil messages;
    private Logger log;
    
    private List<Subscription> selectedDownloads;
    private List<Subscription> selectedUploads;

    /**
     * Constructor.
     */
    public RemoveSubscriptionController() {
        this.log = Logger.getLogger("DEX");
        this.selectedDownloads = new ArrayList<Subscription>();
        this.selectedUploads = new ArrayList<Subscription>();
    }
    
    /**
     * Creates list of all available downloads.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/remove_downloads.htm")
    public String removeDownloads(ModelMap model) {
        model.addAttribute(DOWNLOADS_KEY, 
                subscriptionManager.load(Subscription.LOCAL));
        return REMOVE_DOWNLOADS_VIEW;
    }
    
    /**
     * Creates list of downloads selected fro removal.
     * @param request Http servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/remove_selected_downloads.htm")
    public String removeSelectedDownloads(HttpServletRequest request,
            ModelMap model) {
        String[] downloadIds = request.getParameterValues(DOWNLOAD_IDS);
        if (downloadIds == null) {
            model.addAttribute(DOWNLOADS_KEY, 
                subscriptionManager.load(Subscription.LOCAL));
            return REMOVE_DOWNLOADS_VIEW;
        } else {
            selectedDownloads.clear();
            for (int i = 0; i < downloadIds.length; i++) {
                selectedDownloads.add(subscriptionManager
                        .load(Integer.parseInt(downloadIds[i])));
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
    @RequestMapping("/remove_downloads_status.htm")
    public String removeDownloadsStatus(ModelMap model) {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = txManager.getTransaction(def);
        try {
            for (Subscription s : selectedDownloads) {
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
            model.addAttribute(OK_MSG_KEY, 
                    messages.getMessage(COMPLETED_OK_MSG_KEY));
            log.warn(messages.getMessage(COMPLETED_OK_MSG_KEY));
        } catch (Exception e) {
            txManager.rollback(status);
            model.addAttribute(ERROR_MSG_KEY,
                    messages.getMessage(COMPLETED_FAILURE_MSG_KEY));
            log.error(messages.getMessage(COMPLETED_FAILURE_MSG_KEY));
        }
        return REMOVE_DOWNLOADS_STATUS_VIEW;
    }
    
    /**
     * Creates list of all available uploads.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/remove_uploads.htm")
    public String removeUploads(ModelMap model) {
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
    @RequestMapping("/remove_selected_uploads.htm")
    public String removeSelectedUploads(HttpServletRequest request,
            ModelMap model) {
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
    @RequestMapping("/remove_uploads_status.htm")
    public String removeUploadsStatus(ModelMap model) {
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
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(SubscriptionManager subscriptionManager) 
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

