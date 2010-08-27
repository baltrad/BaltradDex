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

import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.dex.config.model.ConfigurationManager;
import eu.baltrad.dex.config.model.Configuration;

import org.hibernate.HibernateException;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Utility class used to initialize application on startup.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class InitAppUtil {
//---------------------------------------------------------------------------------------- Constants
    // Temporary file prefix
    private final static String TEMP_FILE_PREFIX = "dex";
    // Temporary file suffix
    private final static String TEMP_FILE_SUFFIX = ".dat";
//---------------------------------------------------------------------------------------- Variables
    // Initialize LogManager object
    private static LogManager logManager = new LogManager();
    // Initialize Configuration manager
    private static ConfigurationManager configurationManager = new ConfigurationManager();
    // Node address
    private static String nodeAddress;
    // Node name
    private static String nodeName;
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
    // Temporary directory
    private static String localTempDir;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor performs initialization task
     */
    public InitAppUtil() { initApp(); }
    /**
     * Method initializes application by reading configuration from database.
     */
    public static void initApp() {
        Configuration conf = null;
        try {
            conf = configurationManager.getConfiguration( ConfigurationManager.CONF_REC_ID );
        } catch( HibernateException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while reading configuration"
                    + " from database: " + e.getMessage() );
        }
        if( conf != null ) {
            setNodeName( conf.getNodeName() );
            setNodeAddress( conf.getNodeAddress() );
            setNodeType( conf.getNodeType() );
            setOrgName( conf.getOrgName() );
            setOrgAddress( conf.getOrgAddress() );
            setTimeZone( conf.getTimeZone() );
            setAdminEmail( conf.getAdminEmail() );
            setLocalTempDir( ServletContextUtil.getServletContextPath() + conf.getTempDir() );
            // create temporary directory
            makeDir( getLocalTempDir() );
            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                    "Application successfully initialized" );
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
    public static void setNodeAddress( String _nodeAddress ) { nodeAddress = _nodeAddress; }
    /**
     * Gets node name property.
     *
     * @return Node name property
     */
    public static String getNodeName() { return nodeName; }
    /**
     * Sets node name property.
     *
     * @param _nodeName Node name property to set
     */
    public static void setNodeName( String _nodeName ) { nodeName = _nodeName; }
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
    public static void setNodeType( String _nodeType ) { nodeType = _nodeType; }
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
    public static void setOrgName( String _orgName ) { orgName = _orgName; }
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
    public static void setOrgAddress( String _orgAddress ) { orgAddress = _orgAddress; }
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
    public static void setTimeZone( String _timeZone ) { timeZone = _timeZone; }
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
    public static void setAdminEmail( String _adminEmail ) { adminEmail = _adminEmail; }
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
    public static void setLocalTempDir( String _localTempDir ) { localTempDir = _localTempDir; }
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
     * Creates temporary file.
     *
     * @param tempDir Temporary directory
     * @return Reference to temporary file
     */
    public static File createTempFile( File tempDir ) {
        File f = null;
        try {
            f = File.createTempFile( TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, tempDir );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while creating temporary " +
                    "file: \n" + e.getMessage() );
        }
        return f;
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
        } catch( FileNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        }
    }
    /**
     * Method saves data from input stream to file with a given name.
     *
     * @param is Input data stream
     * @param dstFile Output file
     */
    public static void saveFile( InputStream is, File dstFile ) {
        try {
            FileOutputStream fos = new FileOutputStream( dstFile );
            byte[] bytes = new byte[ 1024 ];
            int len;
            while( ( len = is.read( bytes ) ) > 0 ) {
                fos.write( bytes, 0, len);
            }
            is.close();
            fos.close();
        } catch( FileNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        }
    }
    /**
     * Deletes file.
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
    /**
     * Deletes file.
     * 
     * @param f File object
     */
    public static void deleteFile( File f ) {
        if( !f.delete() ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while deleting file: "
                    + f.getName() );
        }
    }
    /**
     * Creates ObjectOutputStream connected to a given file and writes given object to the stream.
     *
     * @param o Input object to write
     * @param f Input file for writing object
     */
    public static void writeObjectToStream( Object obj, File f ) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( f ) );
            oos.writeObject( obj );
            oos.close();
        } catch( FileNotFoundException e )  {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while writing object to stream: " + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while writing object to stream: " + e.getMessage() );
        }
    }
    /**
     * Creates ObjectInputStream connected to a given file and reads object from the stream.
     *
     * @param f Input file
     * @return Object read from file upon success, null in case of a failure
     */
    public static Object readObjectFromStream( File f ) {
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream( new FileInputStream( f ) );
            obj = ois.readObject();
            ois.close();
        } catch( ClassNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while reading object from stream: " + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while reading object from stream: " + e.getMessage() );
        }
        return obj;
    }
}
//--------------------------------------------------------------------------------------------------