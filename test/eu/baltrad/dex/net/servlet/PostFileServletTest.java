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
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.db.manager.IBltFileManager;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.DuplicateEntry;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.oh5.Metadata;
import eu.baltrad.bdb.oh5.MetadataMatcher;
import eu.baltrad.bdb.util.FileEntryNamer;
import eu.baltrad.bdb.expr.Expression;

import eu.baltrad.beast.message.IBltMessage;
import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.db.IFilter;
import eu.baltrad.dex.net.util.FramePublisherManager;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * Post file servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class PostFileServletTest {
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    private final static String ENTRY_NAME = 
            "PVOL NOD:plbrz 2012-06-26T07:50:31";
    private final static String ENTRY_UUID = 
            "72cbb2e6-96ea-4313-9e15-50dc4b7bdb0b";
    
    private PFServlet classUnderTest;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private DateFormat format;
    private List<Subscription> subscriptions;
    private Subscription s1;
    private Subscription s2;
    private List mocks;
    
    protected class PFServlet extends PostFileServlet {
        public PFServlet() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
    }
    
    protected interface Matcher {
        public boolean match(Metadata metadata, Expression ex);
    }
    
    protected class EasyMetadataMatcher extends MetadataMatcher 
                                                        implements Matcher {
        @Override
        public boolean match(Metadata metadata, Expression ex) {
            return true;
        }
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
    
    private void setAttributes(MockHttpServletRequest request) {
        request.setAttribute("Content-Type", "text/html");  
        request.setAttribute("Content-MD5", Base64.encodeBase64String(
                request.getRequestURI().getBytes()));
        request.setAttribute("Date", format.format(new Date()));
        request.addHeader("Authorization", "test.baltrad.eu" + ":" + 
            "AO1fnJYwLAIUEc0CevXIhG7ppda2VPHTfHfbYDMCFB5_rDppVDY07Vh4yh2nT89qnT0_");   
        request.addHeader("Node-Name", "test.baltrad.eu");
        request.addHeader("Node-Address", "http://test.baltrad.eu");
        request.setContent("A test file content".getBytes());
    }
    
    private HttpResponse createResponse(int code, String reason) 
            throws Exception {
        ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(version, code, reason);
        HttpResponse res = new BasicHttpResponse(statusLine);
        return res;
    }
    
    @Before
    public void setUp() {
        mocks = new ArrayList();
        classUnderTest = new PFServlet();
        classUnderTest.setLog(Logger.getLogger("DEX"));
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        format = new SimpleDateFormat(DATE_FORMAT);
        long time = 1340189763867L;
        subscriptions = new ArrayList<Subscription>();
        s1 = new Subscription(1, time, "upload", "User1", "S1", true, true);
        subscriptions.add(s1);
        s2 = new Subscription(2, time, "upload", "User2", "DS2", true, true);
        subscriptions.add(s2);
        setAttributes(request);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        resetAll();
    }
    
    @Test
    public void handleRequest_MessageVerificationError() throws Exception {
        Authenticator authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        authenticatorMock.authenticate(isA(String.class), isA(String.class),
                isA(String.class));
        
        expectLastCall().andThrow(new KeyczarException(
                "Failed to verify message"));
        
        replayAll();
        
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(messages
                .getMessage("postfile.server.message_verifier_error"), 
                    response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_Unauthorized() throws Exception {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.FALSE);
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(
            messages.getMessage("postfile.server.unauthorized_request"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_GenericError() throws Exception {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        
        FileCatalog fileCatalogMock = 
                (FileCatalog) createMock(FileCatalog.class);
        expect(fileCatalogMock.store(isA(InputStream.class))).andReturn(null);
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(
            messages.getMessage("postfile.server.generic_post_file_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_DuplicateEntry() throws Exception {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        FileCatalog fileCatalogMock = 
                (FileCatalog) createMock(FileCatalog.class);
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andThrow(new DuplicateEntry());
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
        assertEquals(
            messages.getMessage("postfile.server.duplicate_entry_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_DatabaseError() throws Exception {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        FileCatalog fileCatalogMock = 
                (FileCatalog) createMock(FileCatalog.class);
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andThrow(new DatabaseError());
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.handleRequest(request, response);
        verify(authMock);
        verify(fileCatalogMock);
        
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals(
            messages.getMessage("postfile.server.database_error"), 
            response.getErrorMessage());
    }
        
    @Test
    public void handleRequest_OK() throws Exception {
        Authenticator authMock = (Authenticator) createMock(Authenticator.class);
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        authMock.addCredentials(isA(HttpUriRequest.class), isA(String.class));
        authMock.addCredentials(isA(HttpUriRequest.class), isA(String.class));
        
        FileEntry entryMock = (FileEntry) createMock(FileEntry.class);
        expect(entryMock.getUuid()).andReturn(UUID.fromString(ENTRY_UUID))
                .anyTimes();
        expect(entryMock.getMetadata()).andReturn(null).anyTimes();
        expect(entryMock.getContentStream())
            .andReturn(new ByteArrayInputStream("content stream".getBytes()))
            .anyTimes();
        
        FileCatalog fileCatalogMock = 
                (FileCatalog) createMock(FileCatalog.class);
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andReturn(entryMock);
        
        FileEntryNamer namerMock = 
                (FileEntryNamer) createMock(FileEntryNamer.class);
        expect(namerMock.name(isA(FileEntry.class))).andReturn(ENTRY_NAME);
        
        IBltMessageManager messageManagerMock = 
                (IBltMessageManager) createMock(IBltMessageManager.class);
        messageManagerMock.manage(isA(IBltMessage.class));
        
        ISubscriptionManager subscriptionManagerMock = 
                (ISubscriptionManager) createMock(ISubscriptionManager.class);
        expect(subscriptionManagerMock.load(Subscription.PEER)).
                andReturn(subscriptions);
        IFilter filterMock = (IFilter) createMock(IFilter.class);
        expect(filterMock.getExpression()).andReturn(null).anyTimes();
        
        
        IBltFileManager fileManagerMock = 
                (IBltFileManager) createMock(IBltFileManager.class);
        expect(fileManagerMock.loadFilter(s1.getDataSource()))
                .andReturn(filterMock);
        expect(fileManagerMock.loadFilter(s2.getDataSource()))
                .andReturn(filterMock);
        
        IUserManager userManagerMock = 
                (IUserManager) createMock(IUserManager.class);
        User account = new User(1, "user", "user", "s3cret", "org", "unit", 
                "locality", "state", "PL", "http://test.baltrad.eu");
        expect(userManagerMock.load(isA(String.class)))
                .andReturn(account).anyTimes();
        
        IRegistryManager deliveryRegistryManagerMock =
          (IRegistryManager) createMock(IRegistryManager.class);
        
        expect(deliveryRegistryManagerMock.store(isA(RegistryEntry.class)))
                .andReturn(Integer.SIZE).anyTimes();
        
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, null);
        IHttpClientUtil httpClientMock = 
                (IHttpClientUtil) createMock(IHttpClientUtil.class);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res).anyTimes();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNamer(namerMock);
        classUnderTest.setMessageManager(messageManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setFileManager(fileManagerMock);
        classUnderTest.setMatcher(new EasyMetadataMatcher());
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setRegistryManager(deliveryRegistryManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setFramePublisherManager(new FramePublisherManager());
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
}
