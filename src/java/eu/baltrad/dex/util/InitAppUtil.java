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

package eu.baltrad.dex.util;

import eu.baltrad.dex.model.log.LogManager;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Properties;

/**
 * Utility class used to initialize application on startup.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class InitAppUtil {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.init.properties";
    // Node address property
    private static final String NODE_ADDRESS_PROP = "node.address";
    // Node type property key
    private static final String NODE_TYPE_PROP = "node.type";
    // Organization name property key
    private static final String ORG_NAME_PROP = "node.organization.name";
    // Organization address property key
    private static final String ORG_ADDRESS_PROP = "node.organization.address";
    // Timezone property key
    private static final String TIME_ZONE_PROP = "node.timezone";
    // Node administrator email property key
    private static final String ADMIN_EMAIL_PROP = "node.admin.email";
    // Local production directory property key
    private static final String PROD_DIR_PROP = "local.production.dir";
    // Temporary directory property key
    private static final String TEMP_DIR_PROP = "local.temp.dir";
    // Incoming data directory property key
    private static final String INCOMING_DIR_PROP = "incoming.data.dir";
//---------------------------------------------------------------------------------------- Variables
    // Reference to LogManager object
    private static LogManager logManager = new LogManager();
    // Node address
    private static String nodeAddress;
    // Node type
    private static String nodeType;
    // Organization name
    private static String orgName;
    // Organization address
    private static String orgAddress;
    // Time zone
    private static String timeZone;
    // Node administrator email
    private static String adminEmail;
    // Directory storing data from local production system
    private static String localProdDir;
    // Temporary directory for local data
    private static String localTempDir;
    // Directory storing incoming data from foreign nodes
    private static String incomingDataDir;
    
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor performs initialization task
     */
    public InitAppUtil() {
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                setNodeAddress( props.getProperty( NODE_ADDRESS_PROP ) );
                setNodeType( props.getProperty( NODE_TYPE_PROP ) );
                setOrgName( props.getProperty( ORG_NAME_PROP ) );
                setOrgAddress( props.getProperty( ORG_ADDRESS_PROP ) );
                setTimeZone( props.getProperty( TIME_ZONE_PROP ) );
                setAdminEmail( props.getProperty( ADMIN_EMAIL_PROP ) );
                setLocalProdDir( ServletContextUtil.getServletContextPath() +
                        props.getProperty( PROD_DIR_PROP ) );
                setIncomingDataDir( ServletContextUtil.getServletContextPath() +
                        props.getProperty( INCOMING_DIR_PROP ) );
                setLocalTempDir( props.getProperty( TEMP_DIR_PROP ) );
                // Create directories
                //makeDir( getLocalProdDir() );
                makeDir( getIncomingDataDir() );
                logManager.addEntry( new Date(), LogManager.MSG_INFO,
                        "Application successfully initialized" );
            } else {
                logManager.addEntry( new Date(), LogManager.MSG_ERR,
                        "Failed to load properties file: " + PROPS_FILE_NAME );
            }
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                        "Failed to load properties file: " + PROPS_FILE_NAME );
        }
    }
    /**
     * Gets node address property.
     *
     * @return Node address property
     */
    public static String getNodeAddress() { return nodeAddress; }
    /**
     * Sets node address property.
     *
     * @param _nodeAddress Node address property to set
     */
    public void setNodeAddress( String _nodeAddress ) { nodeAddress = _nodeAddress; }
    /**
     * Gets node type property.
     *
     * @return Node type property
     */
    public static String getNodeType() { return nodeType; }
    /**
     * Sets node type property.
     *
     * @param _nodeType Node type property to set
     */
    public void setNodeType( String _nodeType ) { nodeType = _nodeType; }
    /**
     * Gets organization name property.
     *
     * @return Organization name property
     */
    public static String getOrgName() { return orgName; }
    /**
     * Sets organization name property.
     *
     * @param _orgName Organization name property to set
     */
    public void setOrgName( String _orgName ) { orgName = _orgName; }
    /**
     * Gets organization address property.
     *
     * @return Organization address property
     */
    public static String getOrgAddress() { return orgAddress; }
    /**
     * Sets organization address property.
     *
     * @param _orgAddress Organization address property to set
     */
    public void setOrgAddress( String _orgAddress ) { orgAddress = _orgAddress; }
    /**
     * Gets time zone property.
     *
     * @return Time zone property
     */
    public static String getTimeZone() { return timeZone; }
    /**
     * Sets time zone property.
     *
     * @param _timeZone Time zone property to set
     */
    public void setTimeZone( String _timeZone ) { timeZone = _timeZone; }
    /**
     * Gets administrator email property.
     *
     * @return Administrator email property
     */
    public static String getAdminEmail() { return adminEmail; }
    /**
     * Sets administrator email property.
     *
     * @param _adminEmail Administrator email property to set
     */
    public void setAdminEmail( String _adminEmail ) { adminEmail = _adminEmail; }
    /**
     * Method gets local production directory name.
     *
     * @return The name of local production directory
     */
    public static String getLocalProdDir() { return localProdDir; }
    /**
     * Method sets local production directory name.
     *
     * @param _localProdDir The name of local production directory
     */
    public void setLocalProdDir( String _localProdDir ) { localProdDir = _localProdDir; }
    /**
     * Method gets local temporary directory name.
     *
     * @return The name of local temporary directory
     */
    public static String getLocalTempDir() { return localTempDir; }
    /**
     * Method sets local temporary directory name.
     *
     * @param _localTempDir The name of local temporary directory
     */
    public void setLocalTempDir( String _localTempDir ) { localTempDir = _localTempDir; }
    /**
     * Method gets incoming data directory name.
     *
     * @return Incoming data directory name
     */
    public static String getIncomingDataDir() { return incomingDataDir; }
    /**
     * Method sets incoming data directory name.
     *
     * @param _incomingDataDir The name of incoming data directory
     */
    public void setIncomingDataDir( String _incomingDataDir ) {
        incomingDataDir = _incomingDataDir;
    }
    /**
     * Method extracts relative file name from absolute file path string.
     *
     * @param absFilePath Absolute file path string
     * @return Relative file name
     */
    public static String getRelFileName( String absFilePath ) {
        return absFilePath.substring( absFilePath.lastIndexOf( File.separator ) + 1,
                    absFilePath.length() );
    }
    /**
     * Method creates new directory.
     *
     * @param directoryPath Path to the directory
     */
    public static void makeDir( String directoryPath ) {
        File dir = new File( directoryPath );
        if( !dir.exists() ) {
            dir.mkdirs();
            logManager.addEntry( new Date(), LogManager.MSG_INFO, "New directory created: \n"
                    + directoryPath );
        }
    }
    /**
     * Method saves data from input stream to file with a given name.
     *
     * @param is Input data stream
     * @param dstFileName Output file name
     */
    public static void saveFile( InputStream is, String dstFileName ) {
        try {
            File dstFile = new File( dstFileName );
            FileOutputStream fos = new FileOutputStream( dstFile );
            byte[] bytes = new byte[ 1024 ];
            int len;
            while( ( len = is.read( bytes ) ) > 0 ) {
                fos.write( bytes, 0, len);
            }
            is.close();
            fos.close();
            logManager.addEntry( new Date(), LogManager.MSG_INFO, "Incoming data succesfully " +
                    " saved: \n" + getRelFileName( dstFileName ) );
        } catch( FileNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        }
    }
    /**
     * Method deletes file
     *
     * @param filePath Absolute file path
     */
    public static void deleteFile( String filePath ) {
        try {
            File f = new File( filePath );
            f.delete();
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while deleting file: \n"
                    + e.getMessage() );
        }
    }
}
//--------------------------------------------------------------------------------------------------
