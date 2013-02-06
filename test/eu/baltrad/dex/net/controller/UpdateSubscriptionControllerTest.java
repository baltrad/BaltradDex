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

import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.net.auth.EasyAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.model.impl.Node;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.manager.INodeManager;
import eu.baltrad.dex.user.model.Account;
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

import org.keyczar.exceptions.KeyczarException;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;


/**
 * Post subscription request controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class UpdateSubscriptionControllerTest {
    
    private static final String JSON_SUBSCRIPTIONS_OK = 
            "[{\"id\":1,\"type\":\"" + 
            "download\",\"date\":1340189763867,\"active\":true,\"user\":" +
            "\"User1\",\"dataSource\":\"DataSource1\",\"operator\":\"" + 
            "Operator1\",\"syncronized\":true},{\"id\":2,\"type\":\"download" + 
            "\",\"date\":1340189763867,\"active\":true,\"user\":\"" + 
            "User2\",\"dataSource\":\"DataSource2\",\"operator\":\"Operator2" + 
            "\",\"syncronized\":false},{\"id\":3,\"type\":\"upload\",\"" +
            "date\":1340189763867,\"active\":false,\"user\":\"User3\",\"" +
            "dataSource\":\"DataSource3\",\"operator\":\"Operator3\",\"" + 
            "syncronized\":true}]";
            
    private static final String JSON_SUBSCRIPTIONS_PARTIAL =
            "[{\"id\":1,\"type\":\"" + 
            "download\",\"date\":1340189763867,\"active\":true,\"user\":" +
            "\"User1\",\"dataSource\":\"DataSource1\",\"operator\":\"" + 
            "Operator1\",\"syncronized\":true},{\"id\":3,\"type\":\"upload\"" +
            ",\"date\":1340189763867,\"active\":false,\"user\":\"User3\"" +
            ",\"dataSource\":\"DataSource3\",\"operator\":\"Operator3\",\"" + 
            "syncronized\":true}]";
    
    private GSController classUnderTest;
    private List<Object> mocks;
    private IHttpClientUtil httpClientMock;
    private INodeManager nodeManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private MessageResourceUtil messages;
    private Logger log;
    private JsonUtil jsonUtil;
    private List<Node> nodes;
    private List<Subscription> subscriptions;
    private List<Subscription> subscriptionByPeer;
    private Subscription s1;
    private Subscription s2;
    private Subscription s3;
    private Subscription s4;
    
    class GSController extends UpdateSubscriptionController {
        public GSController() {
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
        classUnderTest = new GSController();
        mocks = new ArrayList<Object>();
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        classUnderTest.setAuthenticator(new EasyAuthenticator());
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        log = Logger.getLogger("DEX");
        classUnderTest.setLog(log);
        httpClientMock = (IHttpClientUtil) createMock(IHttpClientUtil.class);
        nodeManagerMock = (INodeManager) createMock(INodeManager.class);
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        nodes = new ArrayList<Node>();
        nodes.add(new Node(1, "Node1", "http://node1.eu"));
        nodes.add(new Node(2, "Node2", "http://node2.eu"));
        nodes.add(new Node(3, "Node3", "http://node3.eu"));
        
        assertEquals(3, nodes.size());
        
        long time = 1340189763867L;
        subscriptions = new ArrayList<Subscription>();
        s1 = new Subscription(1, time, "download", "test.baltrad.eu", "User1", 
                "DataSource1", true, true);
        subscriptions.add(s1);
        s2 = new Subscription(2, time, "download", "test.baltrad.eu", "User2", 
                "DataSource2", true, false);
        subscriptions.add(s2);
        s3 = new Subscription(3, time, "download", "test.baltrad.eu", "User3", 
                "DataSource3", false, true);
        subscriptions.add(s3);
        
        s4 = new Subscription(4, time, "download", "test.baltrad.eu", "User4", 
                "DataSource4", false, true);
        assertEquals(3, subscriptions.size());
        
        subscriptionByPeer = new ArrayList<Subscription>();
        subscriptionByPeer.add(s1);
        
        assertEquals(1, subscriptionByPeer.size());
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
            return IOUtils.toString(is, "UTF-8");
        } finally {
            is.close();
        }
    }
    
    @Test
    public void subscribedPeers() {
        expect(nodeManagerMock.loadOperators()).andReturn(nodes);
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscribedPeers(model);
        assertEquals("subscribed_peers", viewName);
        assertTrue(model.containsAttribute("subscribed_peers"));
        assertEquals(nodes, model.asMap().get("subscribed_peers"));
        
        verifyAll();
    }
    
    @Test
    public void subscriptionByPeer() {
        expect(subscriptionManagerMock.load(Subscription.DOWNLOAD,
                "test.baltrad.eu")).andReturn(subscriptionByPeer);
        replayAll();
        
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
        
        verifyAll();
    }
    
    @Test
    public void selectedSubscription_NotChanged() {
        expect(subscriptionManagerMock.load(Subscription.DOWNLOAD,
                "test.baltrad.eu")).andReturn(subscriptions)
                .anyTimes();
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
    
        replayAll();
        
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
        
        verifyAll();
    }
    
    @Test
    public void selectedSubscription_OK() {
        expect(subscriptionManagerMock.load(Subscription.DOWNLOAD,
                "test.baltrad.eu")).andReturn(subscriptions)
                .anyTimes();
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).times(1, 1);
        expect(subscriptionManagerMock.load(2)).andReturn(s4).times(1, 2);
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
    
        replayAll();
        
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
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_AddCredentialsError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        expectLastCall().andThrow(new KeyczarException(
                "Failed to sign message"));
        
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(
                model, "test.baltrad.eu", activeSubscriptionIds, 
                inactiveSubscriptionIds);
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "getsubscription.controller.message_signer_error",
                new String[] {"test.baltrad.eu"}),
                model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to sign message", 
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_MessageVerificationError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_UNAUTHORIZED, 
                "Failed to verify message", "");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.getSubscription(
                model, "test.baltrad.eu", activeSubscriptionIds, 
                inactiveSubscriptionIds);
        
        verifyAll();
        
        assertEquals("subscription_status", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage(
                "getsubscription.controller.subscription_server_error",
                new String[] {"test.baltrad.eu"}),
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to verify message", model.asMap()
                .get("error_details"));
    }
    
    @Test
    public void getSubscription_HttpConnectionError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new IOException("Http connection error"));
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_GenericConnectionError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new Exception("Generic connection error"));
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_InternalControllerError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse response = createResponse(HttpServletResponse.SC_OK, 
                null, "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        assertEquals("Failed to read server response", 
                model.asMap().get("error_details"));
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_InternalServerError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse response = createResponse(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
            "Internal server error", "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_SubscriptionFailedError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse response = createResponse(
            HttpServletResponse.SC_NOT_FOUND, "Subscription error", "");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        
        verifyAll();
    }
    
    @Test
    public void getSubscription_PartialSubscriptionError() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
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
        expect(subscriptionManagerMock.store(isA(Subscription.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        
        verifyAll();
    }      
    
    @Test
    public void getSubscription_OK() throws Exception {
        expect(nodeManagerMock.load("test.baltrad.eu"))
            .andReturn(new Node("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse response = createResponse(HttpServletResponse.SC_OK, 
                null, JSON_SUBSCRIPTIONS_OK);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(response);
        expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
        expect(subscriptionManagerMock.load(isA(String.class), isA(String.class),
                isA(String.class))).andReturn(null).anyTimes();
        expect(subscriptionManagerMock.store(isA(Subscription.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        replayAll();
        
        classUnderTest.setNodeManager(nodeManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
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
        
        verifyAll();
    }
    
}
