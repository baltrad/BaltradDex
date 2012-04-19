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

package eu.baltrad.dex.net.controller;

import eu.baltrad.dex.net.util.Authenticator;
import eu.baltrad.dex.net.util.KeyczarAuthenticator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletInputStream;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * Test servlet. Receives and handles post file requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class PostFileServlet extends HttpServlet {
    
    private Authenticator authenticator;
    
    public PostFileServlet() {
        this.authenticator = new KeyczarAuthenticator("./keystore", 
                "dev.baltrad.eu");
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    {
        try {
            if (authenticator.authenticate(authenticator.getMessage(
                    request), authenticator.getSignature(request))) {
                try {
                    ServletInputStream sis = null;
                    FileWriter writer = null;
                    try {
                        sis = request.getInputStream();
                        writer = new FileWriter(new File("testfile.txt"));
                        IOUtils.copy(sis, writer);
                    } finally {
                        writer.close();
                        sis.close();
                    }
                    response.setStatus(HttpServletResponse.SC_OK);        
                } catch(IOException e) {
                    response.setStatus(
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
}
