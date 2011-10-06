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

package eu.baltrad.dex.config.model;

import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.net.URL;

/**
 * Class implemens configuration object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class ConfigurationManager {
//---------------------------------------------------------------------------------------- Constants
    /** Properties file name */
    private static final String DEX_INIT_PROPS = "dex.init.properties";
    /** Properties file comment */
    private static final String DEX_PROPS_FILE_COMMENT = "BaltradDex configuration file";
    
    // Application configuration

    /** Node name property */
    private static final String NODE_NAME_PROP = "node.name";
    /** Node type property */
    private static final String NODE_TYPE_PROP = "node.type"; 
    /** Software version property */
    private static final String SOFT_VERSION_PROP = "software.version";
    /** Communication scheme property */
    private static final String COM_SCHEME_PROP = "communication.scheme";
    /** Host afddress property */
    private static final String HOST_ADDRESS_PROP = "host.address";
    /** Port number property */
    private static final String PORT_NUMBER_PROP = "port.number";
    /** Application context property */
    private static final String APP_CTX_PROP = "application.context";
    /** Entry point property */
    private static final String ENTRY_ADDRESS_PROP = "entry.address";
    /** Socket timeout property */
    private static final String SO_TIMEOUT_PROP = "socket.timeout";
    /** Connection timeout property */
    private static final String CONN_TIMEOUT_PROP = "connection.timeout";
    /** Organization's name property */
    private static final String ORG_NAME_PROP = "organization.name";
    /** Organization's address property */
    private static final String ORG_ADDRESS_PROP = "organization.address";
    /** Time zone property */
    private static final String TIME_ZONE_PROP = "time.zone";
    /** Work directory property */
    private static final String WORK_DIR_PROP = "work.directory";
    /** Images directory property */
    private static final String IMAGES_DIR_PROP = "images.directory";
    /** Thumbnails directory property */
    private static final String THUMBNAILS_DIR_PROP = "thumbnails.directory";
    /** Node administrator's email property */
    private static final String EMAIL_PROP = "admin.email";

    // System messages configuration

    /** Trim by number toggle */
    private static final String MSG_TRIM_BY_NUMBER = "messages.trim_by_number";
    /** Trim by date toggle */
    private static final String MSG_TRIM_BY_AGE = "messages.trim_by_age";
    /** Records limit */
    private static final String MSG_REC_LIMIT = "messages.rec_limit";
    /** Age limit - days */
    private static final String MSG_MAX_AGE_DAYS = "messages.max_age_days";
    /** Age limit - hours */
    private static final String MSG_MAX_AGE_HOURS = "messages.max_age_hours";
    /** Age limit - minutes */
    private static final String MSG_MAX_AGE_MINUTES = "messages.max_age_minutes";

    // Delivery registry configuration

    /** Trim by number toggle */
    private static final String REG_TRIM_BY_NUMBER = "registry.trim_by_number";
    /** Trim by date toggle */
    private static final String REG_TRIM_BY_AGE = "registry.trim_by_age";
    /** Records limit */
    private static final String REG_REC_LIMIT = "registry.rec_limit";
    /** Age limit - days */
    private static final String REG_MAX_AGE_DAYS = "registry.max_age_days";
    /** Age limit - hours */
    private static final String REG_MAX_AGE_HOURS = "registry.max_age_hours";
    /** Age limit - minutes */
    private static final String REG_MAX_AGE_MINUTES = "registry.max_age_minutes";
//---------------------------------------------------------------------------------------- Variables
    /** References logger object */
    private static Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public ConfigurationManager() {
        log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Loads system configuration from properties file.
     *
     * @return Configuration object
     */
    public AppConfiguration loadAppConf() {
        try {
            URL url = ConfigurationManager.class.getResource ( DEX_INIT_PROPS );
            Properties props = new Properties();
            props.load( url.openStream() );
            AppConfiguration conf = new AppConfiguration( props.getProperty( NODE_NAME_PROP ),
                props.getProperty( NODE_TYPE_PROP ), props.getProperty( SOFT_VERSION_PROP ),
                props.getProperty( COM_SCHEME_PROP ), props.getProperty( HOST_ADDRESS_PROP ),
                Integer.parseInt( props.getProperty( PORT_NUMBER_PROP ) ),
                props.getProperty( APP_CTX_PROP ), props.getProperty( ENTRY_ADDRESS_PROP ),
                Integer.parseInt( props.getProperty( SO_TIMEOUT_PROP ) ),
                Integer.parseInt( props.getProperty( CONN_TIMEOUT_PROP ) ),
                props.getProperty( WORK_DIR_PROP ), props.getProperty( IMAGES_DIR_PROP ),
                props.getProperty( THUMBNAILS_DIR_PROP ), props.getProperty( ORG_NAME_PROP),
                props.getProperty( ORG_ADDRESS_PROP ), props.getProperty( TIME_ZONE_PROP ),
                props.getProperty( EMAIL_PROP ) );
            return conf;
        } catch( IOException e ) {
            log.error( "Failed to load properties: " + e.getMessage() );
            return null;
        } catch( Exception e ) {
            log.error( "Failed to load properties: " + e.getMessage() );
            return null;
        }
    }
    /**
     * Saves system configuration in properties file.
     *
     * @param appConf Application configuration object
     * @throws IOException
     * @throws Exception
     */
    public void saveAppConf( AppConfiguration appConf ) throws IOException, Exception {
        try {
            URL url = ConfigurationManager.class.getResource ( DEX_INIT_PROPS );
            Properties props = new Properties();
            props.load( url.openStream() );
            //
            props.setProperty( NODE_NAME_PROP, appConf.getNodeName() );
            props.setProperty( NODE_TYPE_PROP, appConf.getNodeType() );
            props.setProperty( SOFT_VERSION_PROP, appConf.getVersion() );
            props.setProperty( COM_SCHEME_PROP, appConf.getScheme() );
            props.setProperty( HOST_ADDRESS_PROP, appConf.getHostAddress() );
            props.setProperty( PORT_NUMBER_PROP, Integer.toString( appConf.getPort() ) );
            props.setProperty( APP_CTX_PROP, appConf.getAppCtx() );
            props.setProperty( ENTRY_ADDRESS_PROP, appConf.getEntryAddress() );
            props.setProperty( SO_TIMEOUT_PROP, Integer.toString( appConf.getSoTimeout() ) );
            props.setProperty( CONN_TIMEOUT_PROP, Integer.toString( appConf.getConnTimeout()) );
            props.setProperty( WORK_DIR_PROP, appConf.getWorkDir() );
            props.setProperty( IMAGES_DIR_PROP, appConf.getImagesDir() );
            props.setProperty( THUMBNAILS_DIR_PROP, appConf.getThumbsDir() );
            props.setProperty( ORG_NAME_PROP, appConf.getOrganization() );
            props.setProperty( ORG_ADDRESS_PROP, appConf.getAddress() );
            props.setProperty( TIME_ZONE_PROP, appConf.getTimeZone() );
            props.setProperty( EMAIL_PROP, appConf.getEmail() );
            //
            OutputStream os = new FileOutputStream( url.getPath() );
            props.store( os, DEX_PROPS_FILE_COMMENT );
        } catch( IOException e ) {
            log.error( "Failed to save properties: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            log.error( "Failed to save properties: " + e.getMessage() );
            throw e;
        }
    }
    /**
     * Load messages configuration.
     *
     * @return Messages configuration
     */
    public LogConfiguration loadMsgConf() {
        try {
            URL url = ConfigurationManager.class.getResource ( DEX_INIT_PROPS );
            Properties props = new Properties();
            props.load( url.openStream() );
            LogConfiguration conf = new LogConfiguration( Boolean.parseBoolean( props.getProperty(
                    MSG_TRIM_BY_NUMBER ) ), Boolean.parseBoolean( props.getProperty(
                    MSG_TRIM_BY_AGE ) ), Integer.parseInt( props.getProperty( MSG_REC_LIMIT ) ),
                    Integer.parseInt( props.getProperty( MSG_MAX_AGE_DAYS ) ),
                    Integer.parseInt( props.getProperty( MSG_MAX_AGE_HOURS ) ),
                    Integer.parseInt( props.getProperty( MSG_MAX_AGE_MINUTES ) ) );
            return conf;
        } catch( IOException e ) {
            log.error( "Failed to load properties: " + e.getMessage() );
            return null;
        } catch( Exception e ) {
            log.error( "Failed to load properties: " + e.getMessage() );
            return null;
        }
    }
    /**
     * Save messages configuration.
     *
     * @param msgConf Messages configuration
     * @throws IOException
     * @throws Exception
     */
    public void saveMsgConf( LogConfiguration msgConf ) throws IOException, Exception {
        try {
            URL url = ConfigurationManager.class.getResource ( DEX_INIT_PROPS );
            Properties props = new Properties();
            props.load( url.openStream() );
            //
            props.setProperty( MSG_TRIM_BY_NUMBER, Boolean. toString( msgConf.getTrimByNumber() ) );
            props.setProperty( MSG_TRIM_BY_AGE, Boolean.toString( msgConf.getTrimByAge() ) );
            props.setProperty( MSG_REC_LIMIT, Integer.toString( msgConf.getRecordLimit() ) );
            props.setProperty( MSG_MAX_AGE_DAYS, Integer.toString( msgConf.getMaxAgeDays() ) );
            props.setProperty( MSG_MAX_AGE_HOURS, Integer.toString( msgConf.getMaxAgeHours() ) );
            props.setProperty( MSG_MAX_AGE_MINUTES, Integer.toString( msgConf.getMaxAgeMinutes() ) );
            //
            OutputStream os = new FileOutputStream( url.getPath() );
            props.store( os, DEX_PROPS_FILE_COMMENT );
        } catch( IOException e ) {
            log.error( "Failed to save messages properties: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            log.error( "Failed to save messages properties: " + e.getMessage() );
            throw e;
        }
    }
    /**
     * Load delivery registry configuration.
     *
     * @return Delivery registry configuration
     */
    public LogConfiguration loadRegConf() {
        try {
            URL url = ConfigurationManager.class.getResource ( DEX_INIT_PROPS );
            Properties props = new Properties();
            props.load( url.openStream() );
            LogConfiguration conf = new LogConfiguration( Boolean.parseBoolean( props.getProperty(
                    REG_TRIM_BY_NUMBER ) ), Boolean.parseBoolean( props.getProperty(
                    REG_TRIM_BY_AGE ) ), Integer.parseInt( props.getProperty( REG_REC_LIMIT ) ),
                    Integer.parseInt( props.getProperty( REG_MAX_AGE_DAYS ) ),
                    Integer.parseInt( props.getProperty( REG_MAX_AGE_HOURS ) ),
                    Integer.parseInt( props.getProperty( REG_MAX_AGE_MINUTES ) ) );
            return conf;
        } catch( IOException e ) {
            log.error( "Failed to load properties: " + e.getMessage() );
            return null;
        } catch( Exception e ) {
            log.error( "Failed to load properties: " + e.getMessage() );
            return null;
        }
    }
    /**
     * Save delivery registry configuration.
     *
     * @param regConf Delivery registry configuration
     * @throws IOException
     * @throws Exception
     */
    public void saveRegConf( LogConfiguration regConf ) throws IOException, Exception {
        try {
            URL url = ConfigurationManager.class.getResource ( DEX_INIT_PROPS );
            Properties props = new Properties();
            props.load( url.openStream() );
            //
            props.setProperty( REG_TRIM_BY_NUMBER, Boolean. toString( regConf.getTrimByNumber() ) );
            props.setProperty( REG_TRIM_BY_AGE, Boolean.toString( regConf.getTrimByAge() ) );
            props.setProperty( REG_REC_LIMIT, Integer.toString( regConf.getRecordLimit() ) );
            props.setProperty( REG_MAX_AGE_DAYS, Integer.toString( regConf.getMaxAgeDays() ) );
            props.setProperty( REG_MAX_AGE_HOURS, Integer.toString( regConf.getMaxAgeHours() ) );
            props.setProperty( REG_MAX_AGE_MINUTES, Integer.toString( regConf.getMaxAgeMinutes() ) );
            //
            OutputStream os = new FileOutputStream( url.getPath() );
            props.store( os, DEX_PROPS_FILE_COMMENT );
        } catch( IOException e ) {
            log.error( "Failed to save messages properties: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            log.error( "Failed to save messages properties: " + e.getMessage() );
            throw e;
        }
    }
}
//--------------------------------------------------------------------------------------------------
