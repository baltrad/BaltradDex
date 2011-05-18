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
import eu.baltrad.dex.log.model.*;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.sql.SQLException;

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
    private static final String DS_SELECT_REMOVE_VIEW = "dsSelectRemove";
    /** Remove data source view */
    private static final String DS_REMOVE_VIEW = "dsRemove";
    /** Data sources list model key */
    private static final String DS_SELECT_REMOVE_KEY = "dataSources";
    /** Submit button key */
    private static final String DS_SUBMIT_BUTTON_KEY = "submitButton";
    /** Selected data sources key */
    private static final String DS_SELECTED_SOURCES = "selectedSources";
    /** Remove data source error key */
    private static final String DS_REMOVE_ERROR_KEY = "dsRemoveError";
//---------------------------------------------------------------------------------------- Variables
    /** References data source manager object */
    private DataSourceManager dataSourceManager;
    /** References log manager object */
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Renders select data source page.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsSelectRemove( HttpServletRequest request, HttpServletResponse response ) {
        List<DataSource> dataSources = dataSourceManager.getDataSources();
        return new ModelAndView( DS_SELECT_REMOVE_VIEW, DS_SELECT_REMOVE_KEY, dataSources );
    }
    /**
     * Removes selected data sources.
     *
     * @param request Current HTTP request
     * @param response Current HTTP response
     * @return The prepared model and view
     */
    public ModelAndView dsRemove( HttpServletRequest request, HttpServletResponse response ) {
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
                        dataSourceManager.deleteDataSource( Integer.parseInt(
                                parameterValues[ i ] ) );
                        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN,
                                "Data source " + dataSourceName + " successfully removed" ) );
                    } catch( SQLException e ) {
                        modelAndView.addObject( DS_REMOVE_ERROR_KEY, "SQL exception" );
                        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                                "Failed to remove data source: SQL Exception " + e.getMessage() ) );
                    } catch( Exception e ) {
                        modelAndView.addObject( DS_REMOVE_ERROR_KEY, "Exception" );
                        logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                                "Failed to remove data source: Exception " + e.getMessage() ) );
                    }
                }
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
     * Gets reference to LogManager.
     *
     * @return Reference to LogManager
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Method sets reference to LogManager.
     *
     * @param logManager Reference to LogManager
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------
