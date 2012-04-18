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

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Controls access to data sources available for subscription.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListController implements Controller {
    
    /** Current view */
    private static final String CONNECT_VIEW = "connect";
    /** Data sources view rendered upon successful connection */
    private static final String DATA_SOURCES_VIEW = "datasources";
    /** Data sources object used to render the view */
    private static final String DATA_SOURCES_KEY = "data_sources_key";
    /** URL selected from the drop-down list */
    private static final String URL_SELECT_PARM = "url_select";
    /** URL typed in the input box */
    private static final String URL_INPUT_PARM = "url_input";
    
    /** Message keys */
    private static final String INVALID_URL_MSG = "node.url.invalid";
    private static final String DS_READ_ERROR_MSG = "datasource.read.error";
    private static final String DS_SERVER_ERROR_MSG = "datasource.server.error";
    
    private Authenticator authenticator;
    private UrlValidatorUtil urlValidator;
    private RequestFactory requestFactory;
    private HttpClientUtil httpClient;
    private JsonUtil jsonUtil;
    private MessageResourceUtil messages;
            
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        ModelAndView modelAndView = new ModelAndView();
        // Validate URL 
        String url = urlValidator.validate(
            request.getParameter(URL_INPUT_PARM)) ? 
                request.getParameter(URL_INPUT_PARM) : 
                request.getParameter(URL_SELECT_PARM);
        if (!urlValidator.validate(url)) {
            modelAndView.addObject(INVALID_URL_MSG, messages.getMessage(
                    INVALID_URL_MSG));
            modelAndView.setViewName(CONNECT_VIEW);
        } else {
            // Post request
            requestFactory = new DefaultRequestFactory(URI.create(url));
            HttpUriRequest req = requestFactory
                    .createGetDataSourceListingRequest();
            this.authenticator.addCredentials(req);
            try {
                HttpResponse res = httpClient.post(req);
                response.setStatus(res.getStatusLine().getStatusCode());
                if(res.getStatusLine().getStatusCode() == 
                        HttpServletResponse.SC_OK) {
                    String jsonSources = "";
                    try {
                        StringWriter writer = new StringWriter();
                        InputStream is = null;
                        try {
                            is = res.getEntity().getContent();
                            IOUtils.copy(is, writer);
                            jsonSources = writer.toString();
                        } finally {
                            writer.close();
                            is.close();
                        }
                        Set<DataSource> sources = jsonUtil
                                .jsonStringToDataSources(jsonSources);
                        modelAndView.setViewName(DATA_SOURCES_VIEW);
                        modelAndView.addObject(DATA_SOURCES_KEY, sources);
                    } catch (IOException e) {
                        modelAndView.setViewName(CONNECT_VIEW);
                        modelAndView.addObject(DS_READ_ERROR_MSG, 
                                messages.getMessage(DS_READ_ERROR_MSG));
                    }
                } else {
                    modelAndView.setViewName(CONNECT_VIEW);
                    modelAndView.addObject(DS_SERVER_ERROR_MSG, 
                                messages.getMessage(DS_SERVER_ERROR_MSG));
                }   
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                modelAndView.setViewName(CONNECT_VIEW);
            }
        }
        return modelAndView;
    }

    /**
     * @return the authenticator
     */
    public Authenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * @param authenticator the authenticator to set
     */
    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * @return the urlValidator
     */
    public UrlValidatorUtil getUrlValidator() {
        return urlValidator;
    }

    /**
     * @param urlValidator the urlValidator to set
     */
    public void setUrlValidator(UrlValidatorUtil urlValidator) {
        this.urlValidator = urlValidator;
    }

    /**
     * @return the requestFactory
     */
    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * @param requestFactory the requestFactory to set
     */
    public void setRequestFactory(RequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    /**
     * @return the httpClient
     */
    public HttpClientUtil getHttpClient() {
        return httpClient;
    }

    /**
     * @param httpClient the httpClient to set
     */
    public void setHttpClient(HttpClientUtil httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * @return the jsonUtil
     */
    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    /**
     * @param jsonUtil the jsonUtil to set
     */
    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    /**
     * @return the messages
     */
    public MessageResourceUtil getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}
