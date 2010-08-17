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

package eu.baltrad.dex.channel.controller;

import eu.baltrad.dex.data.model.DataManager;
import eu.baltrad.dex.util.FileCatalogConnector;
import eu.baltrad.fc.FileCatalog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.io.IOException;

/**
 * Implemens functionality allowing for listing products available for a given data channel.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class ChannelDataController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    public static final String MAP_KEY = "data_from_channel";
    public static final String CHANNEL_NAME = "channelName";
//---------------------------------------------------------------------------------------- Variables
    private DataManager dataManager = null;
    private static FileCatalog fileCatalog = null;
    private FileCatalogConnector fcConnector = new FileCatalogConnector();
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request
     *
     * @param request Http request
     * @param response Http response
     * @return Model and view
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request,
            HttpServletResponse response )
            throws ServletException, IOException {
        String channelName = request.getParameter( CHANNEL_NAME );
        // Initialize file catalog if null
        if( fileCatalog == null ) {
            fileCatalog = fcConnector.connect();
        }
        List dataList = dataManager.getDataFromChannel( fileCatalog, channelName );
        return new ModelAndView( getSuccessView(), MAP_KEY, dataList );
        
    }
    /**
     * Method returns reference to data manager object.
     *
     * @return Reference to data manager object
     */
    public DataManager getDataManager() { return dataManager; }
    /**
     * Method sets reference to data manager object.
     *
     * @param Reference to data manager object
     */
    public void setDataManager( DataManager dataManager ) { this.dataManager = dataManager; }
    /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() { return successView; }
    /**
     * Method sets reference to success view name string.
     *
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) { this.successView = successView; }
}
//--------------------------------------------------------------------------------------------------
