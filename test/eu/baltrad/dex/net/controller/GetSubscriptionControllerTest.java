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
import eu.baltrad.dex.datasource.model.DataSource;

import org.mortbay.jetty.testing.ServletTester;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import java.util.Set;
import java.util.HashSet;

/**
 * Get subscription controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class GetSubscriptionControllerTest {
    
    private ServletTester tester;
    private String context;
    private GetSubscriptionController classUnderTest;
    private UrlValidatorUtil urlValidator;
    private Authenticator authenticator;
    private JsonUtil jsonUtil;
    private HttpClientUtil httpClient;
    
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    
    @Before
    public void setUp() throws Exception {
        tester = new ServletTester();
        tester.setContextPath("/");
        tester.addServlet(GetSubscriptionServlet.class, 
                "/GetSubscriptionServlet/getsubscription.htm");
        context = tester.createSocketConnector(true);
        tester.start();
        
        classUnderTest = new GetSubscriptionController();
        urlValidator = new UrlValidatorUtil();
        classUnderTest.setUrlValidator(urlValidator);
        authenticator = new KeyczarAuthenticator("./keystore", 
                "dev.baltrad.eu");
        classUnderTest.setAuthenticator(authenticator); 
        httpClient = new HttpClientUtil(60000, 60000);
        classUnderTest.setHttpClient(httpClient);
        jsonUtil = new JsonUtil();
        classUnderTest.setJsonUtil(jsonUtil);
        classUnderTest.setMessages(new MessageResourceUtil(
                "resources/messages"));
        
        request = new MockHttpServletRequest("POST", "/postsubscription.htm");
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
        assertEquals("getsubscription", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().containsKey("node.url.invalid"));
    }
    
    @Test
    public void handleRequest_Unauthorized() throws Exception {
        request.addParameter("target_node_url", context + 
                "/GetSubscriptionServlet");
        classUnderTest.setAuthenticator(new KeyczarAuthenticator(
                "./fake_keystore", "dev.baltrad.eu"));
        ModelAndView modelAndView = classUnderTest.handleRequest(request, 
                response);
        assertEquals("getsubscription", modelAndView.getViewName());
        assertTrue(modelAndView.getModel().containsKey(
                "subscription.server.error"));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());  
    }
    
    @Test
    public void handleRequest_OK() throws Exception {
        request.addParameter("target_node_url", context + 
                "/GetSubscriptionServlet");
        ModelAndView modelAndView = classUnderTest.handleRequest(request,
                response);
        // validate model and view
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("showsubscription", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel());
        assertTrue(modelAndView.getModel().containsKey("subscriptions_key"));
        Set<DataSource> subscriptions = (HashSet<DataSource>) 
                modelAndView.getModel().get("subscriptions_key");
        assertNotNull(subscriptions);
        assertEquals(3, subscriptions.size()); 
    }
   
    
}
