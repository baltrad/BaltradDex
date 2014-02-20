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
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.user.manager.impl.UserManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.web.servlet.ModelAndView;

import org.keyczar.exceptions.KeyczarException;

import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.*;

import org.apache.log4j.Logger;

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
 * Data source list servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListServletTest extends EasyMockSupport {
  interface MethodMock {
    public void doPost(HttpServletRequest request, HttpServletResponse response);
  };
    
    
    private DSLServlet classUnderTest;
    private MessageResourceUtil messages;
    private Authenticator authenticatorMock;
    private ProtocolManager protocolManager;
    private UserManager userManager;
    private DataSourceManager dataSourceManager;
    
    
    @SuppressWarnings("serial")
    class DSLServlet extends DataSourceListServlet {
        public DSLServlet() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
    }
    
    @Before
    public void setUp() throws Exception {
        authenticatorMock = createMock(Authenticator.class);
        protocolManager = createMock(ProtocolManager.class);
        userManager = createMock(UserManager.class);
        dataSourceManager = createMock(DataSourceManager.class);
        messages = createMock(MessageResourceUtil.class);

        classUnderTest = new DSLServlet();
        classUnderTest.setLog(Logger.getLogger("DEX"));
        classUnderTest.setProtocolManager(protocolManager);
        classUnderTest.setMessages(messages);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setUserManager(userManager);
        classUnderTest.setDataSourceManager(dataSourceManager);
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
      
      classUnderTest = new DSLServlet() {
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
    public void doPost_createPeerAccount() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      User peer = createMock(User.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.0").anyTimes();
      expect(requestParser.isAuthenticated(authenticatorMock)).andReturn(true);
      expect(requestParser.getUserAccount()).andReturn(peer);
      expect(peer.getName()).andReturn("test.baltrad.eu").anyTimes();
      expect(userManager.load("test.baltrad.eu")).andReturn(null);
      peer.setRole(Role.PEER);
      expect(userManager.store(peer)).andReturn(1);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.userAccountResponse(classUnderTest.localNode.getName(), classUnderTest.localNode, HttpServletResponse.SC_CREATED);
      
      replayAll();
        
      classUnderTest.doPost(request, response);
        
      verifyAll();
    }

    @Test
    public void doPost_accountExists() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter responseWriter = createMock(ResponseWriter.class);
      User peer = createMock(User.class);
      User loadedPeer = createMock(User.class);
      List<DataSource> userDataSources = new ArrayList<DataSource>(); 
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.0").anyTimes();
      expect(requestParser.isAuthenticated(authenticatorMock)).andReturn(true);
      expect(requestParser.getUserAccount()).andReturn(peer);
      expect(peer.getName()).andReturn("test.baltrad.eu").anyTimes();
      expect(userManager.load("test.baltrad.eu")).andReturn(loadedPeer);
      expect(loadedPeer.getId()).andReturn(10).anyTimes();
      expect(dataSourceManager.load(10, DataSource.LOCAL)).andReturn(userDataSources);
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.dataSourcesResponse(classUnderTest.localNode.getName(), userDataSources, HttpServletResponse.SC_OK);
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
      expect(requestParser.getProtocolVersion()).andReturn("2.0").anyTimes();
      expect(requestParser.isAuthenticated(authenticatorMock)).andThrow(new KeyczarException("keyczar exception"));
      expect(messages.getMessage("datasource.server.message_verifier_error")).andReturn("a message");
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
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
      expect(requestParser.getProtocolVersion()).andReturn("2.0").anyTimes();
      expect(requestParser.isAuthenticated(authenticatorMock)).andReturn(false);
      expect(messages.getMessage("datasource.server.unauthorized_request")).andReturn("a message");
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
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
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.0").anyTimes();
      expect(requestParser.isAuthenticated(authenticatorMock)).andReturn(true);
      expect(requestParser.getUserAccount()).andThrow(new RuntimeException("runtime exception"));
      expect(messages.getMessage(eq("datasource.server.internal_server_error"), aryEq(new Object[]{"runtime exception"}))).andReturn("a message");
      expect(requestParser.getWriter(response)).andReturn(responseWriter);
      responseWriter.messageResponse("a message", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          
      replayAll();
        
      classUnderTest.doPost(request, response);
        
      verifyAll();
    }
}
