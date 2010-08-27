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

package eu.baltrad.dex.config.controller;

import eu.baltrad.dex.config.model.Configuration;
import eu.baltrad.dex.config.model.ConfigurationManager;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.log.model.LogManager;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import org.hibernate.HibernateException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

/**
 * Controller class creates new system configuration or modifies existing configuration.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class SaveConfigurationController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    public static final String NODE_TYPES = "node_types";
    public static final String PRIMARY_NODE = "Primary";
    public static final String BACKUP_NODE = "Backup";
    public static final String MSG = "message";
//---------------------------------------------------------------------------------------- Variables
    private ConfigurationManager configurationManager;
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Fetches Configuration object from the database.
     *
     * @param request HttpServletRequest
     * @return Configuration class object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        Configuration conf = null;
        conf = configurationManager.getConfiguration( ConfigurationManager.CONF_REC_ID );
        if( conf == null ) {
            conf = new Configuration( "Short node name", PRIMARY_NODE,
                    "Full node address", "Your organization name", "Your organization address",
                    "Local time zone", "Temporary directory", "Node administrator's email" );
        }
        return conf;
    }
    /**
     * Returns HashMap holding list of available node types.
     *
     * @param request HttpServletRequest
     * @return HashMap object holding node types
     * @throws Exception
     */
    @Override
    protected HashMap referenceData( HttpServletRequest request ) throws Exception {
        HashMap model = new HashMap();
        List< String > nodeTypes = new ArrayList< String >();
        nodeTypes.add( PRIMARY_NODE );
        nodeTypes.add( BACKUP_NODE );
        model.put( NODE_TYPES, nodeTypes );
        return model;
    }
    /**
     * Saves configuration object.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param command Command object
     * @param errors Errors object
     * @return ModelAndView object
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) {
        Configuration conf = ( Configuration )command;
        try {
            configurationManager.saveConfiguration( conf );
            request.getSession().setAttribute( MSG, getMessageSourceAccessor().getMessage(
                "message.saveconf.savesuccess" ) );
            // read modified configuration
            InitAppUtil.initApp();
            logManager.addEntry( new Date(), LogManager.MSG_WRN, "System configuration saved" );
        } catch( HibernateException e ) {
            request.getSession().setAttribute( MSG, getMessageSourceAccessor().getMessage(
                "message.saveconf.savefail" ) );
            errors.reject( "message.saveconf.savefail" ); 
        }
        return new ModelAndView( getSuccessView() );
    }
    /**
     * Gets reference to ConfigurationManager object.
     *
     * @return Reference to ConfigurationManager object
     */
    public ConfigurationManager getConfigurationManager() { return configurationManager; }
    /**
     * Sets reference to ConfigurationManager object.
     *
     * @param configurationManager Reference to ConfigurationManager object.
     */
    public void setConfigurationManager( ConfigurationManager configurationManager ) {
        this.configurationManager = configurationManager;
    }
    /**
     * Method gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Method sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------
