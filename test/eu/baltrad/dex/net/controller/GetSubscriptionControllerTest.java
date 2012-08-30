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
import eu.baltrad.dex.log.util.MessageLogger;

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

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.InputStream;

/**
 * Post subscription request controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class GetSubscriptionControllerTest {
    
    private static final String JSON_SUBSCRIPTIONS_OK = 
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
            + "\"operatorName\":\"Operator3\",\"active\":false,\"synkronized\""
            + ":true}]";
            
    private static final String JSON_SUBSCRIPTIONS_PARTIAL =
            "[{\"id\":1,\"type\":\"download\",\"timeStamp\"" 
            + ":1340189763867,\"userName\":\"User1\",\"nodeAddress\":" 
            + "\"http://test.baltrad.eu\",\"dataSourceName\":\"DS1\"," 
            + "\"operatorName\":\"Operator1\",\"active\":true,\"synkronized\""
            + ":true},{\"id\":3,\"type\":\"upload\",\"timeStamp\":"
            + "1340189763867,\"userName\":\"User3\",\"nodeAddress\":"
            + "\"http://baltrad.imgw.pl\",\"dataSourceName\":\"DS3\","
            + "\"operatorName\":\"Operator3\",\"active\":false,\"synkronized\""
            + ":true}]";
    
    private GSController classUnderTest;
    private IHttpClientUtil httpClientMock;
    private INodeConnectionManager nodeConnectionManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private MessageResourceUtil messages;
    private Logger log;
    private JsonUtil jsonUtil;
    private List<Subscription> subscriptions;
    private List<Subscription> subscriptionByPeer;
    private Subscription s1;
    private Subscription s2;
    private Subscription s3;
    private Subscription s4;
    
    class GSController extends GetSubscriptionController {
        public GSController() {
            this.nodeName = "test.baltrad.eu";
            this.nodeAddress = "http://test.baltrad.eu";
        }
        @Override
        public void initConfiguration() {}
    }
    
    @Before
    public void setUp() throws Exception {
        classUnderTest = new GSController();
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        classUnderTest.setAuthenticator(new EasyAuthenticator());
        messages = new MessageResourceUtil("resources/messages"); 
        classUnderTest.setMessages(messages);
        log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
        classUnderTest.setLog(log);
        httpClientMock = createMock(IHttpClientUtil.class);
        nodeConnectionManagerMock = createMock(INodeConnectionManager.class);
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
        
        expect(nodeConnectionManagerMock.get("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu",
                "http://test.baltrad.eu")).anyTimes();
        replay(nodeConnectionManagerMock);
        classUnderTest.setNodeConnectionManager(nodeConnectionManagerMock);
        
        long time = 1340189763867L;
        subscriptions = new ArrayList<Subscription>();
        s1 = new Subscription(1, time, "User1", "DS1", "test.batrad.eu", 
                "download", true, true, "http://test.baltrad.eu");
        subscriptions.add(s1);
        s2 = new Subscription(2, time, "User2", "DS2", "test.baltrad.eu", 
                "download", true, true, "http://test.baltrad.eu");
        subscriptions.add(s2);
        s3 = new Subscription(3, time, "User3", "DS3", "test.baltrad.eu", 
                "download", true, true, "http://test.baltrad.eu");
        subscriptions.add(s3);
        s4 = new Subscription(4, time, "User2", "DS2", "test.baltrad.eu", 
                "download", false, true, "http://test.baltrad.eu");
        
        assertEquals(3, subscriptions.size());
        
        subscriptionByPeer = new ArrayList<Subscription>();
        subscriptionByPeer.add(s1);
        
        assertEquals(1, subscriptionByPeer.size());
    }
    
    @After
    public void tearDown() throws Exception {
        classUnderTest = null;
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
    public void subscribedPeers() {
        expect(subscriptionManagerMock.loadOperators(
                Subscription.SUBSCRIPTION_DOWNLOAD)).andReturn(subscriptions);
        replay(subscriptionManagerMock);
        
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscribedPeers(model);
        assertEquals("subscribed_peers", viewName);
        assertTrue(model.containsAttribute("subscribed_peers"));
        assertEquals(subscriptions, model.asMap().get("subscribed_peers"));
        
        verify(subscriptionManagerMock);
        reset(subscriptionManagerMock);
    }
    
    @Test
    public void subscriptionByPeer() {
        expect(subscriptionManagerMock.load("test.baltrad.eu",
            Subscription.SUBSCRIPTION_DOWNLOAD)).andReturn(subscriptionByPeer);
        replay(subscriptionManagerMock);
        
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscriptionByPeer(model, 
                "test.baltrad.eu");
        assertEquals("subscription_by_peer", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("subscription_by_peer"));
        assertEquals("test.baltrad.eu", model.asMap().get("peer_name"));
        assertEquals(subscriptionByPeer, 
                model.asMap().get("subscription_by_peer"));
        
        verify(subscriptionManagerMock);
        reset(subscriptionManagerMock);
    }
    
    @Test
    public void selectedSubscription_NotChanged() {
        expect(subscriptionManagerMock.load("test.baltrad.eu",
            Subscription.SUBSCRIPTION_DOWNLOAD)).andReturn(subscriptions)
                .anyTimes();
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(subscriptionManagerMock);
        
        String[] currentSubscriptionIds = {"1", "2", "3"};
        String[] selectedSubscriptionIds = {"1", "2", "3"};
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.selectedSubscription(model, 
            "test.baltrad.eu", currentSubscriptionIds, selectedSubscriptionIds);
        assertEquals("subscription_by_peer", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("subscription_by_peer"));
        assertEquals("test.baltrad.eu", model.asMap().get("peer_name"));
        assertEquals(subscriptions, 
                model.asMap().get("subscription_by_peer"));
        
        verify(subscriptionManagerMock);
        reset(subscriptionManagerMock);
    }
    
    @Test
    public void selectedSubscription_OK() {
        expect(subscriptionManagerMock.load("test.baltrad.eu",
            Subscription.SUBSCRIPTION_DOWNLOAD)).andReturn(subscriptions)
                .anyTimes();
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).times(1, 1);
        expect(subscriptionManagerMock.load(2)).andReturn(s4).times(1, 2);
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(subscriptionManagerMock);
        
        String[] currentSubscriptionIds = {"1", "2", "3"};
        String[] selectedSubscriptionIds = {"1", "2", "3"};
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.selectedSubscription(model, 
            "test.baltrad.eu", currentSubscriptionIds, selectedSubscriptionIds);
        
        assertEquals("selected_subscription", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("selected_subscription"));
        assertEquals("test.baltrad.eu", model.asMap().get("peer_name"));
        assertEquals(subscriptions, model.asMap().get("selected_subscription"));
        
        verify(subscriptionManagerMock);
        reset(subscriptionManagerMock);
    }
    
    @Test
    public void getSubscription_HttpConnectionError() throws Exception {
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new IOException("Http connection error"));
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.http_connection_error",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Http connection error", 
                model.asMap().get("error_details"));
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);
    }
    
    @Test
    public void getSubscription_GenericConnectionError() throws Exception {
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new Exception("Generic connection error"));
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.generic_connection_error",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Generic connection error", 
                model.asMap().get("error_details"));
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);
    }
    
    @Test
    public void getSubscription_InternalControllerError() throws Exception {
        HttpResponse response = createResponse(HttpServletResponse.SC_OK, 
                null, "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.internal_controller_error",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to convert JSON string to data sources", 
                model.asMap().get("error_details"));
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);
    }
    
    @Test
    public void getSubscription_InternalServerError() throws Exception {
        HttpResponse response = createResponse(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
            "Internal server error", "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.subscription_server_error",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Internal server error", 
                model.asMap().get("error_details"));
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);
    }
    
    @Test
    public void getSubscription_SubscriptionFailedError() throws Exception {
        HttpResponse response = createResponse(
            HttpServletResponse.SC_NOT_FOUND, "Subscription error", "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.subscription_server_error",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Subscription error", 
                model.asMap().get("error_details"));
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);
    }
    
    @Test
    public void getSubscription_PartialSubscriptionError() throws Exception {
        HttpResponse response = createResponse(
            HttpServletResponse.SC_PARTIAL_CONTENT, null, 
                JSON_SUBSCRIPTIONS_PARTIAL);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).anyTimes();
        
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.subscription_server_partial",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("error_message"));
        assertNotNull(getResponseBody(response));
        assertEquals(JSON_SUBSCRIPTIONS_PARTIAL, getResponseBody(response));
        List<Subscription> subs = jsonUtil.jsonToSubscriptions(
                getResponseBody(response));
        assertNotNull(subs);
        assertEquals(2, subs.size());
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);
    }      
    
    @Test
    public void getSubscription_OK() throws Exception {
         HttpResponse response = createResponse(HttpServletResponse.SC_OK, 
                null, JSON_SUBSCRIPTIONS_OK);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.storeNoId(isA(Subscription.class)))
                .andReturn(1).anyTimes();
        
        replay(httpClientMock);
        replay(subscriptionManagerMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("success_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.subscription_server_success",
                    new String[] {"test.baltrad.eu"}),
                    model.asMap().get("success_message"));
        assertNotNull(getResponseBody(response));
        assertEquals(JSON_SUBSCRIPTIONS_OK, getResponseBody(response));
        List<Subscription> subs = jsonUtil.jsonToSubscriptions(
                getResponseBody(response));
        assertNotNull(subs);
        assertEquals(3, subs.size());
        
        verify(httpClientMock);
        verify(subscriptionManagerMock);
        verify(nodeConnectionManagerMock);
        reset(httpClientMock);
        reset(subscriptionManagerMock);
        reset(nodeConnectionManagerMock);   
    }
    
}
