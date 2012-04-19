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
import eu.baltrad.dex.util.MessageResourceUtil;

import org.mortbay.jetty.testing.ServletTester;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;

/**
 *
 * @author szewczenko
 */
public class PostFileControllerTest {
    
    private ServletTester tester;
    private String context;
    private PostFileController classUnderTest;
    private Authenticator authenticator;
    private UrlValidatorUtil urlValidator;
    private HttpClientUtil httpClient;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @Before
    public void setUp() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(PostFileServlet.class, 
                "/PostFileServlet/postfile.htm");
        context = tester.createSocketConnector(true);
        tester.start();
        
        classUnderTest = new PostFileController();
        urlValidator = new UrlValidatorUtil();
        classUnderTest.setUrlValidator(urlValidator);
        authenticator = new KeyczarAuthenticator("./keystore", 
                "dev.baltrad.eu");
        classUnderTest.setAuthenticator(authenticator); 
        httpClient = new HttpClientUtil(60000, 60000);
        classUnderTest.setHttpClient(httpClient);
        classUnderTest.setMessages(new MessageResourceUtil(
                "resources/messages"));
        
        request = new MockHttpServletRequest("POST", "/postfile.htm");
        response = new MockHttpServletResponse();
    }
    
    @After
    public void tearDown() throws Exception {
        tester.stop();
        tester = null;
    }
    
    @Test
    public void handleRequest_InvalidURL() throws Exception {
        request.addParameter("target_node_url", "http://invalid");
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertNotNull(modelAndView);
        assertTrue(modelAndView.getModel().containsKey("node.url.invalid"));
    }
    
    @Test
    public void handleRequest_Unauthorized() throws Exception {
        request.addParameter("target_node_url", context + 
                "/PostFileServlet");
        classUnderTest.setAuthenticator(new KeyczarAuthenticator(
                "./fake_keystore", "dev.baltrad.eu"));
        classUnderTest.setFileContent(new ByteArrayInputStream(
                "testfilecontent".getBytes()));
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertNotNull(modelAndView);
        assertTrue(modelAndView.getModel().containsKey(
                "postfile.server.error"));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());  
    }
    
    @Test
    public void handleRequest_NullContent() throws Exception {
        request.addParameter("target_node_url", context + 
                "/PostFileServlet");
        classUnderTest.setFileContent(null);
        ModelAndView modelAndView = classUnderTest.handleRequest(request,
                response);
        assertNotNull(modelAndView);
        assertTrue(modelAndView.getModel().containsKey(
                "postfile.nullcontent.error"));
    }
    
    @Test
    public void handleRequest_OK() throws Exception {
        request.addParameter("target_node_url", context + 
                "/PostFileServlet");
        classUnderTest.setFileContent(new ByteArrayInputStream(
                "testfilecontent".getBytes()));
        ModelAndView modelAndView = classUnderTest.handleRequest(request,
                response);
        assertNotNull(modelAndView);
        assertTrue(modelAndView.getModel().containsKey("postfile.success.msg"));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());        
    }
    
}
