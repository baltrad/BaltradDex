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

package eu.baltrad.dex.datasource.controller;

import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.net.manager.impl.SubscriptionManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.db.CoreFilterManager;
import eu.baltrad.beast.db.IFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.IOException;

/**
 * Allows to select and remove existing data source.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.6.5
 */
@Controller
public class RemoveDataSourceController {

    // View names
    private static final String DS_REMOVE_VIEW = "remove_datasource";
    private static final String DS_REMOVE_SELECTED_VIEW = 
            "remove_selected_datasource";
    private static final String DS_REMOVE_STATUS_VIEW = 
            "remove_datasource_status";
    
    // Model keys
    private static final String DS_KEY = "data_sources";
    private static final String DS_SELECTED_KEY = "selected_data_sources";
    
    private static final String REMOVE_DS_OK_MSG_KEY = 
            "removedatasource.success";
    private static final String REMOVE_DS_COMPLETED_OK_MSG_KEY = 
            "removedatasource.completed_success";
    private static final String REMOVE_DS_COMPLETED_ERROR_MSG_KEY = 
            "removedatasource.completed_failure"; 
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";

    private DataSourceManager dataSourceManager;
    private SubscriptionManager subscriptionManager;
    private CoreFilterManager filterManager;
    private PlatformTransactionManager txManager;
    private MessageResourceUtil messages;
    private Logger log;

    /**
     * Constructor.
     */
    public RemoveDataSourceController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Renders show data sources page.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/remove_datasource.htm")
    public String removeDatasources(ModelMap model) {
        List<DataSource> dataSources = dataSourceManager.load(DataSource.LOCAL);
        Collections.sort(dataSources);
        model.addAttribute(DS_KEY, dataSources);
        return DS_REMOVE_VIEW;
    }

    /**
     * Renders data source selection page.
     * @param request Http servlet request
     * @param response Http servlet response
     * @param model Model map
     * @return View name
     * @throws IOException 
     */
    @RequestMapping("/remove_selected_datasource.htm")
    public String removeSelectedDataSources(HttpServletRequest request,
            HttpServletResponse response, ModelMap model) throws IOException {
        String[] dataSourceIds = request.getParameterValues(DS_SELECTED_KEY);
        if (dataSourceIds != null) {
            List<DataSource> dataSources = new ArrayList<DataSource>();
            for (int i = 0; i < dataSourceIds.length; i++) {
                dataSources.add(dataSourceManager
                        .load(Integer.parseInt(dataSourceIds[i])));
            }
            model.addAttribute(DS_SELECTED_KEY, dataSources);
        } else {
            response.sendRedirect("remove_datasource.htm");
        }
        return DS_REMOVE_SELECTED_VIEW;
    }
    
    /**
     * Remove selected data sources.
     * @param request Http servlet request
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/remove_datasource_status.htm")
    public String removeDatasourcesStatus(HttpServletRequest request, 
            ModelMap model) {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = txManager.getTransaction(def);
        try {
            String[] dataSourceIds = request.getParameterValues(DS_SELECTED_KEY);
            List<Subscription> uploads = 
                        subscriptionManager.load(Subscription.PEER);
            for (int i = 0; i < dataSourceIds.length; i++) {
                int dsId = Integer.parseInt(dataSourceIds[i]);
                String dsName = dataSourceManager.load(dsId).getName();
                int filterId = dataSourceManager.loadFilterId(dsId);
                IFilter filter = filterManager.load(filterId);
                filterManager.remove(filter);
                dataSourceManager.delete(dsId);
                // Remove subscriptions
                for (Subscription s : uploads) {
                    if (s.getDataSource().equals(dsName)) {
                        subscriptionManager.delete(s.getId());
                    }
                }
                String msg = messages.getMessage(REMOVE_DS_OK_MSG_KEY, 
                                new Object[] {dsName});
                log.warn(msg);
            }
            txManager.commit(status);
            request.getSession().setAttribute(OK_MSG_KEY, 
            messages.getMessage(REMOVE_DS_COMPLETED_OK_MSG_KEY));
        } catch (Exception e) {
            txManager.rollback(status);
            request.getSession().setAttribute(ERROR_MSG_KEY, 
                messages.getMessage(REMOVE_DS_COMPLETED_ERROR_MSG_KEY));
        }
        return DS_REMOVE_STATUS_VIEW;
    }
    
    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
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
     * @param filterManager the filterManager to set
     */
    @Autowired
    public void setFilterManager(CoreFilterManager filterManager) {
        this.filterManager = filterManager;
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

