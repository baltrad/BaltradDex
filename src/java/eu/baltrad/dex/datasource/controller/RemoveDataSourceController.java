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
import eu.baltrad.dex.log.model.MessageLogger;

import eu.baltrad.beast.db.CoreFilterManager;
import eu.baltrad.beast.db.IFilter;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

/**
 * Allows to select and remove existing data source.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.5
 * @since 0.6.5
 */
public class RemoveDataSourceController extends MultiActionController {
//---------------------------------------------------------------------------------------- Constants
    /** Select remove data source view */
    private static final String DS_SELECT_REMOVE_VIEW = "remove_datasource";
    /** Data source selection view */
    private static final String DS_TO_REMOVE_VIEW = "remove_selected_datasource";
    /** Remove data source view */
    private static final String DS_REMOVE_VIEW = "remove_datasource_status";
    /** Data sources list model key */
    private static final String DS_SELECT_REMOVE_KEY = "dataSources";
    /** Submit button key */
    private static final String DS_SUBMIT_BUTTON_KEY = "submitButton";
    /** Selected data sources key */
    private static final String DS_SELECTED_SOURCES = "selectedSources";
    /** Remove data source message key */
    private static final String DS_REMOVE_MSG_KEY = "message";
    /** Remove data source error key */
    private static final String DS_REMOVE_ERROR_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    /** References data source manager object */
    private DataSourceManager dataSourceManager;
    /** References core filter manager object */
    private CoreFilterManager coreFilterManager;
    /** References message logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public RemoveDataSourceController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Renders select data source page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView remove_datasource( HttpServletRequest request, 
            HttpServletResponse response ) {
        List<DataSource> dataSources = dataSourceManager.getDataSources();
        Collections.sort( dataSources );
        return new ModelAndView( DS_SELECT_REMOVE_VIEW, DS_SELECT_REMOVE_KEY, dataSources );
    }
    /**
     * Prepares a list of data sources selected for removal.
     * 
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView remove_selected_datasource( HttpServletRequest request, 
            HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        Map parameterMap = request.getParameterMap();
        String[] parameterValues = null;
        List<DataSource> dataSources = new ArrayList<DataSource>();
        if( parameterMap.containsKey( DS_SUBMIT_BUTTON_KEY ) ) {
            parameterValues = ( String[] )parameterMap.get( DS_SELECTED_SOURCES );
            if( parameterValues != null ) {
                for( int i = 0; i < parameterValues.length; i++ ) {
                    try {
                        dataSources.add( dataSourceManager.getDataSource(
                                Integer.parseInt( parameterValues[ i ] ) ) );
                    } catch( Exception e ) {
                        String msg = "Failed to fetch data sources";
                        modelAndView.addObject( DS_REMOVE_ERROR_KEY, msg );
                        log.error( msg, e );
                    }
                }
                modelAndView = new ModelAndView( DS_TO_REMOVE_VIEW, DS_SELECT_REMOVE_KEY,
                        dataSources );
            } else {
                modelAndView.addObject( DS_SELECT_REMOVE_KEY, dataSourceManager.getDataSources() );
                modelAndView.setViewName( DS_SELECT_REMOVE_VIEW );
            }
        }
        return modelAndView;
    }
    /**
     * Removes selected data sources.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView remove_datasource_status( HttpServletRequest request, 
            HttpServletResponse response ) {
        ModelAndView modelAndView = new ModelAndView();
        Map parameterMap = request.getParameterMap();
        String[] parameterValues = null;
        if( parameterMap.containsKey( DS_SUBMIT_BUTTON_KEY ) ) {
            parameterValues = ( String[] )parameterMap.get( DS_SELECTED_SOURCES );
            if( parameterValues != null ) {
                for( int i = 0; i < parameterValues.length; i++ ) {
                    try {
                        DataSource dataSource = dataSourceManager.getDataSource(
                                Integer.parseInt( parameterValues[ i ] ) );
                        String dataSourceName = dataSource.getName();
                        // Delete filters
                        int filterId = dataSourceManager.getFilterId( dataSource.getId() );
                        IFilter filter = coreFilterManager.load( filterId );
                        coreFilterManager.remove( filter );
                        // Delete data source
                        dataSourceManager.deleteDataSource( Integer.parseInt(
                                parameterValues[ i ] ) );
                        String msg = "Data source successfully removed: " + dataSourceName;
                        log.warn( msg );
                    } catch( Exception e ) {
                        modelAndView.addObject( DS_REMOVE_ERROR_KEY, "SQL exception" );
                        log.error( "Failed to remove data source", e );
                    }
                }
                String msg = "Selected data sources have been removed.";
                modelAndView.addObject( DS_REMOVE_MSG_KEY, msg );
                modelAndView.setViewName( DS_REMOVE_VIEW );
            } else {
                modelAndView.addObject( DS_SELECT_REMOVE_KEY, dataSourceManager.getDataSources() );
                modelAndView.setViewName( DS_SELECT_REMOVE_VIEW );
            }
        }
        return modelAndView;
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
     * Gets reference to CoreFilterManager.
     *
     * @return Reference to CoreFilterManager
     */
    public CoreFilterManager getCoreFilterManager() {
        return coreFilterManager;
    }
    /**
     * Sets reference to CoreFilterManager.
     *
     * @param coreFilterManager Reference tp CoreFilterManager to set
     */
    public void setCoreFilterManager( CoreFilterManager coreFilterManager ) {
        this.coreFilterManager = coreFilterManager;
    }
}
//--------------------------------------------------------------------------------------------------
