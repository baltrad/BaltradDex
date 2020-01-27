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
import eu.baltrad.beast.security.ISecurityManager;
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
import org.apache.log4j.Logger;

import org.keyczar.exceptions.KeyczarException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements Keyczar authenticator. Uses the security manager in beast.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class KeyczarAuthenticator implements Authenticator {
    private static final String AUTH_HDR = "Authorization";
    /**
     * The security manager
     */
    private ISecurityManager securityManager;
    
    /**
     * DEX logger
     */
    //private Logger log;
    
    /**
     * Constructor.
     * @param keystoreRoot Keystore root directory
     */
    public KeyczarAuthenticator(ISecurityManager securityManager/*String keystoreRoot*/) {
      this.securityManager = securityManager;
      //this.log = Logger.getLogger("DEX");
    }
    
    /**
     * Adds signature as a header field.
     * @param request Http request
     * @param keyName Private key used to sign a message 
     */
    public void addCredentials(HttpUriRequest request, String keyName) 
                throws KeyczarException {
        String message = securityManager.createSignatureMessage(request);
        request.addHeader(AUTH_HDR, keyName + ":" + securityManager.getSigner(keyName).sign(message));
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
      return securityManager.getVerifier(keyName).verify(message, signature);
    }

    /**
     * @param securityManager the security manager
     */
    @Autowired
    public void setSecurityManager(ISecurityManager securityManager) {
      this.securityManager = securityManager;
    }
}
