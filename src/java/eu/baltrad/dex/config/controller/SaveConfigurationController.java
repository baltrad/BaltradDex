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

package eu.baltrad.dex.config.controller;

import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.config.model.ConfigurationManager;
import eu.baltrad.dex.util.ServletContextUtil;
import eu.baltrad.dex.util.InitAppUtil;
import eu.baltrad.dex.log.util.MessageLogger;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;


/**
 * Controller class creates new system configuration or modifies existing configuration.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
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
    private static final String OK_MSG_KEY = "message";
    private static final String ERROR_MSG_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    private ConfigurationManager configurationManager;
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SaveConfigurationController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Fetches Configuration object from the database.
     *
     * @param request HttpServletRequest
     * @return Configuration class object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        AppConfiguration conf = configurationManager.loadAppConf();
        if( conf == null ) {
            conf = new AppConfiguration("Node name", "http://localhost:8084/BaltradDex/dispatch.htm",
                PRIMARY_NODE, "0.7.3", 60000, 60000, "work", "images", "thumbs", "My Organization", 
                    "Organization's address", "Time zone", "Node admin's email", 
                    "/opt/baltrad/etc/bltnode-keys");
        }
        return conf;
    }
    /**
     * Initialize the given binder instance with custom property editors.
     *
     * @param request Current servlet request
     * @param binder New binder instance
     */
    @Override
    protected void initBinder( HttpServletRequest request, ServletRequestDataBinder binder ) {
        binder.registerCustomEditor( Integer.class, new CustomNumberEditor( Integer.class,
                new DecimalFormat( "#" ), true ) );
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
            log.error( "Failed to load time zones configuration file", e );
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
        AppConfiguration appConf = ( AppConfiguration )command;
        try {
            configurationManager.saveAppConf( appConf );
            InitAppUtil.saveAppConf( appConf );
            request.getSession().setAttribute( OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveconf.savesuccess" ) );
            log.warn( getMessageSourceAccessor().getMessage( "message.saveconf.savesuccess" ) );
        } catch( Exception e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveconf.savefail" ) );
            log.error( getMessageSourceAccessor().getMessage( "message.saveconf.savefail" ), e );
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
}
//--------------------------------------------------------------------------------------------------
