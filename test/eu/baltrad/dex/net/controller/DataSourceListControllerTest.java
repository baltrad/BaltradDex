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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.util.*;
import eu.baltrad.dex.net.model.*;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.log.model.MessageLogger;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import org.apache.http.StatusLine;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.Set;
import java.util.HashSet;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

/**
 * Data source list controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListControllerTest {
    
    private static final String JSON_SOURCES = 
            "[{\"name\":\"DS1\",\"id\":1,\"description\":\"A test "
            + "data source\"},{\"name\":\"DS2\",\"id\":2,\"description\":\"One "
            + "more test data source\"},{\"name\":\"DS3\",\"id\":3,\"" 
            + "description\":\"Yet another test data source\"}]";
    private UrlValidatorUtil urlValidator;
    private JsonUtil jsonUtil;
    private IJsonUtil jsonUtilMock;
    private MessageResourceUtil messages;
    private DataSourceListController classUnderTest;
    private INodeConnectionManager connectionManagerMock;
    private IHttpClientUtil httpClientMock;
    private Logger log;
    
    @Before
    public void setUp() throws Exception {
        classUnderTest = new DataSourceListController("test.baltrad.eu", 
                "http://test.baltrad.eu");
        classUnderTest.setAuthenticator(new EasyAuthenticator());
        urlValidator = new UrlValidatorUtil();
        classUnderTest.setUrlValidator(urlValidator);
        jsonUtil = new JsonUtil(); 
        messages = new MessageResourceUtil("resources/messages");
        classUnderTest.setMessages(messages);
        log = MessageLogger.getLogger(MessageLogger.SYS_DEX);
        classUnderTest.setLog(log);
        
        connectionManagerMock = createMock(INodeConnectionManager.class);
        expect(connectionManagerMock.get("test.baltrad.eu"))
            .andReturn(new NodeConnection("test.baltrad.eu", 
                "http://test.baltrad.eu")).anyTimes();
        expect(connectionManagerMock.get()).andReturn(null).anyTimes();
        replay(connectionManagerMock);
        classUnderTest.setNodeConnectionManager(connectionManagerMock);
        jsonUtilMock = createMock(IJsonUtil.class);
        httpClientMock = createMock(IHttpClientUtil.class);
    }
    
    @After
    public void tearDown() throws Exception {
        classUnderTest = null;
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
    public void dsConnect() {
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnect(model);
        assertEquals("dsconnect", viewName);
    }
    
    @Test
    public void dsConnected_InvalidURL() throws Exception {
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model, null, 
                "http://invalid");
        assertEquals("dsconnect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
                messages.getMessage("datasource.controller.invalid_node_url"), 
                    (String) model.asMap().get("error_message"));
    }
    
    @Test
    public void dsConnected_InternalServerError() throws Exception {
        HttpResponse res = createResponse(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Internal server error");
        
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res).anyTimes();
        replay(httpClientMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model, null, 
                "http://test.baltrad.eu");
        
        verify(httpClientMock);
        assertEquals("dsconnect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage(
                "datasource.controller.internal_server_error"), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Internal server error", model.asMap()
                .get("error_details"));
        reset(httpClientMock);
    }
    
    @Test
    public void dsConnected_HttpConnectionError() throws Exception {
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
            .andThrow(new IOException("Http connection exception")).anyTimes();
        replay(httpClientMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model, null, 
                "http://test.baltrad.eu");
        
        verify(httpClientMock);
        assertEquals("dsconnect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
            messages.getMessage("datasource.controller.http_connection_error",
                new String[] {"http://test.baltrad.eu"}), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Http connection exception", model.asMap()
                .get("error_details"));
        reset(httpClientMock);
    }
    
    @Test
    public void dsConnected_GenericConnectionError() throws Exception {
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andThrow(new Exception("Generic connection exception"))
                .anyTimes();
        replay(httpClientMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model, null, 
                "http://test.baltrad.eu");
        
        verify(httpClientMock);
        assertEquals("dsconnect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(messages.getMessage(
                "datasource.controller.generic_connection_error",
                new String[] {"http://test.baltrad.eu"}), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Generic connection exception", model.asMap()
                .get("error_details"));
        reset(httpClientMock);
    }

    @Test
    public void dsConnected_InternalControllerError() throws Exception {
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res).anyTimes();
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andThrow(new RuntimeException("Data source read error"))
                .anyTimes();
        replay(httpClientMock);
        replay(jsonUtilMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model, null,
                "http://test.baltrad.eu");
        
        verify(httpClientMock);
        verify(jsonUtilMock);
        assertEquals("dsconnect", viewName);
        assertTrue(model.containsAttribute("error_message"));
        assertEquals(
                messages.getMessage(
                    "datasource.controller.internal_controller_error", 
                    new String[] {"test.baltrad.eu"}), 
            model.asMap().get("error_message"));
        assertTrue(model.containsAttribute("error_details"));
        assertEquals("Data source read error", model.asMap()
                .get("error_details"));
        reset(httpClientMock);
        reset(jsonUtilMock);
    }
    
    @Test
    public void dsConnected_URLInput() throws Exception {
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        Set<DataSource> sources = jsonUtil.jsonToDataSources(JSON_SOURCES);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res).anyTimes();
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andReturn(sources).anyTimes();
        replay(httpClientMock);
        replay(jsonUtilMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model, null,
                "http://test.baltrad.eu");
        
        verify(httpClientMock);
        verify(jsonUtilMock);
        assertEquals("dsconnected", viewName);
        assertTrue(model.containsAttribute("data_sources"));
        Set<DataSource> dataSources = (HashSet<DataSource>) 
                model.asMap().get("data_sources");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
        reset(httpClientMock);
        reset(jsonUtilMock);
    }
    
    @Test
    public void dsConnected_NodeSelect() throws Exception {
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        Set<DataSource> sources = jsonUtil.jsonToDataSources(JSON_SOURCES);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res).anyTimes();
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andReturn(sources).anyTimes();
        replay(httpClientMock);
        replay(jsonUtilMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model,
                "test.baltrad.eu", null);
        
        verify(httpClientMock);
        verify(jsonUtilMock);
        verify(connectionManagerMock);
        assertEquals("dsconnected", viewName);
        assertTrue(model.containsAttribute("data_sources"));
        Set<DataSource> dataSources = (HashSet<DataSource>) 
                model.asMap().get("data_sources");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
        reset(httpClientMock);
        reset(jsonUtilMock);
        reset(connectionManagerMock);
    }
    
    @Test
    public void dsConnected_URLInputAndNodeSelect() throws Exception {
        HttpResponse res = createResponse(HttpServletResponse.SC_OK, "OK");
        Set<DataSource> sources = jsonUtil.jsonToDataSources(JSON_SOURCES);
        expect(httpClientMock.post(isA(HttpUriRequest.class)))
                .andReturn(res).anyTimes();
        expect(jsonUtilMock.jsonToDataSources(JSON_SOURCES))
                .andReturn(sources).anyTimes();
        replay(httpClientMock);
        replay(jsonUtilMock);
        
        classUnderTest.setHttpClient(httpClientMock);
        classUnderTest.setJsonUtil(jsonUtilMock);
        Model model = new ExtendedModelMap();
        String viewName = classUnderTest.dsConnected(model,
                "test.baltrad.eu", "http://test.baltrad.eu");
        
        verify(httpClientMock);
        verify(jsonUtilMock);
        verify(connectionManagerMock);
        assertEquals("dsconnected", viewName);
        assertTrue(model.containsAttribute("data_sources"));
        Set<DataSource> dataSources = (HashSet<DataSource>) 
                model.asMap().get("data_sources");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size());
        reset(httpClientMock);
        reset(jsonUtilMock);
        reset(connectionManagerMock); 
    }
    
}
