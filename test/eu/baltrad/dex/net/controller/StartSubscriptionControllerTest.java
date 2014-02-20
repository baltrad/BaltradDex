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

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.beast.db.IFilterManager;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.protocol.ResponseParserException;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.manager.ISubscriptionManager;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.easymock.EasyMockSupport;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * Post subscription request controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.1.0
 */
public class StartSubscriptionControllerTest extends EasyMockSupport {    
    interface MethodMock {
      void storeLocalSubscriptions(String nodeName, Set<DataSource> dataSources, String peerName) 
          throws InternalControllerException;
      boolean createDataSourceFilter(int id, DataSource ds);
      Subscription createSubscriptionObject(String type, String nodeName, String dsName, boolean active, boolean sync);
    };
    
    private PSController classUnderTest;
    private IHttpClientUtil httpClientMock;
    private IUserManager userManagerMock;
    private Authenticator authenticatorMock;
    private ISubscriptionManager subscriptionManagerMock;
    private IDataSourceManager dataSourceManagerMock;
    private INodeStatusManager nodeStatusManagerMock;
    private IFilterManager filterManagerMock;
    private ProtocolManager protocolManager;
    private ModelMessageHelper messageHelper;
    private Logger log;
    private MethodMock methods;
    private MockHttpServletRequest request;
    private Set<DataSource> selectedDataSources;
    
    class PSController extends StartSubscriptionController {
        
        public PSController() {
            this.localNode = new User(1, "test", "user", "s3cret", "org", 
                    "unit", "locality", "state", "XX", "http://localhost:8084");
        }
        @Override
        public void initConfiguration() {}
        
        @Override
        protected void storeLocalSubscriptions(String nodeName, Set<DataSource> dataSources, String peerName) 
            throws InternalControllerException {
          methods.storeLocalSubscriptions(nodeName, dataSources, peerName);
        }
        
    }
    
    @Before
    public void setUp() throws Exception {
        log = Logger.getLogger("DEX");
        httpClientMock = createMock(IHttpClientUtil.class);
        userManagerMock = createMock(IUserManager.class);
        subscriptionManagerMock = createMock(ISubscriptionManager.class);
        authenticatorMock = createMock(Authenticator.class);
        dataSourceManagerMock = createMock(IDataSourceManager.class);
        nodeStatusManagerMock = createMock(INodeStatusManager.class);
        filterManagerMock = (IFilterManager) createMock(IFilterManager.class);
        protocolManager = createMock(ProtocolManager.class);
        messageHelper = createMock(ModelMessageHelper.class);
        methods = createMock(MethodMock.class);
        
        request = new MockHttpServletRequest();
        selectedDataSources = setRequestAttributes(request);

        classUnderTest = new PSController();
        classUnderTest.setLog(log);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setSubscriptionManager(subscriptionManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setDataSourceManager(dataSourceManagerMock);
        classUnderTest.setFilterManager(filterManagerMock);
        classUnderTest.setProtocolManager(protocolManager);
        classUnderTest.setMessageHelper(messageHelper);
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
    }
    
    @After
    public void tearDown() throws Exception {
        classUnderTest = null;
    }
    
    private Set<DataSource> setRequestAttributes(MockHttpServletRequest request) {
        Set<DataSource> selectedDataSources = new HashSet<DataSource>();
        selectedDataSources.add(new DataSource(1, "DataSource1", 
                DataSource.PEER, "A test data source", "12374", "SCAN"));
        selectedDataSources.add(new DataSource(2, "DataSource2", 
                DataSource.PEER, "Another test data source", "12331", "SCAN"));
        selectedDataSources.add(new DataSource(3, "DataSource3", 
                DataSource.PEER, "Yet one more test data source", "12374,12331", 
                "PVOL,SCAN"));
        request.setAttribute("selected_data_sources", selectedDataSources);
        request.getSession().setAttribute("selected_data_sources", selectedDataSources);
        return selectedDataSources;
    }
    
    @Test
    public void storeLocalSubscriptions() throws Exception {
      User usr1 = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      Set<DataSource> dataSources = new HashSet<DataSource>();
      DataSource ds1 = createMock(DataSource.class);
      DataSource ds2 = createMock(DataSource.class);
      DataSource eds2 = createMock(DataSource.class);
      Status statusObject = createMock(Status.class);
      
      Subscription subscription1 = createMock(Subscription.class);
      Subscription subscription2 = createMock(Subscription.class);
      Subscription existing2 = createMock(Subscription.class);
      
      dataSources.add(ds1);
      dataSources.add(ds2);
      
      expect(ds1.getName()).andReturn("DS1").anyTimes();
      expect(dataSourceManagerMock.load("DS1", "peer")).andReturn(null);
      expect(userManagerMock.load("PeerName")).andReturn(usr1);
      expect(dataSourceManagerMock.store(ds1)).andReturn(3);
      expect(dataSourceManagerMock.storeUser(3, 1)).andReturn(1);
      expect(methods.createDataSourceFilter(3, ds1)).andReturn(true);
      expect(methods.createSubscriptionObject("local", "NodeName", "DS1", true, true)).andReturn(subscription1);
      expect(subscriptionManagerMock.load("local", "PeerName", "DS1")).andReturn(null);
      expect(subscriptionManagerMock.store(subscription1)).andReturn(1);
      expect(nodeStatusManagerMock.store(isA(Status.class))).andReturn(2);
      expect(nodeStatusManagerMock.store(2, 1)).andReturn(1);

      expect(ds2.getName()).andReturn("DS2").anyTimes();
      expect(dataSourceManagerMock.load("DS2", "peer")).andReturn(eds2);
      expect(userManagerMock.load("PeerName")).andReturn(usr1);
      expect(eds2.getId()).andReturn(3);
      ds2.setId(3);
      expect(dataSourceManagerMock.update(ds2)).andReturn(1);
      expect(methods.createSubscriptionObject("local", "NodeName", "DS2", true, true)).andReturn(subscription2);
      expect(subscriptionManagerMock.load("local", "PeerName", "DS2")).andReturn(existing2);
      expect(existing2.getId()).andReturn(4);
      subscription2.setId(4);
      expect(subscription2.getId()).andReturn(4);
      subscriptionManagerMock.update(subscription2);
      expect(nodeStatusManagerMock.load(4)).andReturn(statusObject);
      statusObject.setDownloads(0);
      expect(nodeStatusManagerMock.update(statusObject, 4)).andReturn(1);
      replayAll();
      
      StartSubscriptionController classUnderTest = new StartSubscriptionController() {
        @Override
        public void initConfiguration() {}
        @Override
        protected boolean createDataSourceFilter(int id, DataSource ds) {
          return methods.createDataSourceFilter(id, ds);
        }
        @Override 
        protected Subscription createSubscriptionObject(String type, String nodeName, String dsName, boolean active, boolean sync) {
          return methods.createSubscriptionObject(type, nodeName, dsName, active, sync);
        }
      };
      classUnderTest.setLog(log);
      classUnderTest.setHttpClient(httpClientMock);
      classUnderTest.setUserManager(userManagerMock);
      classUnderTest.setSubscriptionManager(subscriptionManagerMock);
      classUnderTest.setAuthenticator(authenticatorMock);
      classUnderTest.setDataSourceManager(dataSourceManagerMock);
      classUnderTest.setFilterManager(filterManagerMock);
      classUnderTest.setProtocolManager(protocolManager);
      classUnderTest.setMessageHelper(messageHelper);
      classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
      
      classUnderTest.storeLocalSubscriptions("NodeName", dataSources, "PeerName");
      
      verifyAll();
    }
    
    @Test
    public void createDataSourceFilter() {
      DataSource ds = createMock(DataSource.class);
      IFilter filter = createMock(IFilter.class);
      
      expect(ds.getSource()).andReturn("DS1");
      expect(ds.getFileObject()).andReturn("FO1");
      expect(dataSourceManagerMock.createFilter("DS1", "FO1")).andReturn(filter);
      filterManagerMock.store(filter);
      expect(filter.getId()).andReturn(4);
      expect(dataSourceManagerMock.storeFilter(3, 4)).andReturn(1);
      replayAll();
      
      classUnderTest.createDataSourceFilter(3, ds);
      
      verifyAll();
    }

    @Test
    public void createDataSourceFilter_noSource() {
      DataSource ds = createMock(DataSource.class);
      
      expect(ds.getSource()).andReturn(null);
      expect(ds.getFileObject()).andReturn("FO1");

      replayAll();
      
      classUnderTest.createDataSourceFilter(3, ds);
      
      verifyAll();
    }

    @Test
    public void createDataSourceFilter_noFileObject() {
      DataSource ds = createMock(DataSource.class);
      
      expect(ds.getSource()).andReturn("DS1");
      expect(ds.getFileObject()).andReturn(null);

      replayAll();
      
      classUnderTest.createDataSourceFilter(3, ds);
      
      verifyAll();
    }
    
    @Test
    public void startSubscription() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      Set<DataSource> dset = new HashSet<DataSource>();
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();
      messageHelper.setSuccessMessage(model, "postsubscription.controller.subscription_server_success", "test.baltrad.eu");
      expect(responseParser.getNodeName()).andReturn("anode");
      expect(responseParser.getDataSources()).andReturn(dset);
      methods.storeLocalSubscriptions("anode", dset, "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }

    @Test
    public void startSubscription_AddCredentialsError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expectLastCall().andThrow(new KeyczarException("Failed to sign message"));
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.message_signer_error", "Failed to sign message");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }

    @Test
    public void startSubscription_MessageVerificationError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_UNAUTHORIZED).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Not authorized");
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.subscription_server_error", "Not authorized", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }

    @Test
    public void startSubscription_PeerNotFoundError() throws Exception {
      Model model = createMock(Model.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(null);
      messageHelper.setErrorMessage(model, "postsubscription.controller.peer_not_found_error", "test.baltrad.eu");
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }
 
    @Test
    public void startSubscription_HttpConnectionError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andThrow(new IOException("Failure when posting message"));
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.http_connection_error", "Failure when posting message", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
     
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }
    
    @Test
    public void startSubscription_GenericConnectionError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andThrow(new Exception("Generic error"));
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.generic_connection_error", "Generic error", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }

    @Test
    public void startSubscription_InternalServerError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Internal error");
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.subscription_server_error", "Internal error", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }
    
    @Test
    public void startSubscription_ResponseParserException() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andThrow(new ResponseParserException("Failed to read data"));
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.generic_connection_error", "Failed to read data", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }
    
    @Test
    public void startSubscription_InternalControllerError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      Set<DataSource> dset = new HashSet<DataSource>();
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();
      messageHelper.setSuccessMessage(model, "postsubscription.controller.subscription_server_success", "test.baltrad.eu");
      expect(responseParser.getNodeName()).andReturn("anode");
      expect(responseParser.getDataSources()).andReturn(dset);
      methods.storeLocalSubscriptions("anode", dset, "test.baltrad.eu");
      expectLastCall().andThrow(new InternalControllerException("Internal error"));
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.internal_controller_error", "Internal error", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }
    
    @Test
    public void startSubscription_SubscriptionFailedError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_NOT_FOUND).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Not found");
      messageHelper.setErrorDetailsMessage(model, "postsubscription.controller.subscription_server_error", "Not found", "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }

    @Test
    public void startSubscription_PartialSubscriptionError() throws Exception {
      User usr = new User(1, "test.baltrad.eu", "user", "s3cret", "org", 
          "unit", "locality", "state", "XX", "http://test.baltrad.eu:8084");
      
      Model model = createMock(Model.class);
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);
      HttpUriRequest req = createMock(HttpUriRequest.class);
      HttpResponse resp = createMock(HttpResponse.class);
      Set<DataSource> dset = new HashSet<DataSource>();
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(usr);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createStartSubscriptionRequest(classUnderTest.localNode, selectedDataSources)).andReturn(req);
      authenticatorMock.addCredentials(req, classUnderTest.localNode.getName());
      expect(httpClientMock.post(req)).andReturn(resp);
      expect(protocolManager.createParser(resp)).andReturn(responseParser);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_PARTIAL_CONTENT).anyTimes();
      messageHelper.setErrorMessage(model, "postsubscription.controller.subscription_server_partial", "test.baltrad.eu");
      expect(responseParser.getNodeName()).andReturn("anode");
      expect(responseParser.getDataSources()).andReturn(dset);
      methods.storeLocalSubscriptions("anode", dset, "test.baltrad.eu");
      expect(model.addAttribute("peer_name", "test.baltrad.eu")).andReturn(null);
      
      replayAll();
      
      String viewName = classUnderTest.startSubscription(request, model, "test.baltrad.eu");
      
      verifyAll();
      assertEquals("subscription_start_status", viewName);
    }
}
