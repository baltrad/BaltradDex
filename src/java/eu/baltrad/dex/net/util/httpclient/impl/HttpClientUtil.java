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

package eu.baltrad.dex.net.util.httpclient.impl;

import eu.baltrad.dex.net.util.httpclient.IHttpClientUtil;
import eu.baltrad.dex.net.auth.EasyX509TrustManager;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import java.io.IOException;

/**
 * Http client wrapper.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class HttpClientUtil implements IHttpClientUtil {
    
    /** Maximum number of connections */
    private static final int MAX_TOTAL_CONNS = 200;
    /** Maximum number of connections per route */
    private static final int MAX_PER_ROUTE_CONNS = 20;
    
    private CloseableHttpClient client;

    /**
     * Constructor.
     * @param connTimeout Connection timeout in milliseconds
     * @param soTimeout Socket timeout in milliseconds
     */
    public HttpClientUtil(int connTimeout, int soTimeout) {
        try {
            // Create SSL context with custom trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(
                null,
                new TrustManager[] { new EasyX509TrustManager() },
                new SecureRandom()
            );
            
            // Create SSL connection socket factory with no hostname verification
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                NoopHostnameVerifier.INSTANCE
            );
            
            // Register connection socket factories
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
            
            // Create connection manager
            PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connMgr.setMaxTotal(MAX_TOTAL_CONNS);
            connMgr.setDefaultMaxPerRoute(MAX_PER_ROUTE_CONNS);
            
            // Configure socket timeout
            SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofMilliseconds(soTimeout))
                .build();
            connMgr.setDefaultSocketConfig(socketConfig);
            
            // Configure request timeouts
            RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connTimeout))
                .setConnectTimeout(Timeout.ofMilliseconds(connTimeout))
                .setResponseTimeout(Timeout.ofMilliseconds(soTimeout))
                .build();
            
            // Build HTTP client
            client = HttpClients.custom()
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig)
                .build();
                
        } catch (Exception e) {
            throw new RuntimeException("Failed to create HTTP client", e);
        }
    }
    
    /**
     * Post request.
     * @param request Http request
     * @return Http response
     * @throws IOException 
     * @throws Exception 
     */
    public org.apache.hc.core5.http.HttpResponse post(HttpUriRequest request) throws IOException, 
            Exception {
        org.apache.hc.core5.http.HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
        return response;
    }
    
    /**
     * Shutdown HTTP client
     */
    public void shutdown() {
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            // Ignore
        }
    }
    
}
