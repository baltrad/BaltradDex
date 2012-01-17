/***************************************************************************************************
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
***************************************************************************************************/

package eu.baltrad.dex.core.controller;

import eu.baltrad.frame.model.*;

import junit.framework.TestCase;
import org.junit.*;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.HttpParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.HttpResponse;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.7
 * @since 0.7.7
 */
public class DataDeliveryRequestTest extends TestCase {
//---------------------------------------------------------------------------------------- Constants   
    private final static String KEYCZAR_KEYSTORE = "bltnode-keys";
    private final static String NODE_NAME = "test.baltrad.eu";
    private static final String ADDRESS = "http://localhost:8084/BaltradDex/dispatch.htm";
//---------------------------------------------------------------------------------------- Variables
    private HttpClient client;
    private List<FrameSender> senders;
//------------------------------------------------------------------------------------------ Methods    
    
    @Override @Before
    public void setUp() throws Exception {
        SchemeRegistry schemeReg = new SchemeRegistry();
        
        assertNotNull(schemeReg);
        
        registerHttpScheme(schemeReg);
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(schemeReg);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(20);
        
        assertNotNull(connMgr);
        
        client = new DefaultHttpClient(connMgr);
        
        assertNotNull(client);
        
        HttpParams httpParams = client.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 60000);
        HttpConnectionParams.setSoTimeout(httpParams, 60000);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        
        long timestamp = System.currentTimeMillis();
        String signature = Protocol.getSignatureString(KEYCZAR_KEYSTORE, NODE_NAME, timestamp);
        
        assertNotNull(signature);
        
        senders = new ArrayList<FrameSender>();
        
        assertNotNull(senders);
        
        for (int i = 1; i <= 10; i++) {
            FrameSender sender = new FrameSender(client, Frame.postDataDeliveryRequest(ADDRESS, 
                    ADDRESS, NODE_NAME, timestamp, signature, new File("test_" + 
                    Integer.toString(i) + ".h5")));
            senders.add(sender);
        }
        
        assertEquals(10, senders.size());
    }
    
    /*
     * Uncomment to test data delivery against operational node
     */
    @Test
    public void testPostDataDeliveryRequest() throws Exception {
        /*for (int i = 0; i < 10; i++) {
            senders.get(i).start();
            Thread.sleep(2000);
            
            assertEquals(200, senders.get(i).getStatus());            
        }
        */
    }
    
    private void registerHttpScheme(SchemeRegistry schemeReg) {
        Scheme http = new Scheme("http", 80, new PlainSocketFactory());
        schemeReg.register(http);
    }
    
    class FrameSender extends Thread implements Runnable {
        
        private HttpClient client;
        private Frame frame;
        private int status;
        
        public int getStatus() { return status; }
        
        public FrameSender(HttpClient client, Frame frame) {
            this.client = client;
            this.frame = frame;
        }
        
        @Override
        public void run() {
            HttpResponse res = post(this.frame);
            this.status = res.getStatusLine().getStatusCode();
            System.out.println("Post status :: " + this.status);
        }
        
        private HttpResponse post(Frame frame) {
            HttpResponse response = null;
            try {
                response = this.client.execute(frame);
            } catch(Exception e) {
                System.out.println("Failed to post frame " + e.getMessage());
            } finally {
                frame.abort();
            }
            return response;
        }
    }
}
//--------------------------------------------------------------------------------------------------