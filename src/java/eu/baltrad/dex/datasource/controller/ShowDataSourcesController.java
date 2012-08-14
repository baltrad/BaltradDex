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

package eu.baltrad.dex.datasource.controller;

import eu.baltrad.dex.datasource.model.DataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import java.util.List;
import java.util.Collections;

/**
 * Displays available data sources.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class ShowDataSourcesController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    /** Data sources list model key */
    private static final String DATA_SOURCES_KEY = "data_sources";
//---------------------------------------------------------------------------------------- Variables
    /** References DataSourceManager */
    private DataSourceManager dataSourceManager;
    /** Success view */
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Process the request and return a ModelAndView object which the DispatcherServlet will render.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @return a ModelAndView to render, or null if handled directly
     * @throws ServletException
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        List<DataSource> dataSources = dataSourceManager.load();
        Collections.sort( dataSources );
        return new ModelAndView( getSuccessView(), DATA_SOURCES_KEY, dataSources );
    }
    /**
     * Gets reference to DataSourceManager
     *
     * @return Reference to DataSourceManager
     */
    public DataSourceManager getDataSourceManager() { return dataSourceManager; }
    /**
     * Sets reference to DataSourceManager
     *
     * @param dataSourceManager Reference to DataSourceManager
     */
    public void setDataSourceManager( DataSourceManager dataSourceManager ) {
        this.dataSourceManager = dataSourceManager;
    }
    /**
     * Gets success view.
     *
     * @return Success view
     */
    public String getSuccessView() { return successView; }
    /**
     * Sets success view.
     *
     * @param successView Success view to set
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------
