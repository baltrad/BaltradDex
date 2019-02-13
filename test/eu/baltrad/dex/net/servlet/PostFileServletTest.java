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
import eu.baltrad.dex.keystore.manager.IKeystoreManager;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.db.manager.IBltFileManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.util.FramePublisherManager;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.keystore.model.Key;

import eu.baltrad.bdb.FileCatalog;
import eu.baltrad.bdb.db.DuplicateEntry;
import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.oh5.Metadata;
import eu.baltrad.bdb.oh5.MetadataMatcher;
import eu.baltrad.bdb.util.FileEntryNamer;
import eu.baltrad.bdb.expr.Expression;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.mo.BltDataMessage;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
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
    private Authenticator authMock;
    private IKeystoreManager keystoreManagerMock;
    private INodeStatusManager nodeStatusManagerMock;
    private FileCatalog fileCatalogMock;
    private IBltMessageManager messageManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private IDataSourceManager dataSourceManagerMock;
    private IUserManager userManagerMock;
    private IRegistryManager registryManagerMock;
    private IBltFileManager fileManagerMock;
    private MessageResourceUtil messages;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private DateFormat format;
    private List<Subscription> localSubscriptions;
    private List<Subscription> peerSubscriptions;
    private Key injectorKey;
    private Key peerKey;
    private FileEntry entryMock;
    private FileEntryNamer namerMock;
    private IFilter filterMock;
    private Subscription s1, s2, s3, s4;
    private DataSource ds1, ds2;
    private User user;
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
    
    protected class ImpossibleMetadataMatcher extends MetadataMatcher 
                                                        implements Matcher {
        @Override
        public boolean match(Metadata metadata, Expression ex) {
            return false;
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
        request.setContent("A test file content".getBytes());
    }
    
    @Before
    public void setUp() {
        mocks = new ArrayList();
        classUnderTest = new PFServlet();
        classUnderTest.setLog(Logger.getLogger("DEX"));
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        
        authMock = (Authenticator) createMock(Authenticator.class);
        
        keystoreManagerMock = (IKeystoreManager) 
                createMock(IKeystoreManager.class); 
        injectorKey = new Key(1, "test.baltrad.eu", 
                "fh7629shue7493kd893748du572895fi", true, true);
        peerKey = new Key(2, "test.baltrad.eu", 
                "yu987876xc6886x898df9sdf89sfd97y", true, false);

        fileCatalogMock = (FileCatalog) createMock(FileCatalog.class);
        entryMock = (FileEntry) createMock(FileEntry.class);
        namerMock = (FileEntryNamer) createMock(FileEntryNamer.class);
        messageManagerMock = (IBltMessageManager) 
                createMock(IBltMessageManager.class);
        subscriptionManagerMock = (ISubscriptionManager) 
                createMock(ISubscriptionManager.class);
        dataSourceManagerMock = (IDataSourceManager)
                createMock(IDataSourceManager.class);
        userManagerMock = (IUserManager) createMock(IUserManager.class);
        registryManagerMock = (IRegistryManager)
                createMock(IRegistryManager.class);
        fileManagerMock = (IBltFileManager) 
                createMock(IBltFileManager.class);
        filterMock = (IFilter) createMock(IFilter.class);
        nodeStatusManagerMock = (INodeStatusManager)createMock(INodeStatusManager.class); 
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        format = new SimpleDateFormat(DATE_FORMAT);
        long time = 1340189763867L;
        
        peerSubscriptions = new ArrayList<Subscription>();
        
        s1 = new Subscription(1, time, "peer", "User1", "DS1", true, true);
        peerSubscriptions.add(s1);
        s2 = new Subscription(2, time, "peer", "User2", "DS2", true, true);
        peerSubscriptions.add(s2);
        
        localSubscriptions = new ArrayList<Subscription>();
        
        s3 = new Subscription(3, time, "local", "User1", "DS3", true, true);
        localSubscriptions.add(s3);
        s4 = new Subscription(4, time, "local", "User2", "DS4", true, true);
        localSubscriptions.add(s4);
        
        ds1 = new DataSource(1, "DS1", DataSource.PEER, "Some data source",
                "12374", "PVOL");
        ds2 = new DataSource(2, "DS2", DataSource.PEER, "Another data source",
                "12331", "SCAN");
        
        user = new User(1, "user", "user", "s3cret", "org", "unit", 
                "locality", "state", "PL", "http://test.baltrad.eu");
        
        setAttributes(request);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        authMock = null;
        keystoreManagerMock = null;
        fileCatalogMock = null;
        subscriptionManagerMock = null;
        dataSourceManagerMock = null;
        userManagerMock = null;
        registryManagerMock = null;
        messageManagerMock = null;
        fileManagerMock = null;
        filterMock = null;
        entryMock = null;
        namerMock = null;
        nodeStatusManagerMock = null;
        injectorKey = null;
        peerKey = null;
        s1 = s2 = s3 = s4 = null;
        ds1 = ds2 = null;
        user = null;
        resetAll();
    }
    
    @Test
    public void handleRequest_MessageVerificationError() throws Exception {
        authMock.authenticate(isA(String.class), isA(String.class),
                isA(String.class));
        
        expectLastCall().andThrow(new KeyczarException(
                "Failed to verify message"));
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertEquals(messages
                .getMessage("postfile.server.message_verifier_error"), 
                    response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_Unauthorized() throws Exception {
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
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        expect(fileCatalogMock.store(isA(InputStream.class))).andReturn(null);
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals(
            messages.getMessage("postfile.server.generic_post_file_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_DuplicateEntry() throws Exception {
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andThrow(new DuplicateEntry());
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        classUnderTest.handleRequest(request, response);
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
        assertEquals(
            messages.getMessage("postfile.server.duplicate_entry_error"), 
            response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_DatabaseError() throws Exception {    
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andThrow(new DatabaseError());
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
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
    public void handleRequest_InjectorNoMatch() throws Exception {
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        expect(entryMock.getUuid()).andReturn(UUID.fromString(ENTRY_UUID))
                .anyTimes();
        expect(entryMock.getMetadata()).andReturn(null).anyTimes();
        expect(entryMock.getContentStream())
            .andReturn(new ByteArrayInputStream("content stream".getBytes()))
            .anyTimes();
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andReturn(entryMock);
        expect(namerMock.name(isA(FileEntry.class))).andReturn(ENTRY_NAME);
        expect(keystoreManagerMock.load("test.baltrad.eu"))
                .andReturn(injectorKey);
        messageManagerMock.manage(isA(BltDataMessage.class));
        expectLastCall();
        expect(subscriptionManagerMock.load(Subscription.PEER)).
                andReturn(peerSubscriptions);
        expect(filterMock.getExpression()).andReturn(null).anyTimes();
        expect(fileManagerMock.loadFilter(s1.getDataSource(), DataSource.LOCAL))
                .andReturn(filterMock).once();
        expect(fileManagerMock.loadFilter(s2.getDataSource(), DataSource.LOCAL))
                .andReturn(filterMock).once();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNamer(namerMock);
        classUnderTest.setKeystoreManager(keystoreManagerMock);
        classUnderTest.setMessageManager(messageManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setFileManager(fileManagerMock);
        classUnderTest.setMatcher(new ImpossibleMetadataMatcher());
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
    }
    
    @Test
    public void handleRequest_InjectorSendToSubscribers() throws Exception {
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        
        authMock.addCredentials(isA(HttpUriRequest.class), isA(String.class));
        expectLastCall().times(2);
        expect(entryMock.getUuid()).andReturn(UUID.fromString(ENTRY_UUID))
                .anyTimes();
        expect(entryMock.getMetadata()).andReturn(null).anyTimes();
        expect(entryMock.getContentStream())
            .andReturn(new ByteArrayInputStream("content stream".getBytes()))
            .anyTimes();
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andReturn(entryMock);
        expect(namerMock.name(isA(FileEntry.class))).andReturn(ENTRY_NAME);
        expect(keystoreManagerMock.load("test.baltrad.eu"))
                .andReturn(injectorKey);
        messageManagerMock.manage(isA(BltDataMessage.class));
        expectLastCall();
        expect(subscriptionManagerMock.load(Subscription.PEER)).
                andReturn(peerSubscriptions);
        expect(filterMock.getExpression()).andReturn(null).anyTimes();
        expect(fileManagerMock.loadFilter(s1.getDataSource(), DataSource.LOCAL))
                .andReturn(filterMock).once();
        expect(fileManagerMock.loadFilter(s2.getDataSource(), DataSource.LOCAL))
                .andReturn(filterMock).once();
        expect(dataSourceManagerMock.load("DS1", DataSource.LOCAL))
                .andReturn(ds1).once();
        expect(dataSourceManagerMock.load("DS2", DataSource.LOCAL))
                .andReturn(ds2).once();
        expect(userManagerMock.load(isA(String.class)))
                .andReturn(user).times(2);
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNamer(namerMock);
        classUnderTest.setKeystoreManager(keystoreManagerMock);
        classUnderTest.setMessageManager(messageManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setFileManager(fileManagerMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setMatcher(new EasyMetadataMatcher());
        classUnderTest.setRegistryManager(registryManagerMock);
        classUnderTest.setFramePublisherManager(new FramePublisherManager());
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
    }
    
    @Test
    public void handleRequest_InjectorSendToSubscribersException() 
            throws Exception {
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        
        expect(entryMock.getUuid()).andReturn(UUID.fromString(ENTRY_UUID))
                .anyTimes();
        expect(entryMock.getMetadata()).andReturn(null).anyTimes();
        expect(entryMock.getContentStream())
            .andReturn(new ByteArrayInputStream("content stream".getBytes()))
            .anyTimes();
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andReturn(entryMock);
        expect(namerMock.name(isA(FileEntry.class))).andReturn(ENTRY_NAME);
        expect(keystoreManagerMock.load("test.baltrad.eu"))
                .andReturn(injectorKey);
        messageManagerMock.manage(isA(BltDataMessage.class));
        expectLastCall();
        expect(subscriptionManagerMock.load(Subscription.PEER)).
                andReturn(peerSubscriptions);
        expect(filterMock.getExpression()).andReturn(null).anyTimes();
        expect(fileManagerMock.loadFilter(s1.getDataSource(), DataSource.LOCAL))
                .andReturn(null);
        expect(fileManagerMock.loadFilter(s2.getDataSource(), DataSource.LOCAL))
                .andReturn(null);
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNamer(namerMock);
        classUnderTest.setKeystoreManager(keystoreManagerMock);
        classUnderTest.setMessageManager(messageManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setFileManager(fileManagerMock);
        classUnderTest.setMatcher(new EasyMetadataMatcher());
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
    }
    
    @Test
    public void handleRequest_PeerSubscriptonInvalid() throws Exception {
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        nodeStatusManagerMock.setRuntimeNodeStatus("test.baltrad.eu", HttpServletResponse.SC_OK);
        
        expect(entryMock.getUuid()).andReturn(UUID.fromString(ENTRY_UUID))
                .anyTimes();
        expect(entryMock.getMetadata()).andReturn(null).anyTimes();
        expect(entryMock.getContentStream())
            .andReturn(new ByteArrayInputStream("content stream".getBytes()))
            .anyTimes();
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andReturn(entryMock);
        fileCatalogMock.remove(entryMock);
        expectLastCall().once();
        expect(namerMock.name(isA(FileEntry.class))).andReturn(ENTRY_NAME);
        expect(keystoreManagerMock.load("test.baltrad.eu"))
                .andReturn(peerKey);
        expect(subscriptionManagerMock.load(Subscription.LOCAL)).
                andReturn(localSubscriptions);
        expect(filterMock.getExpression()).andReturn(null).anyTimes();
        expect(fileManagerMock.loadFilter(s3.getDataSource(), DataSource.PEER))
                .andReturn(filterMock).once();
        expect(fileManagerMock.loadFilter(s4.getDataSource(), DataSource.PEER))
                .andReturn(filterMock).once();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNamer(namerMock);
        classUnderTest.setKeystoreManager(keystoreManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setFileManager(fileManagerMock);
        classUnderTest.setMatcher(new ImpossibleMetadataMatcher());
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
    }
    
    @Test
    public void handleRequest_PeerSubscriptionOK() throws Exception {
        expect(authMock.authenticate(isA(String.class), isA(String.class), 
                isA(String.class))).andReturn(Boolean.TRUE);
        expect(entryMock.getUuid()).andReturn(UUID.fromString(ENTRY_UUID))
                .anyTimes();
        expect(entryMock.getMetadata()).andReturn(null).anyTimes();
        expect(entryMock.getContentStream())
            .andReturn(new ByteArrayInputStream("content stream".getBytes()))
            .anyTimes();
        expect(fileCatalogMock.store(isA(InputStream.class)))
                .andReturn(entryMock);
        expect(namerMock.name(isA(FileEntry.class))).andReturn(ENTRY_NAME);
        expect(keystoreManagerMock.load("test.baltrad.eu"))
                .andReturn(peerKey);
        messageManagerMock.manage(isA(BltDataMessage.class));
        expectLastCall();
        expect(subscriptionManagerMock.load(Subscription.LOCAL)).
                andReturn(localSubscriptions);
        expect(filterMock.getExpression()).andReturn(null).anyTimes();
        expect(fileManagerMock.loadFilter(s3.getDataSource(), DataSource.PEER))
                .andReturn(filterMock).once();
        expect(fileManagerMock.loadFilter(s4.getDataSource(), DataSource.PEER))
                .andReturn(filterMock).once();
        
        replayAll();
        
        classUnderTest.setAuthenticator(authMock);
        classUnderTest.setCatalog(fileCatalogMock);
        classUnderTest.setNamer(namerMock);
        classUnderTest.setKeystoreManager(keystoreManagerMock);
        classUnderTest.setMessageManager(messageManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setFileManager(fileManagerMock);
        classUnderTest.setMatcher(new EasyMetadataMatcher());
        classUnderTest.handleRequest(request, response);
    }
    
}
