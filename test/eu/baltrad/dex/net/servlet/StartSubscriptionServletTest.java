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

package eu.baltrad.dex.net.servlet;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.keyczar.exceptions.KeyczarException;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.apache.log4j.Logger;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Post subscription servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class StartSubscriptionServletTest {
    
    private static final String JSON_SOURCES_OK =
            "[{\"name\":\"DS1\",\"type\":\"local\",\"description\"" +
            ":\"A test data source\"},{\"name\":\"DS2\",\"type\":" + 
            "\"local\",\"description\":\"One more test data source\"}," + 
            "{\"name\":\"DS3\",\"type\":\"local\",\"description\":" + 
            "\"Yet another test data source\"}]";
    
    private static final String JSON_SOURCES_PARTIAL =  
            "[{\"name\":\"DS1\",\"type\":\"local\",\"description\"" +
            ":\"A test data source\"},{\"name\":\"DS2\",\"type\":" + 
            "\"local\",\"description\":\"One more test data source\"}]";
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private List<Object> mocks;
    private PSServlet classUnderTest;
    private JsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ISubscriptionManager subscriptionManagerMock;
    private DateFormat format;

    
    class PSServlet extends StartSubscriptionServlet {
        public PSServlet() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
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
    public void setUp() {
        mocks = new ArrayList<Object>();
        classUnderTest = new PSServlet();
        classUnderTest.setLog(Logger.getLogger("DEX"));
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        format = new SimpleDateFormat(DATE_FORMAT);
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        setAttributes(request);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    }
    
    private void setAttributes(MockHttpServletRequest request) {
        request.setAttribute("Content-Type", "text/html");  
        request.setAttribute("Content-MD5", Base64.encodeBase64String(
                request.getRequestURI().getBytes()));
        request.setAttribute("Date", format.format(new Date()));
        request.addHeader("Authorization", "test.baltrad.eu" + ":" + 
            "AO1fnJYwLAIUEc0CevXIhG7ppda2VPHTfHfbYDMCFB5_rDppVDY07Vh4yh2nT89qnT0_");   
        request.addHeader("Node-Name", "test.baltrad.eu");
    }
    
    @Test
    public void handleRequest_MessageVerificationError() throws Exception {
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.authenticate(isA(String.class), isA(String.class),
                isA(String.class));
        
        expectLastCall().andThrow(new KeyczarException(
                "Failed to verify message"));
        
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(messages
                .getMessage("postsubscription.server.message_verifier_error"), 
                    response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_Unauthorized() throws Exception {
        Authenticator authMock = (Authenticator) 
                createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.FALSE).anyTimes();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
            messages.getMessage("postsubscription.server.unauthorized_request"), 
            response.getErrorMessage());
    }
    
    @Test 
    public void handleRequest_InternalServerError() throws Exception {
        Authenticator authMock = (Authenticator)createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        
        IJsonUtil jsonUtilMock = (IJsonUtil)createMock(IJsonUtil.class);
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES_OK))
                .andThrow(new RuntimeException());
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
            messages.getMessage("postsubscription.server.internal_server_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_SubscriptionFailedError() throws Exception {
        
        Authenticator authMock = (Authenticator)createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        
        subscriptionManagerMock.store(isA(Subscription.class));
        expectLastCall().andThrow(new Exception()).anyTimes();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(
            messages.getMessage(
                "postsubscription.server.generic_subscription_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_PartialSubscriptionError() throws Exception {
        Authenticator authMock = (Authenticator)createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.store(isA(Subscription.class)))
                .andReturn(Integer.SIZE).times(2);
        expect(subscriptionManagerMock.store(isA(Subscription.class)))
                .andThrow(new Exception());
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_PARTIAL_CONTENT, 
                response.getStatus());
        assertNotNull(response.getContentAsString());
        assertEquals(JSON_SOURCES_PARTIAL, response.getContentAsString());
    }
    
    @Test 
    public void handleRequest_OK() throws Exception {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.store(isA(Subscription.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(response.getContentAsString());
        assertEquals(JSON_SOURCES_OK, response.getContentAsString());
    }
    
}
