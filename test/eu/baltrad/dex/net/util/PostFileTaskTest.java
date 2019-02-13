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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.model.User;

/**
 * Post file task test.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.9.0
 * @since 1.1.0
 */
public class PostFileTaskTest extends EasyMockSupport {
  private final static String ENTRY_UUID = "ca30c41a-76c2-45c9-ac01-0f4a67a148b9";

  private PFTask classUnderTest;
  private IRegistryManager registryManagerMock;
  private ISubscriptionManager subscriptionManagerMock;
  private INodeStatusManager nodeStatusManagerMock;
  private IHttpClientUtil httpClientUtilMock;

  // private List mocks;
  private HttpUriRequest request;
  private User receiver;
  private DataSource dataSource;
  private Subscription s;
  private PostFileRedirectHandler redirectHandler;

  protected class PFTask extends PostFileTask {

    public PFTask(IRegistryManager registryManager,
        ISubscriptionManager subscriptionManager,
        INodeStatusManager nodeStatusManager, HttpUriRequest request,
        String uuid, User user, DataSource dataSource) {
      this.registryManager = registryManager;
      this.subscriptionManager = subscriptionManager;
      this.nodeStatusManager = nodeStatusManager;
      this.request = request;
      this.uuid = uuid;
      this.user = user;
      this.dataSource = dataSource;
      this.log = Logger.getLogger("DEX");
    }

    public IHttpClientUtil getHttpClient() {
      return httpClient;
    }

    public void setHttpClient(IHttpClientUtil httpClient) {
      this.httpClient = httpClient;
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
    // mocks = new ArrayList();
    request = new HttpPost();
    receiver = new User(1, "test", "user", "s3cret", "org", "unit", "locality",
        "state", "XX", "http://test.baltrad.eu:8084");
    dataSource = new DataSource(1, "DS1", DataSource.PEER, "A data source",
        "12374", "SCAN");
    redirectHandler = createMock(PostFileRedirectHandler.class);
    
    registryManagerMock = (IRegistryManager) createMock(IRegistryManager.class);
    
    subscriptionManagerMock = (ISubscriptionManager) createMock(ISubscriptionManager.class);
    
    nodeStatusManagerMock = (INodeStatusManager) createMock(INodeStatusManager.class);
    
    httpClientUtilMock = (IHttpClientUtil) createMock(IHttpClientUtil.class);

    
    s = new Subscription(1, 1340189763867L, Subscription.PEER,
        receiver.getName(), dataSource.getName(), true, true);
  }

  @After
  public void tearDown() {
    resetAll();
    classUnderTest = null;
    // mocks = null;
    request = null;
    receiver = null;
    dataSource = null;
    s = null;
    redirectHandler = null;
    registryManagerMock = null;
    subscriptionManagerMock = null;
    nodeStatusManagerMock = null;
    httpClientUtilMock = null;
  }

  @Test
  public void run_OK() throws Exception {
    Status st = new Status(0, 0, 0);
    HttpResponse response = createResponse(HttpServletResponse.SC_OK, "OK");

    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();

    expect(httpClientUtilMock.post(request)).andReturn(response);

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(),
        HttpServletResponse.SC_OK);
    
    expect(registryManagerMock.store(isA(RegistryEntry.class))).andReturn(1);
    expectLastCall();

    expect(nodeStatusManagerMock.update(st, 1)).andReturn(1).once();

    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.run();

    verifyAll();
  }

  @Test
  public void run_Exception() throws Exception {
    Status st = new Status(0, 0, 0);

    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();

    expect(httpClientUtilMock.post(request))
        .andThrow(new IOException("Failed to post file"));

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(),
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    expectLastCall();

    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.run();

    verifyAll();
  }

  @Test
  public void run_Forbidden() throws Exception {
    Status st = new Status(0, 0, 0);

    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();

    HttpResponse response = createResponse(HttpServletResponse.SC_FORBIDDEN,
        "Subscription invalid");

    expect(httpClientUtilMock.post(request)).andReturn(response);

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(),
        HttpServletResponse.SC_FORBIDDEN);
    expectLastCall();

    subscriptionManagerMock.delete(s.getId());
    expectLastCall();

    expect(nodeStatusManagerMock.delete(1)).andReturn(1).once();

    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.run();

    verifyAll();
  }
  
  @Test
  public void run_Conflict() throws Exception {
    HttpResponse response = createResponse(HttpServletResponse.SC_CONFLICT, "File exists");
    Status st = new Status(0, 0, 0);
    
    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();

    expect(httpClientUtilMock.post(request)).andReturn(response);
    
    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_CONFLICT);
    expectLastCall();
    
    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.run();

    verifyAll();
  }

  @Test
  public void run_RetrySuccess() throws Exception {
    Status st = new Status(0, 0, 0);

    HttpResponse response = createResponse(
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");

    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();

    expect(httpClientUtilMock.post(request)).andReturn(response).times(2);
    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    expectLastCall();
    
    expect(registryManagerMock.store(isA(RegistryEntry.class))).andReturn(1);

    expect(nodeStatusManagerMock.update(st, 1)).andReturn(1).once();

    expect(redirectHandler.canHandle(response)).andReturn(false);
    response = createResponse(HttpServletResponse.SC_OK, "OK");
    expect(httpClientUtilMock.post(request)).andReturn(response).once();

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_OK);
    expectLastCall();

    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.setRedirectHandler(redirectHandler);
    classUnderTest.run();

    verifyAll();
  }

  @Test
  public void run_RetryFailure() throws Exception {
    HttpResponse response = createResponse(
        HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
    Status st = new Status(0, 0, 0);
    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();

    expect(httpClientUtilMock.post(request)).andReturn(response).times(4);

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    
    expect(registryManagerMock.store(isA(RegistryEntry.class))).andReturn(1);

    expect(nodeStatusManagerMock.update(st, 1)).andReturn(1).once();

    expect(redirectHandler.canHandle(response)).andReturn(false);

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.setRedirectHandler(redirectHandler);
    classUnderTest.run();

    verifyAll();
  }

  @Test
  public void run_Redirect() throws Exception {
    Status st = new Status(0, 0, 0);
    HttpResponse response = createResponse(
        HttpServletResponse.SC_MOVED_PERMANENTLY, "Redirected");

    expect(subscriptionManagerMock.load(Subscription.PEER, receiver.getName(),
        dataSource.getName())).andReturn(s).once();

    expect(nodeStatusManagerMock.load(1)).andReturn(st).once();
    
    expect(httpClientUtilMock.post(request)).andReturn(response).times(1);
    
    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_MOVED_PERMANENTLY);
    
    expect(nodeStatusManagerMock.update(st, 1)).andReturn(1).once();

    expect(redirectHandler.canHandle(response)).andReturn(true);

    HttpResponse redirectResponse = createResponse(HttpServletResponse.SC_OK,"OK");
    expect(redirectHandler.handle(httpClientUtilMock, request, response)).andReturn(redirectResponse);

    expect(registryManagerMock.store(isA(RegistryEntry.class))).andReturn(1);

    nodeStatusManagerMock.setRuntimeNodeStatus(receiver.getName(), HttpServletResponse.SC_OK);

    httpClientUtilMock.shutdown();
    expectLastCall();

    replayAll();

    classUnderTest = new PFTask(registryManagerMock, subscriptionManagerMock,
        nodeStatusManagerMock, request, ENTRY_UUID, receiver, dataSource);
    classUnderTest.setHttpClient(httpClientUtilMock);
    classUnderTest.setRedirectHandler(redirectHandler);
    classUnderTest.run();

    verifyAll();
  }
}
