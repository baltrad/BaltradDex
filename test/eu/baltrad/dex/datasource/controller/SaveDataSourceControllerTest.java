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

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.manager.IFileObjectManager;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.datasource.util.DataSourceValidator;
import eu.baltrad.dex.radar.manager.IRadarManager;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Save data source controller test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class SaveDataSourceControllerTest {
    
    private SaveDataSourceController classUnderTest;
    private IDataSourceManager dataSourceManagerMock;
    private IRadarManager radarManagerMock;
    private IFileObjectManager fileObjectManagerMock;
    private IUserManager userManagerMock;
    private DataSourceValidator validator;
    private MessageResourceUtil messages;
    
    private List mocks;
    
    private DataSource dataSource;
    private List<Radar> radars;
    private List<FileObject> fileObjects;
    private List<User> users;
    
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
    public void setUp() throws Exception {
        
        dataSource = new DataSource(1, "TestDataSource", DataSource.LOCAL, 
                "A test data source");
        
        radars = new ArrayList<Radar>();
        radars.add(new Radar(1, "PL", "SOWR", 220, "Legionowo", "PL41", "12374"));
        radars.add(new Radar(2, "PL", "SOWR", 220, "Rzeszów", "PL44", "12579"));
        radars.add(new Radar(3, "PL", "SOWR", 220, "Poznań", "PL45", "12331"));
        
        fileObjects = new ArrayList<FileObject>();
        fileObjects.add(new FileObject(1, "SCAN", "Polar scan"));
        fileObjects.add(new FileObject(2, "PVOL", "Polar volume"));
        fileObjects.add(new FileObject(3, "IMAGE", "Cartesian image"));
        
        users = new ArrayList<User>();
        users.add(new User(1, "User1", Role.PEER, "s3cret", "org", "unit", 
                "locality", "state", "XX", "http://localhost:8084"));
        users.add(new User(2, "User2", Role.PEER, "s3cret", "org", "unit", 
                "locality", "state", "XX", "http://localhost:8084"));
        
        mocks = new ArrayList();
        classUnderTest = new SaveDataSourceController();
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        validator = new DataSourceValidator();
        validator.setMessages(messages);
        classUnderTest.setValidator(validator);
        
        dataSourceManagerMock = (IDataSourceManager) 
                                        createMock(IDataSourceManager.class);
        radarManagerMock = (IRadarManager) createMock(IRadarManager.class);
        fileObjectManagerMock = 
                (IFileObjectManager) createMock(IFileObjectManager.class);
        userManagerMock = (IUserManager) createMock(IUserManager.class);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        dataSource = null;
        radars = null;
        fileObjects = null;
        users = null;
        resetAll();
    } 
    
    @Test
    public void setupForm_NewDataSource() {
        expect(radarManagerMock.load()).andReturn(radars).once();
        expect(fileObjectManagerMock.load()).andReturn(fileObjects).once();
        expect(userManagerMock.load()).andReturn(users).once();
        
        replayAll();
        
        classUnderTest.setFileObjectManager(fileObjectManagerMock);
        classUnderTest.setRadarManager(radarManagerMock);        
        classUnderTest.setUserManager(userManagerMock);
        
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm(null, model);
        
        verifyAll();
        
        assertEquals("datasources_save", viewName);
        assertTrue(model.containsAttribute("data_source"));
    }
    
    @Test
    public void setupForm_ExistingDataSource() {
        expect(dataSourceManagerMock.load(101)).andReturn(dataSource).once();
        expect(dataSourceManagerMock.loadRadar(101)).andReturn(radars).once();
        expect(dataSourceManagerMock.loadFileObject(101))
                .andReturn(fileObjects).once();
        expect(dataSourceManagerMock.loadUser(101)).andReturn(users).once();
        expect(radarManagerMock.load()).andReturn(radars).once();
        expect(fileObjectManagerMock.load()).andReturn(fileObjects).once();
        expect(userManagerMock.load()).andReturn(users).once();
        
        replayAll();
        
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        classUnderTest.setFileObjectManager(fileObjectManagerMock);
        classUnderTest.setRadarManager(radarManagerMock);        
        classUnderTest.setUserManager(userManagerMock);
        
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm("101", model);
        
        verifyAll();
        
        assertEquals("datasources_save", viewName);
        assertTrue(model.containsAttribute("data_source"));
        
        DataSource ds = (DataSource) model.get("data_source");
        
        assertNotNull(ds);
        assertEquals("TestDataSource", ds.getName());
        assertEquals("A test data source", ds.getDescription());
    }
    
    //@Test
    public void processSubmit_ValidationFailure() {
        DataSource dataSource = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(model, 
                "data_source");
        result.addError(new FieldError("data_source", "name", "Missing name"));
        String viewName = classUnderTest.processSubmit(dataSource, result, 
                model, request);
        assertEquals("datasources_save", viewName);
    }
    
    @Test 
    public void processSubmit_ExistingDataSource() {
        
    }
    
    @Test 
    public void processSubmit_NewDataSource() {
        
    } 
    
    
    @Test
    public void processSubmit_Failure() {
        
    }

    @Test
    public void processSubmit_OK() {
    
    }
    
    @Test
    public void getAllRadars() {
        
    }
    
    @Test
    public void getSelectedRadars() {
        
    }
    
    @Test
    public void getAllFileObjects() {
        
    }
    
    @Test
    public void getSelectedFileObjects() {
        
    }
    
    @Test
    public void getAllUsers() {
        
    }
    
    @Test
    public void getSelectedUsers() {
        
    }
    
}

