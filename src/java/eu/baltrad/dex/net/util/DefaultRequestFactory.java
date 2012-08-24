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

import eu.baltrad.dex.net.util.UrlValidatorUtil;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ByteArrayEntity;

import org.apache.commons.io.IOUtils;
import org.apache.commons.codec.binary.Base64;

import java.net.URI;
import java.util.Date;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Implements default request factory.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DefaultRequestFactory implements RequestFactory {
    
    //private final static String[] SCHEMES = {"http", "https"};
    private final static String DEFAULT_BASE_PATH = "BaltradDex";
    private final static String SCHEME_SEPARATOR = "://";
    private final static String PORT_SEPARATOR = ":";
    private final static String PATH_SEPARATOR = "/";
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    /** URL validator utility */
    private UrlValidatorUtil urlValidator;
    /** Server URI */ 
    private URI serverUri;
    /** Common date format */
    private SimpleDateFormat dateFormat;
        
    /**
     * Constructor. 
     * @param serverUri Server URI 
     */
    public DefaultRequestFactory(URI serverUri)
    {
        this.urlValidator = new UrlValidatorUtil();
        if (urlValidator.validate(serverUri.toString())) {
            this.serverUri = serverUri;
        } else {
            throw new IllegalArgumentException("Invalid server URI: " 
                    + serverUri);
        }
        this.dateFormat = new SimpleDateFormat(DATE_FORMAT);
    }
    
    /**
     * Validates port number.
     * @param port Port number
     * @return True if port is equal or greater than zero
     */
    protected boolean validatePort(int port) {
        if (port > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Validates resource path.
     * @param path Resource path
     * @return True if a given path is a valid resource path, false otherwise
     */
    protected boolean validatePath(String path) {
        if (path != null) {
            if (!path.trim().isEmpty() && !path.trim().equals("/")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes leading and trailing slashes from resource path.
     * @param path Resource path
     * @return Resource path without leading and trailing slashes 
     */
    private String removeSlashes(String path) {
        while (path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
    
    /**
     * Constructs request URI by appending resource path to server URI.
     * @param path Resource path
     * @return Request URI
     */
    protected URI getRequestUri(String path) {
        URI requestUri = null;
        try {
            requestUri = URI.create(
                serverUri.getScheme() + SCHEME_SEPARATOR
                + serverUri.getHost() 
                + (validatePort(serverUri.getPort()) ? PORT_SEPARATOR 
                    + Integer.toString(serverUri.getPort()) : "") 
                + (validatePath(serverUri.getPath()) ? 
                    PATH_SEPARATOR + removeSlashes(serverUri.getPath()) 
                    : PATH_SEPARATOR + DEFAULT_BASE_PATH) + PATH_SEPARATOR 
                    + removeSlashes(path));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid request URI: " +
                    requestUri);
        }
        return requestUri;
    }
    
    /**
     * Creates get data source listing request.
     * @param nodeName Name of the node making a request
     * @param nodeAddress Address of the node making a request
     * @return Http GET request 
     */
    public HttpGet createGetDataSourceListingRequest(String nodeName, 
            String nodeAddress) {
        HttpGet httpGet = new HttpGet(
                getRequestUri("get_datasource_listing.htm"));
        httpGet.addHeader("Node-Name", nodeName);
        httpGet.addHeader("Node-Address", nodeAddress);
        httpGet.addHeader("Content-Type", "text/html");  
        httpGet.addHeader("Content-MD5", Base64.encodeBase64String(
                httpGet.getURI().toString().getBytes()));
        httpGet.addHeader("Date", dateFormat.format(new Date()));
        return httpGet;
    }
    
    /**
     * Creates get subscription request.
     * @param nodeName Name of the node making a request
     * @param nodeAddress Address of the node making a request
     * @param jsonSubscriptions String representation of subscriptions
     * @return Http POST request
     */
    public HttpPost createGetSubscriptionRequest(String nodeName, 
            String nodeAddress, String jsonSubscriptions) {
        HttpPost httpPost = new HttpPost(getRequestUri("get_subscription.htm"));
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonSubscriptions, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpPost.setEntity(entity);
        httpPost.addHeader("Node-Name", nodeName);
        httpPost.addHeader("Node-Address", nodeAddress);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                jsonSubscriptions.getBytes()));
        httpPost.addHeader("Date", dateFormat.format(new Date()));
        return httpPost;
    }
    
    /**
     * Creates post subscription request.
     * @param nodeName Name of the node making a request
     * @param nodeAddress Address of the node making a request
     * @param jsonSources String representation of data sources 
     * @return Http POST request 
     */
    public HttpPost createPostSubscriptionRequest(String nodeName, 
            String nodeAddress, String jsonSources) {
        HttpPost httpPost = new HttpPost(getRequestUri("post_subscription.htm"));
        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonSources, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpPost.setEntity(entity);
        httpPost.addHeader("Node-Name", nodeName);
        httpPost.addHeader("Node-Address", nodeAddress);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                jsonSources.getBytes()));
        httpPost.addHeader("Date", dateFormat.format(new Date()));
        return httpPost;
    }
    
    /**
     * Creates post data file request.
     * @param nodeName Name of the node making a request
     * @param nodeAddress Address of the node making a request
     * @param fileContent File content as stream
     * @return Http POST request 
     */
    public HttpPost createPostFileRequest(String nodeName, 
            String nodeAddress, InputStream fileContent) {
        HttpPost httpPost = new HttpPost(getRequestUri("post_file.htm"));
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(IOUtils.toByteArray(fileContent));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpPost.setEntity(entity);
        httpPost.addHeader("Node-Name", nodeName);
        httpPost.addHeader("Node-Address", nodeAddress);
        httpPost.addHeader("Content-Type", "application/x-hdf5");
        httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                httpPost.getURI().toString().getBytes()));
        httpPost.addHeader("Date", dateFormat.format(new Date()));
        return httpPost;
    }
    
    /**
     * Creates post message request.
     * @param nodeName Name of the node making a request
     * @param nodeAddress Address of the node making a request
     * @param message Message to post
     * @return Http POST request 
     */
    public HttpPost createPostMessageRequest(String nodeName, 
            String nodeAddress, String message) {
        HttpPost httpPost = new HttpPost(getRequestUri("post_message.htm"));
        StringEntity entity = null;
        try {
            entity = new StringEntity(message, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpPost.setEntity(entity);
        httpPost.addHeader("Node-Name", nodeName);
        httpPost.addHeader("Node-Address", nodeAddress);
        httpPost.addHeader("Content-Type", "text/html");
        httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                message.getBytes()));
        httpPost.addHeader("Date", dateFormat.format(new Date()));
        return httpPost;
    }
}
