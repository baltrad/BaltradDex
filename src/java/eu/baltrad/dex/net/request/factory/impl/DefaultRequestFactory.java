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

package eu.baltrad.dex.net.request.factory.impl;

import eu.baltrad.dex.net.request.factory.RequestFactory;
import eu.baltrad.dex.net.util.UrlValidatorUtil;
import eu.baltrad.dex.net.util.json.IJsonUtil;
import eu.baltrad.dex.net.util.json.impl.JsonUtil;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.datasource.model.DataSource;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ByteArrayEntity;

import org.apache.commons.io.IOUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.io.InputStream;
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
    /** JSON format conversion utility */
    private IJsonUtil jsonUtil;
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
        this.jsonUtil = new JsonUtil();
        if (urlValidator.validate(serverUri.toString())) {
            this.serverUri = serverUri;
        } else {
            throw new IllegalArgumentException("Invalid server URI: " 
                    + serverUri);
        }
        // Set universal locale to eliminate non-ascii characters
        this.dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
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
     * Creates data source listing request.
     * @param user Requesting user's account
     * @return Http POST request
     * @throws RuntimeException
     */
    public HttpPost createDataSourceListingRequest(User user) throws 
            RuntimeException {
        try {
            HttpPost httpPost = new HttpPost(
                getRequestUri("datasource_listing.htm"));
            String json = jsonUtil.userAccountToJson(user);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
            httpPost.addHeader("Node-Name", user.getName());
            httpPost.addHeader("Content-Type", "application/json");  
            httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                json.getBytes()));
            httpPost.addHeader("Date", dateFormat.format(new Date()));
            return httpPost;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create data source listing " +
                    "request", e);
        }
    }
    
    /**
     * Creates start subscription request.
     * @param user Requesting user's account
     * @param dataSources List of requested data sources  
     * @return Http POST request
     * @throws RuntimeException
     */
    public HttpPost createStartSubscriptionRequest(User user, 
            Set<DataSource> dataSources) throws RuntimeException {
        try {
            HttpPost httpPost = new HttpPost(
                getRequestUri("start_subscription.htm"));
            String json = jsonUtil.dataSourcesToJson(dataSources);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
            httpPost.addHeader("Node-Name", user.getName());
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                json.getBytes()));
            httpPost.addHeader("Date", dateFormat.format(new Date()));
            return httpPost;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create start subscription " +
                    "request", e);
        }
    }
    
    /**
     * Creates update subscription request.
     * @param user Requesting user's account
     * @param subscriptions List of requested subscriptions
     * @return Http POST request
     * @throws RuntimeException
     */
    public HttpPost createUpdateSubscriptionRequest(User user, 
            List<Subscription> subscriptions) throws RuntimeException {
        try {
            HttpPost httpPost = new HttpPost(
                getRequestUri("update_subscription.htm"));
            String json = jsonUtil.subscriptionsToJson(subscriptions);
            httpPost.setEntity(new StringEntity(json, "UTF-8"));
            httpPost.addHeader("Node-Name", user.getName());
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                json.getBytes()));
            httpPost.addHeader("Date", dateFormat.format(new Date()));
            return httpPost;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create update subscription " +
                    "request", e);
        }
    }
    
    /**
     * Creates post data file request.
     * @param user Requesting user's account
     * @param fileContent File content as byte array
     * @return Http POST request
     * @throws RuntimeException
     */
    public HttpPost createPostFileRequest(User user, byte[] fileContent) 
            throws RuntimeException {
        try {
            HttpPost httpPost = new HttpPost(getRequestUri("post_file.htm"));
            httpPost.setEntity(new ByteArrayEntity(fileContent));
            httpPost.addHeader("Node-Name", user.getName());
            httpPost.addHeader("Content-Type", "application/x-hdf5");
            httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                httpPost.getURI().toString().getBytes()));
            httpPost.addHeader("Date", dateFormat.format(new Date()));
            return httpPost;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create post file request", e);
        }
    }
    
    /**
     * Creates post message request.
     * @param user Requesting user's account
     * @param message Message to post
     * @return Http POST request 
     * @throws RuntimeException
     */
    public HttpPost createPostMessageRequest(User user, String message) 
            throws RuntimeException {
        try {
            HttpPost httpPost = new HttpPost(getRequestUri("post_message.htm"));
            httpPost.setEntity(new StringEntity(message, "UTF-8"));
            httpPost.addHeader("Node-Name", user.getName());
            httpPost.addHeader("Content-Type", "text/html");
            httpPost.addHeader("Content-MD5", Base64.encodeBase64String(
                message.getBytes()));
            httpPost.addHeader("Date", dateFormat.format(new Date()));
            return httpPost;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create post message request", 
                    e);
        }
    }
    
    /**
     * Creates post public key request.
     * @param user Requesting user's account
     * @param keyContent Key content as byte array
     * @return HTTP POST request
     * @throws RuntimeException
     */
    public HttpPost createPostKeyRequest(User user, byte[] keyContent) 
            throws RuntimeException {
        try {
            HttpPost httpPost = new HttpPost(getRequestUri("post_key.htm"));
            httpPost.setEntity(new ByteArrayEntity(keyContent));
            httpPost.addHeader("Node-Name", user.getName());
            httpPost.addHeader("Content-Type", "application/zip");
            httpPost.addHeader("Content-MD5", DigestUtils.md5Hex(keyContent));
            httpPost.addHeader("Date", dateFormat.format(new Date()));
            return httpPost;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create post key request", e);
        }
    } 
    
}
