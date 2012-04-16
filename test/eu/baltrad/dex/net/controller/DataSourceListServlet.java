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
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Set;
import java.util.HashSet;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Test servlet. Receives and handles data source listing requests.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class DataSourceListServlet extends HttpServlet {
    
    private Authenticator authenticator;
    private JsonUtil jsonUtil;
    private Set<DataSource> sources;
    
    public DataSourceListServlet() {
        this.authenticator = new KeyczarAuthenticator("./keystore", 
                "dev.baltrad.eu");
        this.jsonUtil = new JsonUtil();
        this.sources = new HashSet<DataSource>();
        DataSource ds1 = new DataSource(1, "DS1", "A test data source");
        DataSource ds2 = new DataSource(2, "DS2", "One more test data source");
        DataSource ds3 = new DataSource(3, "DS3", "Yet another test data " +
                                                                     "source");
        sources.add(ds1);
        sources.add(ds2);
        sources.add(ds3);
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    {
        try {
            if (authenticator.authenticate(authenticator.getMessage(
                    request), authenticator.getSignature(request))) {
                response.setStatus(HttpServletResponse.SC_OK);
                try {
                    PrintWriter writer = new PrintWriter(
                            response.getOutputStream());
                    try {
                        writer.print(jsonUtil.dataSourcesToJsonString(sources));
                    } finally {
                        writer.close();
                    }
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
