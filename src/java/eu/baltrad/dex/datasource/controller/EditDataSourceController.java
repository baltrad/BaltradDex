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

import java.util.List;
import java.util.Collections;

/**
 * Allows to edit data source selected from the list.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class EditDataSourceController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    /** Data sources list model key */
    private static final String DS_SELECT_EDIT_KEY = "dataSources";
//---------------------------------------------------------------------------------------- Variables
    /** References DataSourceManager */
    private DataSourceManager dataSourceManager;
    /** Success view name */
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Renders select data source page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) {
        List<DataSource> dataSources = dataSourceManager.load();
        Collections.sort( dataSources );
        return new ModelAndView( getSuccessView(), DS_SELECT_EDIT_KEY, dataSources );
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
     * Gets success view name.
     *
     * @return Success view name
     */
    public String getSuccessView() { return successView; }
    /**
     * Method sets success view name.
     *
     * @param successView Success view name to set
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------
