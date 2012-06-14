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

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.io.InputStream;

/**
 * Posts file on the server.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class PostFileController implements Controller {
    
    /** URL of the target node */
    private static final String TARGET_NODE_URL = "target_node_url";

    /** Message keys */
    private static final String INVALID_URL_MSG = "node.url.invalid";
    private static final String POSTFILE_SERVER_ERROR_MSG = 
            "postfile.server.error";
    private static final String POSTFILE_NULLCONTENT_ERROR_MSG = 
            "postfile.nullcontent.error";
    private static final String POSTFILE_SUCCESS_MSG = "postfile.success.msg";
    
    
    private UrlValidatorUtil urlValidator;
    private MessageResourceUtil messages;
    private Authenticator authenticator;
    private RequestFactory requestFactory;
    private HttpClientUtil httpClient;
    
    private InputStream fileContent;
    
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
        
        ModelAndView modelAndView = new ModelAndView();
        // validate target node URL
        String url = request.getParameter(TARGET_NODE_URL);
        if (!urlValidator.validate(url)) {
            modelAndView.addObject(INVALID_URL_MSG, getMessages().getMessage(
                    INVALID_URL_MSG));
        } else {
            requestFactory = new DefaultRequestFactory(URI.create(url));
            try {
                HttpUriRequest req = requestFactory.createPostFileRequest(
                    getFileContent());
                try {
                    //getAuthenticator().addCredentials(req);
                    HttpResponse res = httpClient.post(req);
                    response.setStatus(res.getStatusLine().getStatusCode());
                    if (res.getStatusLine().getStatusCode() == 
                            HttpServletResponse.SC_OK) {
                        modelAndView.addObject(POSTFILE_SUCCESS_MSG, 
                            messages.getMessage(POSTFILE_SUCCESS_MSG));
                    } else {
                        modelAndView.addObject(POSTFILE_SERVER_ERROR_MSG, 
                            messages.getMessage(POSTFILE_SERVER_ERROR_MSG));
                    }
                } catch (Exception e) {
                    modelAndView.addObject(POSTFILE_SERVER_ERROR_MSG, 
                        messages.getMessage(POSTFILE_SERVER_ERROR_MSG));
                }
            } catch (Exception e) {
                modelAndView.addObject(POSTFILE_NULLCONTENT_ERROR_MSG, 
                        messages.getMessage(POSTFILE_NULLCONTENT_ERROR_MSG));
            }
        }
        return modelAndView;
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
     * @return the fileContent
     */
    public InputStream getFileContent() {
        return fileContent;
    }

    /**
     * @param fileContent the fileContent to set
     */
    public void setFileContent(InputStream fileContent) {
        this.fileContent = fileContent;
    }
    
}
