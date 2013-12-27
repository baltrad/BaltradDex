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

package eu.baltrad.dex.net.util;

import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.log4j.Logger;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;

/**
 * Post file task test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.9.0
 * @since 1.1.0
 */
public class PostFileTaskTest {
    private final static String ENTRY_UUID = 
            "ca30c41a-76c2-45c9-ac01-0f4a67a148b9";
    
    private PFTask classUnderTest;
    private IRegistryManager registryManagerMock;
    private ISubscriptionManager subscriptionManagerMock;
    private IHttpClientUtil httpClientUtilMock;
    
    private List mocks;
    private HttpUriRequest request;
    private User receiver;
    private DataSource dataSource;
    private Subscription s;
    
    protected class PFTask extends PostFileTask {
        
        public PFTask(IRegistryManager registryManager, 
                ISubscriptionManager subscriptionManager, 
                HttpUriRequest request, String uuid, User user, 
                DataSource dataSource) {
            this.registryManager = registryManager;
            this.subscriptionManager = subscriptionManager;
            this.request = request;
            this.uuid = uuid;
            this.user = user;
            this.dataSource = dataSource;
            this.log = Logger.getLogger("DEX");
        }
        public IHttpClientUtil getHttpClient() { return httpClient; }
        public void setHttpClient(IHttpClientUtil httpClient) {
            this.httpClient = httpClient;
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
    
    private HttpResponse createResponse(int code, String reason) 
            throws Exception {
        ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(version, code, reason);
        HttpResponse response = new BasicHttpResponse(statusLine);
        return response;
    } 
    
    @Before
    public void setUp() {
        mocks = new ArrayList();
        request = new HttpPost();
        receiver = new User(1, "test", "user", "s3cret", "org", "unit", 
                "locality", "state", "XX", "http://test.baltrad.eu:8084");
        dataSource = new DataSource(1, "DS1", DataSource.PEER, "A data source",
                "12374", "SCAN");
        s = new Subscription(1, 1340189763867L, Subscription.PEER, 
                receiver.getName(), dataSource.getName(), true, true);
    }
    
    @After
    public void tearDown() {
        resetAll();
        classUnderTest = null;
        mocks = null;
        request = null;
        receiver = null;
        dataSource = null;
        s = null;
    }
   
    @Test 
    public void run_OK() throws Exception {
        registryManagerMock = (IRegistryManager) 
                createMock(IRegistryManager.class); 
        expect(registryManagerMock.store(isA(RegistryEntry.class)))
                .andReturn(1);
        
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        HttpResponse response = createResponse(HttpServletResponse.SC_OK, "OK");
        httpClientUtilMock = (IHttpClientUtil) 
                createMock(IHttpClientUtil.class);
        expect(httpClientUtilMock.post(request)).andReturn(response);
        httpClientUtilMock.shutdown();
        expectLastCall();
        
        replayAll();
        
        classUnderTest = new PFTask(registryManagerMock, 
            subscriptionManagerMock, request, ENTRY_UUID, receiver, dataSource);
        classUnderTest.setHttpClient(httpClientUtilMock);
        classUnderTest.run();
        
        verifyAll();        
    }
    
    @Test
    public void run_Exception() throws Exception {
        registryManagerMock = (IRegistryManager) 
                createMock(IRegistryManager.class); 
        
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        httpClientUtilMock = (IHttpClientUtil) 
                createMock(IHttpClientUtil.class);
        expect(httpClientUtilMock.post(request))
                .andThrow(new IOException("Failed to post file"));
        httpClientUtilMock.shutdown();
        expectLastCall();
        
        replayAll();
        
        classUnderTest = new PFTask(registryManagerMock, 
            subscriptionManagerMock, request, ENTRY_UUID, receiver, dataSource);
        classUnderTest.setHttpClient(httpClientUtilMock);
        classUnderTest.run();
        
        verifyAll();
    }
    
    @Test 
    public void run_Forbidden() throws Exception {
        registryManagerMock = (IRegistryManager) 
                createMock(IRegistryManager.class);
        
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        expect(subscriptionManagerMock.load(Subscription.PEER, 
                receiver.getName(), dataSource.getName())).andReturn(s).once();
        subscriptionManagerMock.delete(s.getId());
        expectLastCall();
        
        HttpResponse response = createResponse(HttpServletResponse.SC_FORBIDDEN, 
                "Subscription invalid");
        httpClientUtilMock = (IHttpClientUtil) 
                createMock(IHttpClientUtil.class);
        expect(httpClientUtilMock.post(request)).andReturn(response);
        httpClientUtilMock.shutdown();
        expectLastCall();
        
        replayAll();
        
        classUnderTest = new PFTask(registryManagerMock, 
            subscriptionManagerMock, request, ENTRY_UUID, receiver, dataSource);
        classUnderTest.setHttpClient(httpClientUtilMock);
        classUnderTest.run();
        
        verifyAll();    
    }
    
    @Test
    public void run_Conflict() throws Exception {
        registryManagerMock = (IRegistryManager) 
                createMock(IRegistryManager.class);
        
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        HttpResponse response = createResponse(HttpServletResponse.SC_CONFLICT, 
                "File exists");
        httpClientUtilMock = (IHttpClientUtil) 
                createMock(IHttpClientUtil.class);
        expect(httpClientUtilMock.post(request)).andReturn(response);
        httpClientUtilMock.shutdown();
        expectLastCall();
        
        replayAll();
        
        classUnderTest = new PFTask(registryManagerMock, 
            subscriptionManagerMock, request, ENTRY_UUID, receiver, dataSource);
        classUnderTest.setHttpClient(httpClientUtilMock);
        classUnderTest.run();
        
        verifyAll();
    }
    
    @Test
    public void run_RetrySuccess() throws Exception {
        registryManagerMock = (IRegistryManager) 
                createMock(IRegistryManager.class);
        expect(registryManagerMock.store(isA(RegistryEntry.class)))
                .andReturn(1);
        
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        HttpResponse response = createResponse(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        httpClientUtilMock = (IHttpClientUtil) 
                createMock(IHttpClientUtil.class);
        expect(httpClientUtilMock.post(request)).andReturn(response).times(2);
        response = createResponse(HttpServletResponse.SC_OK, "OK");
        expect(httpClientUtilMock.post(request)).andReturn(response).once();
        
        httpClientUtilMock.shutdown();
        expectLastCall();
        
        replayAll();
        
        classUnderTest = new PFTask(registryManagerMock, 
            subscriptionManagerMock, request, ENTRY_UUID, receiver, dataSource);
        classUnderTest.setHttpClient(httpClientUtilMock);
        classUnderTest.run();
        
        verifyAll();
    }
    
    @Test
    public void run_RetryFailure() throws Exception {
        registryManagerMock = (IRegistryManager) 
                createMock(IRegistryManager.class);
         expect(registryManagerMock.store(isA(RegistryEntry.class)))
                .andReturn(1);
        
        subscriptionManagerMock = (ISubscriptionManager)
                createMock(ISubscriptionManager.class);
        
        HttpResponse response = createResponse(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        httpClientUtilMock = (IHttpClientUtil) 
                createMock(IHttpClientUtil.class);
        expect(httpClientUtilMock.post(request)).andReturn(response).times(4);
        
        httpClientUtilMock.shutdown();
        expectLastCall();
        
        replayAll();
        
        classUnderTest = new PFTask(registryManagerMock, 
            subscriptionManagerMock, request, ENTRY_UUID, receiver, dataSource);
        classUnderTest.setHttpClient(httpClientUtilMock);
        classUnderTest.run();
        
        verifyAll();
    }
    
}
