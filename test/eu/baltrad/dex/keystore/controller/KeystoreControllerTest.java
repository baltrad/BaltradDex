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

package eu.baltrad.dex.keystore.controller;

import eu.baltrad.dex.keystore.controller.KeystoreController;
import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.keystore.manager.IKeystoreManager;
import eu.baltrad.dex.keystore.model.Key;
import java.io.File;

import org.springframework.ui.ModelMap;
import org.springframework.ui.ExtendedModelMap;

import static org.easymock.EasyMock.*;
import org.easymock.EasyMock;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Test case for keystore controller class.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class KeystoreControllerTest {
    
    private KeystoreController classUnderTest;
    private IKeystoreManager keystoreManager;
    private IConfigurationManager confManager;
    private List<Object> mocks;
    private List<Key> keys;
    private AppConfiguration appConf;
    private File incoming;
    private File keyRevoked;
    private File keyGranted;
    
    private Object createMock(Class clazz) {
        Object mock = EasyMock.createMock(clazz);
        mocks.add(mock);
        return mock;
    }
    
    private void replayAll() {
        for (Object mock : mocks) {
            replay(mock);
        }
    }
    
    private void verifyAll() {
        for (Object mock : mocks) {
            verify(mock);
        }
    }
    
    private void resetAll() {
        for (Object mock : mocks) {
            reset(mock);
        }
    }
    
    @Before
    public void setUp() {
        classUnderTest = new KeystoreController();
        mocks = new ArrayList<Object>();
        keys = new ArrayList<Key>();
        keys.add(new Key(1, "test.baltrad.eu", 
                "fh7629shue7493kd893748du572895fi", true));
        keys.add(new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false));
        Properties props = new Properties();
        props.setProperty("node.name", "test.baltrad.eu");
        props.setProperty("keystore.directory", "keystore");
        
        incoming = new File("keystore/.incoming");
        if (!incoming.exists()) {
            incoming.mkdirs();
        }
        keyRevoked = new File("keystore/.incoming/test.baltrad.imgw.pl.pub");
        keyGranted = new File("keystore/test.baltrad.imgw.pl.pub");
        
        appConf = new AppConfiguration(props);
        
        keystoreManager = (IKeystoreManager) createMock(IKeystoreManager.class);
        confManager = (IConfigurationManager) 
                createMock(IConfigurationManager.class);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        if (keyRevoked.exists()) {
            keyRevoked.delete();
        }
        if (keyGranted.exists()) {
            keyGranted.delete();
        }
        if (incoming.exists()) {
            incoming.delete();
        }
        resetAll();
    }
    
    @Test
    public void setupForm() {
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.setupForm(model);
        
        verifyAll();
        
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_Exception() throws Exception {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.update(key)).andThrow(new Exception());
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, "2", null, null, 
                null);
        
        verifyAll();
        
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_GrantFailure() throws Exception {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.update(key)).andReturn(0);
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, "2", null, null, 
                null);
        
        verifyAll();
        
        assertFalse(keyGranted.exists());
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_GrantSuccess() throws Exception {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.update(key)).andReturn(1);
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf).times(3);
        
        replayAll();
        
        if (!keyRevoked.exists()) {
            keyRevoked.mkdirs();
        }
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, "2", null, null, 
                null);
        
        verifyAll();
        
        assertTrue(keyGranted.exists());
        assertFalse(keyRevoked.exists());
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_RevokeFailure() throws Exception {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.update(key)).andReturn(0);
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, null, "2", null, 
                null);
        
        verifyAll();
        
        assertFalse(keyRevoked.exists());
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_RevokeSuccess() throws Exception {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.update(key)).andReturn(1);
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf).times(3);
        
        replayAll();
        
        if (!keyGranted.exists()) {
            keyGranted.mkdirs();
        }
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, null, "2", null, 
                null);
        
        verifyAll();
        
        assertTrue(keyRevoked.exists());
        assertFalse(keyGranted.exists());
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_Delete() {
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, null, null, "2", 
                null);
        
        verifyAll();
        
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("delete_key_id"));
        assertEquals("2", (String) model.get("delete_key_id"));
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));   
    }
    
    @Test
    public void processSubmit_ConfirmDeleteFailure() {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.delete(2)).andReturn(0);
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        if (!keyRevoked.exists()) {
            keyRevoked.mkdirs();
        }
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, null, null, null, 
                "2");
        
        verifyAll();
        
        assertTrue(keyRevoked.exists());
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
    @Test
    public void processSubmit_ConfirmDeleteSuccess() {
        Key key = new Key(2, "test.baltrad.imgw.pl", 
                "yu987876xc6886x898df9sdf89sfd97y", false);
        
        expect(keystoreManager.load(2)).andReturn(key);
        expect(keystoreManager.delete(2)).andReturn(1);
        expect(keystoreManager.load()).andReturn(keys);
        expect(confManager.getAppConf()).andReturn(appConf).times(3);
        
        replayAll();
        
        if (!keyRevoked.exists()) {
            keyRevoked.mkdirs();
        }
        if (!keyGranted.exists()) {
            keyGranted.mkdirs();
        }
        
        classUnderTest.setKeystoreManager(keystoreManager);
        classUnderTest.setConfManager(confManager);
        ModelMap model = new ExtendedModelMap();
        String viewName = classUnderTest.processSubmit(model, null, null, null, 
                "2");
        
        verifyAll();
        
        assertFalse(keyRevoked.exists());
        assertFalse(keyGranted.exists());
        assertEquals("keystore", viewName);
        assertTrue(model.containsAttribute("keys"));
        assertTrue(model.containsAttribute("local_node_name"));
        assertEquals(keys, (List<Key>) model.get("keys"));
        assertEquals("test.baltrad.eu", (String) model.get("local_node_name"));
    }
    
}
