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

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.util.DataSourceValidator;
import eu.baltrad.dex.util.MessageResourceUtil;


import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;


import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.*;

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
    
    private DataSourceValidator validator;
    
    private MessageResourceUtil messages;
    
    
    
    
    private List mocks;
    
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
        expect(dataSourceManagerMock.load(101))
                .andReturn(new DataSource(101, "TestDataSource", "local",
                "A test data source"));
        replayAll();
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    } 
    
    @Test
    public void setupForm_NewDataSource() {
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm(null, model);
        assertEquals("save_datasource", viewName);
        assertTrue(model.containsAttribute("data_source"));
    }
    
    @Test
    public void setupForm_ExistingDataSource() {
        ModelMap model = new ModelMap();
        String viewName = classUnderTest.setupForm("101", model);
        verifyAll();
        assertEquals("save_datasource", viewName);
        assertTrue(model.containsAttribute("data_source"));
        DataSource dataSource = (DataSource) model.get("data_source");
        assertNotNull(dataSource);
        assertEquals("TestDataSource", dataSource.getName());
        assertEquals("A test data source", dataSource.getDescription());
    }
    
    @Test
    public void processSubmit_ValidationFailure() {
        DataSource dataSource = null;
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(model, 
                "data_source");
        result.addError(new FieldError("data_source", "name", "Missing name"));
        String viewName = classUnderTest.processSubmit(dataSource, result, 
                model, request, null, null, null);
        assertEquals("save_datasource", viewName);
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

