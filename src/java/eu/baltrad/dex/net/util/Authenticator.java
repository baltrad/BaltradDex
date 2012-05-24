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

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author szewczenko
 */
public interface Authenticator {
    
    /**
     * Add credentials to outgoing request.
     * @param request Http URI request to add credentials to
     */
    public void addCredentials(HttpUriRequest request);
    
    /**
     * Add credentials to outgoing request.
     * @param request Http servlet equest to add credentials to
     */
    public void addCredentials(HttpServletRequest request);
    
    /**
     * Authenticates request at servlet side.
     * @param message Message to authenticate
     * @param signature Signature to authenticate with
     * @return True upon successful authentication, false otherwise
     */
    public boolean authenticate(String message, String signture);
    
    /**
     * Creates authentication input.  
     * @param request Http URI request
     * @return Message to be authenticated
     */
    public String getMessage(HttpUriRequest request);
    
    /**
     * Creates authentication input.  
     * @param request Http servlet request
     * @return Message to be authenticated
     */
    public String getMessage(HttpServletRequest request);
    
    /**
     * Retrieves signature from request.
     * @param request Http URI request
     * @return Signature string
     */
    public String getSignature(HttpUriRequest request);
    
    /**
     * Retrieves signature from request.
     * @param request Http servlet request
     * @return Signature string
     */
    public String getSignature(HttpServletRequest request);
    
    /**
     * Retrieves user name from request.
     * @param request Http URI request
     * @return User identity string
     */
    public String getUser(HttpUriRequest request);
    
    /**
     * Retrieves user name from request.
     * @param request Http servlet request
     * @return User identity string
     */
    public String getUser(HttpServletRequest request);
    
    /**
     * Retrieves node name from request.
     * @param request Http URI request
     * @return Name of the requesting node
     */
    public String getNodeName(HttpUriRequest request);
    
    /**
     * Retrieves node name from request.
     * @param request Http servlet request
     * @return Name of the requesting node
     */
    public String getNodeName(HttpServletRequest request);
    
    /**
     * Retrieves node address from request.
     * @param request Http URI request
     * @return Address of the requesting node
     */
    public String getNodeAddress(HttpUriRequest request);
    
    /**
     * Retrieves node address from request.
     * @param request Http servlet request
     * @return Address of the requesting node
     */
    public String getNodeAddress(HttpServletRequest request);
    
}
