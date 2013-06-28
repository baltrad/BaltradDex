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
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;

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
    
    private HttpClient client;

    /**
     * Constructor.
     * @param connTimeout Connection timeout
     * @param soTimeout Socket timeout 
     */
    public HttpClientUtil(int connTimeout, int soTimeout) {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        registerHttpScheme(schemeRegistry);
        registerHttpsScheme(schemeRegistry);
        
        ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(
                schemeRegistry);
        connMgr.setMaxTotal(MAX_TOTAL_CONNS);
        connMgr.setDefaultMaxPerRoute(MAX_PER_ROUTE_CONNS);
        
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, soTimeout);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(httpParams, HTTP.UTF_8);
        
        client = new DefaultHttpClient(connMgr, httpParams);
    }
    
    /**
     * Post request.
     * @param request Http request
     * @return Http response
     * @throws IOException 
     * @throws Exception 
     */
    public HttpResponse post(HttpUriRequest request) throws IOException, 
            Exception {
        HttpResponse response = null;
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
        client.getConnectionManager().shutdown();
    }
    
    /**
     * Registers HTTP scheme.
     * 
     * @param schemeReg Scheme registry
     */
    private void registerHttpScheme(SchemeRegistry schemeReg) {
        Scheme http = new Scheme("http", 80, new PlainSocketFactory());
        schemeReg.register(http);
    }
    /**
     * Registers HTTPS scheme.
     * 
     * @param schemeReg Scheme registry
     */
    private void registerHttpsScheme(SchemeRegistry schemeReg) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(
                null,
                new TrustManager[] {
                    new EasyX509TrustManager()
                },
                new SecureRandom()
            );
            Scheme https = new Scheme("https", 443, new SSLSocketFactory(
                sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
            schemeReg.register(https);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register https scheme", e);
        }
    } 
    
}
