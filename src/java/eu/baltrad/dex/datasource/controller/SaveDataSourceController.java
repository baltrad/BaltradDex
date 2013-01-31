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

import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.radar.manager.impl.RadarManager;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.datasource.manager.impl.FileObjectManager;
import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.user.manager.impl.AccountManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.impl.SubscriptionManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.util.DataSourceValidator;
import eu.baltrad.dex.util.WebValidator;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.AttributeFilter;
import eu.baltrad.beast.db.CombinedFilter;
import eu.baltrad.beast.db.CoreFilterManager;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.ui.ModelMap;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.ArrayList;

/**
 * Allows to configure new data source or to modify an existing one.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
@Controller
@RequestMapping("/save_datasource.htm")
@SessionAttributes("data_source")
public class SaveDataSourceController {
    
    private static final String FORM_VIEW = "save_datasource";
    private static final String SUCCESS_VIEW = "save_datasource_status";
    
    /** ODIM what/source attribute key */
    private static final String DS_SOURCE_ATTR_STR = "what/source:WMO";
    /** ODIM what/object attribute key */
    private static final String DS_OBJECT_ATTR_STR = "what/object";
    
    private static final String MISSING_RADAR_ERROR_MSG_KEY = 
            "savedatasource.missing.radar";
    private static final String MISSING_RADAR_MODEL_KEY = "missing_radar_error";
    private static final String DATA_SOURCE_MODEL_KEY = "data_source";
    private static final String SAVE_DATASOURCE_ERROR_MSG_KEY = 
            "savedatasource.failure";
    private static final String SAVE_DATASOURCE_OK_MSG_KEY = 
            "savedatasource.success";
    private static final String ERROR_MSG_KEY = "error";
    private static final String OK_MSG_KEY = "message";
    
    private PlatformTransactionManager transactionManager;
    private RadarManager radarManager;
    private FileObjectManager fileObjectManager;
    private AccountManager userManager;
    private IDataSourceManager dataSourceManager;
    private CoreFilterManager coreFilterManager;
    private SubscriptionManager subscriptionManager;
    private DataSourceValidator validator;
    private MessageResourceUtil messages;
    private Logger log;
    
    /**
     * Constructor.
     */
    public SaveDataSourceController() {
        this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Load form backing object.
     * @param dsId Data source id
     * @param model Model map
     * @return Form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam(value="ds_id", required=false) 
            String dsId, ModelMap model) {
        DataSource dataSource;
        if (WebValidator.validate(dsId)) {
            dataSource = dataSourceManager.load(Integer.parseInt(dsId));
        } else {
            dataSource = new DataSource();
        }
        model.addAttribute(DATA_SOURCE_MODEL_KEY, dataSource);
        return FORM_VIEW;
    }
    
    /**
     * Process form submission and save data source and its parameters.
     * @param dataSource Data source objects
     * @param result Binding result
     * @param model Model map
     * @param selectedRadars Selected radars parameter
     * @param selectedFileObjects Selected file objects parameter
     * @param selectedUsers Selected users parameter 
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(
            @ModelAttribute("data_source") DataSource dataSource,
                BindingResult result, ModelMap model,
                HttpServletRequest request,
            @RequestParam(value="selected_radars_hid", required=false) 
                    String[] selectedRadars,
            @RequestParam(value="selected_file_objects_hid", required=false) 
                    String[] selectedFileObjects,            
            @RequestParam(value="selected_users_hid", required=false) 
                    String[] selectedUsers) {
        
        validator.validate(dataSource, result);
        if (result.hasErrors() || selectedRadars == null) {
            if (selectedRadars == null) {
                request.getSession().setAttribute(MISSING_RADAR_MODEL_KEY, 
                        messages.getMessage(MISSING_RADAR_ERROR_MSG_KEY));
            }
            return FORM_VIEW;
        } else {
            // begin transaction
            TransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {
                int dataSourceId = 0;
                if (dataSource.getId() > 0) {
                    dataSourceManager.update(dataSource);
                    dataSourceId = dataSource.getId();
                } else {
                    dataSourceId = dataSourceManager.store(dataSource);
                }
                if (dataSourceId > 0) {
                    // Remove filter parameter
                    int filterId = dataSourceManager.loadFilterId(dataSourceId);
                    if (filterId > 0) {
                        IFilter filter = coreFilterManager.load(filterId);
                        coreFilterManager.remove(filter);
                    }
                    // Save radar parameter
                    dataSourceManager.deleteRadar(dataSourceId);
                    String wmoAttrValue = "";
                    if (selectedRadars != null) {    
                        for (int i = 0; i < selectedRadars.length; i++) {
                            String[] radarParms = selectedRadars[i].split("/");
                            Radar radar = radarManager
                                    .load(radarParms[0].trim());
                            dataSourceManager.storeRadar(dataSourceId, 
                                    radar.getId());
                            // Set filter value
                            wmoAttrValue += radar.getRadarWmo() + ",";
                        }
                    }
                    // Save file object parameter
                    dataSourceManager.deleteFileObject(dataSourceId);
                    String fileObjectAttrValue = "";
                    if (selectedFileObjects != null) {
                        for (int i = 0; i < selectedFileObjects.length; i++) {
                            int fileObjectId = (fileObjectManager
                                .load(selectedFileObjects[i])).getId();
                            dataSourceManager.storeFileObject(dataSourceId, 
                                    fileObjectId);
                            // Set filter value
                            fileObjectAttrValue += selectedFileObjects[i] + ",";
                        }
                    }
                    wmoAttrValue = wmoAttrValue
                            .substring(0, wmoAttrValue.lastIndexOf(","));
                    if (!fileObjectAttrValue.isEmpty()) {
                        fileObjectAttrValue = fileObjectAttrValue
                            .substring( 0, fileObjectAttrValue.lastIndexOf(","));
                    }
                    /* 
                     * Save user parameter. Remove respective subscriptions 
                     * in case when data source access right was revoked
                     * for a given user
                     */
                    List<Subscription> uploads = subscriptionManager
                                .load(Subscription.UPLOAD);
                    dataSourceManager.deleteUser(dataSourceId);
                    if (selectedUsers != null) {
                        List<Account> selectedAccounts = 
                                new ArrayList<Account>();
                        for (int i = 0; i < selectedUsers.length; i++) {
                            Account account = userManager.load(selectedUsers[i]);
                            selectedAccounts.add(account);    
                            dataSourceManager.storeUser(dataSourceId, 
                                    account.getId());
                        }
                        for (Subscription s : uploads) {
                            if (s.getDataSource().equals(dataSource.getName())
                                    && !containsUser(selectedAccounts, 
                                        s.getUser())) {
                                subscriptionManager.delete(s.getId());
                            }
                        }
                    } else {
                        for (Subscription s : uploads) {
                            if (s.getDataSource().equals(dataSource.getName()))
                            {
                                subscriptionManager.delete(s.getId());
                            }
                        }
                    }
                    // Configure filters
                    CombinedFilter combinedFilter = new CombinedFilter();
                    combinedFilter.setMatchType(CombinedFilter.MatchType.ALL);
                    AttributeFilter sourceFilter = new AttributeFilter();
                    if (!wmoAttrValue.isEmpty()) {
                        sourceFilter.setAttribute(DS_SOURCE_ATTR_STR);
                        sourceFilter.setValueType(
                                AttributeFilter.ValueType.STRING);
                        sourceFilter.setOperator(AttributeFilter.Operator.IN);
                        sourceFilter.setValue(wmoAttrValue);
                        combinedFilter.addChildFilter(sourceFilter);
                    }
                    AttributeFilter fileObjectFilter = new AttributeFilter();
                    if (!fileObjectAttrValue.isEmpty()) {
                        fileObjectFilter.setAttribute(DS_OBJECT_ATTR_STR);
                        fileObjectFilter.setValueType(
                                AttributeFilter.ValueType.STRING);
                        fileObjectFilter.setOperator(
                                AttributeFilter.Operator.IN);
                        fileObjectFilter.setValue(fileObjectAttrValue);
                        combinedFilter.addChildFilter(fileObjectFilter);
                    }
                    // Save filter parameter
                    coreFilterManager.store(combinedFilter);
                    dataSourceManager.deleteFilter(dataSourceId);
                    dataSourceManager.storeFilter(dataSourceId, 
                            combinedFilter.getId());
                    String msg = messages
                            .getMessage(SAVE_DATASOURCE_OK_MSG_KEY, 
                            new Object[] {dataSource.getName()});
                    model.addAttribute(OK_MSG_KEY, msg);
                    log.warn(msg);
                } else {
                    String msg = messages
                            .getMessage(SAVE_DATASOURCE_ERROR_MSG_KEY, 
                            new Object[] {dataSource.getName()});
                    model.addAttribute(ERROR_MSG_KEY, msg);
                    log.error(msg);
                }
                transactionManager.commit(status);
            } catch (Exception e) {
                transactionManager.rollback(status);
                String msg = messages
                            .getMessage(SAVE_DATASOURCE_ERROR_MSG_KEY, 
                            new Object[] {dataSource.getName()});
                model.addAttribute(ERROR_MSG_KEY, msg);
                log.error(msg, e);
            }
            return SUCCESS_VIEW;
        }
    }
    
    /**
     * Get radars available for selection.
     * @param dsId Data source id
     * @return List of available radars
     */
    @ModelAttribute("all_radars")
    public List<Radar> getAllRadars(
            @RequestParam(value="ds_id", required=false) String dsId) {
        if (dsId != null) {
            List<Radar> selectedRadars = dataSourceManager
                    .loadRadar(Integer.parseInt(dsId));
            List<Radar> allButSelectedRadars = new ArrayList<Radar>();
            for (Radar radar : radarManager.load()) {
                if (!selectedRadars.contains(radar)) {       
                    allButSelectedRadars.add(radar);
                }
            }
            return allButSelectedRadars;
        } else {
            return radarManager.load();
        }
    }
    
    /**
     * Get selected radars
     * @param dsId Data source id
     * @return List of selected radars
     */
    @ModelAttribute("selected_radars")
    public List<Radar> getSelectedRadars(
            @RequestParam(value="ds_id", required=false) String dsId) {
        if (dsId != null) {
            return dataSourceManager.loadRadar(Integer.parseInt(dsId));
        } else {
            return null;
        }
    }
    
    /**
     * Get file objects available for selection.
     * @param dsId Data source id
     * @return List of available file objects
     */
    @ModelAttribute("all_file_objects")
    public List<FileObject> getAllFileObjects(
            @RequestParam(value="ds_id", required=false) String dsId) {
        if (dsId != null) {
            List<FileObject> selectedFileObjects = dataSourceManager
                    .loadFileObject(Integer.parseInt(dsId));
            List<FileObject> allButSelectedFileObjects = 
                    new ArrayList<FileObject>();
            for (FileObject fileObject : fileObjectManager.load()) {
                if (!selectedFileObjects.contains(fileObject)) {
                    allButSelectedFileObjects.add(fileObject);
                }
            }
            return allButSelectedFileObjects;
        } else {
            return fileObjectManager.load();
        }
    }
    
    /**
     * Get selected file objects.
     * @param dsId Data source id
     * @return List of selected file objects
     */
    @ModelAttribute("selected_file_objects")
    public List<FileObject> getSelectedFileObjects(
            @RequestParam(value="ds_id", required=false) String dsId) {
        if (dsId != null) {
            return dataSourceManager.loadFileObject(Integer.parseInt(dsId));
        } else {
            return null;
        }
    }
    
    /**
     * Get users available for selection.
     * @param dsId Data source id
     * @return List of available users
     */
    @ModelAttribute("all_users")
    public List<Account> getAllUsers(
            @RequestParam(value="ds_id", required=false) String dsId) {
        if (dsId != null) {
            List<Account> selectedUsers = dataSourceManager
                    .loadUser(Integer.parseInt(dsId));
            List<Account> allButSelectedUsers = new ArrayList<Account>();
            for (Account account : userManager.load()) {
                if (!selectedUsers.contains(account)) {
                    allButSelectedUsers.add(account);
                }
            }
            return allButSelectedUsers;
        } else {
            return userManager.load();
        }
    }
    
    /**
     * Get selected users.
     * @param dsId Data source id
     * @return List of selected users
     */
    @ModelAttribute("selected_users")
    public List<Account> getSelectedUsers(
            @RequestParam(value="ds_id", required=false) String dsId) {
        if (dsId != null) {
            return dataSourceManager.loadUser(Integer.parseInt(dsId));
        } else {
            return null;
        }
    }
    
    /**
     * Checks if a given user is present on the list.
     * @param users List of users
     * @param user User to look for
     * @return True in case user is present 
     */
    private boolean containsUser(List<Account> accounts, String userName) {
        boolean result = false;
        for (Account availableUser : accounts) {
            if (availableUser.getName().equals(userName)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * @param transactionManager the transactionManager to set
     */
    @Autowired
    public void setTransactionManager(
            PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    /**
     * @param radarManager the radarManager to set
     */
    @Autowired
    public void setRadarManager(RadarManager radarManager) {
        this.radarManager = radarManager;
    }

    /**
     * @param fileObjectManager the fileObjectManager to set
     */
    @Autowired
    public void setFileObjectManager(FileObjectManager fileObjectManager) {
        this.fileObjectManager = fileObjectManager;
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(AccountManager userManager) {
        this.userManager = userManager;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(IDataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    /**
     * @param coreFilterManager the coreFilterManager to set
     */
    @Autowired
    public void setCoreFilterManager(CoreFilterManager coreFilterManager) {
        this.coreFilterManager = coreFilterManager;
    }

    /**
     * @param validator the validator to set
     */
    @Autowired
    public void setValidator(DataSourceValidator validator) {
        this.validator = validator;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }

    /**
     * @param subscriptionManager the subscriptionManager to set
     */
    @Autowired
    public void setSubscriptionManager(SubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }

}
