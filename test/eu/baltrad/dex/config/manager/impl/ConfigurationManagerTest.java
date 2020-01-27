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
import java.io.File;
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
        props.setProperty("framepublisher.min_poolsize", "1");
        props.setProperty("framepublisher.max_poolsize", "5");
        props.setProperty("framepublisher.queuesize", "100");
        
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
        AppConfiguration ac = new AppConfiguration(loadProperties(saveProperties(props)));
        assertTrue(this.appConf.equals(ac));
    }
    
    public void testSaveLogConf() {
        LogConfiguration lc = new LogConfiguration(loadProperties(saveProperties(props)));
        assertTrue(this.logConf.equals(lc));
    }
    
    public void testSaveRegistryConf() {
        RegistryConfiguration rc = new RegistryConfiguration(loadProperties(saveProperties(props)));
        assertTrue(this.regConf.equals(rc));
    }
    
    private Properties loadProperties() {
      return loadProperties(new File(this.getClass().getResource("fixtures/dex.properties").getFile()));
    }
    
    private Properties loadProperties(File f) {
      try {
        Properties properties = new Properties();
        properties.load(new FileInputStream(f));
        return properties;
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    private File saveProperties(Properties properties) {
      try {
        File file = File.createTempFile("tempproperties", null);
        file.deleteOnExit();
        properties.store(new FileOutputStream(file), "DEX main configuration file");
        return file;
      } catch (Exception e) {
        return null;
      }
    }
}

