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

import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.protocol.RequestParserException;
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.web.servlet.ModelAndView;

import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.*;

import org.apache.log4j.Logger;

import org.keyczar.exceptions.KeyczarException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * Post subscription servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class UpdateSubscriptionServletTest extends EasyMockSupport {
    interface MethodMock {
      List<Subscription> storePeerSubscriptions(String nodeName, List<Subscription> requestedSubscription);
      public void doPost(HttpServletRequest request, HttpServletResponse response);
    };
    
    private GSServlet classUnderTest;
    private MessageResourceUtil messages;
    private ISubscriptionManager subscriptionManagerMock;
    private Authenticator authenticator;
    private ProtocolManager protocolManager;
    private MethodMock methods;
    
    @SuppressWarnings("serial")
    class GSServlet extends UpdateSubscriptionServlet {
        public GSServlet() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", "unit", 
                    "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
        @Override
        protected List<Subscription> storePeerSubscriptions(String nodeName, List<Subscription> requestedSubscription) {
          return methods.storePeerSubscriptions(nodeName, requestedSubscription);
        }
    }
    
  @Before
  public void setUp() {
    messages = createMock(MessageResourceUtil.class);
    subscriptionManagerMock = createMock(ISubscriptionManager.class);
    authenticator = createMock(Authenticator.class);
    protocolManager = createMock(ProtocolManager.class);
    methods = createMock(MethodMock.class);

    classUnderTest = new GSServlet();
    classUnderTest.setLog(Logger.getLogger("DEX"));
    classUnderTest.setMessages(messages);
    classUnderTest.setSubscriptionManager(subscriptionManagerMock);
    classUnderTest.setAuthenticator(authenticator);
    classUnderTest.setProtocolManager(protocolManager);
  }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    }
    
    @SuppressWarnings("serial")
    @Test
    public void handleRequest() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      HttpSession httpSession = createMock(HttpSession.class);
      
      expect(request.getSession(true)).andReturn(httpSession);
      methods.doPost(request, response);
      
      classUnderTest = new GSServlet() {
        public void doPost(HttpServletRequest request, HttpServletResponse response) {
          methods.doPost(request, response);
        }
      };
      
      replayAll();
      
      ModelAndView result = classUnderTest.handleRequest(request, response);
      
      verifyAll();
      assertNotNull(result);
    }
    
    @Test
    public void doPost() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      List<Subscription> requestedSubscription = new ArrayList<Subscription>();
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      requestedSubscription.add(new Subscription());
      subscriptions.add(new Subscription());
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getSubscriptions()).andReturn(requestedSubscription);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(methods.storePeerSubscriptions("NodeName", requestedSubscription)).andReturn(subscriptions);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.subscriptionResponse(classUnderTest.localNode.getName(), subscriptions, HttpServletResponse.SC_OK);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }

    @Test
    public void doPost_partial() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      List<Subscription> requestedSubscription = new ArrayList<Subscription>();
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      requestedSubscription.add(new Subscription());
      requestedSubscription.add(new Subscription());
      subscriptions.add(new Subscription());
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getSubscriptions()).andReturn(requestedSubscription);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(methods.storePeerSubscriptions("NodeName", requestedSubscription)).andReturn(subscriptions);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.subscriptionResponse(classUnderTest.localNode.getName(), subscriptions, HttpServletResponse.SC_PARTIAL_CONTENT);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }    

    @Test
    public void doPost_noSubscriptions() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      List<Subscription> requestedSubscription = new ArrayList<Subscription>();
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      requestedSubscription.add(new Subscription());
      requestedSubscription.add(new Subscription());
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getSubscriptions()).andReturn(requestedSubscription);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(methods.storePeerSubscriptions("NodeName", requestedSubscription)).andReturn(subscriptions);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      expect(messages.getMessage("getsubscription.server.generic_subscription_error")).andReturn("a message");
      responseWriter.messageResponse("a message", HttpServletResponse.SC_NOT_FOUND);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }    

    
    @Test
    public void doPost_MessageVerificationError() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andThrow(new KeyczarException("keyczar exception"));
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      expect(messages.getMessage("postsubscription.server.message_verifier_error")).andReturn("a message");
      responseWriter.messageResponse("a message", HttpServletResponse.SC_UNAUTHORIZED);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }    

    @Test
    public void doPost_Unauthorized() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(false);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      expect(messages.getMessage("getsubscription.server.unauthorized_request")).andReturn("a message");
      responseWriter.messageResponse("a message", HttpServletResponse.SC_UNAUTHORIZED);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }    
    
    @Test
    public void doPost_InternalServerError() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      List<Subscription> requestedSubscription = new ArrayList<Subscription>();
      List<Subscription> subscriptions = new ArrayList<Subscription>();
      requestedSubscription.add(new Subscription());
      subscriptions.add(new Subscription());
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getSubscriptions()).andThrow(new RequestParserException("request error"));
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      expect(messages.getMessage("getsubscription.server.internal_server_error")).andReturn("a message");
      responseWriter.messageResponse("a message", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }
}
