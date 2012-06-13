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
import eu.baltrad.dex.net.model.ISubscriptionManager;
import eu.baltrad.dex.net.model.Subscription;
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

/**
 * Post subscription servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class PostSubscriptionServletTest {
    
    private static final String JSON_SOURCES_OK =  
            "[{\"name\":\"DS1\",\"id\":0,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":0,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":0,\"" 
            + "description\":\"Yet another test data source\"}]";
    
    private static final String JSON_SOURCES_PARTIAL =  
            "[{\"name\":\"DS1\",\"id\":0,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":0,\"description\":\"One "
            + "more test data source\"}]";
    
    private PostSubscriptionServlet classUnderTest;
    private JsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ISubscriptionManager subscriptionManagerMock;
    
    @Before
    public void setUp() {
        classUnderTest = new PostSubscriptionServlet("test.baltrad.eu", 
                "http://test.baltrad.eu");
        classUnderTest.setLog(MessageLogger.getLogger(MessageLogger.SYS_DEX));
        messages = new MessageResourceUtil("resources/messages");
        classUnderTest.setMessages(messages);
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
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
            messages.getMessage("postsubscription.server.unauthorized_request"), 
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
        replay(authMock);
        
        IJsonUtil jsonUtilMock = createMock(IJsonUtil.class);
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES_OK))
                .andThrow(new RuntimeException());
        replay(jsonUtilMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(jsonUtilMock);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
            messages.getMessage("postsubscription.server.internal_server_error"), 
            response.getErrorMessage());
        reset(authMock);
        reset(jsonUtilMock);
    }
    
    @Test
    public void handleRequest_SubscriptionFailedError() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(null, null)).andReturn(Boolean.TRUE)
                .anyTimes();
        expect(authMock.getMessage(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getSignature(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getNodeName(isA(MockHttpServletRequest.class)))
                .andReturn("test.baltrad.eu").anyTimes();
        replay(authMock);
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(0).anyTimes();
        replay(subscriptionManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(subscriptionManagerMock);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(
            messages.getMessage(
                "postsubscription.server.generic_subscription_error"), 
            response.getErrorMessage());
        
        reset(authMock);
        reset(subscriptionManagerMock);
    }
    
    @Test
    public void handleRequest_PartialSubscriptionError() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(null, null)).andReturn(Boolean.TRUE)
                .anyTimes();
        expect(authMock.getMessage(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getSignature(isA(MockHttpServletRequest.class)))
                .andReturn(null).anyTimes();
        expect(authMock.getNodeName(isA(MockHttpServletRequest.class)))
                .andReturn("test.baltrad.eu").anyTimes();
        replay(authMock);
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).times(2);
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(0).times(1);
        replay(subscriptionManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(subscriptionManagerMock);
        assertEquals(HttpServletResponse.SC_PARTIAL_CONTENT, 
                response.getStatus());
        assertNotNull(response.getContentAsString());
        assertEquals(JSON_SOURCES_PARTIAL, response.getContentAsString());
        
        reset(authMock);
        reset(subscriptionManagerMock);
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
        replay(authMock);
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).times(3);
        replay(subscriptionManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SOURCES_OK.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(subscriptionManagerMock);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(response.getContentAsString());
        assertEquals(JSON_SOURCES_OK, response.getContentAsString());
        
        reset(authMock);
        reset(subscriptionManagerMock);
    }*/
    
}
