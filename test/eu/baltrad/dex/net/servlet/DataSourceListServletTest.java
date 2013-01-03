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

import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.user.manager.IAccountManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.keyczar.exceptions.KeyczarException;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Data source list servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListServletTest {
    
    private static final String JSON_ACCOUNT = "{\"nodeAddress\":\"" +
            "http://localhost:8084\",\"repeatPassword\":\"s3cret\",\"" +
            "roleName\":\"user\",\"name\":\"test\",\"id\":1,\"state\":\"" + 
            "state\",\"orgName\":\"org\",\"orgUnit\":\"unit\",\"locality\":\"" +
            "locality\",\"countryCode\":\"XX\",\"password\":\"s3cret\"}";
    
    
    private static final String JSON_SOURCES = 
            "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":3,\"" 
            + "description\":\"Yet another test data source\"}]";
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private List<Object> mocks;
    private DateFormat format;
    private DSLServlet classUnderTest;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Authenticator authenticatorMock;
    private Account account;
    
    class DSLServlet extends DataSourceListServlet {
        public DSLServlet() {
            this.localNode = new Account(1, "test", "s3cret", "org", "unit", 
                    "locality", "state", "XX", "user", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
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
        mocks = new ArrayList<Object>();
        authenticatorMock = (Authenticator) createMock(Authenticator.class);
        
        classUnderTest = new DSLServlet();
        classUnderTest.setLog(Logger.getLogger("DEX"));
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        account = new Account(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", "http://localhost:8084");
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        format = new SimpleDateFormat(DATE_FORMAT);
        setRequestAttributes(request);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    }
    
    private void setRequestAttributes(MockHttpServletRequest request) {
        request.setAttribute("Content-Type", "text/html");  
        request.setAttribute("Content-MD5", Base64.encodeBase64String(
                request.getRequestURI().getBytes()));
        request.setAttribute("Date", format.format(new Date()));
        request.addHeader("Authorization", "test.baltrad.eu" + ":" + 
            "AO1fnJYwLAIUEc0CevXIhG7ppda2VPHTfHfbYDMCFB5_rDppVDY07Vh4yh2nT89qnT0_");   
        request.addHeader("Node-Name", "test.baltrad.eu");
        request.addHeader("Node-Address", "http://test.baltrad.eu");
    }
    
    @Test
    public void handleRequest_MessageVerificationError() throws Exception {
        authenticatorMock.authenticate(isA(String.class), isA(String.class),
                isA(String.class));
        
        expectLastCall().andThrow(new KeyczarException(
                "Failed to verify message"));
        
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
                messages.getMessage("datasource.server.message_verifier_error"), 
                response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_Unauthorized() throws Exception {
        expect(authenticatorMock.authenticate(isA(String.class), 
                isA(String.class), isA(String.class)))
                .andReturn(Boolean.FALSE).anyTimes();
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
                messages.getMessage("datasource.server.unauthorized_request"), 
                response.getErrorMessage());
    }
    
    @Test 
    public void handleRequest_InternalServerError() throws Exception {
        expect(authenticatorMock.authenticate(isA(String.class), 
                isA(String.class), isA(String.class)))
                .andReturn(Boolean.TRUE).anyTimes();
        IAccountManager userManagerMock = 
                (IAccountManager) createMock(IAccountManager.class);
        expect(userManagerMock.load("test.baltrad.eu")).andReturn(null)
                .anyTimes();
        expect(userManagerMock.store(isA(Account.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        IJsonUtil jsonUtilMock = 
                (IJsonUtil) createMock(IJsonUtil.class);
        expect(jsonUtilMock.jsonToUserAccount(JSON_ACCOUNT))
                .andThrow(new RuntimeException("Internal server error"));
        
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setAccountManager(userManagerMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        request.setContent(JSON_ACCOUNT.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
                messages.getMessage("datasource.server.internal_server_error",
                    new String[] {"Internal server error"}), 
                response.getErrorMessage());
    }
    
    @Test 
    public void handleRequest_CreatedAccount() throws Exception {
        expect(authenticatorMock.authenticate(isA(String.class), 
                isA(String.class), isA(String.class)))
                .andReturn(Boolean.TRUE).anyTimes();
        
        IAccountManager userManagerMock = 
                (IAccountManager) createMock(IAccountManager.class);
        expect(userManagerMock.load("test.baltrad.eu")).andReturn(null)
                .anyTimes();
        expect(userManagerMock.store(isA(Account.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        IJsonUtil jsonUtilMock = (IJsonUtil) createMock(IJsonUtil.class);
        expect(jsonUtilMock.jsonToUserAccount(JSON_ACCOUNT))
                .andReturn(account);
        expect(jsonUtilMock.userAccountToJson(account)).andReturn(JSON_ACCOUNT);
        
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setAccountManager(userManagerMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        request.setContent(JSON_ACCOUNT.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }
    
    @Test 
    public void handleRequest_OK() throws Exception {
        expect(authenticatorMock.authenticate(isA(String.class), 
                isA(String.class), isA(String.class)))
                .andReturn(Boolean.TRUE).anyTimes();
        
        IAccountManager userManagerMock = 
                (IAccountManager) createMock(IAccountManager.class);
        expect(userManagerMock.load("test.baltrad.eu"))
                .andReturn(account).anyTimes();
        expect(userManagerMock.store(isA(Account.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        IJsonUtil jsonUtilMock = (IJsonUtil) createMock(IJsonUtil.class);
        expect(jsonUtilMock.jsonToUserAccount(JSON_ACCOUNT))
                .andReturn(account);
        expect(jsonUtilMock.dataSourcesToJson(isA(HashSet.class)))
                .andReturn(JSON_SOURCES);
        
        IDataSourceManager dataSourceManagerMock = 
                (IDataSourceManager) createMock(IDataSourceManager.class);
        expect(dataSourceManagerMock.loadByUser(1))
                .andReturn(new ArrayList<DataSource>()).anyTimes();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setAccountManager(userManagerMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        request.setContent(JSON_ACCOUNT.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        
        String dataSourceString = response.getContentAsString();
        JsonUtil jsonUtil = new JsonUtil();
        Set<DataSource> dataSources = jsonUtil
                .jsonToDataSources(dataSourceString);
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
    }
    
}
