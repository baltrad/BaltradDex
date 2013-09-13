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

package eu.baltrad.dex.datasource.controller;

import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.util.DataSourceValidator;
import eu.baltrad.dex.util.WebValidator;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.datasource.manager.IFileObjectManager;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.radar.manager.IRadarManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Allows to configure new data source or to modify an existing one.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
@Controller
@RequestMapping("/datasources_save.htm")
@SessionAttributes("data_source")
public class SaveDataSourceController {
    
    private static final String FORM_VIEW = "datasources_save";
    private static final String SUCCESS_VIEW = "datasources_save_status";
    
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
    private static final String ERROR_MSG_KEY = "datasource_save_error";
    private static final String OK_MSG_KEY = "datasource_save_success";
    
    private PlatformTransactionManager transactionManager;
    private IRadarManager radarManager;
    private IFileObjectManager fileObjectManager;
    private IUserManager userManager;
    private IDataSourceManager dataSourceManager;
    private CoreFilterManager coreFilterManager;
    private ISubscriptionManager subscriptionManager;
    private DataSourceValidator validator;
    private MessageResourceUtil messages;
    private Logger log;
    
    private Map<Integer, Radar> radarsAvailable;
    private Map<Integer, Radar> radarsSelected;
    private Map<Integer, FileObject> fileObjectsAvailable;
    private Map<Integer, FileObject> fileObjectsSelected;
    private Map<Integer, User> usersAvailable;
    private Map<Integer, User> usersSelected;
    
    /**
     * Constructor.
     */
    public SaveDataSourceController() {
        this.log = Logger.getLogger("DEX");
        radarsAvailable = new HashMap<Integer, Radar>();
        radarsSelected = new HashMap<Integer, Radar>();
        fileObjectsAvailable = new HashMap<Integer, FileObject>();
        fileObjectsSelected = new HashMap<Integer, FileObject>();
        usersAvailable = new HashMap<Integer, User>();
        usersSelected = new HashMap<Integer, User>();
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
        clearModelData();
        if (WebValidator.validate(dsId)) {
            dataSource = dataSourceManager.load(Integer.parseInt(dsId));
            // determine selected and available radars
            List<Radar> radars = dataSourceManager
                    .loadRadar(Integer.parseInt(dsId));
            for (Radar radar : radars) {
                radarsSelected.put(radar.getId(), radar);
            }
            for (Radar radar : radarManager.load()) {
                if (!radarsSelected.containsKey(radar.getId())) {       
                    radarsAvailable.put(radar.getId(), radar);
                }
            }
            // determine selected and available file objects
            List<FileObject> fileObjects = dataSourceManager
                    .loadFileObject(Integer.parseInt(dsId));
            for (FileObject fileObject : fileObjects) {
                fileObjectsSelected.put(fileObject.getId(), fileObject);
            }
            for (FileObject fileObject : fileObjectManager.load()) {
                if (!fileObjectsSelected.containsKey(fileObject.getId())) {       
                    fileObjectsAvailable.put(fileObject.getId(), fileObject);
                }
            }
            // determine selected and available users
            List<User> users = dataSourceManager
                    .loadUser(Integer.parseInt(dsId));
            for (User user : users) {
                usersSelected.put(user.getId(), user);
            }
            for (User user : userManager.load()) {
                if (!usersSelected.containsKey(user.getId()) && 
                        user.getRole().equals(Role.PEER)) {       
                    usersAvailable.put(user.getId(), user);
                }
            }
        } else {
            dataSource = new DataSource();
            // load all radars 
            for (Radar radar : radarManager.load()) {
                radarsAvailable.put(radar.getId(), radar);
            }
            // load all file objects 
            for (FileObject fileObject : fileObjectManager.load()) {
                fileObjectsAvailable.put(fileObject.getId(), fileObject);
            }
            // load all users
            for (User user : userManager.load()) {
                if (user.getRole().equals(Role.PEER)) {
                    usersAvailable.put(user.getId(), user);
                }
            }
        }
        model.addAttribute(DATA_SOURCE_MODEL_KEY, dataSource);
        setModelAttributes(model);
        return FORM_VIEW;
    }
    
    /**
     * Process form submission and save data source and its parameters.
     * @param dataSource Data source objects
     * @param result Binding result
     * @param model Model map
     * @param request HTTP servlet request
     * @return View name
     */
    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(
            @ModelAttribute("data_source") DataSource dataSource,
                BindingResult result, ModelMap model, 
                HttpServletRequest request) {
        String view = "";
        // add radar 
        if (request.getParameter("add_radar") != null) {
            int[] radars = 
                    toIntArray(request.getParameterValues("radars_available"));
            if (radars != null) {
                for (int i = 0; i < radars.length; i++) {
                    radarsSelected.put(radars[i], 
                            radarsAvailable.get(radars[i]));
                    radarsAvailable.remove(radars[i]);
                }
            }
            view = FORM_VIEW;
        }
        // remove radar
        if (request.getParameter("remove_radar") != null) {
            int[] radars = 
                    toIntArray(request.getParameterValues("radars_selected"));
            if (radars != null) {
                for (int i = 0; i < radars.length; i++) {
                    radarsAvailable.put(radars[i], 
                            radarsSelected.get(radars[i]));
                    radarsSelected.remove(radars[i]);
                }
            }
            view = FORM_VIEW;
        }
        // add file object
        if (request.getParameter("add_file_object") != null) {
            int[] file_objects = toIntArray(request
                    .getParameterValues("file_objects_available"));
            if (file_objects != null) {
                for (int i = 0; i < file_objects.length; i++) {
                    fileObjectsSelected.put(file_objects[i], 
                            fileObjectsAvailable.get(file_objects[i]));
                    fileObjectsAvailable.remove(file_objects[i]);
                }
            }
            view = FORM_VIEW;
        }
        // remove file object
        if (request.getParameter("remove_file_object") != null) {
            int[] file_objects = toIntArray(request
                    .getParameterValues("file_objects_selected"));
            if (file_objects != null) {
                for (int i = 0; i < file_objects.length; i++) {
                    fileObjectsAvailable.put(file_objects[i], 
                            fileObjectsSelected.get(file_objects[i]));
                    fileObjectsSelected.remove(file_objects[i]);
                }
            }
            view = FORM_VIEW;
        }
        // add user
        if (request.getParameter("add_user") != null) {
            int[] users = 
                    toIntArray(request.getParameterValues("users_available"));
            if (users != null) {
                for (int i = 0; i < users.length; i++) {
                    usersSelected.put(users[i], usersAvailable.get(users[i]));
                    usersAvailable.remove(users[i]);
                }
            }
            view = FORM_VIEW;
        }
        // remove user
        if (request.getParameter("remove_user") != null) {
            int[] users = 
                    toIntArray(request.getParameterValues("users_selected"));
            if (users != null) {
                for (int i = 0; i < users.length; i++) {
                    usersAvailable.put(users[i], usersSelected.get(users[i]));
                    usersSelected.remove(users[i]);
                }
            }
            view = FORM_VIEW;
        }
        if (request.getParameter("save_data_source") != null) {
            validator.validate(dataSource, result);
            if (result.hasErrors() || radarsSelected.isEmpty()) {
                
                if (radarsSelected.isEmpty()) {
                    request.getSession().setAttribute(MISSING_RADAR_MODEL_KEY, 
                            messages.getMessage(MISSING_RADAR_ERROR_MSG_KEY));
                }
                view = FORM_VIEW;
            } else {
                // begin transaction
                TransactionDefinition def = new DefaultTransactionDefinition();
                TransactionStatus status = 
                        transactionManager.getTransaction(def);
                try {
                    int dataSourceId = 0;
                    // set data source type
                    dataSource.setType(DataSource.LOCAL);
                    if (dataSource.getId() > 0) {
                        dataSourceManager.update(dataSource);
                        dataSourceId = dataSource.getId();
                    } else {
                        dataSourceId = dataSourceManager.store(dataSource);
                    }
                    if (dataSourceId > 0) {
                        // Remove filter parameter
                        int filterId = 
                                dataSourceManager.loadFilterId(dataSourceId);
                        if (filterId > 0) {
                            IFilter filter = coreFilterManager.load(filterId);
                            coreFilterManager.remove(filter);
                        }
                        // Save radar parameter
                        dataSourceManager.deleteRadar(dataSourceId);
                        String wmoAttrValue = "";
                        if (!radarsSelected.isEmpty()) {    
                            for (Radar radar : radarsSelected.values()) {
                                dataSourceManager.storeRadar(dataSourceId, 
                                        radar.getId());
                                // Set filter value
                                wmoAttrValue += radar.getRadarWmo() + ",";
                            }
                        }
                        // Save file object parameter
                        dataSourceManager.deleteFileObject(dataSourceId);
                        String fileObjectAttrValue = "";
                        if (!fileObjectsSelected.isEmpty()) {
                            for (FileObject fileObject : 
                                    fileObjectsSelected.values()) {;
                                dataSourceManager.storeFileObject(dataSourceId, 
                                        fileObject.getId());
                                // Set filter value
                                fileObjectAttrValue += fileObject.getName()
                                        + ",";
                            }
                        }
                        wmoAttrValue = wmoAttrValue
                                .substring(0, wmoAttrValue.lastIndexOf(","));
                        if (!fileObjectAttrValue.isEmpty()) {
                            fileObjectAttrValue = fileObjectAttrValue
                                .substring( 0, 
                                        fileObjectAttrValue.lastIndexOf(","));
                        }
                        /* 
                         * Save user parameter. Remove respective subscriptions 
                         * in case when data source access right was revoked
                         * for a given user
                         */
                        List<Subscription> uploads = subscriptionManager
                                    .load(Subscription.PEER);
                        dataSourceManager.deleteUser(dataSourceId);
                        if (!usersSelected.isEmpty()) {
                            for (User user : usersSelected.values()) {
                                dataSourceManager.storeUser(dataSourceId, 
                                        user.getId());
                            }
                            for (Subscription s : uploads) {
                                User user = userManager.load(s.getUser());
                                if (s.getDataSource()
                                        .equals(dataSource.getName())
                                        && usersSelected
                                            .containsKey(user.getId())) {
                                    subscriptionManager.delete(s.getId());
                                }
                            }
                        } else {
                            for (Subscription s : uploads) {
                                if (s.getDataSource()
                                        .equals(dataSource.getName())) {
                                    subscriptionManager.delete(s.getId());
                                }
                            }
                        }
                        // Configure filters
                        CombinedFilter combinedFilter = new CombinedFilter();
                        combinedFilter.setMatchType(CombinedFilter
                                .MatchType.ALL);
                        AttributeFilter sourceFilter = new AttributeFilter();
                        if (!wmoAttrValue.isEmpty()) {
                            sourceFilter.setAttribute(DS_SOURCE_ATTR_STR);
                            sourceFilter.setValueType(
                                    AttributeFilter.ValueType.STRING);
                            sourceFilter.setOperator(AttributeFilter
                                    .Operator.IN);
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
                view = SUCCESS_VIEW;
            }
        }
        setModelAttributes(model);
        return view;
    }
    
    /**
     * Set model attributes.
     * @param model Model map 
     */
    private void setModelAttributes(ModelMap model) {
        model.addAttribute("radars_selected", radarsSelected);
        model.addAttribute("radars_available", radarsAvailable);
        model.addAttribute("file_objects_selected", fileObjectsSelected);
        model.addAttribute("file_objects_available", fileObjectsAvailable);
        model.addAttribute("users_selected", usersSelected);
        model.addAttribute("users_available", usersAvailable);
    }
    
    /**
     * Clear model data.
     */
    private void clearModelData() {
        radarsAvailable.clear();
        radarsSelected.clear();
        fileObjectsAvailable.clear();
        fileObjectsSelected.clear();
        usersAvailable.clear();
        usersSelected.clear();   
    }
    
    /**
     * Convert string array to integer array.
     * @param stringArray Input string array
     * @return Array containing integer values
     */
    private int[] toIntArray(String[] stringArray) {
        int[] intArray = new int[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            intArray[i] = Integer.parseInt(stringArray[i].trim());
        }
        return intArray;
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
    public void setRadarManager(IRadarManager radarManager) {
        this.radarManager = radarManager;
    }

    /**
     * @param fileObjectManager the fileObjectManager to set
     */
    @Autowired
    public void setFileObjectManager(IFileObjectManager fileObjectManager) {
        this.fileObjectManager = fileObjectManager;
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
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
    public void setSubscriptionManager(ISubscriptionManager subscriptionManager) 
    {
        this.subscriptionManager = subscriptionManager;
    }

}
