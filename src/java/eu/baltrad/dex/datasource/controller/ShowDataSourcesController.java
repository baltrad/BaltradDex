/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.datasource.controller;

import eu.baltrad.dex.datasource.manager.impl.DataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Collections;

/**
 * Displays available data sources.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
@Controller
public class ShowDataSourcesController {

    // View names
    private static final String SHOW_DATA_SOURCES_VIEW = "datasources_show";
    private static final String EDIT_DATA_SOURCES_VIEW = "datasources_edit";
    
    // Model keys
    private static final String DATA_SOURCES_KEY = "data_sources";
    
    private DataSourceManager dataSourceManager;
    
    /**
     * Show data sources.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/datasources_show.htm")
    public String showDataSources(ModelMap model) {
        List<DataSource> dataSources = dataSourceManager.load(DataSource.LOCAL);
        Collections.sort(dataSources);
        model.addAttribute(DATA_SOURCES_KEY, dataSources);
        return SHOW_DATA_SOURCES_VIEW;
    }
    
    /**
     * Edit data sources.
     * @param model Model map
     * @return View name
     */
    @RequestMapping("/datasources_edit.htm")
    public String editDataSources(ModelMap model) {
        List<DataSource> dataSources = dataSourceManager.load(DataSource.LOCAL);
        Collections.sort(dataSources);
        model.addAttribute(DATA_SOURCES_KEY, dataSources);
        return EDIT_DATA_SOURCES_VIEW;
    }

    /**
     * @param dataSourceManager the dataSourceManager to set
     */
    @Autowired
    public void setDataSourceManager(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }
    
}

