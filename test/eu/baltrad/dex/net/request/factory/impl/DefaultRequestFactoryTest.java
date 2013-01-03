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

package eu.baltrad.dex.net.request.factory.impl;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.user.model.Account;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Request factory test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DefaultRequestFactoryTest {
    
    private DefaultRequestFactory classUnderTest;
    private Account account;
    
    @Before
    public void setUp() {
        classUnderTest = new DefaultRequestFactory(
                URI.create("http://example.com/")
        );
        account = new Account(1, "localnode", "s3cret", "org", "unit", "locality", 
                "state", "XX", "user", "http://localhost");
    }
    
    private String getHeader(HttpUriRequest request, String name) {
        Header header = request.getFirstHeader(name);
        if (header != null) {
            return header.getValue();
        } else {
            return null;
        }
    }
    
    private HttpEntity getEntity(HttpUriRequest request) {
        HttpEntityEnclosingRequest entityRequest = null;
        try {
            entityRequest = (HttpEntityEnclosingRequest) request;
        } catch (ClassCastException e) {
            return null;
        }
        return entityRequest.getEntity();
    }
    
    @Test
    public void createDataSourceListingRequest() {
        HttpUriRequest request = classUnderTest
                .createDataSourceListingRequest(account);
        assertEquals("POST", request.getMethod());
        assertEquals(URI.create(
            "http://example.com/BaltradDex/datasource_listing.htm"), 
            request.getURI());
        assertEquals("localnode", getHeader(request, "Node-Name"));
        assertEquals("application/json", getHeader(request, "Content-Type"));
        assertNotNull(getHeader(request, "Content-MD5"));
        assertNotNull(getHeader(request, "Date"));
    }
    
    @Test
    public void createStartSubscriptionRequest() {
        HttpUriRequest request = classUnderTest
                .createStartSubscriptionRequest(account, 
                    new HashSet<DataSource>());
        assertEquals("POST", request.getMethod());
        assertEquals(URI.create(
            "http://example.com/BaltradDex/start_subscription.htm"), 
            request.getURI());
        assertEquals("localnode", getHeader(request, "Node-Name"));
        assertEquals("application/json", getHeader(request, "Content-Type"));
        assertNotNull(getHeader(request, "Content-MD5"));
        assertNotNull(getHeader(request, "Date"));
    }
    
    @Test
    public void createUpdateSubscriptionRequest() {
        HttpUriRequest request = classUnderTest
                .createUpdateSubscriptionRequest(account, 
                    new ArrayList<Subscription>());
        assertEquals("POST", request.getMethod());
        assertEquals(URI.create(
            "http://example.com/BaltradDex/update_subscription.htm"), 
            request.getURI());
        assertEquals("localnode", getHeader(request, "Node-Name"));
        assertEquals("application/json", getHeader(request, "Content-Type"));
        assertNotNull(getHeader(request, "Content-MD5"));
        assertNotNull(getHeader(request, "Date"));
    }
    
    @Test
    public void createPostFileRequest() {
        InputStream is = new ByteArrayInputStream("datafilecontent".getBytes());
        HttpUriRequest request = classUnderTest
                .createPostFileRequest(account, is);
        assertEquals("POST", request.getMethod());
        assertEquals(URI.create(
            "http://example.com/BaltradDex/post_file.htm"), request.getURI());
        assertEquals("localnode", getHeader(request, "Node-Name"));
        assertEquals("application/x-hdf5", getHeader(request, "Content-Type"));
        assertNotNull(getHeader(request, "Content-MD5"));
        assertNotNull(getHeader(request, "Date"));
    }
    
    @Test
    public void createPostMessageRequest() {
        HttpUriRequest request = classUnderTest
                .createPostMessageRequest(account, "Hello world!");
        assertEquals("POST", request.getMethod());
        assertEquals(URI.create(
            "http://example.com/BaltradDex/post_message.htm"), 
            request.getURI());
        assertEquals("localnode", getHeader(request, "Node-Name"));
        assertEquals("text/html", getHeader(request, "Content-Type"));
        assertNotNull(getHeader(request, "Content-MD5"));
        assertNotNull(getHeader(request, "Date"));
    }
    
    
    @Test
    public void getRequestUri_ServerWithoutSlash() {
        classUnderTest = new DefaultRequestFactory(
            URI.create("http://example.com:8084")
        );
        assertEquals(
            URI.create("http://example.com:8084/BaltradDex/resource.htm"),
            classUnderTest.getRequestUri("resource.htm"));   
    }
    
    @Test
    public void getRequestUri_ServerWithSlash() {
        classUnderTest = new DefaultRequestFactory(
            URI.create("http://example.com:8084/")
        );
        assertEquals(
            URI.create("http://example.com:8084/BaltradDex/resource.htm"),
            classUnderTest.getRequestUri("resource.htm"));
    }
    
    @Test
    public void getRequestUri_ServerWithContext() {
        classUnderTest = new DefaultRequestFactory(
            URI.create("http://example.com:8084/BaltradDex")
        );
        assertEquals(
            URI.create("http://example.com:8084/BaltradDex/resource.htm"),
            classUnderTest.getRequestUri("resource.htm"));   
    }
    
    @Test
    public void getRequestUri_ServerWithContextAndSlash() {
        classUnderTest = new DefaultRequestFactory(
            URI.create("http://example.com:8084/BaltradDex/")
        );
        assertEquals(
            URI.create("http://example.com:8084/BaltradDex/resource.htm"),
            classUnderTest.getRequestUri("resource.htm"));
    }
    
    @Test
    public void getRequestUri_MultipleSlashes() {
        classUnderTest = new DefaultRequestFactory(
            URI.create("http://example.com:8084/BaltradDex/")
        );
        assertEquals(
            URI.create("http://example.com:8084/BaltradDex/resource.htm"),
            classUnderTest.getRequestUri("//resource.htm//"));
    }
    
    @Test
    public void getRequestUri_ServerWithContextAndPath() {
        classUnderTest = new DefaultRequestFactory(
            URI.create("http://example.com:8084/BaltradDex/")
        );
        assertEquals(
            URI.create("http://example.com:8084/BaltradDex/res/resource.htm"),
            classUnderTest.getRequestUri("res/resource.htm"));   
    }
    
}
