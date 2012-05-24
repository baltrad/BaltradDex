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

import eu.baltrad.dex.auth.util.CryptoFactory;
import eu.baltrad.dex.auth.util.KeyczarCryptoFactory;
import eu.baltrad.dex.auth.util.Signer;
import eu.baltrad.dex.auth.util.Verifier;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.Header;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.keyczar.exceptions.KeyczarException;

/**
 * Implements Keyczar authenticator.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class KeyczarAuthenticator implements Authenticator {
    
    private static final String[] HEADERS = {"Content-Type", "Content-MD5",
        "Date"};
    private static final String NODE_NAME_HDR = "Node-Name";
    private static final String NODE_ADDR_HDR = "Node-Address";
    private static final String AUTH_HDR = "Authorization";
    private static final String HDR_SEPARATOR = ":";
    
    private CryptoFactory cryptoFactory;
    private Signer signer;
    private Verifier verifier;
    private String keyName;
    
    /**
     * Constructor.
     * @param keystoreRoot Keystore root directory
     * @param keyName Name of the key to use for authentication
     */
    public KeyczarAuthenticator(String keystoreRoot, String keyName) {
        this.cryptoFactory = new KeyczarCryptoFactory(new File(keystoreRoot));
        this.keyName = keyName;
        this.signer = cryptoFactory.createSigner(keyName);
        this.verifier = cryptoFactory.createVerifier(keyName);
    }
    
    /**
     * Adds signature as a header field.
     * @param request Http request
     */
    public void addCredentials(HttpUriRequest request) {
        String message = getMessage(request);
        String signature;
        try {
            signature = signer.sign(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign message", e);
        }
        request.addHeader(AUTH_HDR, keyName + ":" + signature);   
    }
    
    /**
     * Adds signature as a header field.
     * @param request Http request
     */
    public void addCredentials(HttpServletRequest request) {
        String message = getMessage(request);
        String signature;
        try {
            signature = signer.sign(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign message", e);
        }
        request.setAttribute(AUTH_HDR, keyName + ":" + signature);
    }
    
    /**
     * Authenticates request at servlet side.
     * @param message Message to authenticate
     * @param signature Signature to authenticate with
     * @return True upon success, false otherwise
     */
    public boolean authenticate(String message, String signature) {
        boolean result = false;
        try {
            result = verifier.verify(message, signature);
        } catch (KeyczarException e) {
            System.out.println("Failed to authenticate message: " 
                    + e.getMessage());
        }
        return result;
    }
    
    /**
     * Creates signature using given header fields.
     * @param request Http request
     * @return Signature string 
     */
    protected String sign(HttpUriRequest request) {
        String message = getMessage(request);
        return signer.sign(message);
    }
    
    /**
     * Prepares message for signing.
     * @param request Http request
     * @return Message to be signed
     */
    public String getMessage(HttpUriRequest request) {
        List<String> result = new ArrayList<String>();
        result.add(request.getMethod());
        result.add(request.getURI().toString());
        for (String headerName : HEADERS) {
            Header header = request.getFirstHeader(headerName);
            if (header != null) {
                String headerValue = header.getValue();
                headerValue = StringUtils.strip(headerValue);
                result.add(headerValue);
            }
        }
        return StringUtils.join(result, '\n');
    }
    
    /**
     * Retrieves message for authentication.
     * @param request Http request
     * @return Message to be signed
     */
    public String getMessage(HttpServletRequest request) {
        List<String> result = new ArrayList<String>();
        result.add(request.getMethod());
        result.add(request.getRequestURL().toString());
        for (int i = 0; i < HEADERS.length; i++) {
            String headerValue = 
                ((String) request.getAttribute(HEADERS[i]) != null) ?
                (String) request.getAttribute(HEADERS[i]) :
                request.getHeader(HEADERS[i]);
            if (headerValue != null) {
                headerValue = StringUtils.strip(headerValue);
                result.add(headerValue);
            }
        }
        return StringUtils.join(result, '\n');
    }
    
    /**
     * Extracts signature from request header. 
     * @param request Http URI request
     * @return Signature string
     */
    public String getSignature(HttpUriRequest request) {
        Header header = request.getFirstHeader(AUTH_HDR);
        String headerValue = header.getValue();
        String[] parts = headerValue.split(HDR_SEPARATOR);
        return parts[1];
    }
    
    /**
     * Extracts signature from request header. 
     * @param request Http servlet request
     * @return Signature string
     */
    public String getSignature(HttpServletRequest request) {
        String headerValue = 
            ((String) request.getAttribute(AUTH_HDR) != null) ?
            (String) request.getAttribute(AUTH_HDR) : 
            request.getHeader(AUTH_HDR);    
        String[] parts = headerValue.split(HDR_SEPARATOR);
        return parts[1];
    }
    
    /**
     * Extracts user name from request header. 
     * @param request Http URI request
     * @return User identity string
     */
    public String getUser(HttpUriRequest request) {
        Header header = request.getFirstHeader(AUTH_HDR);
        String headerValue = header.getValue();
        String[] parts = headerValue.split(HDR_SEPARATOR);
        return parts[0];
    }
    
    /**
     * Extracts user name from request header. 
     * @param request Http servlet request
     * @return User identity string
     */
    public String getUser(HttpServletRequest request) {
        String headerValue = 
            ((String) request.getAttribute(AUTH_HDR) != null) ?
            (String) request.getAttribute(AUTH_HDR) : 
            request.getHeader(AUTH_HDR);    
        String[] parts = headerValue.split(HDR_SEPARATOR);
        return parts[0];
    }
    
    /**
     * Retrieves node name from request.
     * @param request Http URI request
     * @return Name of the requesting node
     */
    public String getNodeName(HttpUriRequest request) {
        Header header = request.getFirstHeader(NODE_NAME_HDR);
        return header.getValue();
    }
    
    /**
     * Retrieves node name from request.
     * @param request Http servlet request
     * @return Name of the requesting node
     */
    public String getNodeName(HttpServletRequest request) {
        String headerValue = 
            ((String) request.getAttribute(NODE_NAME_HDR) != null) ?
            (String) request.getAttribute(NODE_NAME_HDR) : 
            request.getHeader(NODE_NAME_HDR);
        return headerValue;
    }
    
    /**
     * Retrieves node address from request.
     * @param request Http URI request
     * @return Address of the requesting node
     */
    public String getNodeAddress(HttpUriRequest request) {
        Header header = request.getFirstHeader(NODE_ADDR_HDR);
        return header.getValue();
    }
    
    /**
     * Retrieves node address from request.
     * @param request Http servlet request
     * @return Address of the requesting node
     */
    public String getNodeAddress(HttpServletRequest request) {
        String headerValue = 
            ((String) request.getAttribute(NODE_ADDR_HDR) != null) ?
            (String) request.getAttribute(NODE_ADDR_HDR) : 
            request.getHeader(NODE_ADDR_HDR);
        return headerValue;
    }
}
            
