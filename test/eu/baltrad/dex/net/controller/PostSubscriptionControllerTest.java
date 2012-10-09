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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.net.model.*;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.util.MessageResourceUtil;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.InputStream;
import org.keyczar.exceptions.KeyczarException;

/**
 * Post subscription request controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.1.0
 */
public class PostSubscriptionControllerTest {
    
    private static final String[] SELECTED_DATA_SOURCES = {
        "1_DataSource1_A test data source",
        "2_DataSource2_Another test data source",
        "3_DataSource3_Yet one more test data source"
    };
    
    private static final String JSON_SOURCES_OK =  
            "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":3,\"" 
            + "description\":\"Yet another test data source\"}]";
    
    private static final String JSON_SOURCES_PARTIAL =  
            "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"}]";
    
    private List<Object> mocks;
    private PSController classUnderTest;
    private IHttpClientUtil httpClientMock;
    private INodeConnectionManager nodeConnectionManagerMock;
    private Authenticator authenticatorMock;
    private ISubscriptionManager subscriptionManagerMock;
    private MessageResourceUtil messages;
    private Logger log;
    private JsonUtil jsonUtil;
    
    class PSController extends PostSubscriptionController {
        public PSController() {
            this.nodeName = "test.baltrad.eu";
            this.nodeAddress = "http://test.baltrad.eu";
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
        classUnderTest = new PSController();
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        messages = new MessageResourceUtil("resources/messages");
        classUnderTest.setMessages(messages);
        log = Logger.getLogger("DEX");
        classUnderTest.setLog(log);
        httpClientMock = (IHttpClientUtil) createMock(IHttpClientUtil.class);
        nodeConnectionManagerMock = (INodeConnectionManager)
                createMock(INodeConnectionManager.class);
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        authenticatorMock = (Authenticator) createMock(Authenticator.class);
    }
    
    @After
    public void tearDown() throws Exception {
        classUnderTest = null;
        resetAll();
    }
    
    private HttpResponse createResponse(int code, String reason, 
            String jsonSources) throws Exception {
        ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(version, code, reason);
        HttpResponse response = new BasicHttpResponse(statusLine);
        response.addHeader("Node-Name", "test.baltrad.eu");
        response.addHeader("Node-Address", "http://test.baltrad.eu");
        response.setEntity(new StringEntity(jsonSources));
        return response;
    } 
    
    private String getResponseBody(HttpResponse response) throws IOException {
        InputStream is = null;
        try {
            is = response.getEntity().getContent();
            return IOUtils.toString(is);
        } finally {
            is.close();
        }
    }
    
    @Test
    public void postSubscription_AddCredentialsError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        expectLastCall().andThrow(new KeyczarException(
                "Failed to sign message"));
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.message_signer_error",
                new String[] {"test.baltrad.eu"}),
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to sign message", 
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_MessageVerificationError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_UNAUTHORIZED, 
                "Failed to verify message", "");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        
        verifyAll();
        
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage(
                "postsubscription.controller.subscription_server_error",
                new String[] {"test.baltrad.eu"}),
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to verify message", model.asMap()
                .get("error_details"));
    }
    
    @Test
    public void postSubscription_HttpConnectionError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
      
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new IOException("Http connection error"));
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.http_connection_error",
                new String[] {"test.baltrad.eu"}),
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Http connection error", 
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_GenericConnectionError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new Exception("Generic connection error"));
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.generic_connection_error",
                new String[] {"test.baltrad.eu"}),
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Generic connection error", 
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_InternalServerError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        HttpResponse response = createResponse(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
            "Internal server error", "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.subscription_server_error",
                new String[] {"test.baltrad.eu"}),
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Internal server error", 
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_InternalControllerError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        HttpResponse response = createResponse(HttpServletResponse.SC_OK, 
                null, "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.internal_controller_error",
                new String[] {"test.baltrad.eu"}), 
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to convert JSON string to data sources",
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_SubscriptionFailedError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        HttpResponse response = createResponse(
            HttpServletResponse.SC_NOT_FOUND, "Subscription error", "");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.subscription_server_error",
                new String[] {"test.baltrad.eu"}),
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Subscription error", model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_PartialSubscriptionError() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        HttpResponse response = createResponse(
            HttpServletResponse.SC_PARTIAL_CONTENT, null, JSON_SOURCES_PARTIAL);
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, "test.baltrad.eu",
                SELECTED_DATA_SOURCES);
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.subscription_server_partial",
                new String[] {"test.baltrad.eu"}), 
                model.asMap().get("error_message"));
        assertNotNull(getResponseBody(response));
        assertEquals(JSON_SOURCES_PARTIAL, getResponseBody(response));
        Set<DataSource> dataSources = jsonUtil.jsonToDataSources(
                getResponseBody(response));
        assertNotNull(dataSources);
        assertEquals(2, dataSources.size());
        
        verifyAll();
    }
    
    @Test
    public void postSubscription_OK() throws Exception {
        expect(nodeConnectionManagerMock.load("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        
        HttpResponse response = createResponse(HttpServletResponse.SC_OK, 
                null, JSON_SOURCES_OK);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        expect(subscriptionManagerMock.load(isA(String.class), 
            isA(String.class), isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.postSubscription(model, 
                "test.baltrad.eu", SELECTED_DATA_SOURCES);
        assertEquals("subscribe", viewName);
        assertTrue(model.containsAttribute("success_message"));
        assertEquals(messages.getMessage(
                "postsubscription.controller.subscription_server_success",
                new String[] {"test.baltrad.eu"}), 
                model.asMap().get("success_message"));
        assertNotNull(getResponseBody(response));
        assertEquals(JSON_SOURCES_OK, getResponseBody(response));
        Set<DataSource> dataSources = jsonUtil.jsonToDataSources(
                getResponseBody(response));
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
        
        verifyAll();
    }
    
}
