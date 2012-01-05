/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.core.controller;

import junit.framework.TestCase;

import eu.baltrad.frame.model.*;
import static eu.baltrad.frame.model.Protocol.*;

import org.apache.http.HttpResponse;
import javax.servlet.http.HttpServletResponse;
import java.security.cert.Certificate;
import java.io.File;
/**
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.7
 * @since 0.7.7
 */
public class DataDeliveryRequestTest extends TestCase {
//---------------------------------------------------------------------------------------- Constants   
    /** Keystore file */
    private static final String KS_FILE_PATH = ".dex_keystore.jks";
    /** Certificate alias */
    private static final String CERT_ALIAS = "baltrad.imgw.pl";
    /** Keystore password */
    private static final String KEYSTORE_PASSWD = "s3cret";
    /** Sender node's name */
    private final static String SENDER_NODE_NAME = "test_node";
    /** Receiver's address */
    private static final String RECEIVER_ADDRESS = "http://localhost:8084/BaltradDex/dispatch.htm";
    /** Sender's address */
    private static final String SENDER_ADDRESS = "http://localhost:8084/BaltradDex/dispatch.htm";
    /** Connection timeout */
    private static final int CONN_TIMEOUT = 60000;
    /** Socket timeout */
    private static final int SO_TIMEOUT = 60000;
    /** Test data file */
    private static final String TEST_DATA_FILE = "test_20.h5";
//---------------------------------------------------------------------------------------- Variables
    /** Certificate file */
    private File certFile;
    /** Signature file */
    private File sigFile;
    /** Data file */
    private File dataFile;
    /** Certificate frame */
    private Frame certFrame;
    /** Data frame */
    private Frame dataFrame;
    /** Frame handler */
    private Handler handler;
//------------------------------------------------------------------------------------------ Methods    
    
    @Override
    public void setUp() throws Exception {
        Certificate cert = loadCert(KS_FILE_PATH, CERT_ALIAS, KEYSTORE_PASSWD);
        
        assertNotNull(cert);
        
        certFile = saveCertToFile(cert, ".", SENDER_NODE_NAME);
        
        assertNotNull(certFile);
        
        certFrame = Frame.postCertRequest(RECEIVER_ADDRESS, SENDER_ADDRESS, SENDER_NODE_NAME,
                certFile); 
        
        assertNotNull(certFrame);
        
        long timestamp = System.currentTimeMillis();
        sigFile = saveSignatureToFile(getSignatureBytes(KS_FILE_PATH, CERT_ALIAS, KEYSTORE_PASSWD,
                timestamp), ".");
        
        assertNotNull(sigFile);
        
        dataFile = new File(TEST_DATA_FILE);
        
        assertNotNull(dataFile);
        
        dataFrame = Frame.postDataDeliveryRequest(RECEIVER_ADDRESS, SENDER_ADDRESS, 
                SENDER_NODE_NAME, timestamp, sigFile, dataFile);
        
        assertNotNull(dataFrame);
        
        handler = new Handler(CONN_TIMEOUT, SO_TIMEOUT);
        
        assertNotNull(handler);
    }
    
    public void testPostCertRequest() throws Exception {
        /*
        HttpResponse response = handler.post(certFrame);
        
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
        */
    }
    
    public void testPostDataDeliveryRequest() throws Exception {
        /*
        HttpResponse response = handler.post(dataFrame);
        
        assertNotNull(response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
        */
    }
    
    @Override
    public void tearDown() throws Exception {
        //certFile.delete();
        //sigFile.delete();
        //dataFile.delete();
    }
}
//--------------------------------------------------------------------------------------------------