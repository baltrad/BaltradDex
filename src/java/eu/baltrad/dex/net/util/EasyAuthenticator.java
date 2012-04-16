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
 * Implements easy authenticator.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class EasyAuthenticator implements Authenticator {
    
    /**
     * Method stub.
     * @param request Http URI request 
     */
    public void addCredentials(HttpUriRequest request) {
        
    }
    
    /**
     * Method stub.
     * @param request Http servlet request 
     */
    public void addCredentials(HttpServletRequest request) {
        
    }
    
    /**
     * Lets all the requests through.
     * @param message Message to authenticate
     * @param signature Signature to use for message verification
     * @return True upon successful authentication, false otherwise
     */ 
    public boolean authenticate(String message, String signature) {
        return true;
    }
    
    /**
     * Method stub.
     * @param request Http URI request
     * @return null
     */
    public String getMessage(HttpUriRequest request) {
        return null;
    }
    
    /**
     * Method stub.
     * @param request Http servlet request
     * @return null
     */
    public String getMessage(HttpServletRequest request) {
        return null;
    }
    
    /**
     * Method stub.
     * @param request Http URI request
     * @return 
     */
    public String getSignature(HttpUriRequest request) {
        return null;
    }
    
    /**
     * Method stub.
     * @param request Http servlet request
     * @return null
     */
    public String getSignature(HttpServletRequest request) {
        return null;
    }
    
    /**
     * Method stub.
     * @param request Http URI request
     * @return 
     */
    public String getUser(HttpUriRequest request) {
        return null;
    }
    
    /**
     * Method stub.
     * @param request Http servlet request
     * @return null
     */
    public String getUser(HttpServletRequest request) {
        return null;
    }
    
}
