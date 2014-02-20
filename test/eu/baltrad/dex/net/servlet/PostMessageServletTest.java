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

import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestParser;
import eu.baltrad.dex.net.protocol.RequestParserException;
import eu.baltrad.dex.net.protocol.ResponseWriter;
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.IBltXmlMessage;


import org.easymock.EasyMockSupport;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.keyczar.exceptions.KeyczarException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Post message servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class PostMessageServletTest extends EasyMockSupport {
    interface MethodMock {
      public void doPost(HttpServletRequest request, HttpServletResponse response);
    };
    
    private PMServlet classUnderTest;
    private MessageResourceUtil messages;
    private Authenticator authenticator;
    private ProtocolManager protocolManager;
    private IBltMessageManager bltMessageManager;
    
    @SuppressWarnings("serial")
    protected class PMServlet extends PostMessageServlet {
        public PMServlet() {}
        @Override
        public void initConfiguration() {}
    }
    
    @Before
    public void setUp() {
        messages = createMock(MessageResourceUtil.class);
        authenticator = createMock(Authenticator.class);
        protocolManager = createMock(ProtocolManager.class);
        bltMessageManager = createMock(IBltMessageManager.class);

        classUnderTest = new PMServlet();
        classUnderTest.setMessages(messages);
        classUnderTest.setAuthenticator(authenticator);
        classUnderTest.setBltMessageManager(bltMessageManager);
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
      HttpSession session = createMock(HttpSession.class);
      
      final MethodMock methods = createMock(MethodMock.class);
      expect(request.getSession(true)).andReturn(session);
      methods.doPost(request, response);
      
      classUnderTest = new PMServlet() {
        @Override
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
      ResponseWriter writer = createMock(ResponseWriter.class);
      IBltXmlMessage xmlMessage = createMock(IBltXmlMessage.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1");
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(requestParser.getBltXmlMessage()).andReturn(xmlMessage);
      expect(requestParser.getWriter(response)).andReturn(writer);
      writer.statusResponse(HttpServletResponse.SC_OK);
      
      bltMessageManager.manage(xmlMessage);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }
    
    @Test
    public void doPost_RequestParserException() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter writer = createMock(ResponseWriter.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1");
      expect(requestParser.isAuthenticated(authenticator)).andReturn(true);
      expect(requestParser.getNodeName()).andReturn("NodeName");
      expect(requestParser.getBltXmlMessage()).andThrow(new RequestParserException("not a beast message"));
      expect(requestParser.getWriter(response)).andReturn(writer);
      expect(messages.getMessage("postmessage.server.internal_server_error")).andReturn("a message");
      writer.messageResponse("a message", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }
    
    @Test
    public void doPost_MessageVerificationError() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter writer = createMock(ResponseWriter.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1");
      expect(requestParser.isAuthenticated(authenticator)).andThrow(new KeyczarException("keyczar exception"));
      expect(requestParser.getWriter(response)).andReturn(writer);
      expect(messages.getMessage("postmessage.server.message_verifier_error")).andReturn("a message");
      writer.messageResponse("a message", HttpServletResponse.SC_UNAUTHORIZED);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }
   
    @Test
    public void doPost_Unauthorized() throws Exception {
      HttpServletRequest request = createMock(HttpServletRequest.class);
      HttpServletResponse response = createMock(HttpServletResponse.class);
      RequestParser requestParser = createMock(RequestParser.class);
      ResponseWriter writer = createMock(ResponseWriter.class);
      
      expect(protocolManager.createParser(request)).andReturn(requestParser);
      expect(requestParser.getProtocolVersion()).andReturn("2.1");
      expect(requestParser.isAuthenticated(authenticator)).andReturn(false);
      expect(requestParser.getWriter(response)).andReturn(writer);
      expect(messages.getMessage("postmessage.server.unauthorized_request")).andReturn("a message");
      writer.messageResponse("a message", HttpServletResponse.SC_UNAUTHORIZED);
      
      replayAll();
      
      classUnderTest.doPost(request, response);
      
      verifyAll();
    }
}
