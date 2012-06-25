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

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Post subscription servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class GetSubscriptionServletTest {
    
    private static final String JSON_SUBSCRIPTIONS = 
            "[{\"id\":1,\"type\":\"download\",\"timeStamp\"" 
            + ":1340189763867,\"userName\":\"User1\",\"nodeAddress\":" 
            + "\"http://test.baltrad.eu\",\"dataSourceName\":\"DS1\"," 
            + "\"operatorName\":\"Operator1\",\"active\":true,\"synkronized\""
            + ":true},{\"id\":2,\"type\":\"download\",\"timeStamp\"" 
            + ":1340189763867,\"userName\":\"User2\",\"nodeAddress\":"
            + "\"http://baltrad.eu\",\"dataSourceName\":\"DS2\","
            + "\"operatorName\":\"Operator2\",\"active\":true,\"synkronized\":"
            + "false},{\"id\":3,\"type\":\"upload\",\"timeStamp\":"
            + "1340189763867,\"userName\":\"User3\",\"nodeAddress\":"
            + "\"http://baltrad.imgw.pl\",\"dataSourceName\":\"DS3\","
            + "\"operatorName\":\"Operator3\",\"active\":true,\"synkronized\""
            + ":true}]";
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private GSServlet classUnderTest;
    private JsonUtil jsonUtil;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ISubscriptionManager subscriptionManagerMock;
    private DateFormat format;
    
    class GSServlet extends GetSubscriptionServlet {
        public GSServlet() {
            this.nodeName = "test.baltrad.eu";
            this.nodeAddress = "http://test.baltrad.eu";
        }
        @Override
        public void initConfiguration() {}
    }
    
    @Before
    public void setUp() {
        classUnderTest = new GSServlet();
        classUnderTest.setLog(MessageLogger.getLogger(MessageLogger.SYS_DEX));
        messages = new MessageResourceUtil("resources/messages");
        classUnderTest.setMessages(messages);
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        format = new SimpleDateFormat(DATE_FORMAT);
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
    }
    
    private void setAttributes(MockHttpServletRequest request) {
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
    public void handleRequest_Unauthorized() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.FALSE).anyTimes();
        setAttributes(request);
        replay(authMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
            messages.getMessage("getsubscription.server.unauthorized_request"), 
            response.getErrorMessage());
        reset(authMock);
    }
    
    @Test 
    public void handleRequest_InternalServerError() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        setAttributes(request);
        replay(authMock);
        
        IJsonUtil jsonUtilMock = createMock(IJsonUtil.class);
        expect(jsonUtilMock.jsonToSubscriptions(JSON_SUBSCRIPTIONS))
                .andThrow(new RuntimeException());
        replay(jsonUtilMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        request.setContent(JSON_SUBSCRIPTIONS.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(jsonUtilMock);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
            messages.getMessage("getsubscription.server.internal_server_error"), 
            response.getErrorMessage());
        reset(authMock);
        reset(jsonUtilMock);
    }
    
    @Test
    public void handleRequest_SubscriptionFailedError() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        setAttributes(request);
        replay(authMock);
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(0).anyTimes();
        replay(subscriptionManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SUBSCRIPTIONS.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(subscriptionManagerMock);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(
            messages.getMessage(
                "getsubscription.server.generic_subscription_error"), 
            response.getErrorMessage());
        
        reset(authMock);
        reset(subscriptionManagerMock);
    }
    
    @Test
    public void handleRequest_PartialSubscriptionError() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        setAttributes(request);
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
        request.setContent(JSON_SUBSCRIPTIONS.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(subscriptionManagerMock);
        assertEquals(HttpServletResponse.SC_PARTIAL_CONTENT, 
                response.getStatus());
        assertNotNull(response.getContentAsString());
        List<Subscription> subs = 
                jsonUtil.jsonToSubscriptions(response.getContentAsString()); 
        assertEquals(2, subs.size());
        reset(authMock);
        reset(subscriptionManagerMock);
    }
    
    @Test 
    public void handleRequest_OK() throws Exception {
        Authenticator authMock = createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE).anyTimes();
        setAttributes(request);
        replay(authMock);
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).times(3);
        replay(subscriptionManagerMock);
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        request.setContent(JSON_SUBSCRIPTIONS.getBytes());
        classUnderTest.handleRequest(request, response);
        
        verify(authMock);
        verify(subscriptionManagerMock);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertNotNull(response.getContentAsString());
        List<Subscription> subs = 
                jsonUtil.jsonToSubscriptions(response.getContentAsString()); 
        assertEquals(3, subs.size());
        
        reset(authMock);
        reset(subscriptionManagerMock);
    }    
    
}
