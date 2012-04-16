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

package eu.baltrad.dex.net.util;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.Header;

import org.apache.commons.codec.binary.Base64;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Keyczar authenticator test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class KeyczarAuthenticatorTest {
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private RequestFactory requestFactory;
    private KeyczarAuthenticator classUnderTest;
    private DateFormat format;
    
    @Before
    public void setUp() {
        requestFactory = new DefaultRequestFactory(
                URI.create("http://example.com"));
        classUnderTest = new KeyczarAuthenticator("./keystore", 
                "dev.baltrad.eu");
        assertNotNull(classUnderTest);
        format = new SimpleDateFormat(DATE_FORMAT);
    }
    
    @Test
    public void getMessage() {
        HttpUriRequest request = requestFactory
                .createGetDataSourceListingRequest();
        String message = classUnderTest.getMessage(request);
        assertNotNull(message);
        assertTrue(message.length() > 0);
    }
    
    @Test
    public void sign() {
        HttpUriRequest request = requestFactory.createGetSubscriptionRequest();
        String signature = classUnderTest.sign(request);
        assertNotNull(signature);
        assertTrue(signature.length() > 0);   
    }
    
    @Test
    public void addCredentials() {
        HttpUriRequest request = requestFactory.createGetSubscriptionRequest();
        classUnderTest.addCredentials(request);
        Header header = request.getFirstHeader("Authorization");
        assertNotNull(header);
        assertNotNull(header.getValue());
    }
    
    @Test
    public void getSignature() {
        HttpServletRequest request = new MockHttpServletRequest("GET", 
                "/getdatasourcelisting.htm");
        setAttributes(request);
        classUnderTest.addCredentials(request);
        String signature = classUnderTest.getSignature(request);
        assertNotNull(signature);
        assertTrue(signature.length() > 0);
    }
    
    @Test
    public void getUser() {
        HttpServletRequest request = new MockHttpServletRequest("GET", 
                "/getdatasourcelisting.htm");
        setAttributes(request);
        classUnderTest.addCredentials(request);
        String userName = classUnderTest.getUser(request);
        assertNotNull(userName);
        assertTrue(userName.length() > 0);
    }
    
    @Test 
    public void authenticate_Success() {
        HttpServletRequest request = new MockHttpServletRequest("GET", 
                "/getdatasourcelisting.htm");
        setAttributes(request);
        classUnderTest.addCredentials(request);
        assertTrue(classUnderTest.authenticate(classUnderTest.getMessage(
                request), classUnderTest.getSignature(request)));
    }
    
    @Test
    public void authenticate_Failure() {
        HttpServletRequest request = new MockHttpServletRequest("GET", 
                "/getdatasourcelisting.htm");
        setAttributes(request);
        classUnderTest.addCredentials(request);
        request.setAttribute("Date", "");
        assertFalse(classUnderTest.authenticate(classUnderTest.getMessage(
                request), classUnderTest.getSignature(request)));
    }
    
    private void setAttributes(HttpServletRequest request) {
        request.setAttribute("Content-Type", "text/html");
        request.setAttribute("Content-MD5", Base64.encodeBase64String(
                request.getRequestURI().getBytes()));
        request.setAttribute("Date", format.format(new Date()));
    }
    
}
