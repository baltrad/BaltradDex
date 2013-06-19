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

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.net.controller.exception.InternalControllerException;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import org.apache.http.entity.StringEntity;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * Data source list controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.1.0
 */
public class DataSourceListControllerTest {
    
    private static final String JSON_SOURCES = 
            "[{\"name\":\"DS1\",\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"description\":" +
            "\"Yet another test data source\"}]";
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private List<Object> mocks;
    private UrlValidatorUtil urlValidator;
    private JsonUtil jsonUtil;
    private IJsonUtil jsonUtilMock;
    private MessageResourceUtil messages;
    private DSLController classUnderTest;
    private IConfigurationManager configManagerMock;
    private IUserManager  userManagerMock;
    private Authenticator authenticatorMock;
    private IHttpClientUtil httpClientMock;
    private Logger log;
    private DateFormat format;
    
    class DSLController extends DataSourceListController {
        public DSLController() {
            this.localNode = new User(1, "test", "s3cret", "org", "unit", 
                    "locality", "state", "XX", "user", "http://localhost:8084");
        }
        @Override
        protected void initConfiguration() {}
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
    public void setUp() throws Exception {
        mocks = new ArrayList<Object>();
        configManagerMock = (IConfigurationManager) 
                createMock(IConfigurationManager.class);
        userManagerMock = (IUserManager) createMock(IUserManager.class);
        authenticatorMock = (Authenticator) 
                createMock(Authenticator.class);
        httpClientMock = (IHttpClientUtil) createMock(IHttpClientUtil.class);
        jsonUtilMock = (IJsonUtil) createMock(IJsonUtil.class);
        classUnderTest = new DSLController();
        urlValidator = new UrlValidatorUtil();
        classUnderTest.setUrlValidator(urlValidator);
        jsonUtil = new JsonUtil(); 
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
        log = Logger.getLogger("DEX");
        classUnderTest.setLog(log);
        format = new SimpleDateFormat(DATE_FORMAT);
    }
    
    @After
    public void tearDown() throws Exception {
        classUnderTest = null;
        resetAll();
    }
    
    private HttpResponse createResponse(int code, String reason) 
            throws Exception {
        ProtocolVersion version = new ProtocolVersion("HTTP", 1, 1);
        StatusLine statusLine = new BasicStatusLine(version, code, reason);
        HttpResponse response = new BasicHttpResponse(statusLine);
        response.addHeader("Node-Name", "test.baltrad.eu");
        response.addHeader("Node-Address", "http://test.baltrad.eu");
        response.setEntity(new StringEntity(JSON_SOURCES));
        return response;
    } 
    
    @Test
    public void nodeConnect() {
        userManagerMock = (IUserManager) createMock(IUserManager.class);
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        replayAll();
        
        Model model = new ExtendedModelMap();
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.nodeConnect(model);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_InvalidURL() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        replayAll();
        
        Model model = new ExtendedModelMap();
        classUnderTest.setUserManager(userManagerMock);
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://invalid", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
                messages.getMessage("datasource.controller.invalid_node_url"), 
                    (String) model.asMap().get("error_message"));
    }
    
    @Test
    public void nodeConnected_SendKeyInternalControllerError() {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages
                .getMessage("datasource.controller.send_key_controller_error"), 
                    (String) model.asMap().get("error_message"));
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyInternalServerError() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        
        expect(configManagerMock.getAppConf()).andReturn(appConf);
        
        classUnderTest.localNode.setName("localhost");
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Internal server error");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        replayAll();
        
        classUnderTest.setConfigurationManager(configManagerMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages
                .getMessage("datasource.controller.send_key_server_error"), 
                    (String) model.asMap().get("error_message"));
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Internal server error", model.asMap()
                .get("error_details"));
    }
    
    @Test 
    public void nodeConnected_SendKeyUnauthorized() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        
        expect(configManagerMock.getAppConf()).andReturn(appConf);
        
        classUnderTest.localNode.setName("localhost");
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_UNAUTHORIZED, null);
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        replayAll();
        
        classUnderTest.setConfigurationManager(configManagerMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages
                .getMessage("datasource.controller.send_key_unauthorized"), 
                    (String) model.asMap().get("error_message"));
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyConflict() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        
        expect(configManagerMock.getAppConf()).andReturn(appConf);
        
        classUnderTest.localNode.setName("localhost");
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_CONFLICT, null);
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        replayAll();
        
        classUnderTest.setConfigurationManager(configManagerMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages
                .getMessage("datasource.controller.send_key_exists"), 
                    (String) model.asMap().get("error_message"));
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_SendKeyOK() throws Exception {
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        
        expect(configManagerMock.getAppConf()).andReturn(appConf);
        
        classUnderTest.localNode.setName("localhost");
        
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, null);
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        replayAll();
        
        classUnderTest.setConfigurationManager(configManagerMock);
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", null, "send_key");
        
        verifyAll();
        
        assertTrue(model.containsAttribute("success_message"));
        assertEquals(messages
                .getMessage("datasource.controller.send_key_server_msg"), 
                    (String) model.asMap().get("success_message"));
        assertEquals("node_connect", viewName);
    }
    
    @Test
    public void nodeConnected_AddCredentialsError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        expectLastCall().andThrow(new KeyczarException(
                "Failed to sign message"));
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage("datasource.controller.message_signer_error"), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to sign message", model.asMap()
                .get("error_details"));
    }
    
    @Test
    public void nodeConnected_MessageVerificationError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_UNAUTHORIZED, 
                "Failed to verify message");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage("datasource.controller.server_error"), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to verify message", model.asMap()
                .get("error_details"));
    }
    
    @Test
    public void nodeConnected_InternalServerError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Internal server error");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage("datasource.controller.server_error"), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Internal server error", model.asMap()
                .get("error_details"));
    }
    
    
    @Test
    public void nodeConnected_HttpConnectionError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new IOException("Http connection exception"));
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
       
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
            
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage("datasource.controller.http_connection_error",
                new String[] {"http://test.baltrad.eu"}), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Http connection exception", model.asMap()
                .get("error_details"));
    }
    
    @Test
    public void nodeConnected_GenericConnectionError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new Exception("Generic connection exception"));
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
       
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
            
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null, 
                "http://test.baltrad.eu","connect", null);
        
        verifyAll();
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage("datasource.controller.generic_connection_error",
                new String[] {"http://test.baltrad.eu"}), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Generic connection exception", model.asMap()
                .get("error_details"));
    }

    @Test
    public void nodeConnected_InternalControllerError() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        res.setEntity(null);
        
        expect(httpClientMock.post(isA(HttpUriRequest.class))).andReturn(res);
        
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null,
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
                messages.getMessage(
                    "datasource.controller.internal_controller_error", 
                    new String[] {"test.baltrad.eu"}), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Failed to read server response", model.asMap()
                .get("error_details"));
    }
    
    @Test
    public void nodeConnected_URLInput() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        Set<DataSource> sources = jsonUtil.jsonToDataSources(JSON_SOURCES);
        
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andReturn(sources);
        
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, null,
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connected", viewName);
        assertTrue(model.containsAttribute("data_sources"));
        Set<DataSource> dataSources = (HashSet<DataSource>) 
                model.asMap().get("data_sources");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
    }
    
    @Test
    public void nodeConnected_NodeSelect() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        Set<DataSource> sources = jsonUtil.jsonToDataSources(JSON_SOURCES);
        
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andReturn(sources);
        
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, "test.baltrad.eu",
                null, "connect", null);
        
        verifyAll();
        
        assertEquals("node_connected", viewName);
        assertTrue(model.containsAttribute("data_sources"));
        Set<DataSource> dataSources = (HashSet<DataSource>) 
                model.asMap().get("data_sources");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
    }
    
    @Test
    public void nodeConnected_URLInputAndNodeSelect() throws Exception {
        expect(userManagerMock.load("test.baltrad.eu"))
            .andReturn(new User(1, "test.baltrad.eu", "s3cret", "org", "unit", 
                "locality", "state", "XX", "user", 
                "http://test.baltrad.eu:8084")).anyTimes();
        
        List<String> peers = Arrays.asList(new String[] {"test.baltrad.eu", 
            "peer.baltrad.eu"});
        
        expect(userManagerMock.loadPeers()).andReturn(peers);
        
        authenticatorMock.addCredentials(isA(HttpUriRequest.class), 
                isA(String.class));
        
        Set<DataSource> sources = jsonUtil.jsonToDataSources(JSON_SOURCES);
        
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andReturn(sources);
        
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res);
        
        expectLastCall();
        replayAll();
        
        classUnderTest.setUserManager(userManagerMock);
        classUnderTest.setAuthenticator(authenticatorMock);
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.nodeConnected(model, "test.baltrad.eu",
                "http://test.baltrad.eu", "connect", null);
        
        verifyAll();
        
        assertEquals("node_connected", viewName);
        assertTrue(model.containsAttribute("data_sources"));
        Set<DataSource> dataSources = (HashSet<DataSource>) 
                model.asMap().get("data_sources");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
    }
    
}
