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

import eu.baltrad.dex.net.model.NodeRequest;
import eu.baltrad.dex.auth.util.Signer;
import eu.baltrad.dex.auth.util.KeyczarCryptoFactory;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.Header;

import org.apache.commons.codec.binary.Base64;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
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
    
    private KeyczarCryptoFactory cryptoFactory;
    private RequestFactory requestFactory;
    private KeyczarAuthenticator classUnderTest;
    private DateFormat format;
    
    @Before
    public void setUp() {
        cryptoFactory = new KeyczarCryptoFactory(new File("./keystore"));
        requestFactory = new DefaultRequestFactory(
                URI.create("http://example.com"));
        classUnderTest = new KeyczarAuthenticator("./keystore");
        assertNotNull(classUnderTest);
        format = new SimpleDateFormat(DATE_FORMAT);
    }
    
    private void setAttributes(HttpServletRequest request) {
        request.setAttribute("Content-Type", "text/html");  
        request.setAttribute("Content-MD5", Base64.encodeBase64String(
                request.getRequestURI().getBytes()));
        request.setAttribute("Date", format.format(new Date()));
    }
    
    @Test
    public void addCredentials() {
        HttpUriRequest request = requestFactory.createGetSubscriptionRequest(
            "localnode", "http://localhost",
                "json string will be passed here");
        classUnderTest.addCredentials(request, "localhost");
        Header header = request.getFirstHeader("Authorization");
        assertNotNull(header);
        assertNotNull(header.getValue());
    }
    
    @Test 
    public void authenticate_Success() {
        HttpServletRequest request = new MockHttpServletRequest("GET", 
                "/get_datasource_listing.htm");
        setAttributes(request);
        NodeRequest req = new NodeRequest(request);
        Signer signer = cryptoFactory.createSigner("localhost");
        String signature = signer.sign(req.getMessage());
        req.setAttribute("Authorization", "localhost" + ":" + signature);
        assertTrue(classUnderTest.authenticate(req.getMessage(), 
                req.getSignature(), "localhost"));
    }
    
    @Test
    public void authenticate_Failure() {
        HttpServletRequest request = new MockHttpServletRequest("GET", 
                "/get_datasource_listing.htm");
        setAttributes(request);
        NodeRequest req = new NodeRequest(request);
        Signer signer = cryptoFactory.createSigner("localhost");
        String signature = signer.sign(req.getMessage());
        req.setAttribute("Authorization", "localhost" + ":" + signature);
        request.setAttribute("Date", "");
        assertFalse(classUnderTest.authenticate(req.getMessage(), 
                req.getSignature(), "localhost"));
    }
    
}
