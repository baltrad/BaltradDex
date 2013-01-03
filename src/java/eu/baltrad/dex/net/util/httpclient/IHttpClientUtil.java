/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.baltrad.dex.net.util.httpclient;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 *
 * @author szewczenko
 */
public interface IHttpClientUtil {
    
    public HttpResponse post(HttpUriRequest request) throws IOException, 
            Exception;
    
}
