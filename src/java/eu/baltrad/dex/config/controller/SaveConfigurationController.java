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
import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.log.model.*;
import eu.baltrad.dex.core.model.NodeConnection;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
    public static final String TIME_ZONES = "time_zones";
    private static final String TZ_FILE = "includes/timezones_eu.txt";
    public static final String PRIMARY_NODE = "Primary";
    public static final String BACKUP_NODE = "Backup";
    private static final String OK_MSG_KEY = "ok_message";
    private static final String ERROR_MSG_KEY = "error_message";
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
        try {
            conf = configurationManager.getConfiguration( ConfigurationManager.CONF_REC_ID );
        } catch( SQLException e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR, 
                "Error while loading configuration from database: " + e.getMessage() ) );
        } catch( Exception e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                "Error while loading configuration from database: " + e.getMessage() ) );
        }
        if( conf == null ) {
            conf = new Configuration( "Short node name", PRIMARY_NODE,
                    "Short node address", "8084", "Host organization name", "Host organization " +
                    "address", "Local time zone", "temp", "Node administrator's " +
                    "email" );
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
        // node types
        List< String > nodeTypes = new ArrayList< String >();
        nodeTypes.add( PRIMARY_NODE );
        nodeTypes.add( BACKUP_NODE );
        // time zones
        List< String > timeZones = new ArrayList< String >();
        try {
            String tzFile = ServletContextUtil.getServletContextPath() + TZ_FILE;
            BufferedReader br = new BufferedReader( new FileReader( tzFile ) );
            String strLine;
            while( ( strLine = br.readLine() ) != null ) {
                timeZones.add( strLine );
            }
        } catch( IOException e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                "Failed to load time zones from file: " + e.getMessage() ) );
        }
        model.put( NODE_TYPES, nodeTypes );
        model.put( TIME_ZONES, timeZones );
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
            Object command, BindException errors ) {
        Configuration conf = ( Configuration )command;
        // set node's full address
        conf.setFullAddress( NodeConnection.HTTP_PREFIX + conf.getShortAddress() +
            NodeConnection.PORT_SEPARATOR + conf.getPortNumber() +
            NodeConnection.ADDRESS_SEPARATOR + NodeConnection.APP_CONTEXT +
            NodeConnection.ADDRESS_SEPARATOR + NodeConnection.ENTRY_ADDRESS );
        try {
            configurationManager.saveOrUpdate( conf );
            // read modified configuration
            InitAppUtil.initApp();
            request.getSession().setAttribute( OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveconf.savesuccess" ) );
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN,
                getMessageSourceAccessor().getMessage( "message.saveconf.savesuccess" ) ) );
        } catch( SQLException e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                        "message.saveconf.savefail" ) );
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                getMessageSourceAccessor().getMessage( "message.saveconf.savefail" ) + ": " +
                e.getMessage() ) );
            errors.reject( "message.saveconf.savefail" ); 
        } catch( Exception e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                        "message.saveconf.savefail" ) );
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                getMessageSourceAccessor().getMessage( "message.saveconf.savefail" ) + ": " +
                e.getMessage() ) );
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
