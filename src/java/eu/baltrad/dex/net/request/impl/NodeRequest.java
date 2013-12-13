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

package eu.baltrad.dex.net.request.impl;

import eu.baltrad.dex.net.request.INodeRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * Http servlet request wrapper.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.1.1
 */
public class NodeRequest extends HttpServletRequestWrapper 
                                                implements INodeRequest {
    /**
     * Constructor.
     * @param request Http servlet request 
     */
    public NodeRequest(HttpServletRequest request) {
        super(request);
    }
    
    /**
     * Retrieves message for authentication.
     * @return Message to be signed
     */
    public String getMessage() {
        List<String> result = new ArrayList<String>();
        result.add(getMethod());
        result.add(getRequestURL().toString());
        for (int i = 0; i < HEADERS.length; i++) {
            String headerValue = 
                ((String) getAttribute(HEADERS[i]) != null) ?
                (String) getAttribute(HEADERS[i]) :
                    getHeader(HEADERS[i]);
            if (headerValue != null) {
                headerValue = StringUtils.strip(headerValue);
                result.add(headerValue);
            }
        }
        return StringUtils.join(result, '\n');
    }
    
    /**
     * Extracts signature from request header. 
     * @return Signature string
     */
    public String getSignature() {
        String headerValue = 
            ((String) getAttribute(AUTH_HDR) != null) ?
            (String) getAttribute(AUTH_HDR) : 
                getHeader(AUTH_HDR);    
        String[] parts = headerValue.split(HDR_SEPARATOR);
        return parts[1];
    }
    
    /**
     * Extracts user name from request header. 
     * @return User identity string
     */
    public String getUser() {
        String headerValue = 
            ((String) getAttribute(AUTH_HDR) != null) ?
            (String) getAttribute(AUTH_HDR) : 
                getHeader(AUTH_HDR);    
        String[] parts = headerValue.split(HDR_SEPARATOR);
        return parts[0];
    }
    
    /**
     * Retrieves node name from request.
     * @return Name of the requesting node
     */
    public String getNodeName() {
        String headerValue = 
            ((String) getAttribute(NODE_NAME_HDR) != null) ?
            (String) getAttribute(NODE_NAME_HDR) : 
                getHeader(NODE_NAME_HDR);
        return headerValue;
    }
    
    /**
     * Retrieves provider name from request.
     * @return Name of the provider
     */
    public String getProvider() {
        String headerValue = 
            ((String) getAttribute(PROVIDER_HDR) != null) ?
            (String) getAttribute(PROVIDER_HDR) : 
                getHeader(PROVIDER_HDR);
        return headerValue;
    }
    
}
