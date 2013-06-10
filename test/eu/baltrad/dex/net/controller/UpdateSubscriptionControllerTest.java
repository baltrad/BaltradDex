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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.net.auth.EasyAuthenticator;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.manager.IUserManager;
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
            "[{\"type\":\"download\",\"date\":1340189763867," + 
            "\"active\":true,\"user\":\"User1\",\"dataSource\":\"DataSource1" + 
            "\",\"syncronized\":true},{\"type\":\"download" + 
            "\",\"date\":1340189763867,\"active\":true,\"user\":\"" + 
            "User2\",\"dataSource\":\"DataSource2\",\"syncronized\":false}," + 
            "{\"type\":\"upload\",\"date\":1340189763867,\"active\"" +
            ":false,\"user\":\"User3\",\"dataSource\":\"DataSource3\"," + 
            "\"syncronized\":true}]";
            
    private static final String JSON_SUBSCRIPTIONS_PARTIAL =
            "[{\"type\":\"download\",\"date\":1340189763867,\"active\":true," +
            "\"user\":\"User1\",\"dataSource\":\"DataSource1\",\"syncronized" +
            "\":true},{\"type\":\"upload\",\"date\":1340189763867," + 
            "\"active\":false,\"user\":\"User3\",\"dataSource\":\"" +
            "DataSource3\",\"syncronized\":true}]";
    
    private GSController classUnderTest;
    private List<Object> mocks;
    private IHttpClientUtil httpClientMock;
    private IUserManager userManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private MessageResourceUtil messages;
    private Logger log;
    private JsonUtil jsonUtil;
    private List<User> users;
    private List<Subscription> subscriptions;
    private List<Subscription> subscriptionByPeer;
    private Subscription s1;
    private Subscription s2;
    private Subscription s3;
    private Subscription s4;
    
    class GSController extends UpdateSubscriptionController {
        public GSController() {
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
        userManagerMock = (IUserManager) createMock(IUserManager.class);
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        users = new ArrayList<User>();
        users.add(new User(1, "test1.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test1.baltrad.eu"));
        users.add(new User(2, "test2.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test2.baltrad.eu"));
        users.add(new User(3, "test3.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test3.baltrad.eu"));
        
        assertEquals(3, users.size());
        
        long time = 1340189763867L;
        subscriptions = new ArrayList<Subscription>();
        s1 = new Subscription(1, time, "download", "User1", "DataSource1", true, 
                true);
        subscriptions.add(s1);
        s2 = new Subscription(2, time, "download", "User2", "DataSource2", true, 
                false);
        subscriptions.add(s2);
        s3 = new Subscription(3, time, "download", "User3", "DataSource3", false, 
                true);
        subscriptions.add(s3);
        s4 = new Subscription(4, time, "download", "User4", "DataSource4", false, 
                true);
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
        expect(userManagerMock.loadOperators()).andReturn(users);
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscribedPeers(model);
        assertEquals("subscription_peers", viewName);
        assertTrue(model.containsAttribute("subscribed_peers"));
        assertEquals(users, model.asMap().get("subscribed_peers"));
        
        verifyAll();
    }
    
    @Test
    public void subscriptionByPeer() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL,
                "test.baltrad.eu")).andReturn(subscriptionByPeer);
        replayAll();
        
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscriptionByPeer(model, 
                "test.baltrad.eu");
        assertEquals("subscription_show", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("subscription_by_peer"));
        assertEquals("test.baltrad.eu", model.asMap().get("peer_name"));
        assertEquals(subscriptionByPeer, 
                model.asMap().get("subscription_by_peer"));
        
        verifyAll();
    }
    
    @Test
    public void selectedSubscription_NotChanged() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL,
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
        assertEquals("subscription_show", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("subscription_by_peer"));
        assertEquals("test.baltrad.eu", model.asMap().get("peer_name"));
        assertEquals(subscriptions, 
                model.asMap().get("subscription_by_peer"));
        
        verifyAll();
    }
    
    @Test
    public void selectedSubscription_OK() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL,
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
        
        assertEquals("subscription_selected", viewName);
        assertTrue(model.containsAttribute("peer_name"));
        assertTrue(model.containsAttribute("subscription_selected"));
        assertEquals("test.baltrad.eu", model.asMap().get("peer_name"));
        assertEquals(subscriptions, model.asMap().get("subscription_selected"));
        
        verifyAll();
    }
    
    @Test
    public void updateSubscription_AddCredentialsError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(
                model, "test.baltrad.eu", activeSubscriptionIds, 
                inactiveSubscriptionIds);
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_MessageVerificationError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(
                model, "test.baltrad.eu", activeSubscriptionIds, 
                inactiveSubscriptionIds);
        
        verifyAll();
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_HttpConnectionError() throws Exception {
       expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_GenericConnectionError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_InternalControllerError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_InternalServerError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_SubscriptionFailedError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_PartialSubscriptionError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
    public void updateSubscription_OK() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
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
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String[] activeSubscriptionIds = {"1", "2"};
        String[] inactiveSubscriptionIds = {"3"};
        String viewName = classUnderTest.updateSubscription(model, 
            "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        
        assertEquals("subscription_update_status", viewName);
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
