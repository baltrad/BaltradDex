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

package eu.baltrad.dex.config.manager.impl;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.model.RegistryConfiguration;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.util.ServletContextUtil;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;

import java.util.Properties;

/**
 * Class implements configuration object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.1
 * @since 1.0.1
 */
public class ConfigurationManager implements IConfigurationManager, 
        InitializingBean {
    
    static final String DEX_PROPS_FILE = "dex.properties";
    
    private AppConfiguration appConf;
    private LogConfiguration logConf;
    private RegistryConfiguration registryConf;
    
    private IUserManager userManager;
    
    private Logger log;
    
    /**
     * Constructor
     */
    public ConfigurationManager() {
        this.log = Logger.getLogger("DEX");
        Properties props = loadProperties();
        this.appConf = new AppConfiguration(props); 
        this.logConf = new LogConfiguration(props);
        this.registryConf = new RegistryConfiguration(props);
        createDirs();
    }
    
    /**
     * Create local peer account.
     */
    public void afterPropertiesSet() {
        User user = new User(appConf.getNodeName(), Role.NODE, null,
                appConf.getOrgName(), appConf.getOrgUnit(), 
                appConf.getLocality(), appConf.getState(), 
                appConf.getCountryCode(), appConf.getNodeAddress());
        try {
            if (userManager.load(appConf.getNodeName()) == null) {
                userManager.store(user);
                log.warn("Created local peer account: " 
                        + appConf.getNodeName());
            }
        } catch (Exception e) {
            log.error("Failed to create local peer account", e);
        }
    }
    
    /**
     * @return the appConf
     */
    public AppConfiguration getAppConf() {
        return appConf;
    }
    
    /**
     * @return the logConf
     */
    public LogConfiguration getLogConf() {
        return logConf;
    }

    /**
     * @return the registryConf
     */
    public RegistryConfiguration getRegistryConf() {
        return registryConf;
    }
    
    /**
     * Loads system configuration from properties file.
     * @return Application properties
     */
    private Properties loadProperties() {
        try {
            FileInputStream fis = null;
            Properties props = new Properties();
            try {
                fis = new FileInputStream(
                    ServletContextUtil.getServletContextPath() 
                        + DEX_PROPS_FILE);
                props.load(fis);
                return props;
            } finally {
                fis.close();
            }
        } catch (Exception e) {
           log.error("Failed to load application properties", e);
           return null;
        }
    }
    
    /**
     * Save properties to system configuration file.
     * @param props Properties to save
     */
    private void saveProperties(Properties props) {
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(
                        ServletContextUtil.getServletContextPath() 
                            + DEX_PROPS_FILE); 
                props.store(fos, "DEX main configuration file");
            } finally {
                fos.close();
            }
        } catch (Exception e) {
            log.error("Failed to save application properties", e);
        }
    }
     
    /**
     * Saves system configuration in properties file.
     * @param conf Application configuration object
     * @throws Exception
     */
    public void saveAppConf(AppConfiguration conf) {
        try {
            Properties props = loadProperties();
            props.setProperty(AppConfiguration.NODE_NAME, 
                    conf.getNodeName());
            props.setProperty(AppConfiguration.NODE_ADDRESS, 
                    conf.getNodeAddress());
            props.setProperty(AppConfiguration.NODE_TYPE, 
                    conf.getNodeType());
            props.setProperty(AppConfiguration.ORG_NAME, conf.getOrgName());
            props.setProperty(AppConfiguration.ORG_UNIT, conf.getOrgUnit());
            props.setProperty(AppConfiguration.LOCALITY, conf.getLocality());
            props.setProperty(AppConfiguration.STATE, conf.getState());
            props.setProperty(AppConfiguration.COUNTRY_CODE, 
                    conf.getCountryCode());
            props.setProperty(AppConfiguration.TIME_ZONE, 
                    conf.getTimeZone());
            props.setProperty(AppConfiguration.ADMIN_EMAIL, 
                    conf.getAdminEmail());
            props.setProperty(AppConfiguration.WORK_DIR, conf.getWorkDir());
            props.setProperty(AppConfiguration.IMAGES_DIR, 
                    conf.getImagesDir() == null ? 
                    props.getProperty(AppConfiguration.IMAGES_DIR) 
                        : conf.getImagesDir());
            props.setProperty(AppConfiguration.THUMBS_DIR, 
                    conf.getThumbsDir() == null ?
                    props.getProperty(AppConfiguration.THUMBS_DIR)
                        : conf.getThumbsDir());
            props.setProperty(AppConfiguration.KEYSTORE_DIR, 
                    conf.getKeystoreDir() == null ?
                    props.getProperty(AppConfiguration.KEYSTORE_DIR)
                        : conf.getKeystoreDir());
            props.setProperty(AppConfiguration.VERSION, 
                    conf.getVersion() == null ?
                    props.getProperty(AppConfiguration.VERSION)
                        : conf.getVersion());
            props.setProperty(AppConfiguration.SO_TIMEOUT, 
                    conf.getSoTimeout() == null ?
                    props.getProperty(AppConfiguration.SO_TIMEOUT)
                        : conf.getSoTimeout());
            props.setProperty(AppConfiguration.CONN_TIMEOUT, 
                    conf.getConnTimeout() == null ?
                    props.getProperty(AppConfiguration.CONN_TIMEOUT)
                        : conf.getConnTimeout());
            saveProperties(props);
            this.appConf = new AppConfiguration(props);
            createDirs();
        } catch (Exception e) {
            log.error("Failed to save application properties", e);
        }
    }
    
    /**
     * Save system log configuration.
     * @param conf Log configuration object
     */
    public void saveLogConf(LogConfiguration conf) {
        try {
            Properties props = loadProperties();
            props.setProperty(LogConfiguration.TRIM_BY_NUMBER, 
                    conf.getMsgTrimByNumber());
            props.setProperty(LogConfiguration.TRIM_BY_AGE, 
                    conf.getMsgTrimByAge());
            props.setProperty(LogConfiguration.REC_LIMIT, 
                    conf.getMsgRecordLimit());
            props.setProperty(LogConfiguration.MAX_DAYS, 
                    conf.getMsgMaxAgeDays());
            props.setProperty(LogConfiguration.MAX_HOURS, 
                    conf.getMsgMaxAgeHours());
            props.setProperty(LogConfiguration.MAX_MINUTES, 
                    conf.getMsgMaxAgeMinutes());
            saveProperties(props);
            this.logConf = new LogConfiguration(props);
        } catch( Exception e ) {
            log.error("Failed to save system log properties", e);
        }
    }
    
    /**
     * Save delivery registry configuration.
     * @param conf Delivery registry configuration object
     */
    public void saveRegistryConf(RegistryConfiguration conf) {
        try {
            Properties props = loadProperties();
            props.setProperty(RegistryConfiguration.TRIM_BY_NUMBER, 
                    conf.getRegTrimByNumber());
            props.setProperty(RegistryConfiguration.TRIM_BY_AGE, 
                    conf.getRegTrimByAge());
            props.setProperty(RegistryConfiguration.REC_LIMIT, 
                    conf.getRegRecordLimit());
            props.setProperty(RegistryConfiguration.MAX_DAYS, 
                    conf.getRegMaxAgeDays());
            props.setProperty(RegistryConfiguration.MAX_HOURS,
                    conf.getRegMaxAgeHours());
            props.setProperty(RegistryConfiguration.MAX_MINUTES, 
                    conf.getRegMaxAgeMinutes());
            saveProperties(props);
            this.registryConf = new RegistryConfiguration(props);
        } catch( Exception e ) {
            log.error("Failed to save delivery registry properties", e);
        }
    }
    
    /**
     * Creates new storage directory.
     * @param folder Either relative or absolute path to the folder
     * @return Name of the directory created
     */
    private boolean createDir(String folder) {
        String dir;
        if (folder.startsWith(File.separator)) {
            dir = folder;
        } else {
            dir = ServletContextUtil.getServletContextPath() + folder;
        }
        File f = new File(dir);
        if (!f.exists()) {
            return f.mkdirs();
        }
        return false;
    }
    
    /**
     * Create necessary directories. 
     */
    private void createDirs() {
        if (createDir(appConf.getWorkDir())) {
            log.warn("Created new work folder: " + appConf.getWorkDir());
        }
        if (createDir(appConf.getWorkDir() + File.separator 
                + appConf.getImagesDir())) {
            log.warn("Created new image folder: " + appConf.getWorkDir() 
                    + File.separator + appConf.getImagesDir());
        }
        if (createDir(appConf.getWorkDir() + File.separator 
                + appConf.getThumbsDir())) {
            log.warn("Created new thumbnails folder: " + appConf.getWorkDir() 
                    + File.separator + appConf.getThumbsDir());
        }        
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }
    
}

