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
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.util.MessageResourceUtil;

import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.testing.ServletTester;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.util.HashSet;

/**
 * Data source list controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListControllerTest {

    private ServletTester tester;
    private String context;
    private DataSourceListController classUnderTest;
    private Authenticator keyczarAuthenticator;
    private Authenticator easyAuthenticator;
    private UrlValidatorUtil urlValidator;
    private HttpClientUtil httpClient;
    private JsonUtil jsonUtil;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    
    @Before
    public void setUp() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/");
        
        tester.addServlet (DataSourceListServlet.class, 
                "/DataSourceListServlet/getdatasourcelisting.htm");
        context = tester.createSocketConnector(true);
        tester.start();
        
        keyczarAuthenticator = new KeyczarAuthenticator("./keystore", 
                "dev.baltrad.eu");
        easyAuthenticator = new EasyAuthenticator();
        
        classUnderTest = new DataSourceListController();
        urlValidator = new UrlValidatorUtil();
        classUnderTest.setUrlValidator(urlValidator);
        httpClient = new HttpClientUtil(60000, 60000);
        classUnderTest.setHttpClient(httpClient);
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        classUnderTest.setMessages(new MessageResourceUtil(
                "resources/messages"));
        
        request = new MockHttpServletRequest("GET", 
                "/getdatasourcelisting.htm");
        response = new MockHttpServletResponse();
    }
    
    @After
    public void tearDown() throws Exception {
        tester.stop();
        tester = null;
    }
    
    @Test
    public void handleRequest_WrongAddress() throws Exception {
        request.addParameter("url_input", "http://invalid");
        classUnderTest.setAuthenticator(keyczarAuthenticator);
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertEquals("connect", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().containsKey("node.url.invalid"));
    }
    
    @Test
    public void handleRequest_NewAddress() throws Exception {
        request.addParameter("url_input", context + "/DataSourceListServlet");
        classUnderTest.setAuthenticator(keyczarAuthenticator);
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertEquals("datasources", modelAndView.getViewName());
    }
    
    @Test
    public void handleRequest_ExistingAddress() throws Exception {
        request.addParameter("url_select", context + "/DataSourceListServlet");
        classUnderTest.setAuthenticator(keyczarAuthenticator);
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertEquals("datasources", modelAndView.getViewName());
    }
    
    @Test
    public void handleRequest_BothAddresses() throws Exception {
        request.addParameter("url_input", context + "/DataSourceListServlet");
        request.addParameter("url_select", context + "/DataSourceListServlet");
        classUnderTest.setAuthenticator(keyczarAuthenticator);
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertEquals("datasources", modelAndView.getViewName());
    }
    
    @Test
    public void handleRequest_ServiceUnavailable() throws Exception {
        request.addParameter("url_input", context + "/DataSourceListServlet");
        classUnderTest.setAuthenticator(keyczarAuthenticator);
        tester.stop();
        ModelAndView modelAndView = classUnderTest.handleRequest(request,
                response);
        assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE, 
                response.getStatus());
        assertEquals("connect", modelAndView.getViewName());
        tester.start();
    }
    
    @Test
    public void handleRequest_InternalServerError() throws Exception {
        request.addParameter("url_input", context + "/DataSourceListServlet");
        classUnderTest.setAuthenticator(easyAuthenticator);
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                response.getStatus());
        assertEquals("connect", modelAndView.getViewName());
    }
    
    @Test
    public void handleRequest_OK() throws Exception {
        request.addParameter("url_input", context + "/DataSourceListServlet");
        classUnderTest.setAuthenticator(keyczarAuthenticator);
        ModelAndView modelAndView = classUnderTest.handleRequest(request,
                response);
        // validate model and view
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("datasources", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel());
        assertTrue(modelAndView.getModel().containsKey("data_sources_key"));
        HashSet<DataSource> dataSources = (HashSet<DataSource>) 
                modelAndView.getModel().get("data_sources_key");
        assertNotNull(dataSources);
        assertEquals(3, dataSources.size()); 
    }
    
}
