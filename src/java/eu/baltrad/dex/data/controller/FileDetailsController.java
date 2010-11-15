/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.data.controller;

import eu.baltrad.dex.data.model.DataManager;
import eu.baltrad.dex.data.model.Data;
import eu.baltrad.dex.util.FileCatalogConnector;

import eu.baltrad.fc.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Implements functionality allowing for accessing detailed information about radar data file.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class FileDetailsController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    private static final String FC_FILE_UUID = "uuid";
    private static final String FILE_DETAILS = "file_details";
//---------------------------------------------------------------------------------------- Variables
    private DataManager dataManager;
    private static FileCatalog fc;
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Handles HTTP request.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @return ModelAndView object
     * @throws ServletException 
     * @throws IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        String uuid = request.getParameter( FC_FILE_UUID );
        if( fc == null ) {
            fc = FileCatalogConnector.connect();
        }
        Data data = dataManager.getDataByID( fc, uuid );
        return new ModelAndView( getSuccessView(), FILE_DETAILS, data );
    }
    /**
     * Gets reference to data manager object.
     *
     * @return Reference to data manager object
     */
    public DataManager getDataManager() { return dataManager; }
    /**
     * Sets reference to data manager object.
     *
     * @param Reference to data manager object
     */
    public void setDataManager( DataManager dataManager ) { this.dataManager = dataManager; }
    /**
     * Gets reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Sets reference to success view name string.
     *
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------