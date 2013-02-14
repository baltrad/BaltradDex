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

package eu.baltrad.dex.net.auth;

import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.auth.util.CryptoFactory;
import eu.baltrad.dex.auth.util.KeyczarCryptoFactory;
import eu.baltrad.dex.auth.util.Signer;
import eu.baltrad.dex.auth.util.Verifier;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.Header;

import org.apache.commons.lang3.StringUtils;

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
    private static final String AUTH_HDR = "Authorization";
    
    private CryptoFactory cryptoFactory;
    
    /**
     * Constructor.
     * @param keystoreRoot Keystore root directory
     */
    public KeyczarAuthenticator(String keystoreRoot) {
        this.cryptoFactory = new KeyczarCryptoFactory(new File(keystoreRoot));
    }
    
    /**
     * Adds signature as a header field.
     * @param request Http request
     * @param keyName Private key used to sign a message 
     */
    public void addCredentials(HttpUriRequest request, String keyName) 
                throws KeyczarException {
        String message = getMessage(request);
        Signer signer = cryptoFactory.createSigner(keyName);
        request.addHeader(AUTH_HDR, keyName + ":" + signer.sign(message));
    }
    
    /**
     * Authenticates request at servlet side.
     * @param message Message to authenticate
     * @param signature Signature to authenticate with
     * @param keyName Public key used to verify a message 
     * @return True upon success, false otherwise
     */
    public boolean authenticate(String message, String signature, 
            String keyName) throws KeyczarException {
        Verifier verifier = cryptoFactory.createVerifier(keyName);
        return verifier.verify(message, signature);
    }
    
    /**
     * Prepares message for signing.
     * @param request Http request
     * @return Message to be signed
     */
    private String getMessage(HttpUriRequest request) {
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
}
