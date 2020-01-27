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

package eu.baltrad.dex.net.auth;

import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.request.factory.impl.DefaultRequestFactory;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.beast.security.ISecurityManager;
import eu.baltrad.beast.security.SecurityStorageException;
import eu.baltrad.beast.security.crypto.Signer;
import eu.baltrad.beast.security.crypto.Verifier;
import eu.baltrad.dex.auth.util.KeyczarCryptoFactory;
import eu.baltrad.dex.user.model.User;

import org.keyczar.exceptions.KeyczarException;

import org.apache.http.client.methods.HttpUriRequest;
import org.easymock.EasyMockSupport;
import org.apache.http.Header;

import org.apache.commons.codec.binary.Base64;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import static org.easymock.EasyMock.*;

/**
 * Keyczar authenticator test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class KeyczarAuthenticatorTest extends EasyMockSupport {
    
    private final static String DATE_FORMAT = "E, d MMM yyyy HH:mm:ss z";
    
    private ISecurityManager securityManager;
    private Signer signer;
    private Verifier verifier;
    private RequestFactory requestFactory;
    private KeyczarAuthenticator classUnderTest;
    private User user;
    private DateFormat format;
    
    @Before
    public void setUp() {
      requestFactory = new DefaultRequestFactory(URI.create("http://example.com"));
      securityManager = createMock(ISecurityManager.class);
      signer = createMock(Signer.class);
      verifier = createMock(Verifier.class);
      user = new User(1, "test", "s3cret", "org", "unit", "locality", "state", "XX", "user", "http://localhost:8084");
      classUnderTest = new KeyczarAuthenticator(securityManager);
    }
    
    @After
    public void tearDown() {
      requestFactory = null;
      securityManager = null;
      signer = null;
      verifier = null;
      classUnderTest = null;
    }

    @Test(expected = SecurityStorageException.class)
    public void addCredentials_InvalidKeystore() throws Exception {
      HttpUriRequest request = requestFactory.createUpdateSubscriptionRequest(user, null);

      expect(securityManager.createSignatureMessage(request)).andReturn("hello");
      expect(securityManager.getSigner("localhost")).andThrow(new SecurityStorageException());

      replayAll();
      classUnderTest.addCredentials(request, "localhost");
      verifyAll();
    }

    @Test
    public void addCredentials_OK() throws Exception {
        HttpUriRequest request = requestFactory.createUpdateSubscriptionRequest(user, null);
        
        expect(securityManager.createSignatureMessage(request)).andReturn("hello");
        expect(securityManager.getSigner("localhost")).andReturn(signer);
        expect(signer.sign("hello")).andReturn("safely signed");
        replayAll();
        
        classUnderTest.addCredentials(request, "localhost");
        
        verifyAll();
        
        Header header = request.getFirstHeader("Authorization");
        assertEquals("localhost:safely signed", header.getValue());
    }
    
    @Test(expected = KeyczarException.class)
    public void authenticate_InvalidKeystore() throws Exception {
      expect(securityManager.getVerifier("localhost")).andReturn(verifier);
      expect(verifier.verify("abc", "signature")).andThrow(new KeyczarException("bad"));
      
      replayAll();
      
      classUnderTest.authenticate("abc", "signature", "localhost");
      
      verifyAll();
    }

    @Test
    public void authenticate_Failure() throws Exception {
      expect(securityManager.getVerifier("localhost")).andReturn(verifier);
      expect(verifier.verify("abc", "signature")).andReturn(false);
      replayAll();
      
      boolean result = classUnderTest.authenticate("abc", "signature", "localhost");
      
      verifyAll();
      assertEquals(false, result);
    }
    
    @Test
    public void authenticate_Success() throws Exception {
      expect(securityManager.getVerifier("localhost")).andReturn(verifier);
      expect(verifier.verify("abc", "signature")).andReturn(true);
      replayAll();
      
      boolean result = classUnderTest.authenticate("abc", "signature", "localhost");
      
      verifyAll();
      assertEquals(true, result);
    }
}
