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

import eu.baltrad.dex.util.ServletContextUtil;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;

/**
 * Class implements configuration object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class ConfigurationManager {
//---------------------------------------------------------------------------------------- Constants
    /** Default properties file name */
    private static final String DEX_DEFAULT_PROPS = "dex.default.properties";
    /** User-defined properties file name */
    private static final String DEX_USER_PROPS = "dex.user.properties";
    
    /** Default properties key */
    private static String DEFAULT_PROPS_KEY = "default_props";
    /** User properties key */
    private static String USER_PROPS_KEY = "user_props";
    
    /** Properties file comment */
    private static final String DEX_PROPS_FILE_COMMENT = "BaltradDex configuration file";
    
    // Application configuration

    /** Node name property */
    private static final String NODE_NAME_PROP = "node.name";
    /** Fully qualified node address */
    private static final String NODE_ADDRESS_PROP = "node.address";
    /** Node type property */
    private static final String NODE_TYPE_PROP = "node.type"; 
    /** Software version property */
    private static final String SOFT_VERSION_PROP = "software.version";
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
    
    /** Keystore directory */
    private static final String KEYSTORE_DIR_PROP = "keystore.directory";
    
//---------------------------------------------------------------------------------------- Variables
    /** References logger object */
    private static Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     */
    public ConfigurationManager() {
        this.log = Logger.getLogger("DEX"); 
    }
    /**
     * Loads properties from files into hash map object.
     * 
     * @return Hash map containing a set of properties 
     * @throws IOException 
     */
    private HashMap<String, Properties> loadProperties() throws IOException {
        HashMap<String, Properties> props = new HashMap<String, Properties>();
        FileInputStream in = new FileInputStream( ServletContextUtil.getServletContextPath() + 
                DEX_DEFAULT_PROPS );
        Properties defaultProps = new Properties();
        defaultProps.load( in );
        in.close();
        props.put( DEFAULT_PROPS_KEY, defaultProps );
        //
        in = new FileInputStream( ServletContextUtil.getServletContextPath() + DEX_USER_PROPS );
        Properties userProps = new Properties( defaultProps );
        userProps.load( in );
        in.close();
        props.put( USER_PROPS_KEY, userProps );
        //
        /*in = new FileInputStream( ServletContextUtil.getServletContextPath() + DEX_VERSION_PROPS );
        Properties versionProps = new Properties();
        versionProps.load( in );
        in.close();
        props.put( VERSION_PROPS_KEY, versionProps );
        /*/
        return props;
    }
    /**
     * Loads system configuration from properties file.
     *
     * @return Configuration object
     */
    public AppConfiguration loadAppConf() {
        try {
            HashMap<String, Properties> props = loadProperties();
            Properties userProps = props.get( USER_PROPS_KEY );
            //Properties versionProps = props.get( VERSION_PROPS_KEY );
            //
            AppConfiguration conf = new AppConfiguration(userProps.getProperty(NODE_NAME_PROP),
                userProps.getProperty(NODE_ADDRESS_PROP), userProps.getProperty(NODE_TYPE_PROP), 
                userProps.getProperty(SOFT_VERSION_PROP), Integer.parseInt(userProps.getProperty(
                SO_TIMEOUT_PROP)), Integer.parseInt(userProps.getProperty(CONN_TIMEOUT_PROP)),
                userProps.getProperty(WORK_DIR_PROP), userProps.getProperty(IMAGES_DIR_PROP),
                userProps.getProperty(THUMBNAILS_DIR_PROP), userProps.getProperty(ORG_NAME_PROP),
                userProps.getProperty(ORG_ADDRESS_PROP), userProps.getProperty(TIME_ZONE_PROP),
                userProps.getProperty(EMAIL_PROP), 
                userProps.getProperty(KEYSTORE_DIR_PROP));
            return conf;
        } catch( Exception e ) {
            log.error( "Failed to load properties", e );
            return null;
        }
    }
    /**
     * Saves system configuration in properties file.
     *
     * @param appConf Application configuration object
     * @throws Exception
     */
    public void saveAppConf( AppConfiguration appConf ) throws Exception {
        try {
            HashMap<String, Properties> props = loadProperties();
            Properties userProps = props.get( USER_PROPS_KEY );
            //
            userProps.setProperty(NODE_NAME_PROP, appConf.getNodeName());
            userProps.setProperty(NODE_ADDRESS_PROP, appConf.getNodeAddress());
            userProps.setProperty(NODE_TYPE_PROP, appConf.getNodeType());
            userProps.setProperty(SO_TIMEOUT_PROP, Integer.toString(appConf.getSoTimeout()));
            userProps.setProperty(CONN_TIMEOUT_PROP, Integer.toString(appConf.getConnTimeout()));
            userProps.setProperty(WORK_DIR_PROP, appConf.getWorkDir());
            userProps.setProperty(IMAGES_DIR_PROP, appConf.getImagesDir());
            userProps.setProperty(THUMBNAILS_DIR_PROP, appConf.getThumbsDir());
            userProps.setProperty(ORG_NAME_PROP, appConf.getOrganization());
            userProps.setProperty(ORG_ADDRESS_PROP, appConf.getAddress());
            userProps.setProperty(TIME_ZONE_PROP, appConf.getTimeZone());
            userProps.setProperty(EMAIL_PROP, appConf.getEmail());
            userProps.setProperty(KEYSTORE_DIR_PROP, appConf.getKeystoreDir());
            //
            FileOutputStream out = new FileOutputStream( ServletContextUtil.getServletContextPath()
                    + DEX_USER_PROPS );
            userProps.store( out, DEX_PROPS_FILE_COMMENT );
            out.close();
        } catch( Exception e ) {
            log.error( "Failed to save properties", e );
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
            HashMap<String, Properties> props = loadProperties();
            Properties userProps = props.get( USER_PROPS_KEY );
            LogConfiguration conf = new LogConfiguration( Boolean.parseBoolean( 
                userProps.getProperty( MSG_TRIM_BY_NUMBER ) ), Boolean.parseBoolean(
                userProps.getProperty( MSG_TRIM_BY_AGE ) ), Integer.parseInt( 
                userProps.getProperty( MSG_REC_LIMIT ) ), Integer.parseInt( 
                userProps.getProperty( MSG_MAX_AGE_DAYS ) ), Integer.parseInt( 
                userProps.getProperty( MSG_MAX_AGE_HOURS ) ), Integer.parseInt( 
                    userProps.getProperty( MSG_MAX_AGE_MINUTES ) ) );
            return conf;
        } catch( Exception e ) {
            log.error( "Failed to load properties", e );
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
    public void saveMsgConf( LogConfiguration msgConf ) throws Exception {
        try {
            HashMap<String, Properties> props = loadProperties();
            Properties userProps = props.get( USER_PROPS_KEY );
            //
            userProps.setProperty( MSG_TRIM_BY_NUMBER, Boolean. toString( 
                    msgConf.getTrimByNumber() ) );
            userProps.setProperty( MSG_TRIM_BY_AGE, Boolean.toString( msgConf.getTrimByAge() ) );
            userProps.setProperty( MSG_REC_LIMIT, Integer.toString( msgConf.getRecordLimit() ) );
            userProps.setProperty( MSG_MAX_AGE_DAYS, Integer.toString( msgConf.getMaxAgeDays() ) );
            userProps.setProperty( MSG_MAX_AGE_HOURS, Integer.toString( 
                    msgConf.getMaxAgeHours() ) );
            userProps.setProperty( MSG_MAX_AGE_MINUTES, Integer.toString( 
                    msgConf.getMaxAgeMinutes() ) );
            //
            FileOutputStream out = new FileOutputStream( ServletContextUtil.getServletContextPath()
                    + DEX_USER_PROPS );
            userProps.store( out, DEX_PROPS_FILE_COMMENT );
            out.close();
        } catch( Exception e ) {
            log.error( "Failed to save messages properties", e );
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
            HashMap<String, Properties> props = loadProperties();
            Properties userProps = props.get( USER_PROPS_KEY );
            LogConfiguration conf = new LogConfiguration( Boolean.parseBoolean( 
                userProps.getProperty( REG_TRIM_BY_NUMBER ) ), Boolean.parseBoolean( 
                userProps.getProperty( REG_TRIM_BY_AGE ) ), Integer.parseInt( 
                userProps.getProperty( REG_REC_LIMIT ) ), Integer.parseInt( userProps.getProperty( 
                REG_MAX_AGE_DAYS ) ), Integer.parseInt( userProps.getProperty( REG_MAX_AGE_HOURS ) ),
                Integer.parseInt( userProps.getProperty( REG_MAX_AGE_MINUTES ) ) );
            return conf;
        } catch( Exception e ) {
            log.error( "Failed to load properties", e );
            return null;
        }
    }
    /**
     * Save delivery registry configuration.
     *
     * @param regConf Delivery registry configuration
     * @throws Exception
     */
    public void saveRegConf( LogConfiguration regConf ) throws Exception {
        try {
            HashMap<String, Properties> props = loadProperties();
            Properties userProps = props.get( USER_PROPS_KEY );
            //
            userProps.setProperty( REG_TRIM_BY_NUMBER, Boolean. toString( 
                    regConf.getTrimByNumber() ) );
            userProps.setProperty( REG_TRIM_BY_AGE, Boolean.toString( regConf.getTrimByAge() ) );
            userProps.setProperty( REG_REC_LIMIT, Integer.toString( regConf.getRecordLimit() ) );
            userProps.setProperty( REG_MAX_AGE_DAYS, Integer.toString( regConf.getMaxAgeDays() ) );
            userProps.setProperty( REG_MAX_AGE_HOURS, Integer.toString( 
                    regConf.getMaxAgeHours() ) );
            userProps.setProperty( REG_MAX_AGE_MINUTES, Integer.toString( 
                    regConf.getMaxAgeMinutes() ) );
            //
            FileOutputStream out = new FileOutputStream( ServletContextUtil.getServletContextPath()
                    + DEX_USER_PROPS );
            userProps.store( out, DEX_PROPS_FILE_COMMENT );
            out.close();
        } catch( Exception e ) {
            log.error( "Failed to save messages properties", e );
            throw e;
        }
    }
}
//--------------------------------------------------------------------------------------------------
