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

package eu.baltrad.dex.net.servlet;

import eu.baltrad.dex.net.util.Authenticator;
import eu.baltrad.dex.net.util.IJsonUtil;
import eu.baltrad.dex.net.util.JsonUtil;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.IUserManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.IDataSourceManager;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.log.model.MessageLogger;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Data source list servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListServletTest {
    private static final String JSON_SOURCES = 
            "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":3,\"" 
            + "description\":\"Yet another test data source\"}]";
    
    private DataSourceListServlet classUnderTest;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @Before
    public void setUp() throws Exception {
        classUnderTest = new DataSourceListServlet("test.baltrad.eu", 
                "http://test.baltrad.eu");
        classUnderTest.setLog(MessageLogger.getLogger(MessageLogger.SYS_DEX));
        messages = new MessageResourceUtil("resources/messages");
        classUnderTest.setMessages(messages);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
    }   
    
    @Test
    public void foo() {}
    
    /*@Test
    public void handleRequest_Unauthorized() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(null, null)).andReturn(Boolean.FALSE)
                .anyTimes();
        expect(authMock.getMessage(isA(MockHttpServletRequest.class)))
                .andReturn(null);
        expect(authMock.getSignature(isA(MockHttpServletRequest.class)))
                .andReturn(null);
        replay(authMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
                messages.getMessage("datasource.server.unauthorized_request"), 
                response.getErrorMessage());
        reset(authMock);
    }
    
    @Test 
    public void handleRequest_InternalServerError() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(null, null)).andReturn(Boolean.TRUE)
                .anyTimes();
        expect(authMock.getMessage(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getSignature(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getNodeName(isA(MockHttpServletRequest.class)))
                .andReturn("test.baltrad.eu").anyTimes();
        expect(authMock.getNodeAddress(isA(MockHttpServletRequest.class)))
                .andReturn("http://test.baltrad.eu").anyTimes();
        replay(authMock);
        
        IUserManager userManagerMock = createMock(IUserManager.class);
        expect(userManagerMock.getByName("test.baltrad.eu")).andReturn(null)
                .anyTimes();
        expect(userManagerMock.saveOrUpdatePeer(isA(User.class)))
                .andThrow(new Exception("Internal server error")).anyTimes();
        replay(userManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(userManagerMock);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
                messages.getMessage("datasource.server.internal_server_error",
                    new String[] {"Internal server error"}), 
                response.getErrorMessage());
        reset(authMock);
        reset(userManagerMock);
    }
    
    @Test 
    public void handleRequest_OK() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(null, null)).andReturn(Boolean.TRUE)
                .anyTimes();
        expect(authMock.getMessage(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getSignature(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getNodeName(isA(MockHttpServletRequest.class)))
                .andReturn("test.baltrad.eu").anyTimes();
        expect(authMock.getNodeAddress(isA(MockHttpServletRequest.class)))
                .andReturn("http://test.baltrad.eu").anyTimes();
        replay(authMock);
        
        IUserManager userManagerMock = createMock(IUserManager.class);
        expect(userManagerMock.getByName("test.baltrad.eu")).andReturn(null)
                .anyTimes();
        expect(userManagerMock.saveOrUpdatePeer(isA(User.class)))
                .andReturn(1).anyTimes();
        replay(userManagerMock);
        
        IJsonUtil jsonUtilMock = createMock(IJsonUtil.class);
        expect(jsonUtilMock.dataSourcesToJson(isA(HashSet.class)))
                .andReturn(JSON_SOURCES).anyTimes();
        replay(jsonUtilMock);
        
        IDataSourceManager dataSourceManagerMock = createMock(
                IDataSourceManager.class);
        expect(dataSourceManagerMock.load(0))
                .andReturn(new ArrayList<DataSource>()).anyTimes();
        replay(dataSourceManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(userManagerMock);
        verify(jsonUtilMock);
        verify(dataSourceManagerMock);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        String dataSourceString = response.getContentAsString();
        JsonUtil jsonUtil = new JsonUtil();
        Set<DataSource> dataSources = jsonUtil
                .jsonToDataSources(dataSourceString);
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
        
        reset(authMock);
        reset(userManagerMock);
        reset(jsonUtilMock);
        reset(dataSourceManagerMock);
    }*/
    
}
