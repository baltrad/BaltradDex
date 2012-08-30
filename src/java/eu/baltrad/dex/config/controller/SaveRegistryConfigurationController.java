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

import eu.baltrad.dex.log.util.MessageLogger;
import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.model.ConfigurationManager;
import eu.baltrad.dex.registry.model.RegistryManager;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.validation.BindException;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;

/**
 * Controller class creates new delivery registry configuration or modifies existing configuration.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.3
 * @since 0.7.3
 */
public class SaveRegistryConfigurationController extends SimpleFormController {
//---------------------------------------------------------------------------------------- Constants
    /** Success message key */
    private static final String OK_MSG_KEY = "message";
    /** Error message key */
    private static final String ERROR_MSG_KEY = "error";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to configuration manager */
    private ConfigurationManager configurationManager;
    /** Reference to delivery registry manager object */
    private RegistryManager registryManager;
    /** Reference to logger object */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SaveRegistryConfigurationController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Retrieve a backing object for the current form from the given request.
     *
     * @param request Current servlet request
     * @return The backing object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        LogConfiguration regConf = configurationManager.loadRegConf();
        if( regConf == null ) {
            regConf = new LogConfiguration();
        }
        return regConf;
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
     * Submit callback with system messages configuration parameters.
     *
     * @param request Current servlet request
     * @param response Current servlet response
     * @param command Form object with request parameters bound onto it
     * @param Errors instance without errors
     * @return The prepared model and view or null
     * @throws Exception
     */
    @Override
    protected ModelAndView onSubmit( HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors ) throws Exception {
        LogConfiguration regConf = ( LogConfiguration )command;
        try {
            configurationManager.saveRegConf( regConf );
            if( regConf.getTrimByNumber() ) {
                registryManager.setTrimmer( regConf.getRecordLimit() );
            } else {
                registryManager.removeTrimmer(
                        RegistryManager.TRIM_REG_BY_NUMBER_TG );
            }
            if( regConf.getTrimByAge() ) {
                registryManager.setTrimmer( regConf.getMaxAgeDays(),
                        regConf.getMaxAgeHours(), regConf.getMaxAgeMinutes() );
            } else {
                registryManager.removeTrimmer(
                        RegistryManager.TRIM_REG_BY_AGE_TG );
            }
            request.getSession().setAttribute( OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveregconf.savesuccess" ) );
            log.warn( getMessageSourceAccessor().getMessage( "message.saveregconf.savesuccess" ) );
        } catch( Exception e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveregconf.savefail" ) );
            log.error( getMessageSourceAccessor().getMessage( "message.saveregconf.savefail" ), e );
            errors.reject( "message.saveregconf.savefail" );
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
     * Gets reference to delivery registry manager object.
     *
     * @return logManager Reference to delivery registry manager object
     */
    public RegistryManager getDeliveryRegisterManager() { return registryManager; }
    /**
     * Sets reference to delivery registry manager object.
     *
     * @param deliveryRegisterManager Reference to delivery registry manager object
     */
    public void setRegistryManager( RegistryManager registryManager ) {
        this.registryManager = registryManager;
    }
}
//--------------------------------------------------------------------------------------------------
