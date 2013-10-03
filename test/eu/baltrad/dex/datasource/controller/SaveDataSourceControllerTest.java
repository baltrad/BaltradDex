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

import eu.baltrad.beast.db.AttributeFilter;
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
import eu.baltrad.dex.net.manager.ISubscriptionManager;

import eu.baltrad.beast.db.IFilterManager;
import eu.baltrad.beast.db.CombinedFilter;
import eu.baltrad.beast.db.IFilter;
import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.net.model.impl.Subscription;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionException;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Save data source controller test.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class SaveDataSourceControllerTest {
    
    private SaveDSController classUnderTest;
    private IDataSourceManager dataSourceManagerMock;
    private IRadarManager radarManagerMock;
    private IFileObjectManager fileObjectManagerMock;
    private PlatformTransactionManager transactionManagerMock;
    private IUserManager userManagerMock;
    private IFilterManager filterManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private DataSourceValidator validator;
    private MessageResourceUtil messages;
    
    private List mocks;
    
    private DataSource dataSource;
    private List<Radar> radarsAvailable;
    private List<Radar> radarsSelected;
    private List<FileObject> fileObjectsAvailable;
    private List<FileObject> fileObjectsSelected;
    private List<User> usersAvailable;
    private List<User> usersSelected;
    
    class SaveDSController extends SaveDataSourceController {
        
        public Map<Integer, Radar> getRadarsAvailable() {
            return radarsAvailable;
        }
        public void setRadarsAvailable(Map<Integer, Radar> radarsAvailable) {
            this.radarsAvailable = radarsAvailable;
        }
        public Map<Integer, Radar> getRadarsSelected() {
            return radarsSelected;
        }
        public void setRadarsSelected(Map<Integer, Radar> radarsSelected) {
            this.radarsSelected = radarsSelected;
        }
        public Map<Integer, FileObject> getFileObjectsAvailable() {
            return fileObjectsAvailable;
        }
        public void setFileObjectsAvailable(
                Map<Integer, FileObject> fileObjectsAvailable) {
            this.fileObjectsAvailable = fileObjectsAvailable;
        }
        public Map<Integer, FileObject> getFileObjectsSelected() {
            return fileObjectsSelected;
        }
        public void setFileObjectsSelected(
                Map<Integer, FileObject> fileObjectsSelected) {
            this.fileObjectsSelected = fileObjectsSelected;
        }
        public Map<Integer, User> getUsersAvailable() {
            return usersAvailable;
        }
        public void setUsersAvailable(Map<Integer, User> usersAvailable) {
            this.usersAvailable = usersAvailable;
        }
        public Map<Integer, User> getUsersSelected() {
            return usersSelected;
        }
        public void setUsersSelected(Map<Integer, User> usersSelected) {
            this.usersSelected = usersSelected;
        }
        
    }
    
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
        
        radarsAvailable = new ArrayList<Radar>();
        radarsAvailable.add(new Radar(1, "PL", "SOWR", 220, "Legionowo", "PL41", 
                "12374"));
        radarsAvailable.add(new Radar(2, "PL", "SOWR", 220, "Rzeszów", "PL44", 
                "12579"));
        radarsAvailable.add(new Radar(3, "PL", "SOWR", 220, "Poznań", "PL45", 
                "12331"));
        
        radarsSelected = new ArrayList<Radar>();
        radarsSelected.add(new Radar(1, "PL", "SOWR", 220, "Legionowo", "PL41", 
                "12374"));
        
        fileObjectsAvailable = new ArrayList<FileObject>();
        fileObjectsAvailable.add(new FileObject(1, "SCAN", "Polar scan"));
        fileObjectsAvailable.add(new FileObject(2, "PVOL", "Polar volume"));
        fileObjectsAvailable.add(new FileObject(3, "IMAGE", "Cartesian image"));
        
        fileObjectsSelected = new ArrayList<FileObject>();
        fileObjectsSelected.add(new FileObject(2, "PVOL", "Polar volume"));
        
        usersAvailable = new ArrayList<User>();
        usersAvailable.add(new User(1, "User1", Role.PEER, "s3cret", "org", 
                "unit", "locality", "state", "XX", "http://localhost:8084"));
        usersAvailable.add(new User(2, "User2", Role.PEER, "s3cret", "org", 
                "unit", "locality", "state", "XX", "http://localhost:8084"));
        
        usersSelected = new ArrayList<User>();
        usersSelected.add(new User(1, "User1", Role.PEER, "s3cret", "org", 
                "unit", "locality", "state", "XX", "http://localhost:8084"));
        usersSelected.add(new User(2, "User2", Role.PEER, "s3cret", "org", 
                "unit", "locality", "state", "XX", "http://localhost:8084"));
        
        mocks = new ArrayList();
        
        classUnderTest = new SaveDSController();
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
        transactionManagerMock = (PlatformTransactionManager) 
                createMock(PlatformTransactionManager.class);
        filterManagerMock = (IFilterManager) 
                createNiceMock(IFilterManager.class);
        subscriptionManagerMock = 
                (ISubscriptionManager) createMock(ISubscriptionManager.class);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        dataSource = null;
        radarsAvailable = null;
        radarsSelected = null;
        fileObjectsAvailable = null;
        fileObjectsSelected = null;
        usersAvailable = null;
        usersSelected = null;
        resetAll();
    } 
    
    @Test
    public void setupForm_NewDataSource() {
        expect(radarManagerMock.load()).andReturn(radarsAvailable).once();
        expect(fileObjectManagerMock.load()).andReturn(fileObjectsAvailable)
                .once();
        expect(userManagerMock.load()).andReturn(usersAvailable).once();
        
        replayAll();
        
        classUnderTest.setFileObjectManager(fileObjectManagerMock);
        classUnderTest.setRadarManager(radarManagerMock);        
        classUnderTest.setUserManager(userManagerMock);
        
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm(null, model);
        
        verifyAll();
        
        assertEquals("datasources_save", viewName);
        assertTrue(model.containsAttribute("data_source"));
        assertEquals(3, classUnderTest.getRadarsAvailable().size());
        assertEquals(0, classUnderTest.getRadarsSelected().size());
        assertEquals(3, classUnderTest.getFileObjectsAvailable().size());
        assertEquals(0, classUnderTest.getFileObjectsSelected().size());
        assertEquals(2, classUnderTest.getUsersAvailable().size());
        assertEquals(0, classUnderTest.getUsersSelected().size());
    }
    
    @Test
    public void setupForm_ExistingDataSource() {
        expect(dataSourceManagerMock.load(101)).andReturn(dataSource).once();
        expect(dataSourceManagerMock.loadRadar(101)).andReturn(radarsSelected)
                .once();
        expect(dataSourceManagerMock.loadFileObject(101))
                .andReturn(fileObjectsSelected).once();
        expect(dataSourceManagerMock.loadUser(101)).andReturn(usersSelected)
                .once();
        expect(radarManagerMock.load()).andReturn(radarsAvailable).once();
        expect(fileObjectManagerMock.load()).andReturn(fileObjectsAvailable)
                .once();
        expect(userManagerMock.load()).andReturn(usersAvailable).once();
        
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
        
        assertEquals(2, classUnderTest.getRadarsAvailable().size());
        assertEquals(1, classUnderTest.getRadarsSelected().size());
        assertEquals(2, classUnderTest.getFileObjectsAvailable().size());
        assertEquals(1, classUnderTest.getFileObjectsSelected().size());
        assertEquals(0, classUnderTest.getUsersAvailable().size());
        assertEquals(2, classUnderTest.getUsersSelected().size());
    }
    
    @Test
    public void processSubmit_AddRadar() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("add_radar", "Add radar");
        request.addParameter("radars_available", new String[] {"1", "3"});
        
        BindingResult result = new BeanPropertyBindingResult(dataSource, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", dataSource);
        
        Map<Integer, Radar> radars = new HashMap<Integer, Radar>();
        for (Radar radar : radarsAvailable) {
            radars.put(radar.getId(), radar);
        }
        classUnderTest.setRadarsAvailable(radars);
        
        String viewName = classUnderTest
                .processSubmit(dataSource, result, model, request);
        
        assertEquals("datasources_save", viewName);
        assertEquals(1, classUnderTest.getRadarsAvailable().size());
        assertEquals(2, classUnderTest.getRadarsSelected().size());
    }
    
    @Test
    public void processSubmit_RemoveRadar() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("remove_radar", "Remove radar");
        request.addParameter("radars_selected", new String[] {"1"});
        BindingResult result = new BeanPropertyBindingResult(dataSource, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", dataSource);
        
        Map<Integer, Radar> radarsAv = new HashMap<Integer, Radar>();
        Map<Integer, Radar> radarsSel = new HashMap<Integer, Radar>();
        for (Radar radar : radarsAvailable) {
            if (radar.getId() == 3) {        
                radarsAv.put(radar.getId(), radar);
            }
        }
        classUnderTest.setRadarsAvailable(radarsAv);
        for (Radar radar : radarsAvailable) {
            if (radar.getId() == 1 || radar.getId() == 2) {
                radarsSel.put(radar.getId(), radar);
            }
        }
        classUnderTest.setRadarsSelected(radarsSel);
        
        String viewName = classUnderTest
                .processSubmit(dataSource, result, model, request);
        
        assertEquals("datasources_save", viewName);
        assertEquals(2, classUnderTest.getRadarsAvailable().size());
        assertEquals(1, classUnderTest.getRadarsSelected().size());
    }
    
    @Test
    public void processSubmit_AddFileObject() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("add_file_object", "Add file object");
        request.addParameter("file_objects_available", new String[] {"1", "2"});
        
        BindingResult result = new BeanPropertyBindingResult(dataSource, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", dataSource);
        
        Map<Integer, FileObject> fileObjects = 
                new HashMap<Integer, FileObject>();
        for (FileObject fileObject : fileObjectsAvailable) {
            fileObjects.put(fileObject.getId(), fileObject);
        }
        classUnderTest.setFileObjectsAvailable(fileObjects);
        
        String viewName = classUnderTest
                .processSubmit(dataSource, result, model, request);
        
        assertEquals("datasources_save", viewName);
        assertEquals(1, classUnderTest.getFileObjectsAvailable().size());
        assertEquals(2, classUnderTest.getFileObjectsSelected().size());
    }
    
    @Test
    public void processSubmit_RemoveFileObject() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("remove_file_object", "Remove file object");
        request.addParameter("file_objects_selected", new String[] {"3"});
        BindingResult result = new BeanPropertyBindingResult(dataSource, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", dataSource);
        
        Map<Integer, FileObject> fileObjectsAv = 
                new HashMap<Integer, FileObject>();
        Map<Integer, FileObject> fileObjectsSel = 
                new HashMap<Integer, FileObject>();
        for (FileObject fileObject: fileObjectsAvailable) {
            if (fileObject.getId() == 1 || fileObject.getId() == 2) {        
                fileObjectsAv.put(fileObject.getId(), fileObject);
            }
        }
        classUnderTest.setFileObjectsAvailable(fileObjectsAv);
        for (FileObject fileObject : fileObjectsAvailable) {
            if (fileObject.getId() == 3) {
                fileObjectsSel.put(fileObject.getId(), fileObject);
            }
        }
        classUnderTest.setFileObjectsSelected(fileObjectsSel);
        
        String viewName = classUnderTest
                .processSubmit(dataSource, result, model, request);
        
        assertEquals("datasources_save", viewName);
        assertEquals(3, classUnderTest.getFileObjectsAvailable().size());
        assertEquals(0, classUnderTest.getFileObjectsSelected().size());
    }
    
    @Test
    public void processSubmit_AddUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("add_user", "Add user");
        request.addParameter("users_available", new String[] {"1", "2"});
        
        BindingResult result = new BeanPropertyBindingResult(dataSource, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", dataSource);
        
        Map<Integer, User> users = new HashMap<Integer, User>();
        for (User user : usersAvailable) {
            users.put(user.getId(), user);
        }
        classUnderTest.setUsersAvailable(users);
        
        String viewName = classUnderTest
                .processSubmit(dataSource, result, model, request);
        
        assertEquals("datasources_save", viewName);
        assertEquals(0, classUnderTest.getUsersAvailable().size());
        assertEquals(2, classUnderTest.getUsersSelected().size());
    }
    
    @Test
    public void processSubmit_RemoveUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("remove_user", "Remove user");
        request.addParameter("users_selected", new String[] {"2"});
        BindingResult result = new BeanPropertyBindingResult(dataSource, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", dataSource);
        
        Map<Integer, User> usersAv = new HashMap<Integer, User>();
        Map<Integer, User> usersSel = new HashMap<Integer, User>();
        for (User user : usersAvailable) {
            if (user.getId() == 1) {        
                usersAv.put(user.getId(), user);
            }
        }
        classUnderTest.setUsersAvailable(usersAv);
        for (User user : usersAvailable) {
            if (user.getId() == 2) {
                usersSel.put(user.getId(), user);
            }
        }
        classUnderTest.setUsersSelected(usersSel);
        
        String viewName = classUnderTest
                .processSubmit(dataSource, result, model, request);
        
        assertEquals("datasources_save", viewName);
        assertEquals(2, classUnderTest.getUsersAvailable().size());
        assertEquals(0, classUnderTest.getUsersSelected().size());
    }
    
    @Test
    public void processSubmit_NullDataSource() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(model, 
                "data_source");
        
        DataSource ds = null;
        String viewName = classUnderTest.processSubmit(ds, result, 
                model, request);
        
        assertEquals("datasources_save", viewName);
    }
    
    @Test
    public void processSubmit_DataSourceWithMissingName() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        DataSource ds = new DataSource(null, DataSource.LOCAL, 
                "A test data source");
        BindingResult result = new BeanPropertyBindingResult(ds, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", ds);
        
        String viewName = classUnderTest
                .processSubmit(ds, result, model, request);
        FieldError error = result.getFieldError("name");
        
        assertTrue(result.hasFieldErrors());
        assertEquals("savedatasource.missing.name", error.getCode());
        assertEquals(messages.getMessage("savedatasource.missing.name"), 
                error.getDefaultMessage());
        assertEquals("datasources_save", viewName);
    }
    
    @Test
    public void processSubmit_DataSourceWithMissingDescription() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        DataSource ds = new DataSource("TestPVOL", DataSource.LOCAL, null);
        BindingResult result = new BeanPropertyBindingResult(ds, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", ds);
        
        String viewName = classUnderTest
                .processSubmit(ds, result, model, request);
        
        FieldError error = result.getFieldError("description");
        
        assertTrue(result.hasFieldErrors());
        assertEquals("savedatasource.missing.description", error.getCode());
        assertEquals(messages.getMessage("savedatasource.missing.description"), 
                error.getDefaultMessage());
        assertEquals("datasources_save", viewName);
    }
    
    @Test
    public void processSubmit_DataSourceWithMissingRadars() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        
        DataSource ds = new DataSource("TestPVOL", DataSource.LOCAL, 
                "A test data source");
        BindingResult result = new BeanPropertyBindingResult(ds, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", ds);
        
        String viewName = classUnderTest
                .processSubmit(ds, result, model, request);
        
        assertNotNull(request.getSession().getAttribute("missing_radar_error"));
        assertEquals(messages.getMessage("savedatasource.missing.radar"),
                request.getSession().getAttribute("missing_radar_error"));
        assertEquals("datasources_save", viewName);
    }
    
    @Test
    public void processSubmit_TransactionException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        DataSource ds = new DataSource("TestPVOL", DataSource.LOCAL, 
                "A test data source");
        BindingResult result = new BeanPropertyBindingResult(ds, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", ds);
        Map<Integer, Radar> radarsSel = new HashMap<Integer, Radar>();
        for (Radar radar : radarsSelected) {
            radarsSel.put(radar.getId(), radar);
        }
        classUnderTest.setRadarsSelected(radarsSel);
        
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = new SimpleTransactionStatus();
        TransactionException te = 
                new CannotCreateTransactionException("Transaction exception");
        expect(transactionManagerMock.getTransaction(def)).andReturn(status)
                .once();
        transactionManagerMock.commit(status);
        expectLastCall().andThrow(te);
        transactionManagerMock.rollback(status);
        expectLastCall();
        expect(dataSourceManagerMock.store(ds)).andReturn(0).once();
        
        replayAll();
        
        classUnderTest.setTransactionManager(transactionManagerMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        
        String viewName = classUnderTest
                .processSubmit(ds, result, model, request);
        
        assertEquals("datasources_save_status", viewName);
        assertTrue(model.containsAttribute("datasource_save_error"));
        assertEquals(messages.getMessage("savedatasource.failure", 
            new Object[] {ds.getName()}), model.get("datasource_save_error"));
    }
    
    @Test
    public void processSubmit_SaveFail() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        DataSource ds = new DataSource("TestPVOL", DataSource.LOCAL, 
                "A test data source");
        BindingResult result = new BeanPropertyBindingResult(ds, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", ds);
        Map<Integer, Radar> radarsSel = new HashMap<Integer, Radar>();
        for (Radar radar : radarsSelected) {
            radarsSel.put(radar.getId(), radar);
        }
        classUnderTest.setRadarsSelected(radarsSel);
        
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = new SimpleTransactionStatus();
        expect(transactionManagerMock.getTransaction(def)).andReturn(status)
                .once();
        transactionManagerMock.commit(status);
        expectLastCall();
        expect(dataSourceManagerMock.store(ds)).andReturn(0).once();
        
        replayAll();
        
        classUnderTest.setTransactionManager(transactionManagerMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        
        String viewName = classUnderTest
                .processSubmit(ds, result, model, request);
        
        assertEquals("datasources_save_status", viewName);
        assertTrue(model.containsAttribute("datasource_save_error"));
        assertEquals(messages.getMessage("savedatasource.failure", 
            new Object[] {ds.getName()}), model.get("datasource_save_error"));
    }

    @Test
    public void processSubmit_OK() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("save_data_source", "Save");
        
        DataSource ds = new DataSource("TestPVOL", DataSource.LOCAL, 
                "A test data source");
        BindingResult result = new BeanPropertyBindingResult(ds, 
                "data_source");
        ModelMap model = new ModelMap();
        model.addAttribute("data_source", ds);
        Map<Integer, Radar> radarsSel = new HashMap<Integer, Radar>();
        for (Radar radar : radarsSelected) {
            radarsSel.put(radar.getId(), radar);
        }
        classUnderTest.setRadarsSelected(radarsSel);
        
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = new SimpleTransactionStatus();
        
        dataSourceManagerMock = (IDataSourceManager) 
                createNiceMock(IDataSourceManager.class);
        expect(transactionManagerMock.getTransaction(def)).andReturn(status)
                .once();
        transactionManagerMock.commit(status);
        expectLastCall();
        
        replayAll();
        
        classUnderTest.setTransactionManager(transactionManagerMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        
        String viewName = classUnderTest
                .processSubmit(ds, result, model, request);
        
        assertEquals("datasources_save_status", viewName);
    }
    
}

