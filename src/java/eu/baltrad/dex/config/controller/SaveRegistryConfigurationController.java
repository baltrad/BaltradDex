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

import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.model.ConfigurationManager;
import eu.baltrad.dex.registry.model.DeliveryRegisterManager;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.validation.BindException;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    /** Date format string */
    private static final String DATE_FORMAT_STR = "yyyy/MM/dd HH:mm:ss";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to configuration manager */
    private ConfigurationManager configurationManager;
    /** Reference to delivery registry manager object */
    private DeliveryRegisterManager deliveryRegisterManager;
    /** Reference to logger object */
    private Logger log;
    /** Date format */
    private DateFormat df;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public SaveRegistryConfigurationController() {
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        df = new SimpleDateFormat( DATE_FORMAT_STR );
    }
    /**
     * Retrieve a backing object for the current form from the given request.
     *
     * @param request Current servlet request
     * @return The backing object
     */
    @Override
    protected Object formBackingObject( HttpServletRequest request ) {
        LogConfiguration logConf = null;
        try {
            logConf = configurationManager.getLogConfiguration(
                LogConfiguration.LOG_DELIVERY_REGISTRY );
        } catch( SQLException e ) {
            log.error( "Error while loading delivery registry configuration from database: " +
                    e.getMessage() );
        } catch( Exception e ) {
            log.error( "Error while loading delivery registry configuration from database: " +
                    e.getMessage() );
        }
        if( logConf == null ) {
            logConf = new LogConfiguration();
        }
        return logConf;
    }
    /**
     * Initialize the given binder instance with custom property editors.
     *
     * @param request Current servlet request
     * @param binder New binder instance
     */
    @Override
    protected void initBinder( HttpServletRequest request, ServletRequestDataBinder binder ) {
        binder.registerCustomEditor( Date.class, new CustomDateEditor( df, true ) );
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
        LogConfiguration logConf = ( LogConfiguration )command;
        logConf.setLogId( LogConfiguration.LOG_DELIVERY_REGISTRY );
        try {
            configurationManager.saveOrUpdate( logConf );
            if( logConf.getTrimByNumber() ) {
                deliveryRegisterManager.setTrimmer( logConf.getRecordLimit() );
            } else {
                deliveryRegisterManager.removeTrimmer(
                        DeliveryRegisterManager.TRIM_REG_BY_NUMBER_FUNC );
            }
            if( logConf.getTrimByDate() ) {
                deliveryRegisterManager.setTrimmer( df.format( logConf.getDateLimit() ) );
            } else {
                deliveryRegisterManager.removeTrimmer(
                        DeliveryRegisterManager.TRIM_REG_BY_DATE_FUNC );
            }
            request.getSession().setAttribute( OK_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveregconf.savesuccess" ) );
            log.warn( getMessageSourceAccessor().getMessage( "message.saveregconf.savesuccess" ) );
        } catch( SQLException e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveregconf.savefail" ) );
            log.error( getMessageSourceAccessor().getMessage( "message.saveregconf.savefail" ) +
                    ": " + e.getMessage() );
            errors.reject( "message.saveregconf.savefail" );
        } catch( Exception e ) {
            request.getSession().removeAttribute( OK_MSG_KEY );
            request.getSession().setAttribute( ERROR_MSG_KEY, getMessageSourceAccessor().getMessage(
                    "message.saveregconf.savefail" ) );
            log.error( getMessageSourceAccessor().getMessage( "message.saveregconf.savefail" ) +
                    ": " + e.getMessage() );
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
    public DeliveryRegisterManager getDeliveryRegisterManager() { return deliveryRegisterManager; }
    /**
     * Sets reference to delivery registry manager object.
     *
     * @param deliveryRegisterManager Reference to delivery registry manager object
     */
    public void setDeliveryRegisterManager( DeliveryRegisterManager deliveryRegisterManager ) {
        this.deliveryRegisterManager = deliveryRegisterManager;
    }
}
//--------------------------------------------------------------------------------------------------