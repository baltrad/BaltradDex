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

import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.model.RegistryConfiguration;

import junit.framework.TestCase;

import java.util.Properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author szewczenko
 */
public class ConfigurationManagerTest extends TestCase {
    
    private Properties props;
    private AppConfiguration appConf;
    private LogConfiguration logConf;
    private RegistryConfiguration regConf;
    
    @Override
    public void setUp() {
        props = new Properties();
        props.setProperty("node.name", "dev.baltrad.eu");
        props.setProperty("node.address", "http://127.0.0.1:8084");
        props.setProperty("node.type", "Primary");
        props.setProperty("socket.timeout", "60000");
        props.setProperty("connection.timeout", "60000");
        props.setProperty("organization.name", "Baltrad");
        props.setProperty("organization.unit", "Radar Department");
        props.setProperty("organization.locality", "Locality"); 
        props.setProperty("organization.state", "State");
        props.setProperty("organization.country_code", "XX");
        props.setProperty("time.zone", "CEST Central European Time UTC+1");
        props.setProperty("work.directory", "work");
        props.setProperty("images.directory", "images");
        props.setProperty("thumbnails.directory", "thumbs");
        props.setProperty("admin.email", "admin@baltrad.eu");
        props.setProperty("software.version", "1.5");
        props.setProperty("messages.trim_by_number", "false");
        props.setProperty("messages.rec_limit", "70000");
        props.setProperty("messages.max_age_days", "45");
        props.setProperty("messages.max_age_hours", "18");
        props.setProperty("messages.max_age_minutes", "49");
        props.setProperty("messages.trim_by_age", "false");
        props.setProperty("registry.trim_by_number", "false");
        props.setProperty("registry.rec_limit", "80000");
        props.setProperty("registry.trim_by_age", "true");
        props.setProperty("registry.max_age_days", "50");
        props.setProperty("registry.max_age_hours", "7");
        props.setProperty("registry.max_age_minutes", "16");
        props.setProperty("keystore.directory", "keystore");
        
        this.appConf = new AppConfiguration(props);
        this.logConf = new LogConfiguration(props);
        this.regConf = new RegistryConfiguration(props);
    }
    
    @Override
    public void tearDown() {
        this.props = null;
        this.appConf = null;
        this.logConf = null;
        this.regConf = null;
    }
    
    public void testLoadProperties() {
        assertNotNull(loadProperties());
    }
    
    public void testSaveProperties() {
        saveProperties(props);
    }
    
    public void testSaveAppConf() {
        AppConfiguration ac = new AppConfiguration(loadProperties());
        assertTrue(this.appConf.equals(ac));
    }
    
    public void testSaveLogConf() {
        LogConfiguration lc = new LogConfiguration(loadProperties());
        assertTrue(this.logConf.equals(lc));
    }
    
    public void testSaveRegistryConf() {
        RegistryConfiguration rc = new RegistryConfiguration(loadProperties());
        assertTrue(this.regConf.equals(rc));
    }
    
    private Properties loadProperties() {
        try {
            FileInputStream fis = null;
            Properties properties = new Properties();
            try {
                fis = new FileInputStream("conf/dex.properties");
                properties.load(fis);
                return properties;
            } finally {
                fis.close();
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    private void saveProperties(Properties properties) {
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream("conf/dex.properties"); 
                properties.store(fos, "DEX main configuration file");
            } finally {
                fos.close();
            }
        } catch (Exception e) {
            fail();
        }
    }
    
}

