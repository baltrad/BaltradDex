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

import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * Remove subscription controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.9.0
 * @since 1.9.0
 */
public class RemoveSubscriptionControllerTest {

    private RSController classUnderTest;
    private IUserManager userManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private Authenticator authenticatorMock;
    private IHttpClientUtil httpClientMock;
    private PlatformTransactionManager txManagerMock;
    private IDataSourceManager dataSourceManagerMock;
    
    private List<Object> mocks;
    private User peer;
    private List<User> operators;
    private List<Subscription> downloads;
    private List<Subscription> activeDownloads;
    private List<Subscription> uploads;
    private Subscription s1, s2, s3, s4, s5;
    private MockHttpServletRequest request;
    private MessageResourceUtil messages;
    
    class RSController extends RemoveSubscriptionController {
        public RSController() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
        
        public void setPeerName(String peerName) {
            this.peerName = peerName;
        }
        public List<Subscription> getSelectedDownloads() {
            return this.selectedDownloads;
        }
        public void setSelectedDownloads(List<Subscription> selectedDownloads) {
            this.selectedDownloads = selectedDownloads;
        }
        public List<Subscription> getSelectedActiveDownloads() {
            return this.selectedActiveDownloads;
        }
        public List<Subscription> getSelectedUploads() {
            return this.selectedUploads;
        }
        public void setSelectedUploads(List<Subscription> selectedUploads) {
            this.selectedUploads = selectedUploads;
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
    
    @Before
    public void setUp() {
        classUnderTest = new RSController();
        mocks = new ArrayList<Object>();
        request = new MockHttpServletRequest();
        userManagerMock = (IUserManager) createMock(IUserManager.class);
        subscriptionManagerMock = (ISubscriptionManager) 
                createMock(ISubscriptionManager.class);
        authenticatorMock = (Authenticator) createMock(Authenticator.class);
        httpClientMock = (IHttpClientUtil) createMock(IHttpClientUtil.class);
        txManagerMock = (PlatformTransactionManager) 
                createMock(PlatformTransactionManager.class);
        dataSourceManagerMock = (IDataSourceManager) 
                createMock(IDataSourceManager.class);
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        
        peer = new User("peer.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", "http://peer.baltrad.eu");
        
        operators = new ArrayList<User>();
        operators.add(new User(1, "test1.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test1.baltrad.eu"));
        operators.add(new User(2, "test2.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test2.baltrad.eu"));
        operators.add(new User(3, "test3.baltrad.eu", "user", "s3cret", "org", 
                "unit", "locality", "state", "XX", 
                "http://test3.baltrad.eu"));
        
        downloads = new ArrayList<Subscription>();
        long time = 1340189763867L;
        downloads = new ArrayList<Subscription>();
        s1 = new Subscription(1, time, "download", "PeerUser", "DataSource1", 
                true, true);
        downloads.add(s1);
        s2 = new Subscription(2, time, "download", "PeerUser", "DataSource2", 
                true, false);
        downloads.add(s2);
        s3 = new Subscription(3, time, "download", "PeerUser", "DataSource3", 
                false, false);
        downloads.add(s3);
        
        activeDownloads = new ArrayList<Subscription>();
        activeDownloads.add(s1);
        activeDownloads.add(s2);
        
        uploads = new ArrayList<Subscription>();
        s4 = new Subscription(4, time, "upload", "PeerUser", "DataSource4", 
                true, true);
        uploads.add(s4);
        s5 = new Subscription(5, time, "upload", "PeerUser", "DataSource5", 
                true, false);
        uploads.add(s5);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        peer = null;
        request = null;
        operators = null;
        downloads = null;
        activeDownloads = null;
        uploads = null;
        resetAll();
    }
    
    private HttpResponse createResponse(int code, String reason) 
            throws Exception {
        ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(version, code, reason);
        HttpResponse response = new BasicHttpResponse(statusLine);
        response.addHeader("PeerNode", "peer.baltrad.eu");
        return response;
    } 
    
    @Test
    public void subscribedPeers() {
        expect(userManagerMock.loadOperators()).andReturn(operators).once();
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.subscribedPeers(model);
        
        assertEquals("subscription_remove_downloads_peers", viewName);
        assertTrue(model.containsAttribute("downloads_peers"));
        assertEquals(operators, model.asMap().get("downloads_peers"));
        
        verifyAll();
    }
    
    @Test
    public void downloadsByPeer() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL, "PeerUser"))
                .andReturn(downloads).once();
        
        replayAll();
        
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest.downloadsByPeer(model, "PeerUser");
        
        assertEquals("subscription_remove_downloads", viewName);
        assertTrue(model.containsAttribute("downloads"));
        assertEquals(downloads, model.asMap().get("downloads"));
        assertTrue(model.containsAttribute("peer_name"));
        assertEquals("PeerUser", model.asMap().get("peer_name"));
        
        verifyAll();
    }
    
    @Test
    public void removeSelectedDownloads_Unchecked() {
        expect(subscriptionManagerMock.load(Subscription.LOCAL, "PeerUser"))
                .andReturn(downloads).once();
        
        replayAll();
        
        classUnderTest.setPeerName("PeerUser");
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedDownloads(request, model);
        
        assertEquals("subscription_remove_downloads", viewName);
        assertTrue(model.containsAttribute("downloads"));
        assertEquals(downloads, model.asMap().get("downloads"));
        assertTrue(model.containsAttribute("peer_name"));
        assertEquals("PeerUser", model.asMap().get("peer_name"));
        
        verifyAll();
    }
    
    @Test
    public void removeSelectedDownloads_Checked() {
        expect(subscriptionManagerMock.load(1)).andReturn(s1).once();
        expect(subscriptionManagerMock.load(2)).andReturn(s2).once();
        expect(subscriptionManagerMock.load(3)).andReturn(s3).once();
        
        replayAll();
        
        request.addParameter("downloadIds", new String[] {"1", "2", "3"});
        
        classUnderTest.setPeerName("PeerUser");
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedDownloads(request, model);
        
        assertEquals("subscription_remove_selected_downloads", viewName);
        assertEquals(downloads, classUnderTest.getSelectedDownloads());
        assertEquals(activeDownloads, 
                classUnderTest.getSelectedActiveDownloads());
        
        verifyAll();
    }
    
    @Test
    public void removeDownloadsStatus_OK() throws Exception {
        expect(userManagerMock.load("PeerUser")).andReturn(peer).once();
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(200, "OK");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class))).andReturn(res);
        
        TransactionStatus status = new SimpleTransactionStatus();
        
        expect(txManagerMock.getTransaction(isA((TransactionDefinition.class))))
                .andReturn(status);
        txManagerMock.commit(status);
        expectLastCall();
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setTxManager(txManagerMock);
        classUnderTest.setPeerName("PeerUser");
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeDownloadsStatus(model);
        
        assertEquals("subscription_remove_downloads_status", viewName);
        assertTrue(model.containsAttribute("subscription_remove_success"));
        assertEquals("Successfully removed selected subscriptions.",
                model.asMap().get("subscription_remove_success"));
        
        verifyAll();
    }
    
    @Test
    public void removeDownloadsStatus_CancelFailRemoveOK() throws Exception {
        expect(userManagerMock.load("PeerUser")).andReturn(peer).once();
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(500, "Server failure");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class))).andReturn(res);
        
        TransactionStatus status = new SimpleTransactionStatus();
        
        expect(txManagerMock.getTransaction(isA((TransactionDefinition.class))))
                .andReturn(status);
        txManagerMock.commit(status);
        expectLastCall();
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setTxManager(txManagerMock);
        classUnderTest.setPeerName("PeerUser");
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeDownloadsStatus(model);
        
        assertEquals("subscription_remove_downloads_status", viewName);
        assertTrue(model.containsAttribute("subscription_remove_error"));
        assertEquals("Failed to cancel some of the selected subscriptions. " +
                "This may cause unintended incoming data transfer. " + 
                "Contact peer node's administrator to verify the problem.", 
                    model.asMap().get("subscription_remove_error"));
        
        verifyAll();
    }
    
    @Test
    public void removeDownloadsStatus_CancelOKRemoveFail() throws Exception {
        expect(userManagerMock.load("PeerUser")).andReturn(peer).once();
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(200, "OK");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class))).andReturn(res);
        
        TransactionStatus status = new SimpleTransactionStatus();
        expect(txManagerMock.getTransaction(isA((TransactionDefinition.class))))
                .andReturn(status);
        txManagerMock.rollback(status);
        expectLastCall();
        
        expect(dataSourceManagerMock.load(isA(String.class), isA(String.class)))
                .andReturn(null).anyTimes();
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setTxManager(txManagerMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        classUnderTest.setPeerName("PeerUser");
        classUnderTest.setSelectedDownloads(downloads);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeDownloadsStatus(model);
        
        assertEquals("subscription_remove_downloads_status", viewName);
        assertTrue(model.containsAttribute("subscription_remove_error"));
        assertEquals("Failed to remove subscriptions.",
                model.asMap().get("subscription_remove_error"));
        
        verifyAll();
    }
    
    @Test
    public void removeDownloadsStatus_Fail() throws Exception {
        expect(userManagerMock.load("PeerUser")).andReturn(peer).once();
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(500, "Server failure");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class))).andReturn(res);
        
        TransactionStatus status = new SimpleTransactionStatus();
        expect(txManagerMock.getTransaction(isA((TransactionDefinition.class))))
                .andReturn(status);
        txManagerMock.rollback(status);
        expectLastCall();
        
        expect(dataSourceManagerMock.load(isA(String.class), isA(String.class)))
                .andReturn(null).anyTimes();
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setTxManager(txManagerMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        classUnderTest.setPeerName("PeerUser");
        classUnderTest.setSelectedDownloads(downloads);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeDownloadsStatus(model);
        
        assertEquals("subscription_remove_downloads_status", viewName);
        assertTrue(model.containsAttribute("subscription_remove_error"));
        assertEquals("Failed to cancel and remove selected subscriptions.",
                model.asMap().get("subscription_remove_error"));
        
        verifyAll();
    }
    
    @Test
    public void removeUploads() {
        expect(subscriptionManagerMock.load(Subscription.PEER))
                .andReturn(uploads).once();
        
        replayAll();
        
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeUploads(model);
        
        assertEquals("subscription_remove_uploads", viewName);
        assertTrue(model.containsAttribute("uploads"));
        assertEquals(uploads, model.asMap().get("uploads"));
        
        verifyAll();
    }
    
    @Test 
    public void removeSelectedUploads_Unchecked() {
        expect(subscriptionManagerMock.load(Subscription.PEER))
                .andReturn(uploads).once();
        
        replayAll();
        
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedUploads(request, model);
        
        assertEquals("subscription_remove_uploads", viewName);
        assertTrue(model.containsAttribute("uploads"));
        assertEquals(uploads, model.asMap().get("uploads"));
        
        verifyAll();
    }
    
    @Test 
    public void removeSelectedUploads_Checked() {
        expect(subscriptionManagerMock.load(4)).andReturn(s4).once();
        expect(subscriptionManagerMock.load(5)).andReturn(s5).once();
        
        replayAll();
        
        request.addParameter("uploadIds", new String[] {"4", "5"});
        
        Model model = new ExtendedModelMap();
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        String viewName = classUnderTest
                .removeSelectedUploads(request, model);
        
        assertEquals("subscription_remove_selected_uploads", viewName);
        assertEquals(uploads, classUnderTest.getSelectedUploads());
        
        verifyAll();
    }
    
    @Test
    public void removeUploadsStatus_OK() throws Exception {
        subscriptionManagerMock.delete(4);
        expectLastCall();
        subscriptionManagerMock.delete(5);
        expectLastCall();
        
        replayAll();
        
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setSelectedUploads(uploads);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeUploadsStatus(model);
        
        assertEquals("subscription_remove_uploads_status", viewName);
        assertTrue(model
                .containsAttribute("subscription_remove_success"));
        assertEquals("Selected subscriptions successfully removed", 
                model.asMap().get("subscription_remove_success"));
        
        verifyAll();
    }
    
    @Test
    public void removeUploadsStatus_Fail() throws Exception {
        subscriptionManagerMock.delete(4);
        expectLastCall()
                .andThrow(new Exception("Failed to remove subscription"));
        
        replayAll();
        
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setSelectedUploads(uploads);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.removeUploadsStatus(model);
        
        assertEquals("subscription_remove_uploads_status", viewName);
        assertTrue(model.containsAttribute("subscription_remove_error"));
        assertEquals("Failed to remove selected subscriptions",
                model.asMap().get("subscription_remove_error"));
        
        verifyAll();
    }
    
}
