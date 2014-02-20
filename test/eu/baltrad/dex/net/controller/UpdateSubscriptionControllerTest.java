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

import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.manager.IUserManager;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.*;

import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.keyczar.exceptions.KeyczarException;

import java.util.List;
import java.util.ArrayList;

import java.io.IOException;

/**
 * Post subscription request controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class UpdateSubscriptionControllerTest extends EasyMockSupport {
    interface MethodMock {
      List<Subscription> createSubscriptionRequest(String[] activeSubscriptionIds, String[] inactiveSubscriptionIds);
      void storeLocalSubscriptions(String nodeName , List<Subscription> subscriptions) throws InternalControllerException;
    };
    
    private GSController classUnderTest;
    private Authenticator authenticator;
    private IHttpClientUtil httpClientMock;
    private IUserManager userManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private INodeStatusManager nodeStatusManagerMock;
    private Logger log;
    private ModelMessageHelper messageHelper = null;
    private ProtocolManager protocolManager = null;
    private MethodMock methods = null;
    
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
        @Override
        protected List<Subscription> createSubscriptionRequest(String[] activeSubscriptionIds, String[] inactiveSubscriptionIds) {
          return methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds);
        }
        @Override
        protected void storeLocalSubscriptions(String nodeName , List<Subscription> subscriptions) throws InternalControllerException {
          methods.storeLocalSubscriptions(nodeName, subscriptions);
        }
    }
    
    @Before
    public void setUp() throws Exception {
        log = Logger.getLogger("DEX");
        httpClientMock = createMock(IHttpClientUtil.class);
        userManagerMock = createMock(IUserManager.class);
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
        authenticator = createMock(Authenticator.class);
        messageHelper = createMock(ModelMessageHelper.class);
        protocolManager = createMock(ProtocolManager.class);
        methods = createMock(MethodMock.class);
        
        classUnderTest = new GSController();
        classUnderTest.setLog(log);
        classUnderTest.setAuthenticator(authenticator);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setMessageHelper(messageHelper);
        classUnderTest.setProtocolManager(protocolManager);
        nodeStatusManagerMock = (INodeStatusManager)
                createMock(INodeStatusManager.class);
        
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
    
    @Test
    public void subscribedPeers() {
      Model model = createMock(Model.class);
      List<User> users = new ArrayList<User>();
      expect(userManagerMock.loadOperators()).andReturn(users);
      
      expect(model.addAttribute("subscribed_peers", users)).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.subscribedPeers(model);
      
      verifyAll();
      assertEquals("subscription_peers", viewName);
    }
    
    @Test
    public void subscriptionByPeer() {
      Model model = createMock(Model.class);
      List<Subscription> subscriptions = new ArrayList<Subscription>();

      expect(subscriptionManagerMock.load(Subscription.LOCAL, "test.baltrad.eu")).andReturn(subscriptions);
      expect(model.addAttribute("subscription_by_peer", subscriptions)).andReturn(null);
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.subscriptionByPeer(model, "test.baltrad.eu");

      verifyAll();
      assertEquals("subscription_show", viewName);
    }
    
    @Test
    public void selectedSubscription_NotChanged() {
      Model model = createMock(Model.class);
      
      expect(subscriptionManagerMock.load(Subscription.LOCAL,
             "test.baltrad.eu")).andReturn(subscriptions).anyTimes();
      
      expect(subscriptionManagerMock.load(1)).andReturn(s1).anyTimes();
      expect(subscriptionManagerMock.load(2)).andReturn(s2).anyTimes();
      expect(subscriptionManagerMock.load(3)).andReturn(s3).anyTimes();
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      expect(model.addAttribute("subscription_by_peer", subscriptions)).andReturn(null);
      expect(messageHelper.getMessage("getsubscription.controller.subscription_unchanged")).andReturn("a message");
      expect(model.addAttribute("subscription_status_unchanged", "a message")).andReturn(null);
      
      replayAll();
        
      String[] currentSubscriptionIds = {"1", "2", "3"};
      String[] selectedSubscriptionIds = {"1", "2", "3"};
        
      String viewName = classUnderTest.selectedSubscription(model, "test.baltrad.eu", currentSubscriptionIds, selectedSubscriptionIds);

      verifyAll();
      
      assertEquals("subscription_show", viewName);
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
    public void updateSubscription() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      List<Subscription> readSubscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andReturn(response);
      expect(protocolManager.createParser(response)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK);
      expect(responseParser.getNodeName()).andReturn("NodeName");
      expect(responseParser.getSubscriptions()).andReturn(readSubscriptions);
      methods.storeLocalSubscriptions("NodeName", readSubscriptions);
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      messageHelper.setSuccessMessage(model, "getsubscription.controller.subscription_server_success", "test.baltrad.eu");
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }
    
    @Test
    public void updateSubscription_AddCredentialsError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expectLastCall().andThrow(new KeyczarException("Signing failure"));
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.message_signer_error", "Signing failure");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }
 
    @Test
    public void updateSubscription_MessageVerificationError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andReturn(response);
      expect(protocolManager.createParser(response)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_UNAUTHORIZED).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Not authorized");
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.subscription_server_error", "Not authorized", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);

      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }
    
    @Test
    public void updateSubscription_HttpConnectionError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andThrow(new IOException("IO exception"));
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.http_connection_error", "IO exception", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }

    @Test
    public void updateSubscription_GenericConnectionError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andThrow(new Exception("Generic exception"));
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.generic_connection_error", "Generic exception", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }

    @Test
    public void updateSubscription_InternalControllerError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      List<Subscription> readSubscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andReturn(response);
      expect(protocolManager.createParser(response)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK);
      expect(responseParser.getNodeName()).andReturn("NodeName");
      expect(responseParser.getSubscriptions()).andReturn(readSubscriptions);
      methods.storeLocalSubscriptions("NodeName", readSubscriptions);
      expectLastCall().andThrow(new InternalControllerException("I C E"));
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.internal_controller_error", "I C E", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }
    
    @Test
    public void updateSubscription_InternalServerError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andReturn(response);
      expect(protocolManager.createParser(response)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Internal error");
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.subscription_server_error", "Internal error", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }

    @Test
    public void updateSubscription_SubscriptionFailedError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andReturn(response);
      expect(protocolManager.createParser(response)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_NOT_FOUND).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Subscription error");
      messageHelper.setErrorDetailsMessage(model, "getsubscription.controller.subscription_server_error", "Subscription error", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }
 
    @Test
    public void updateSubscription_PartialSubscriptionError() throws Exception {
      Model model = createMock(Model.class);
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", "unit", "locality", "state", "XX", "http://test.baltrad.eu");
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest uriRequest = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      List<Subscription> readSubscriptions = new ArrayList<Subscription>();
      
      classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
      String[] activeSubscriptionIds = {"1", "2"};
      String[] inactiveSubscriptionIds = {"3"};

      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(methods.createSubscriptionRequest(activeSubscriptionIds, inactiveSubscriptionIds)).andReturn(subscriptions);
      expect(requestFactory.createUpdateSubscriptionRequest(classUnderTest.localNode, subscriptions)).andReturn(uriRequest);
      authenticator.addCredentials(uriRequest, classUnderTest.localNode.getName());
      expect(httpClientMock.post(uriRequest)).andReturn(response);
      expect(protocolManager.createParser(response)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_PARTIAL_CONTENT).anyTimes();
      expect(responseParser.getNodeName()).andReturn("NodeName");
      expect(responseParser.getSubscriptions()).andReturn(readSubscriptions);
      methods.storeLocalSubscriptions("NodeName", readSubscriptions);
      messageHelper.setErrorMessage(model, "getsubscription.controller.subscription_server_partial", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      replayAll();
      
      String result = classUnderTest.updateSubscription(model, "test.baltrad.eu", activeSubscriptionIds, inactiveSubscriptionIds);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
      
      verifyAll();
      assertEquals("subscription_update_status", result);
    }
}
