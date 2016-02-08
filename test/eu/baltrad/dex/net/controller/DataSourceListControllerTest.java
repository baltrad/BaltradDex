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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.controller.util.ModelMessageHelper;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.protocol.ResponseParserException;
import eu.baltrad.dex.net.util.UrlValidatorUtil;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.User;

/**
 * Data source list controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.1.0
 */
public class DataSourceListControllerTest extends EasyMockSupport {
    interface MethodMock {
      public byte[] createLocalPubKeyZip();
      public String nodeConnected_connect(Model model, String nodeSelect, String urlInput);
    }
    
    private UrlValidatorUtil urlValidator;
    private DSLController classUnderTest;
    private IConfigurationManager configManagerMock;
    private IUserManager  userManagerMock;
    private Authenticator authenticatorMock;
    private IHttpClientUtil httpClientMock;
    private ProtocolManager protocolManager;
    private ModelMessageHelper messageHelper;
    
    private Logger log;
    private MethodMock methods;
    
    class DSLController extends DataSourceListController {
        public DSLController() {
            this.localNode = new User(1, "test", "s3cret", "org", "unit", 
                    "locality", "state", "XX", "user", "http://localhost:8084");
            this.peerDataSources = new HashMap<String, DataSource>();
        }
        @Override
        protected void initConfiguration() {}
        
        @Override
        protected byte[] createLocalPubKeyZip() {
          return methods.createLocalPubKeyZip();
        }
    }
    
    @Before
    public void setUp() throws Exception {
        configManagerMock =  createMock(IConfigurationManager.class);
        userManagerMock = createMock(IUserManager.class);
        authenticatorMock = createMock(Authenticator.class);
        httpClientMock = createMock(IHttpClientUtil.class);
        urlValidator = new UrlValidatorUtil();
        log = Logger.getLogger("DEX");
        protocolManager = createMock(ProtocolManager.class);
        methods = createMock(MethodMock.class);
        messageHelper = createMock(ModelMessageHelper.class);
        
        classUnderTest = new DSLController();
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setUrlValidator(urlValidator);
        classUnderTest.setLog(log);
        classUnderTest.setProtocolManager(protocolManager);
        classUnderTest.setConfigurationManager(configManagerMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setModelMessageHelper(messageHelper);
    }
    
    @After
    public void tearDown() throws Exception {
        classUnderTest = null;
        resetAll();
    }
    
    @Test
    public void nodeConnect() {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", "peer.baltrad.eu"});
        
        Model model = createMock(Model.class);
        
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        replayAll();
        
        String viewName = classUnderTest.nodeConnect(model);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_InvalidURL() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", "peer.baltrad.eu"});
        
        Model model = createMock(Model.class);
        
        messageHelper.setErrorMessage(model, "datasource.controller.invalid_node_url");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.nodeConnected(model, null, "http://invalid", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyResponseParserException() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", "peer.baltrad.eu"});
        RequestFactory requestFactory = createMock(RequestFactory.class);
       
        Model model = createMock(Model.class);
        byte[] keyContent = new byte[0];
        HttpUriRequest request = createMock(HttpUriRequest.class);
        HttpResponse res = createMock(HttpResponse.class);
        
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andThrow(new ResponseParserException("Boogie"));
        
        messageHelper.setErrorDetailsMessage(model, "datasource.controller.send_key_controller_error", "Boogie");
        
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);

        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyInternalServerError() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", "peer.baltrad.eu"});
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        byte[] keyContent = new byte[]{}; 
        HttpUriRequest request = createMock(HttpUriRequest.class);
        
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.isRedirected()).andReturn(false);        
        expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).anyTimes();
        expect(responseParser.getReasonPhrase()).andReturn("Internal server error");
        messageHelper.setErrorDetailsMessage(model, "datasource.controller.send_key_server_error", "Internal server error");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        
        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();

        assertEquals("node_connect", viewName);
    }
    
    @Test 
    public void nodeConnected_SendKeyUnauthorized() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        byte[] keyContent = new byte[]{}; 
        HttpUriRequest request = createMock(HttpUriRequest.class);
      
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.isRedirected()).andReturn(false);        
        expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_UNAUTHORIZED).anyTimes();
        messageHelper.setErrorMessage(model, "datasource.controller.send_key_unauthorized");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        
        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyConflict() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        byte[] keyContent = new byte[]{}; 
        HttpUriRequest request = createMock(HttpUriRequest.class);
      
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.isRedirected()).andReturn(false);
        expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_CONFLICT).anyTimes();
        messageHelper.setErrorMessage(model, "datasource.controller.send_key_exists");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        
        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyOK() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        byte[] keyContent = new byte[]{}; 
        HttpUriRequest request = createMock(HttpUriRequest.class);
      
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.isRedirected()).andReturn(false);        
        expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();
        messageHelper.setSuccessMessage(model, "datasource.controller.send_key_server_msg");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        
        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void extractBaseUrlFromRedirect() {
    	String result = classUnderTest.extractBaseUrlFromRedirect("http://localhost:1234", "http://localhost:1234/BaltradDex/post.htm", "http://localhost-2:1234/BaltradDex/post.htm");
    	assertEquals("http://localhost-2:1234", result);
    	
      result = classUnderTest.extractBaseUrlFromRedirect("http://localhost:1234/", "http://localhost:1234/BaltradDex/post.htm", "http://localhost-2:1234/BaltradDex/post.htm");
      assertEquals("http://localhost-2:1234", result);

      result = classUnderTest.extractBaseUrlFromRedirect("http://localhost", "http://localhost/BaltradDex/post.htm", "http://localhost-2:1234/BaltradDex/post.htm");
      assertEquals("http://localhost-2:1234", result);
    }
    
    @Test
    public void nodeConnected_isRedirected() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        byte[] keyContent = new byte[]{}; 
        HttpUriRequest request = createMock(HttpUriRequest.class);
      
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.isRedirected()).andReturn(true);
        expect(request.getURI()).andReturn(new URI("http://localhost:9876/BaltradDex/post_key.htm"));
        expect(responseParser.getRedirectURL()).andReturn("http://localhost:1234/BaltradDex/post_key.htm");
        messageHelper.setErrorDetailsMessage(model, 
        	"datasource.controller.send_key_server_redirect", 
        	"",
          "http://localhost:1234");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        
        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_isRedirected_2() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        byte[] keyContent = new byte[]{}; 
        HttpUriRequest request = createMock(HttpUriRequest.class);
      
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(methods.createLocalPubKeyZip()).andReturn(keyContent);
        expect(requestFactory.createPostKeyRequest(classUnderTest.localNode, keyContent)).andReturn(request);
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.isRedirected()).andReturn(true);
        expect(request.getURI()).andReturn(new URI("http://localhost:9876/BaltradDex/post_key.htm"));
        expect(responseParser.getRedirectURL()).andReturn("http://localhost:1234/slurp/BaltradDex/post_key.htm");
        messageHelper.setErrorDetailsMessage(model, 
          "datasource.controller.send_key_server_redirect", 
          "",
          "http://localhost:1234/slurp");
        expect(userManagerMock.loadPeerNames()).andReturn(peers);
        expect(model.addAttribute("nodes", peers)).andReturn(null);
        
        replayAll();
        
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_connectString() {
      List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", "peer.baltrad.eu"});
      
      Model model = createMock(Model.class);
      
      expect(methods.nodeConnected_connect(model, "a", "http://test.baltrad.eu")).andReturn("nisse");
      expect(userManagerMock.loadPeerNames()).andReturn(peers);
      expect(model.addAttribute("nodes", peers)).andReturn(null);
      
      classUnderTest = new DSLController() {
        protected String nodeConnected_connect(Model model, String nodeSelect, String urlInput) {
          return methods.nodeConnected_connect(model, nodeSelect, urlInput);
        }
      };
      classUnderTest.setUserManager(userManagerMock);

      replayAll();

      String viewName = classUnderTest.nodeConnected(model, "a", 
          "http://test.baltrad.eu", "connect", null);
      
      verifyAll();
      assertEquals("nisse", viewName);
    }

    @Test
    public void nodeConnected_connect_KeyNotApproved() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);

      Model model = createMock(Model.class);
      HttpResponse res = createMock(HttpResponse.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(res);
      expect(protocolManager.createParser(res)).andReturn(responseParser);
      expect(responseParser.getProtocolVersion()).andReturn("2.0");
      expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.0");
      expect(responseParser.isRedirected()).andReturn(false);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_NOT_FOUND).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Not approved");
      messageHelper.setErrorDetailsMessage(model, "datasource.controller.key_not_approved", "Not approved");
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_connect_ConnectionUnauthorized() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);

      Model model = createMock(Model.class);
      HttpResponse res = createMock(HttpResponse.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(res);
      expect(protocolManager.createParser(res)).andReturn(responseParser);
      expect(responseParser.getProtocolVersion()).andReturn("2.0");
      expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.0");
      expect(responseParser.isRedirected()).andReturn(false);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_UNAUTHORIZED).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Not approved");
      messageHelper.setErrorDetailsMessage(model, "datasource.controller.connection_unauthorized", "Not approved");
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connect", viewName);

    }
    
    @Test
    public void nodeConnected_connect_InternalServerError() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);

      Model model = createMock(Model.class);
      HttpResponse res = createMock(HttpResponse.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(res);
      expect(protocolManager.createParser(res)).andReturn(responseParser);
      expect(responseParser.getProtocolVersion()).andReturn("2.0");
      expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.0");
      expect(responseParser.isRedirected()).andReturn(false);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).anyTimes();
      expect(responseParser.getReasonPhrase()).andReturn("Not approved");
      messageHelper.setErrorDetailsMessage(model, "datasource.controller.server_error", "Not approved");
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connect", viewName);

    }
    
    
    @Test
    public void nodeConnected_connect_HttpConnectionError() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);

      Model model = createMock(Model.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andThrow(new IOException("Http connection exception"));
      messageHelper.setErrorDetailsMessage(model, "datasource.controller.http_connection_error", "Http connection exception");
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_connect_GenericConnectionError() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);

      Model model = createMock(Model.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andThrow(new Exception("Http connection exception"));
      messageHelper.setErrorDetailsMessage(model, "datasource.controller.generic_connection_error", "Http connection exception", new Object[]{"http://test.baltrad.eu"});
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connect", viewName);
    }

    @Test
    public void nodeConnected_connect_InternalControllerError() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);
      
      Model model = createMock(Model.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      HttpResponse response = createMock(HttpResponse.class);
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(response);
      expect(protocolManager.createParser(response)).andThrow(new ResponseParserException("Boohoo"));
      
      messageHelper.setErrorDetailsMessage(model, "datasource.controller.internal_controller_error", "Boohoo", new Object[]{"http://test.baltrad.eu"});
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_connect_URLInput_2_0() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);

      Model model = createMock(Model.class);
      HttpResponse res = createMock(HttpResponse.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      Set<DataSource> dset = new HashSet<DataSource>();
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(res);
      expect(protocolManager.createParser(res)).andReturn(responseParser);
      expect(responseParser.getProtocolVersion()).andReturn("2.0").anyTimes();
      expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.0");
      expect(responseParser.isRedirected()).andReturn(false);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();

      expect(responseParser.getNodeName()).andReturn("MyName");
      expect(model.addAttribute("peer_name", "MyName")).andReturn(null);

      expect(responseParser.getDataSources()).andReturn(dset);
      expect(model.addAttribute("data_sources", dset)).andReturn(null);

      // protocol == 2.0 can not guarantee no ghost subscriptions
      messageHelper.setTextErrorMessage(model, "Peer node does not support protocol version > 2.0. Local node might not be able to prevent ghost subscriptions.");
      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connected", viewName);
    }

    @Test
    public void nodeConnected_connect_URLInput_2_1() throws Exception {
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);

      Model model = createMock(Model.class);
      HttpResponse res = createMock(HttpResponse.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      Set<DataSource> dset = new HashSet<DataSource>();
      
      expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(res);
      expect(protocolManager.createParser(res)).andReturn(responseParser);
      expect(responseParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.1");
      expect(responseParser.isRedirected()).andReturn(false);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();

      expect(responseParser.getNodeName()).andReturn("MyName");
      expect(model.addAttribute("peer_name", "MyName")).andReturn(null);

      expect(responseParser.getDataSources()).andReturn(dset);
      expect(model.addAttribute("data_sources", dset)).andReturn(null);

      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, null, "http://test.baltrad.eu");
      
      verifyAll();
      assertEquals("node_connected", viewName);
    }

    @Test
    public void nodeConnected_NodeSelect() throws Exception {
      User loadUser = new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
        "locality", "state", "XX", "user", 
        "http://test.baltrad.eu:8084");
      
      RequestFactory requestFactory = createMock(RequestFactory.class);
      ResponseParser responseParser = createMock(ResponseParser.class);

      Model model = createMock(Model.class);
      HttpResponse res = createMock(HttpResponse.class);
      HttpUriRequest request = createMock(HttpUriRequest.class);
      Set<DataSource> dset = new HashSet<DataSource>();
      
      expect(userManagerMock.load("test.baltrad.eu")).andReturn(loadUser);
      expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
      expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
      authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
      expect(httpClientMock.post(request)).andReturn(res);
      expect(protocolManager.createParser(res)).andReturn(responseParser);
      expect(responseParser.getProtocolVersion()).andReturn("2.1").anyTimes();
      expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.1");
      expect(responseParser.isRedirected()).andReturn(false);
      expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();

      expect(responseParser.getNodeName()).andReturn("MyName");
      expect(model.addAttribute("peer_name", "MyName")).andReturn(null);

      expect(responseParser.getDataSources()).andReturn(dset);
      expect(model.addAttribute("data_sources", dset)).andReturn(null);

      replayAll();
      
      String viewName = classUnderTest.nodeConnected_connect(model, "test.baltrad.eu", null);
      
      verifyAll();
      assertEquals("node_connected", viewName);
    }
    
    @Test
    public void nodeConnected_URLInputAndNodeSelect() throws Exception {
      User loadUser = new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
          "locality", "state", "XX", "user", 
          "http://test.baltrad.eu:8084");
        
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        HttpUriRequest request = createMock(HttpUriRequest.class);
        Set<DataSource> dset = new HashSet<DataSource>();
        
        expect(userManagerMock.load("test.baltrad.eu")).andReturn(loadUser);
        expect(protocolManager.getFactory("http://test.baltrad.eu")).andReturn(requestFactory);
        expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
        authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.getProtocolVersion()).andReturn("2.1").anyTimes();
        expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.1");
        expect(responseParser.isRedirected()).andReturn(false);
        expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();

        expect(responseParser.getNodeName()).andReturn("MyName");
        expect(model.addAttribute("peer_name", "MyName")).andReturn(null);

        expect(responseParser.getDataSources()).andReturn(dset);
        expect(model.addAttribute("data_sources", dset)).andReturn(null);

        replayAll();
        
        String viewName = classUnderTest.nodeConnected_connect(model, "test.baltrad.eu", "http://test.baltrad.eu");
        
        verifyAll();
        assertEquals("node_connected", viewName);      
    }

    @Test
    public void nodeConnected_URLInputAndNodeSelect_invalidUrlInput() throws Exception {
      User loadUser = new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
          "locality", "state", "XX", "user", 
          "http://test.baltrad.eu:8084");
        
        RequestFactory requestFactory = createMock(RequestFactory.class);
        ResponseParser responseParser = createMock(ResponseParser.class);

        Model model = createMock(Model.class);
        HttpResponse res = createMock(HttpResponse.class);
        HttpUriRequest request = createMock(HttpUriRequest.class);
        Set<DataSource> dset = new HashSet<DataSource>();
        
        expect(userManagerMock.load("test.baltrad.eu")).andReturn(loadUser);
        expect(protocolManager.getFactory("http://test.baltrad.eu:8084")).andReturn(requestFactory);
        expect(requestFactory.createDataSourceListingRequest(classUnderTest.localNode)).andReturn(request);
        authenticatorMock.addCredentials(request, classUnderTest.localNode.getName());
        expect(httpClientMock.post(request)).andReturn(res);
        expect(protocolManager.createParser(res)).andReturn(responseParser);
        expect(responseParser.getProtocolVersion()).andReturn("2.1").anyTimes();
        expect(responseParser.getConfiguredProtocolVersion()).andReturn("2.1");
        expect(responseParser.isRedirected()).andReturn(false);
        expect(responseParser.getStatusCode()).andReturn(HttpServletResponse.SC_OK).anyTimes();

        expect(responseParser.getNodeName()).andReturn("MyName");
        expect(model.addAttribute("peer_name", "MyName")).andReturn(null);

        expect(responseParser.getDataSources()).andReturn(dset);
        expect(model.addAttribute("data_sources", dset)).andReturn(null);

        replayAll();
        
        String viewName = classUnderTest.nodeConnected_connect(model, "test.baltrad.eu", "htp://test.baltrad.eu");
        
        verifyAll();
        assertEquals("node_connected", viewName);      
    }

}
