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
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;
import java.text.DateFormat;
import org.springframework.web.servlet.ModelAndView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.easymock.EasyMockSupport;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import static org.easymock.EasyMock.*;
import org.junit.After;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.keyczar.exceptions.KeyczarException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Post subscription servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class StartSubscriptionServletTest extends EasyMockSupport {
    interface MethodMock {
      Set<DataSource> storePeerSubscriptions(String nodeName, Set<DataSource> requestedDataSources);
      void doPost(HttpServletRequest request, HttpServletResponse response);
    };
    
    private PSServlet classUnderTest;
    private MessageResourceUtil messages;
    private ISubscriptionManager subscriptionManagerMock;
    private Authenticator authenticator;
    private INodeStatusManager nodeStatusManagerMock;
    private ProtocolManager protocolManager;
    private MethodMock methods;
    
    @SuppressWarnings("serial")
    class PSServlet extends StartSubscriptionServlet {
        public PSServlet() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
        @Override
        protected Set<DataSource> storePeerSubscriptions(String nodeName, Set<DataSource> requestedDataSources) {
          return methods.storePeerSubscriptions(nodeName, requestedDataSources);
        }
    }
    
    @Before
    public void setUp() {
        messages = createMock(MessageResourceUtil.class);
        protocolManager = createMock(ProtocolManager.class);
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
        authenticator = createMock(Authenticator.class);
        nodeStatusManagerMock = createMock(INodeStatusManager.class);
        methods = createMock(MethodMock.class);
        classUnderTest = new PSServlet();
        classUnderTest.setLog(Logger.getLogger("DEX"));
        classUnderTest.setMessages(messages);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setAuthenticator(authenticator);
        classUnderTest.setProtocolManager(protocolManager);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    }
    
    @SuppressWarnings("serial")
    @Test
    public void handleRequest() throws Exception {
      final MethodMock methods = createMock(MethodMock.class);
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      HttpSession httpSession = createMock(HttpSession.class);
      
      classUnderTest = new PSServlet() {
        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) {
          methods.doPost(request, response);
        }
      };
      
      expect(request.getSession(true)).andReturn(httpSession);
      methods.doPost(request, response);
      
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
      
      DataSource ds = new DataSource("DS1", DataSource.PEER, "D1", "S1", "FO1");
      Set<DataSource> requestedDataSources = new HashSet<DataSource>();
      requestedDataSources.add(ds);
      Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
      subscribedDataSources.add(ds);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getDataSources()).andReturn(requestedDataSources);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(methods.storePeerSubscriptions("NodeName", requestedDataSources)).andReturn(subscribedDataSources);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.dataSourcesResponse(classUnderTest.localNode.getName(), subscribedDataSources, HttpServletResponse.SC_OK);
      
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
      
      DataSource ds = new DataSource("DS1", DataSource.PEER, "D1", "S1", "FO1");
      DataSource ds2 = new DataSource("DS2", DataSource.PEER, "D2", "S2", "FO2");
      Set<DataSource> requestedDataSources = new HashSet<DataSource>();
      requestedDataSources.add(ds);
      requestedDataSources.add(ds2);
      Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
      subscribedDataSources.add(ds);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getDataSources()).andReturn(requestedDataSources);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(methods.storePeerSubscriptions("NodeName", requestedDataSources)).andReturn(subscribedDataSources);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.dataSourcesResponse(classUnderTest.localNode.getName(), subscribedDataSources, HttpServletResponse.SC_PARTIAL_CONTENT);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }

    @Test
    public void doPost_noneFound() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      
      DataSource ds = new DataSource("DS1", DataSource.PEER, "D1", "S1", "FO1");
      DataSource ds2 = new DataSource("DS2", DataSource.PEER, "D2", "S2", "FO2");
      Set<DataSource> requestedDataSources = new HashSet<DataSource>();
      requestedDataSources.add(ds);
      requestedDataSources.add(ds2);
      Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getDataSources()).andReturn(requestedDataSources);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(methods.storePeerSubscriptions("NodeName", requestedDataSources)).andReturn(subscribedDataSources);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      expect(messages.getMessage("postsubscription.server.generic_subscription_error")).andReturn("a message");
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
      expect(messages.getMessage("postsubscription.server.unauthorized_request")).andReturn("a message");
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
      
      DataSource ds = new DataSource("DS1", DataSource.PEER, "D1", "S1", "FO1");
      Set<DataSource> requestedDataSources = new HashSet<DataSource>();
      requestedDataSources.add(ds);
      Set<DataSource> subscribedDataSources = new HashSet<DataSource>();
      subscribedDataSources.add(ds);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getDataSources()).andThrow(new RuntimeException("a runtime exception"));
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      expect(messages.getMessage("postsubscription.server.internal_server_error")).andReturn("a message");
      responseWriter.messageResponse("a message", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }
}
