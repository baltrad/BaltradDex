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
import eu.baltrad.dex.util.MessageResourceUtil;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.beast.parser.IXmlMessageParser;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.dom4j.Document;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Post message servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class PostMessageServletTest {
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private PMServlet classUnderTest;
    private List<Object> mocks;
    private MessageResourceUtil messages;
    private DateFormat format;
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    protected class PMServlet extends PostMessageServlet {
        public PMServlet() {
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
    
    private void setAttributes(MockHttpServletRequest request, String content) {
        request.setAttribute("Content-Type", "text/html");  
        request.setAttribute("Content-MD5", Base64.encodeBase64String(
                request.getRequestURI().getBytes()));
        request.setAttribute("Date", format.format(new Date()));
        request.addHeader("Authorization", "test.baltrad.eu" + ":" + 
            "AO1fnJYwLAIUEc0CevXIhG7ppda2VPHTfHfbYDMCFB5_rDppVDY07Vh4yh2nT89qnT0_");   
        request.addHeader("Node-Name", "test.baltrad.eu");
        request.addHeader("Node-Address", "http://test.baltrad.eu");
        request.setContent(content.getBytes());
    }
    
    @Before
    public void setUp() {
        classUnderTest = new PMServlet();
        mocks = new ArrayList<Object>();
        messages = new MessageResourceUtil("resources/messages");
        classUnderTest.setMessages(messages);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        format = new SimpleDateFormat(DATE_FORMAT);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    }
    
    @Test
    public void handleRequest_Unauthorized() {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.FALSE);
        replayAll();
        
        setAttributes(request, "Hi there!");
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
            messages.getMessage("postmessage.server.unauthorized_request"), 
            response.getErrorMessage());
    }
    
    @Test 
    public void handleRequest_InternalServerError() {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        replayAll();
        
        setAttributes(request, "Hi there!");
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
            messages.getMessage("postmessage.server.internal_server_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_OK() {
        IBltXmlMessage bltmsg = new IBltXmlMessage() {
            public Document toDocument() { return null; }
            public void fromDocument(Document arg0) {}
        };
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        IXmlMessageParser xmlMessageParserMock = (IXmlMessageParser) 
                createMock(IXmlMessageParser.class);
        expect(xmlMessageParserMock.parse("<bltalert>..</bltalert>"))
                .andReturn(bltmsg);
        IBltMessageManager bltMessageMAnagerMock = (IBltMessageManager)
                createMock(IBltMessageManager.class);
        bltMessageMAnagerMock.manage(bltmsg);
        
        expectLastCall();
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setBltMessageManager(bltMessageMAnagerMock);
        classUnderTest.setXmlMessageParser(xmlMessageParserMock);
        setAttributes(request, "<bltalert>..</bltalert>");
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    @Test
    public void handleRequest_NotABeastMessage() {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        IXmlMessageParser xmlMessageParserMock = (IXmlMessageParser) 
                createMock(IXmlMessageParser.class);
        expect(xmlMessageParserMock.parse("not a beast message"))
                .andThrow(new RuntimeException("Invalid message format"));
        IBltMessageManager bltMessageMAnagerMock = (IBltMessageManager)
                createMock(IBltMessageManager.class);
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setBltMessageManager(bltMessageMAnagerMock);
        classUnderTest.setXmlMessageParser(xmlMessageParserMock);
        setAttributes(request, "not a beast message");
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
            messages.getMessage("postmessage.server.internal_server_error"), 
            response.getErrorMessage());
    }
    
}
